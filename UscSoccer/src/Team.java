import java.io.PrintWriter;

class Team {
	int GP;
	String name;
	private int w;
	private int t;
	private int l;
	private int GF;
	private int GA;
	private int goalDifferential;
	private double pct;
	private double goalsForPerGame;
	private double goalsAgainstPerGame;
	private double goalDiffPerGame;

	Team() {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		name = "";
		goalDifferential = 0;
	}

	void addGame(Game g) {
		if (!g.result.contains("PPD")) {
			GP++;
			GF += (g.goalsFor);
			GA += (g.goalsAgainst);
		}
		if (g.result.contains("W"))
			w++;
		if (g.result.contains("T"))
			t++;
		if (g.result.contains("L"))
			l++;
	}

	void endCalcs() {
		pct = (w + 0.5 * t) / (GP) * 100;
		goalDifferential = GF - GA;
		goalsForPerGame = (double) GF / GP;
		goalsAgainstPerGame = (double) GA / GP;
		goalDiffPerGame = (double) goalDifferential / GP;
	}

	public void printTeamToTable(PrintWriter writer) {
		writer.println("<tr>");

		writer.print("<td>");
		writer.print(name);
		writer.print("</td>");

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
		writer.printf("%5.1f %%", pct);
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
}
