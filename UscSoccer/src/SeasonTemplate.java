import java.io.PrintWriter;

class SeasonTemplate {
	int GP;
	String schoolName;
	private int w;
	private int t;
	private int l;
	private int GF;
	private int GA;
	private int goalDifferential;
	private double winPct;
	private double goalsForPerGame;
	private double goalsAgainstPerGame;
	private double goalDiffPerGame;
	private int year;

	private SeasonTemplate() {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
	}

	SeasonTemplate(String schoolName_) {
		this();
		schoolName = schoolName_;
	}

	SeasonTemplate(int year_) {
		this();
		year = year_;
	}

	static void writeDataToHTMLTable(PrintWriter writer, final Object[] arr) {
		for (Object o : arr) {
			writer.println("<td>");
			writer.println(o);
			writer.println("</td>");
		}
	}

	private void writeDoublesToHTMLTable(PrintWriter writer, final double[] arr) {
		for (double d : arr) {
			writer.println("<td>");
			writer.printf("%6.2f", d);
			writer.println("</td>");
		}
	}

	void printSeasonToTable(PrintWriter writer, String firstColumnData) {
		endOfSeason();
		if (GP == 0) return;
		writer.println("<tr>");
		writeDataToHTMLTable(writer, new Object[]{firstColumnData, GP, w, t, l, GF, GA, goalDifferential});
		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");
		writeDoublesToHTMLTable(writer, new double[]{goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame});
		writer.println("</tr>");
	}

	private void endOfSeason() {
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