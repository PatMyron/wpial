import com.google.common.collect.ImmutableMap;
import lombok.Cleanup;
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
	private static final Map<Integer, String> sportNumbers = new TreeMap<>(ImmutableMap.<Integer, String>builder()
			.put(1, "FOOTBALL")
			.put(2, "BASEBALL")
			.put(3, "BASKETBALL")
			.put(8, "SOCCER")
			.put(4, "WOMENS BASKETBALL")
			.put(5, "WOMENS SOFTBALL")
			.put(9, "WOMENS SOCCER")
			.build());
	private static final TreeSet<String> allSchoolNames = new TreeSet<>();  // all WPIAL schools
	private static PrintWriter errorWriter;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		errorWriter = new PrintWriter(new File("errors/" + new Date().toLocaleString() + ".txt"));
		@Cleanup PrintWriter schoolWriter = new PrintWriter("WPIAL schools.txt");
		TreeMap<String, String> teamids = new TreeMap<>(); // Keys: sport#,school,year    V: teamid
		fillTeamIds(teamids); // fills schools set and teamids map
		for (String schoolName : allSchoolNames) {
			schoolWriter.println(schoolName);
		}
		allSchoolNames.parallelStream().forEach(schoolName ->
				sportNumbers.keySet().parallelStream().forEach(sportNum ->
						IntStream.range(2003, END_OF_CURRENT_SEASON).parallel().forEach(year -> {
							String teamid = teamids.get(sportNum + schoolName + year);
							if (teamid == null) {
								return;
							}
							Element table = getTable(year, teamid, sportNum);
							if (table == null) {
								log(String.format("table is null." + " school: " + "%-32s" + " sportName: " + "%-20s" + " sport: " + sportNum + " year: " + year, schoolName, sportNumbers.get(sportNum)));
								return;
							}
							try {
								@Cleanup PrintWriter tableWriter = new PrintWriter(new File("tables/" + schoolName + sportNumbers.get(sportNum) + year + ".html"));
								tableWriter.println(table);
							} catch (FileNotFoundException e) {
								log(String.format("MISSED writing table." + " school: " + "%-32s" + " sportName: " + "%-20s" + " sport: " + sportNum + " year: " + year, schoolName, sportNumbers.get(sportNum)));
							}
						})));
		errorWriter.close();
	}

	private static void fillTeamIds(TreeMap<String, String> teamids) {
		sportNumbers.keySet().forEach(sportNum ->
				IntStream.range(2003, END_OF_CURRENT_SEASON).forEach(year -> {
					try {
						Document doc;
						if (year < END_OF_CURRENT_SEASON - 1)
							doc = Jsoup.connect(OLD_BASE_URL + "team_lookup.asp?teamtypeid=" + sportNum + "&py=" + year).timeout(TIMEOUT_TIME).get();
						else
							doc = Jsoup.connect(NEW_BASE_URL + "team_lookup.asp?teamtypeid=" + sportNum + "&py=" + year).timeout(TIMEOUT_TIME).get();
						actuallyFillTeamIds(teamids, doc, sportNum, year);
					} catch (IOException e) {
						log("MISSED teamids for Sport #: " + sportNum + " and year: " + year);
					}
				}));
	}

	private static void actuallyFillTeamIds(TreeMap<String, String> teamids, Document doc, int sportNum, int year) {
		Elements trs = doc.select("table").first().select("tr");
		String[][] trtd = new String[trs.size()][];
		for (int r = 0; r < trs.size(); r++) {
			Elements tds = trs.get(r).select("td");
			trtd[r] = new String[tds.size()];
			for (int c = 0; c < tds.size(); c++) {
				trtd[r][c] = tds.get(c).html();
				String schoolName = tds.get(c).text();
				if (schoolName.length() < 3 || schoolName.contains("201") || schoolName.contains(" Ohio") || schoolName.contains(", La.")) continue;
				if (schoolName.contains("Seton-La")) schoolName = "Seton-La Salle";
				if (schoolName.contains("Quigley")) schoolName = "Quigley Catholic";
				if (schoolName.contains("Geibel")) schoolName = "Geibel Catholic";
				String teamid = trtd[r][c].split("[{}]+")[1];
				teamids.put(sportNum + schoolName + year, teamid);
				allSchoolNames.add(schoolName);
			}
		}
	}

	private static Element getTable(int year, String teamid, int sportNum) {
		try {
			Document doc;
			if (year < END_OF_CURRENT_SEASON - 1)
				doc = Jsoup.connect(OLD_BASE_URL + "team_record.asp?teamtypeid=" + sportNum + "&teamid={" + teamid + "}&py=" + year).timeout(TIMEOUT_TIME).get();
			else
				doc = Jsoup.connect(NEW_BASE_URL + "team_record.asp?teamtypeid=" + sportNum + "&teamid={" + teamid + "}&py=" + year).timeout(TIMEOUT_TIME).get();
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