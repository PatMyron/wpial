import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
	private static final int timeoutTime = 70000;
	private static final int END_OF_CURRENT_SEASON = 2017;
	private static final Map<Integer, String> sportNumbers = new TreeMap<>(); // enum 1=FOOTBALL etc.
	private static final File errorFile = new File("errors/" + new Date().toInstant() + ".txt");
	private static PrintWriter errorWriter;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		fillSportsNumber();
		errorWriter = new PrintWriter(errorFile);
		TreeMap<Integer, TreeMap<String, String>> teamids = new TreeMap<>(); // Keys: sport#,school   V: website code
		TreeSet<String> allSchoolNames = new TreeSet<>();  // all WPIAL schools (142 of them)
		teamIdsFiller(teamids, allSchoolNames); // fills schools set and teamids double map (for new data)
		PrintWriter schoolWriter = new PrintWriter("WPIAL schools.txt");
		for (String schoolName : allSchoolNames) {
			schoolWriter.println(schoolName);
			for (Integer teamtypeid : sportNumbers.keySet()) {
				for (int year = 2003; year < END_OF_CURRENT_SEASON; year++) { // from '03-'04
					Element table = getTable(year, teamids.get(teamtypeid).get(schoolName), teamtypeid, schoolName);
					if (table == null) {
						log("table is null. " + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
						continue;
					}
					File tableFile = new File("tables/" + schoolName + sportNumbers.get(teamtypeid) + year + ".html");
					PrintWriter tWriter = new PrintWriter(tableFile);
					tWriter.println(table);
					tWriter.close();
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

	private static void teamIdsFiller(TreeMap<Integer, TreeMap<String, String>> teamids, TreeSet<String> allSchoolNames) {
		Document lookupDoc;
		for (int sportNum : sportNumbers.keySet()) {
			teamids.put(sportNum, new TreeMap<>());
			try {
				lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid=" + sportNum).timeout(timeoutTime).get();
			} catch (IOException e) {
				log("MISSED ENTIRE SPORT for getting allSchoolNames+teamids. Sport #: " + sportNum);
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
					if (teamName.length() < 3) continue; // skips blanks
					String[] splitUpTeamId = trtd[r][c].split("[{}]+");
					String teamid = splitUpTeamId[1];
					teamids.get(sportNum).put(teamName, teamid);
					allSchoolNames.add(teamName);
				}
			}
			for (int year = 2003; year < END_OF_CURRENT_SEASON; year++) {
				try {
					if (year < END_OF_CURRENT_SEASON-1)
						lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_lookup.asp?teamtypeid="
								+ sportNum + "&py=" + year).timeout(timeoutTime).get();
					else
						lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid="
								+ sportNum + "&py=" + year).timeout(timeoutTime).get();
				} catch (IOException e) {
					log("MISSED teamids for Sport #: " + sportNum + " and year: " + year);
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
						if (teamName.length() < 3) continue; // skips blanks
						String[] splitUpTeamId = trtd[r][c].split("[{}]+");
						String teamid = splitUpTeamId[1];
						teamids.get(sportNum).put(teamName, teamid);
						allSchoolNames.add(teamName);
					}
				}
			}
		}
	}

	private static Element getTable(int year, String teamid, int teamtypeid, String schoolName) {
		Document doc;
		try {
			if (year < END_OF_CURRENT_SEASON-1)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid="
						+ teamtypeid + "&teamid={" + teamid + "}&py=" + year).timeout(timeoutTime).get();
			else
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_record.asp?teamtypeid="
						+ teamtypeid + "&teamid={" + teamid + "}&py=" + year).timeout(timeoutTime).get();
		} catch (IOException e) {
			log("error message: " + e.getMessage() + " school: " + schoolName + " year: " + year + " sport: " + teamtypeid);
			return null;
		}
		return doc.select("table").first();
	}

	private static void log(String s) {
		errorWriter.println(s);
		System.out.println(s);
	}
}