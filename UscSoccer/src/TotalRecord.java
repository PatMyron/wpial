import java.io.PrintWriter;

class TotalRecord {
	private final String schoolName;
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

	private void writeData(PrintWriter writer, final Object[] arr) {
		for (Object o : arr) {
			writer.println("<td>");
			writer.println(o);
			writer.println("</td>");
		}
	}

	private void writeDouble(PrintWriter writer, final double[] arr) {
		for (double d : arr) {
			writer.println("<td>");
			writer.printf("%6.2f", d);
			writer.println("</td>");
		}
	}

	void printSeasonToTable(PrintWriter writer, String firstColumnData) {
		writer.println("<tr>");

		writeData(writer, new Object[]{firstColumnData, GP, w, t, l, GF, GA, goalDifferential});

		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");

		writeDouble(writer, new double[]{goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame});

		writer.println("</tr>");
	}
}