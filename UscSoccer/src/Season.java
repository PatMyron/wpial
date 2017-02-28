import java.io.PrintWriter;

class Season {
	private int GP;
	private int w;
	private int t;
	private int l;
	private int GF;
	private int GA;
	private int year;
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

	public void printSeasonToTable(PrintWriter writer) {
		writer.println("<tr>");

		writer.println("<td>");
		if (year != -1)
			writer.println(year);
		else
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

	public void print() {
		if (year == -1) // not actually year, just total count
			System.out.printf("TOTAL:       GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\n", GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
		else
			System.out.printf("year: %4d   GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\n", year, GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
	}

	public void print(PrintWriter writer, String sport) {
		if (year == -1) // not actually year, just total count
			writer.printf("%-18s  GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\n", sport + ":", GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
		else
			writer.printf("year: %4d   GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\n", year, GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
	}

	public void print(PrintWriter writer) {
		if (year == -1) // not actually year, just total count
			writer.printf("%-11s  GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\r\n", "total:", GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
		else
			writer.printf("year: %4d   GP: %3d  W: %3d T: %2d L: %3d  F: %5d A: %5d Dif: %5d  Pct: %5.1f %%  GF/GP: %5.2f GA/GP: %5.2f +/-: %6.2f\r\n", year, GP, w, t, l, GF, GA, goalDifferential, winPct, goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame);
	}
}
