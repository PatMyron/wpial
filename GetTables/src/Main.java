import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
	private static final int timeoutTime = 70000;
	private static final Map<Integer, String> sportNumbers = new TreeMap<>(); // enum 1=FOOTBALL etc.

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		fillSportsNumber();
		TreeMap<Integer, TreeMap<String, String>> teamids = new TreeMap<>();     // Keys: sport#,school   V: website code
		TreeSet<String> allSchoolNames = new TreeSet<>();  // all WPIAL schools (142 of them)
		PrintWriter errorWriter = new PrintWriter("errors/errors" + new Date().toInstant() + ".txt");
		teamIdsFiller(teamids, allSchoolNames, errorWriter); // fills schools set and teamids double map (for new data)
		PrintWriter schoolWriter = new PrintWriter("WPIAL schools.txt");
		for (String schoolName : allSchoolNames) {        // iterates through all schools
			schoolWriter.println(schoolName);
			for (Integer teamtypeid : sportNumbers.keySet()) {        // iterates through all sports
				for (int year = 3; year < 15; year++) { // go from '03-'04 to '14-'15
					if (tableExists(year, teamids, teamtypeid, schoolName, errorWriter)) {
						Element table = getTable(year, teamids, teamtypeid, schoolName, errorWriter);
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
				}
			}
		}
		schoolWriter.close();
		errorWriter.close();
	}

	private static void fillSportsNumber() {
		sportNumbers.put(1, "FOOTBALL");
		sportNumbers.put(2, "BASEBALL");
		sportNumbers.put(3, "BASKETBALL");
		sportNumbers.put(8, "SOCCER");
		sportNumbers.put(4, "WOMENS BASKETBALL");
		sportNumbers.put(5, "WOMENS SOFTBALL");
		sportNumbers.put(9, "WOMENS SOCCER");
	}

	private static void teamIdsFiller(TreeMap<Integer, TreeMap<String, String>> teamids, TreeSet<String> allSchoolNames, PrintWriter errorWriter) {
		// only call when getting new data
		Document lookupDoc;
		for (int sportNum : sportNumbers.keySet()) {
			teamids.put(sportNum, new TreeMap<>());
			try {
				lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid=" + sportNum).timeout(timeoutTime).get();
			} catch (IOException e) {
				errorWriter.println("MISSED ENTIRE SPORT for getting allSchoolNames+teamids. Sport #: " + sportNum);
				System.out.println("MISSED ENTIRE SPORT for getting allSchoolNames+teamids. Sport #: " + sportNum);
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
					String[] splitUpTeamId = trtd[r][c].split(delims);
					String teamid = splitUpTeamId[1];
					teamids.get(sportNum).put(teamName, teamid);
					allSchoolNames.add(teamName);
				}
			}
			for (int year = 3; year < 15; year++) {
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
						String[] splitUpTeamId = trtd[r][c].split(delims);
						String teamid = splitUpTeamId[1];
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

	private static Element getTable(int year, TreeMap<Integer, TreeMap<String, String>> teamids, int teamtypeid, String schoolName, PrintWriter eWriter) {
		// gets Table from site.. only use when there is new data
		//		table = getTable(year,teamids,teamtypeid,schoolName,eWriter);
		Document doc;
		String teamid = teamids.get(teamtypeid).get(schoolName);
		try {
			if (year < 10)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid="
						+ teamtypeid + "&teamid={" + teamid + "}&py=200" + year).timeout(timeoutTime).get();
			else if (year < 14)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid="
						+ teamtypeid + "&teamid={" + teamid + "}&py=20" + year).timeout(timeoutTime).get();
			else
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_record.asp?teamtypeid="
						+ teamtypeid + "&teamid={" + teamid + "}&py=20" + year).timeout(timeoutTime).get();
		} catch (IOException e) {
			// eWriter.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			// System.out.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			return null;
		}
		return doc.select("table").first();
	}
}