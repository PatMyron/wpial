import java.io.PrintWriter;

class SeasonTemplate {
	int GP;
	int w;
	int t;
	int l;
	int GF;
	int GA;
	int goalDifferential;
	double winPct;
	double goalsForPerGame;
	double goalsAgainstPerGame;
	double goalDiffPerGame;

	static void writeDoublesToHTMLTable(PrintWriter writer, final double[] arr) {
		for (double d : arr) {
			writer.println("<td>");
			writer.printf("%6.2f", d);
			writer.println("</td>");
		}
	}

	static void writeDataToHTMLTable(PrintWriter writer, final Object[] arr) {
		for (Object o : arr) {
			writer.println("<td>");
			writer.println(o);
			writer.println("</td>");
		}
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
		if (!g.result.contains("PPD")) {
			GP++;
			GF += g.goalsFor;
			GA += g.goalsAgainst;
		}
		if (g.result.contains("W"))
			w++;
		if (g.result.contains("T"))
			t++;
		if (g.result.contains("L"))
			l++;
	}
}
