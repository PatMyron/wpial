import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
	private static final int timeoutTime = 60000;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		Map<Integer, String> sportNumbers = new TreeMap<>();                     // enum 1=FOOTBALL etc.
		TreeMap<Integer, TreeMap<String, String>> teamids = new TreeMap<>();     // Keys: sport#,school   V: website code
		TreeSet<String> allSchoolNames = new TreeSet<>();                             // all WPIAL schools (142 of them)
		Date date = new Date();
		PrintWriter eWriter = new PrintWriter("errors/errors" + date.getMonth() + date.getDate() + date.getHours() + date.getMinutes() + ".txt");
		teamIdsFiller(teamids, allSchoolNames, eWriter);                              // fills schools set and teamids double map ( for new data)
		fillSportsNumber(sportNumbers);                        // fills enum map sport #
		Element table;
		PrintWriter schoolWriter = new PrintWriter("WPIAL schools.txt");
		for (String schoolName : allSchoolNames) {        // iterates through all schools
			schoolWriter.println(schoolName);
			for (Integer teamtypeid : sportNumbers.keySet()) {        // iterates through all sports
				for (int year = 3; year < 15; year++) { // go from '03-'04 to '14-'15
					if (year == 14 && teamtypeid != 1 && teamtypeid != 8 && teamtypeid != 9) // hasn't happened yet
						continue;
					if (tableExists(year, teamids, teamtypeid, schoolName, eWriter)) {
						table = getTable(year, teamids, teamtypeid, schoolName, eWriter);
						PrintWriter tWriter = new PrintWriter("tables/" + schoolName + sportNumbers.get(teamtypeid) + year + ".html");
						tWriter.println(table);
						tWriter.close();
					} else
						continue;
					/*
					int[] gameRow = new int[60];
					Elements trs = table.select("tr");
					String[][] trtd = new String[trs.size()][];
					int gamesInSeason = getTableData(trs,trtd,gameRow);   // puts table in trtd[][] and gameRow[] give rows where games are
					 */
				}        // END YEAR LOOP
			}            // end inner for loop
		}            // end outer for loop
		schoolWriter.close();
		eWriter.close();
	}            // end main

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

	private static void fillSportsNumber(Map<Integer, String> sportNumbers) {
		sportNumbers.put(1, "FOOTBALL");
		sportNumbers.put(2, "BASEBALL");
		sportNumbers.put(3, "BASKETBALL");
		sportNumbers.put(8, "SOCCER");
		sportNumbers.put(4, "WOMENS BASKETBALL");
		sportNumbers.put(5, "WOMENS SOFTBALL");
		sportNumbers.put(9, "WOMENS SOCCER");
	}

	private static void teamIdsFiller(TreeMap<Integer, TreeMap<String, String>> teamids, TreeSet<String> allSchoolNames, PrintWriter errorWriter) { // for new data
		// only call when getting new data
		double pctDone = 0;
		Document lookupDoc;
		for (int sportNum = 1; sportNum < 10; sportNum++) {
			if (sportNum == 6 || sportNum == 7) continue; //dont know why... but no sports for #6 or #7
			teamids.put(sportNum, new TreeMap<>());
			try {
				lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid=" + sportNum).timeout(timeoutTime).get();
			} catch (IOException e) {
				errorWriter.println("Missed entire sport for getting allSchoolNames+teamids. Sport #: " + sportNum);
				System.out.println("Missed entire sport for getting allSchoolNames+teamids. Sport #: " + sportNum);
				continue;
			}
			Element table = lookupDoc.select("table").first();
			Elements trs = table.select("tr");
			String[][] trtd = new String[trs.size()][];
			for (int r = 0; r < trs.size(); r++) {
				Elements tds = trs.get(r).select("td");
				trtd[r] = new String[tds.size()];
				for (int c = 0; c < tds.size(); c++) {
					trtd[r][c] = tds.get(c).html();
					String teamName = tds.get(c).text();
					if (teamName.length() < 3) continue; //skips blanks
					String delims = "[{}]+";
					String[] splitupTeamId = trtd[r][c].split(delims);
					String teamid = splitupTeamId[1];
					teamids.get(sportNum).put(teamName, teamid);
					allSchoolNames.add(teamName);
				}
			}
			for (int year = 3; year < 15; year++) {
				System.out.printf("%3.1f", pctDone += 1.1);
				System.out.println(" % teamidsfiller");
				try {
					if (year < 10) {
						lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_lookup.asp?teamtypeid=" + sportNum + "&py=200" + year).timeout(timeoutTime).get();
					} else if (year < 14)
						lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_lookup.asp?teamtypeid=" + sportNum + "&py=20" + year).timeout(timeoutTime).get();
					else
						lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid=" + sportNum + "&py=20" + year).timeout(timeoutTime).get();
				} catch (IOException e) {
					errorWriter.println("Missed teamids for Sport #: " + sportNum + " and year: " + year);
					System.out.println("Missed teamids for Sport #: " + sportNum + " and year: " + year);
					continue;
				}
				// do it for all of them
				table = lookupDoc.select("table").first();
				trs = table.select("tr");
				trtd = new String[trs.size()][];
				for (int r = 0; r < trs.size(); r++) {
					Elements tds = trs.get(r).select("td");
					trtd[r] = new String[tds.size()];
					for (int c = 0; c < tds.size(); c++) {
						trtd[r][c] = tds.get(c).html();
						String teamName = tds.get(c).text();
						if (teamName.length() < 3) continue; //skips blanks
						String delims = "[{}]+";
						String[] splitupTeamId = trtd[r][c].split(delims);
						String teamid = splitupTeamId[1];
						teamids.get(sportNum).put(teamName, teamid);
						allSchoolNames.add(teamName);
					}
				}
			}
		}
	}

	private static boolean tableExists(int year, TreeMap<Integer, TreeMap<String, String>> teamids, int teamtypeid, String schoolName, PrintWriter eWriter) {
		Document doc;
		String teamid = teamids.get(teamtypeid).get(schoolName);
		try {
			if (year < 10)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=200"
						+ year).timeout(timeoutTime).get();
			else if (year < 14)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=20"
						+ year).timeout(timeoutTime).get();
			else
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=20"
						+ year).timeout(timeoutTime).get();
		} catch (IOException e) {
			eWriter.println("error message: " + e.getMessage() + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
			System.out.println("error message: " + e.getMessage() + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
			return false;
		}
		Element table = doc.select("table").first();
		if (table == null) {
			eWriter.println("table is null. " + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
			System.out.println("table is null. " + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
			return false;
		}
		return true;
	}

	private static Element getTable(int year, TreeMap<Integer, TreeMap<String, String>> teamids, int teamtypeid, String schoolName, PrintWriter eWriter) { // for new data
		// gets Table from site.. only use when there is new data
		// // called this way
		//		table = getTable(year,teamids,teamtypeid,schoolName,eWriter);
		Document doc;
		String teamid = teamids.get(teamtypeid).get(schoolName);
		try {
			if (year < 10)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=200"
						+ year).timeout(timeoutTime).get();
			else if (year < 14)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=20"
						+ year).timeout(timeoutTime).get();
			else
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_record.asp?teamtypeid=" + teamtypeid + "&teamid={" + teamid + "}&py=20"
						+ year).timeout(timeoutTime).get();
		} catch (IOException e) {
			// eWriter.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			// System.out.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			return null;
		}
		return doc.select("table").first();
	}
}