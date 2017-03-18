import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Main {
	private static final Map<Integer, String> sportEnums = new TreeMap<>(); // enum 1=FOOTBALL etc.
	private static final TreeSet<String> allSchoolNames = new TreeSet<>(); // all WPIAL schools (142 of them)
	private static final int END_OF_CURRENT_SEASON = 2017;

	public static void main(String[] args) throws IOException {
		fillSportEnumsAndSchoolNames();
		for (String schoolName : allSchoolNames) {
			PrintWriter writerSchool = newPrintWriter("dataBySchool/" + schoolName + ".html", "Sport");
			List<SeasonTemplate> totalRecordsForEachSport = new ArrayList<>();
			for (int sportNum : sportEnums.keySet()) {
				String sportName = sportEnums.get(sportNum);
				PrintWriter writerSpecificSeasons = newPrintWriter("specificData/" + schoolName + " " + sportName + " " + "seasons.html", "Year");
				ArrayList<Game> g = new ArrayList<>(); // all games a team has played
				SeasonTemplate seasons[] = new SeasonTemplate[3000];
				totalRecordsForEachSport.add(new SeasonTemplate());
				int totalGameCounter = 0;
				for (int year = 2003; year < END_OF_CURRENT_SEASON; year++) { // from '03-'04
					seasons[year] = new SeasonTemplate(year);
					File f = new File("tables/" + schoolName + sportName + year + ".html");
					if (!f.exists() || f.isDirectory()) continue;
					Elements trs = Jsoup.parse(f, "UTF-8").select("table").first().select("tr");
					int[] gameRow = new int[100];
					String[][] trtd = new String[trs.size()][];
					int gamesInSeason = getTableData(trs, trtd, gameRow); // puts table in trtd[][] and gameRow[] give rows where games are
					totalGameCounter = addGames(trtd, gameRow, year, seasons, g, gamesInSeason, totalRecordsForEachSport, totalRecordsForEachSport.size() - 1, totalGameCounter); // adds to g, individual season, and total record
				}
				totalRecordsForEachSport.get(totalRecordsForEachSport.size() - 1).printSeasonToTable(writerSchool, sportName);
				createOpponentsTable(g, schoolName, sportNum);
				endTableAndClose(writerSpecificSeasons);
			}
			endTableAndClose(writerSchool);
		}
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

	private static int addGames(String[][] trtd, int[] gameRow, int year, SeasonTemplate[] seasons, ArrayList<Game> g, int gamesInSeason,
								List<SeasonTemplate> totalRecords, int schoolNum, int totalGameCounter) {
		for (int i = 0; i < gamesInSeason; i++) {
			if (trtd[gameRow[i]][3].matches(".*[WTL].*")) {
				if (!trtd[gameRow[i]][3].contains("PPD")) {
					g.add(gameInformation(trtd[gameRow[i]]));
					seasons[year].addGame(g.get(totalGameCounter));
					totalRecords.get(schoolNum).addGame(g.get(totalGameCounter)); // for total count
					totalGameCounter++;
				}
			}
		}
		return totalGameCounter;
	}

	private static void writeTableHeader(PrintWriter writer, String firstColumnTitle) {
		writer.println("<table>");
		writer.println("<tr>");
		SeasonTemplate.writeDataToHTMLTable(writer, new Object[]{firstColumnTitle, "Games", "W", "T", "L", "For", "Against", "Diff", "Pct", "F/GP", "A/GP", "+/-"});
		writer.println("</tr>");
	}

	private static int getTableData(Elements trs, String[][] trtd, int[] gameRow) {
		int gameRowCounter = 0;
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			trtd[i] = new String[tds.size()];
			for (int j = 0; j < tds.size(); j++) {
				trtd[i][j] = tds.get(j).text();
				if (!tds.get(j).text().isEmpty() && j == 0) {
					gameRow[gameRowCounter++] = i; // skip <th> columns and checks to make sure game not already in gameRow[]
				}
			}
		}
		return gameRowCounter;
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

	private static Game gameInformation(String[] actualGameRow) {
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
		if (result.contains("PPD")) {
			goalsFor = 0;
			goalsAgainst = 0;
		} else {
			int goals1 = Integer.parseInt(tokens3[1]);
			int goals2 = Integer.parseInt(tokens3[2]);
			if (result.contains("W")) {
				goalsFor = Math.max(goals1, goals2);
				goalsAgainst = Math.min(goals1, goals2);
			} else { // tie or loss
				goalsFor = Math.min(goals1, goals2);
				goalsAgainst = Math.max(goals1, goals2);
			}
		}
		return new Game(opponent, result, goalsFor, goalsAgainst);
	}

	private static void createOpponentsTable(ArrayList<Game> g, String schoolName, int sportNum) throws IOException {
		TreeMap<String, SeasonTemplate> opponents = new TreeMap<>();
		for (Game games : g) {
			if (!games.result.contains("PPD")) {
				String opponent = games.opponent;
				opponents.computeIfAbsent(opponent, k -> new SeasonTemplate(opponent));
				opponents.get(opponent).addGame(games);
			}
		}
		sortOpponentsByGPAndReallyWriteOpponentsTable(new ArrayList<>(opponents.values()), schoolName, sportNum);
	}

	private static void sortOpponentsByGPAndReallyWriteOpponentsTable(List<SeasonTemplate> opponents, String schoolName, int sportNum) throws IOException {
		opponents.sort((o1, o2) -> o2.GP - o1.GP); // TODO private
		PrintWriter writerSpecificOpponents = newPrintWriter("specificData/" + schoolName + " " + sportEnums.get(sportNum) + " " + "opponents.html", "Opponent");
		for (SeasonTemplate opponent : opponents) {
			opponent.printSeasonToTable(writerSpecificOpponents, opponent.schoolName);
		}
		endTableAndClose(writerSpecificOpponents);
	}
}