import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

public class Main {
	private static final int TIMEOUT_TIME = 0;
	private static final int END_OF_CURRENT_SEASON = 2017;
	private static final String OLD_BASE_URL = "http://old.post-gazette.com/highschoolsports/statsPrevYears/";
	private static final String NEW_BASE_URL = "http://old.post-gazette.com/highschoolsports/stats/";
	private static final Map<Integer, String> sportNumbers = new TreeMap<>(); // enum 1=FOOTBALL etc.
	private static final TreeSet<String> allSchoolNames = new TreeSet<>();  // all WPIAL schools
	private static PrintWriter errorWriter;
	private static PrintWriter schoolWriter;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		errorWriter = new PrintWriter(new File("errors/" + new Date().toLocaleString() + ".txt"));
		schoolWriter = new PrintWriter("WPIAL schools.txt");
		fillSportsNumber();
		TreeMap<Integer, TreeMap<String, String>> teamids = new TreeMap<>(); // Keys: sport#,school   V: website code
		teamIdsFiller(teamids); // fills schools set and teamids double map (for new data)
		for (String schoolName : allSchoolNames) {
			schoolWriter.println(schoolName);
		}
		schoolWriter.close();
		allSchoolNames.parallelStream().forEach(schoolName ->
				sportNumbers.keySet().parallelStream().forEach(teamtypeid ->
						IntStream.range(2003, END_OF_CURRENT_SEASON).parallel().forEach(year -> {
							String teamid = teamids.get(teamtypeid).get(schoolName);
							if (teamid == null) {
								return;
							}
							Element table = getTable(year, teamid, teamtypeid);
							if (table == null) {
								log(String.format("table is null." + " school: " + "%-32s" + " sportName: " + "%-20s" + " sport: " + teamtypeid + " year: " + year, schoolName, sportNumbers.get(teamtypeid)));
								return;
							}
							try {
								PrintWriter tableWriter = new PrintWriter(new File("tables/" + schoolName + sportNumbers.get(teamtypeid) + year + ".html"));
								tableWriter.println(table);
								tableWriter.close();
							} catch (FileNotFoundException e) {
								log("MISSED writing a table." + " year: " + year + " sport: " + teamtypeid + " school: " + schoolName + " sportName: " + sportNumbers.get(teamtypeid));
							}
						})));
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

	private static void teamIdsFiller(TreeMap<Integer, TreeMap<String, String>> teamids) {
		sportNumbers.keySet().parallelStream().forEach(teamtypeid -> {
			teamids.put(teamtypeid, new TreeMap<>());
			try {
				Document lookupDoc = Jsoup.connect(NEW_BASE_URL + "team_lookup.asp?teamtypeid=" + teamtypeid).timeout(TIMEOUT_TIME).get();
				actuallyFill(teamids, lookupDoc, teamtypeid);
			} catch (IOException e) {
				log("MISSED ENTIRE SPORT for getting allSchoolNames+teamids. Sport #: " + teamtypeid);
			}
			IntStream.range(2003, END_OF_CURRENT_SEASON).parallel().forEach(year -> {
				try {
					Document lookupDoc;
					if (year < END_OF_CURRENT_SEASON - 1)
						lookupDoc = Jsoup.connect(OLD_BASE_URL + "team_lookup.asp?teamtypeid=" + teamtypeid + "&py=" + year).timeout(TIMEOUT_TIME).get();
					else
						lookupDoc = Jsoup.connect(NEW_BASE_URL + "team_lookup.asp?teamtypeid=" + teamtypeid + "&py=" + year).timeout(TIMEOUT_TIME).get();
					actuallyFill(teamids, lookupDoc, teamtypeid);
				} catch (IOException e) {
					log("MISSED teamids for Sport #: " + teamtypeid + " and year: " + year);
				}
			});
		});
	}

	private static void actuallyFill(TreeMap<Integer, TreeMap<String, String>> teamids, Document lookupDoc, int sportNum) {
		Elements trs = lookupDoc.select("table").first().select("tr");
		String[][] trtd = new String[trs.size()][];
		for (int r = 0; r < trs.size(); r++) {
			Elements tds = trs.get(r).select("td");
			trtd[r] = new String[tds.size()];
			for (int c = 0; c < tds.size(); c++) {
				trtd[r][c] = tds.get(c).html();
				String teamName = tds.get(c).text();
				if (teamName.length() < 3) continue; // skips blanks
				if (teamName.contains("201") || teamName.contains(" Ohio") || teamName.contains(", La.")) continue;
				String teamid = trtd[r][c].split("[{}]+")[1];
				teamids.get(sportNum).put(teamName, teamid);
				allSchoolNames.add(teamName);
			}
		}
	}

	private static Element getTable(int year, String teamid, int sportNum) {
		try {
			Document doc;
			if (year < END_OF_CURRENT_SEASON - 1)
				doc = Jsoup.connect(OLD_BASE_URL + "team_record.asp?teamtypeid=" + sportNum + "&teamid=" + teamid + "&py=" + year).timeout(TIMEOUT_TIME).get();
			else
				doc = Jsoup.connect(NEW_BASE_URL + "team_record.asp?teamtypeid=" + sportNum + "&teamid=" + teamid + "&py=" + year).timeout(TIMEOUT_TIME).get();
			return doc.select("table").first();
		} catch (IOException e) {
			return null;
		}
	}

	private static void log(String s) {
		errorWriter.println(s);
		System.out.println(s);
	}
}