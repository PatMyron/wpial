import java.io.PrintWriter;

class TotalRecord {
	int GP;
	double winPct;
	private int w;
	private int t;
	private int l;
	private int GF;
	private int GA;
	private int goalDifferential;
	private double goalsForPerGame;
	private double goalsAgainstPerGame;
	private double goalDiffPerGame;
	private final String schoolName;

	TotalRecord(String name) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		schoolName = name;
	}

	void endOfSeason() {
		GP = w + t + l;
		winPct = (w + 0.5 * t) / (GP) * 100;
		goalDifferential = GF - GA;
		goalsForPerGame = (double) GF / GP;
		goalsAgainstPerGame = (double) GA / GP;
		goalDiffPerGame = (double) goalDifferential / GP;
	}

	void addGame(Game g) {
		GP++;
		if (g.result.contains("W"))
			w++;
		if (g.result.contains("T"))
			t++;
		if (g.result.contains("L"))
			l++;
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

	void printSeasonToTable(PrintWriter writer, String firstColumnData) {
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