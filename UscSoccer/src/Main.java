import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Main {
	private static final Map<Integer, String> sportEnums = new TreeMap<>(); // enum 1=FOOTBALL etc.
	private static final TreeSet<String> allSchoolNames = new TreeSet<>(); // all WPIAL schools
	private static final int END_OF_CURRENT_SEASON = 2017;

	public static void main(String[] args) throws IOException {
		fillSportEnumsAndSchoolNames();
		TreeMap<String, List<SeasonTemplate>> allTotalRecords = new TreeMap<>();
		for (String schoolName : allSchoolNames) {
			PrintWriter writerSchool = newPrintWriter("dataBySchool/" + schoolName + ".html", "Sport");
			List<SeasonTemplate> totalRecordsForEachSport = new ArrayList<>();
			allTotalRecords.put(schoolName, totalRecordsForEachSport);
			for (int sportNum : sportEnums.keySet()) {
				String sportName = sportEnums.get(sportNum);
				PrintWriter writerSpecificSeasons = newPrintWriter("specificData/" + schoolName + " " + sportName + " " + "seasons.html", "Year");
				ArrayList<Game> games = new ArrayList<>(); // all games a team has played
				totalRecordsForEachSport.add(new SeasonTemplate(schoolName));
				for (int year = 2003; year < END_OF_CURRENT_SEASON; year++) {
					SeasonTemplate season = new SeasonTemplate(schoolName);
					File f = new File("../GetTables/tables/" + schoolName + sportName + year + ".html");
					if (!f.exists() || f.isDirectory()) continue;
					Elements trs = Jsoup.parse(f, "UTF-8").select("table").first().select("tr");
					TreeSet<Integer> gameRows = new TreeSet<>();
					String[][] trtd = new String[trs.size()][];
					fillInTRTDandGameRows(trs, trtd, gameRows);
					addGames(trtd, gameRows, games, season, totalRecordsForEachSport); // adds to games, season, and total record
					season.printSeasonToTable(writerSpecificSeasons, String.valueOf(year));
				}
				totalRecordsForEachSport.get(totalRecordsForEachSport.size() - 1).printSeasonToTable(writerSpecificSeasons, "TOTAL");
				totalRecordsForEachSport.get(totalRecordsForEachSport.size() - 1).printSeasonToTable(writerSchool, sportName);
				sortAndWriteOpponentsTable(games, schoolName, sportNum);
				endTableAndClose(writerSpecificSeasons);
			}
			endTableAndClose(writerSchool);
		}
		sortAndWriteDataBySportTables(allTotalRecords);
	}

	private static PrintWriter newPrintWriter(String fileName, String firstColumnTitle) throws FileNotFoundException {
		PrintWriter writerSchool = new PrintWriter(new File(fileName));
		writeTableHeader(writerSchool, firstColumnTitle);
		return writerSchool;
	}

	private static void endTableAndClose(PrintWriter writer) {
		writer.println("</table>");
		writer.close();
	}

	private static void addGames(String[][] trtd, TreeSet<Integer> gameRows, ArrayList<Game> games, SeasonTemplate season, List<SeasonTemplate> totalRecordsForEachSport) {
		for (int gameRow : gameRows) {
			if (trtd[gameRow][3].matches(".*[WTL].*")) {
				Game g = createGameByParsingRow(trtd[gameRow]);
				games.add(g);
				season.addGame(g);
				totalRecordsForEachSport.get(totalRecordsForEachSport.size() - 1).addGame(g);
			}
		}
	}

	private static void writeTableHeader(PrintWriter writer, String firstColumnTitle) {
		writer.println("<table>");
		writer.println("<tr>");
		SeasonTemplate.writeDataToHTMLTable(writer, new Object[]{firstColumnTitle, "Games", "W", "T", "L", "For", "Against", "Diff", "Pct", "F/GP", "A/GP", "+/-"});
		writer.println("</tr>");
	}

	private static void fillInTRTDandGameRows(Elements trs, String[][] trtd, TreeSet<Integer> gameRows) {
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			trtd[i] = new String[tds.size()];
			for (int j = 0; j < tds.size(); j++) {
				trtd[i][j] = tds.get(j).text();
				if (!tds.get(j).text().isEmpty() && j == 0) {
					gameRows.add(i); // skip <th> columns and checks to make sure game not already in gameRow[]
				}
			}
		}
	}

	private static void fillSportEnumsAndSchoolNames() throws IOException {
		sportEnums.put(1, "FOOTBALL");
		sportEnums.put(2, "BASEBALL");
		sportEnums.put(3, "BASKETBALL");
		sportEnums.put(8, "SOCCER");
		sportEnums.put(4, "WOMENS BASKETBALL");
		sportEnums.put(5, "WOMENS SOFTBALL");
		sportEnums.put(9, "WOMENS SOCCER");

		BufferedReader reader = new BufferedReader(new FileReader("WPIAL schools.txt"));
		for (String line; (line = reader.readLine()) != null; )
			allSchoolNames.add(line);
		reader.close();
	}

	private static Game createGameByParsingRow(String[] actualGameRow) {
		//  OPPONENT
		String[] tokenOpponent = actualGameRow[1].split("[*]+")[0].split("at ");
		String opponent = tokenOpponent[tokenOpponent.length - 1];
		if (opponent.contains("Seton-La")) opponent = "Seton-La Salle";
		if (opponent.contains("Quigley")) opponent = "Quigley Catholic";
		if (opponent.contains("Geibel")) opponent = "Geibel Catholic";

		// RESULT GOALS FOR/AGAINST
		int goalsFor, goalsAgainst;
		String[] tokens3 = actualGameRow[3].split("[(]")[0].split("[ -]+");
		String result = tokens3[0];
		int goals1 = Integer.parseInt(tokens3[1]);
		int goals2 = Integer.parseInt(tokens3[2]);
		if (result.contains("W")) {
			goalsFor = Math.max(goals1, goals2);
			goalsAgainst = Math.min(goals1, goals2);
		} else { // tie or loss
			goalsFor = Math.min(goals1, goals2);
			goalsAgainst = Math.max(goals1, goals2);
		}
		return new Game(opponent, result, goalsFor, goalsAgainst);
	}

	private static void sortAndWriteDataBySportTables(TreeMap<String, List<SeasonTemplate>> allTotalRecords) throws FileNotFoundException {
		int i = 0;
		for (int sportNum : sportEnums.keySet()) {
			PrintWriter writerSport = newPrintWriter("dataBySport/" + sportEnums.get(sportNum) + ".html", "Team");
			List<SeasonTemplate> schools = new ArrayList<>();
			for (String schoolName : allSchoolNames) {
				SeasonTemplate schoolsAllTimeRecordInSport = allTotalRecords.get(schoolName).get(i);
				if (schoolsAllTimeRecordInSport.GP != 0) {
					schools.add(schoolsAllTimeRecordInSport);
				}
			}
			schools.sort((o1, o2) -> new BigDecimal(o2.winPct - o1.winPct).setScale(0, RoundingMode.UP).intValue()); // TODO private
			for (SeasonTemplate school : schools) {
				school.printSeasonToTable(writerSport, school.schoolName);
			}
			endTableAndClose(writerSport);
			i++;
		}
	}

	private static void sortAndWriteOpponentsTable(ArrayList<Game> games, String schoolName, int sportNum) throws IOException {
		TreeMap<String, SeasonTemplate> opponents = new TreeMap<>();
		for (Game game : games) {
			opponents.computeIfAbsent(game.opponent, k -> new SeasonTemplate(game.opponent));
			opponents.get(game.opponent).addGame(game);
		}
		sortAndWriteOpponentsTableActually(new ArrayList<>(opponents.values()), schoolName, sportNum);
	}

	private static void sortAndWriteOpponentsTableActually(List<SeasonTemplate> opponents, String schoolName, int sportNum) throws IOException {
		opponents.sort((o1, o2) -> o2.GP - o1.GP); // TODO private
		PrintWriter writerSpecificOpponents = newPrintWriter("specificData/" + schoolName + " " + sportEnums.get(sportNum) + " " + "opponents.html", "Opponent");
		for (SeasonTemplate opponent : opponents) {
			opponent.printSeasonToTable(writerSpecificOpponents, opponent.schoolName);
		}
		endTableAndClose(writerSpecificOpponents);
	}
}