import java.io.PrintWriter;

class TotalRecord extends SeasonTemplate {
	private final String schoolName;

	TotalRecord(String schoolName_) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		schoolName = schoolName_;
	}

	void printSeasonToTable(PrintWriter writer, String firstColumnData) {
		writer.println("<tr>");

		writeDataToHTMLTable(writer, new Object[]{firstColumnData, GP, w, t, l, GF, GA, goalDifferential});

		writer.println("<td>");
		writer.printf("%5.1f %%", winPct);
		writer.println("</td>");

		writeDoublesToHTMLTable(writer, new double[]{goalsForPerGame, goalsAgainstPerGame, goalDiffPerGame});

		writer.println("</tr>");
	}
}