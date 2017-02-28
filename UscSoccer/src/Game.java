import java.io.PrintWriter;

class Game {
	final String opponent;
	final String result;   // could be char    (remember F and PPD)
	int goalsFor;
	int goalsAgainst;

	Game(String opponent_, String result_, int goalsFor_, int goalsAgainst_) {
		opponent = opponent_;
		result = result_;
		if (goalsFor_ > -1) {
			goalsFor = goalsFor_;
		} else {
			System.out.println("ERROR. NEGATIVE GOALS FOR");
		}
		if (goalsAgainst_ > -1) {
			goalsAgainst = goalsAgainst_;
		} else {
			System.out.println("ERROR. NEGATIVE GOALS AGAINST");
		}
	}

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
}
