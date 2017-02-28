import java.io.PrintWriter;

class Season extends SeasonTemplate {
	private final int year;

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

	public void printSeasonToTable(PrintWriter writer) {
		writer.println("<tr>");

		if (year != -1)
			writeDataToHTMLTable(writer, new Object[]{year, GP, w, t, l, GF, GA, goalDifferential});
		else
			writeDataToHTMLTable(writer, new Object[]{"TOTAL", GP, w, t, l, GF, GA, goalDifferential});

		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");

		writeDoublesToHTMLTable(writer, new double[]{goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame});

		writer.println("</tr>");
	}
}
