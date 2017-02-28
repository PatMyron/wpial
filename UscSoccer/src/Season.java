import java.io.PrintWriter;

class Season {
	private final int year;
	private int GP;
	private int w;
	private int t;
	private int l;
	private int GF;
	private int GA;
	private double winPct;
	private int goalDifferential;
	private double goalsForPerGame;
	private double goalsAgainstPerGame;
	private double goalDiffPerGame;

	Season(int year_) {
		year = year_;
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
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

	public void printSeasonToTable(PrintWriter writer) {
		writer.println("<tr>");

		if (year != -1)
			Game.writeDataToHTMLTable(writer, new Object[]{year, GP, w, t, l, GF, GA, goalDifferential});
		else
			Game.writeDataToHTMLTable(writer, new Object[]{"TOTAL", GP, w, t, l, GF, GA, goalDifferential});

		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");

		Game.writeDoublesToHTMLTable(writer, new double[]{goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame});

		writer.println("</tr>");
	}
}
