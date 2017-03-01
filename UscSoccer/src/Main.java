import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class Main {
	private static final Map<Integer, String> sportEnums = new TreeMap<>(); // enum 1=FOOTBALL etc.
	private static final TreeSet<String> allSchoolNames = new TreeSet<>(); // all WPIAL schools (142 of them)

	public static void main(String[] args) throws IOException {
		fillSportEnumsAndSchoolNames();
		Element table;
		for (String schoolName : allSchoolNames) {
			PrintWriter writerSchool = new PrintWriter("dataBySchool/" + schoolName + ".html", "UTF-8");
			writeTableHeader(writerSchool, "Sport");
			List<SeasonTemplate> totalRecords = new ArrayList<>();
			for (Integer teamtypeid : sportEnums.keySet()) { // iterates through all sports
				if (schoolName.contains("Apollo") && teamtypeid == 9) // idk whats up with this team
					continue;
				// PrintWriter writerSort = new PrintWriter("specificData/"+schoolName+"+"+sportEnums.get(teamtypeid)+"/opponentsGP.html", "UTF-8");
				// writerSpecificSeasons.println(schoolName+" "+sportEnums.get(teamtypeid) + "\n");
				ArrayList<Game> g = new ArrayList<>(); // all games a team has played
				SeasonTemplate seasons[] = new SeasonTemplate[15];
				totalRecords.add(new SeasonTemplate(schoolName));
				int totalGameCounter = 0;
				for (int year = 3; year < 15; year++) { // go from '03-'04 to '14-'15
					if (year == 14 && teamtypeid != 1 && teamtypeid != 8 && teamtypeid != 9) // hasn't happened yet
						continue;
					seasons[year] = new SeasonTemplate(year + 2000);
					File f = new File("tables/" + schoolName + sportEnums.get(teamtypeid) + year + ".html");
					if (f.exists() && !f.isDirectory()) {
						Document doc = Jsoup.parse(f, "UTF-8");
						table = doc.select("table").first();
					} else continue;
					if (table == null) continue;
					int[] gameRow = new int[60];
					Elements trs = table.select("tr");
					String[][] trtd = new String[trs.size()][];
					int gamesInSeason = getTableData(trs, trtd, gameRow); // puts table in trtd[][] and gameRow[] give rows where games are
					totalGameCounter = addGames(trtd, gameRow, year, seasons, g, gamesInSeason, totalRecords, totalRecords.size() - 1, totalGameCounter); // adds to g, individual season, and total record
					seasons[year].endOfSeason();
				} // END YEAR LOOP
				if (totalRecords.get(totalRecords.size() - 1).GP == 0) { // TODO private
					totalRecords.remove(totalRecords.size() - 1);
				} else {
					totalRecords.get(totalRecords.size() - 1).endOfSeason();
					totalRecords.get(totalRecords.size() - 1).printSeasonToTable(writerSchool, sportEnums.get(teamtypeid));
					TreeSet<String> opponents = new TreeSet<>();
					for (Game games : g) {
						if (!games.result.contains("PPD"))
							opponents.add(games.opponent); // initializing with all opponents
					}
					TreeMap<String, SeasonTemplate> teamMap = new TreeMap<>();
					List<SeasonTemplate> opposingTeams = new ArrayList<>(opponents.size());
					for (String opponent : opponents) {
						SeasonTemplate t = new SeasonTemplate(opponent);
						opposingTeams.add(t);
						teamMap.put(opponent, t);
					}
					setupOpponentAlphabetically(g, teamMap); // alphabetical into opposingTeams
					sortOpponentsByGP(opposingTeams); // sorts by games played into opposingTeams
				}
			} // end inner for loop
			writerSchool.println("</table>");
			writerSchool.close();
		}
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
		writer.print("<table style=\"width:100%\">\n  <tr>\n    <th>" + firstColumnTitle + "</th>\n    <th>Games</th>\n    <th>W</th>\n    <th>T</th>\n    <th>L</th>\n    <th>Pct</th>\n    <th>For</th>\n    <th>Against</th>\n    <th>Diff</th>\n    <th>F/GP</th>\n    <th>A/GP</th>\n    <th>+/-</th>\n  </tr>");
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

	private static void setupOpponentAlphabetically(ArrayList<Game> g, TreeMap<String, SeasonTemplate> teamMap) {
		for (Game games : g) {
			teamMap.get(games.opponent).addGame(games);
		}
		for (Map.Entry<String, SeasonTemplate> entry : teamMap.entrySet()) {
			entry.getValue().endOfSeason();
		}
	}

	private static void sortOpponentsByGP(List<SeasonTemplate> teams) {
		teams.sort((o1, o2) -> o2.GP - o1.GP); // TODO private
	}
}