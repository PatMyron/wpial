import java.io.PrintWriter;

public class TotalRecord {
	public int GP;
	public int w;
	public int t;
	public int l;
	public int PPD; //postponed games
	public int GF;
	public int GA;
	public double winPct;
	public double regPct;
	public double playoffPct;
	public String outcome;
	public boolean madePlayoffs;
	public boolean madeStates;
	public int goalDifferential;
	public double goalsForPerGame;
	public double goalsAgainstPerGame;
	public double goalDiffPerGame;
	public String schoolName;
	public int teamtypeid;

	public TotalRecord() {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		PPD = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
	}

	public TotalRecord(String name) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		PPD = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		schoolName = name;
	}

	public TotalRecord(String name, Integer typeid) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		PPD = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		schoolName = name;
		teamtypeid = typeid;
	}

	public void endOfSeason() {
		GP = w + t + l;
		winPct = (w + 0.5 * t) / (GP) * 100;
		goalDifferential = GF - GA;
		goalsForPerGame = (double) GF / GP;
		goalsAgainstPerGame = (double) GA / GP;
		goalDiffPerGame = (double) goalDifferential / GP;
	}

	public void addGame(Game g) {
		GP++;
		if (g.result.contains("W"))
			w++;
		if (g.result.contains("T"))
			t++;
		if (g.result.contains("L"))
			l++;
		if (g.result.contains("PPD"))
			PPD++;
		GF += g.goalsFor;
		GA += g.goalsAgainst;
	}

	public void printSeasonToTable(PrintWriter writer, boolean printSchoolName) {
		if (printSchoolName) {
			writer.println("<tr>");

			writer.println("<td>");
			writer.println(schoolName);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GP);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(w);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(t);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(l);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.1f %%", winPct);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GF);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GA);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(goalDifferential);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.2f", goalsForPerGame);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.2f", goalsAgainstPerGame);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%6.2f", goalDiffPerGame);
			writer.println("</td>");

			writer.println("</tr>");
		} else {
			writer.println("<tr>");

			writer.println("<td>");
			writer.println("TOTAL");
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GP);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(w);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(t);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(l);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GF);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(GA);
			writer.println("</td>");

			writer.println("<td>");
			writer.println(goalDifferential);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.1f %%", winPct);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.2f", goalsForPerGame);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%5.2f", goalsAgainstPerGame);
			writer.println("</td>");

			writer.println("<td>");
			writer.printf("%6.2f", goalDiffPerGame);
			writer.println("</td>");

			writer.println("</tr>");
		}
	}

	public void printSeasonToTable(PrintWriter writer, String firstColumnData) {
		writer.println("<tr>");

		writer.println("<td>");
		writer.println(firstColumnData);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(GP);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(w);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(t);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(l);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(GF);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(GA);
		writer.println("</td>");

		writer.println("<td>");
		writer.println(goalDifferential);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%5.2f", goalsForPerGame);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%5.2f", goalsAgainstPerGame);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%6.2f", goalDiffPerGame);
		writer.println("</td>");

		writer.println("</tr>");
	}

	public int compareTo(TotalRecord o) {
		int returnVal = 0;
		if (winPct < o.winPct) {
			returnVal = -1;
		} else if (winPct > o.winPct) {
			returnVal = 1;
		} else if (winPct == o.winPct) {
			returnVal = 0;
		}
		return returnVal;
	}
}