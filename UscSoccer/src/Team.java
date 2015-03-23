import java.io.PrintWriter;
import java.util.Date;


public class Team {
	public int GP;
	public int w;
	public int t;
	public int l;
	public int PPD; //postponed games (not doing yet)
	public int GF;
	public int GA;
	public int goalDifferential;
	public String name;
	public double pct;
	public double goalsForPerGame;
	public double goalsAgainstPerGame;
	public double goalDiffPerGame;

	public Team( ) {
		GP=0;
		w=0;
		t=0;
		l=0;
		PPD=0;
		GF=0;
		GA=0;
		name="";
		goalDifferential=0;
	}
	public void addGame(Game g){
		if(!g.result.contains("PPD")){
			GP++;
			GF+=(g.goalsFor);
			GA+=(g.goalsAgainst);
		}
		if(g.result.contains("W"))
			w++;
		if(g.result.contains("T"))
			t++;
		if(g.result.contains("L"))
			l++;
	}
	public void endCalcs(){
		pct = (w+0.5*t)/(GP)*100;
		goalDifferential = GF-GA;
		goalsForPerGame = (double)GF/GP;
		goalsAgainstPerGame = (double)GA/GP;
		goalDiffPerGame = (double)goalDifferential/GP;
	}
	public void printTeamToTable(PrintWriter writer){
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
		writer.printf("%5.1f %%",pct);
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
		writer.printf("%5.2f",goalsForPerGame);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%5.2f",goalsAgainstPerGame);
		writer.println("</td>");

		writer.println("<td>");
		writer.printf("%6.2f",goalDiffPerGame);
		writer.println("</td>");


		writer.println("</tr>");
	}
	public void print() {
		System.out.printf("%-38s GP: %2d      W: %2d  T: %2d  L: %2d     win pct.: %3.0f%%   GF: %4d  GA: %4d   +/-: %4d\n",name,GP,w,t,l,pct,GF,GA,goalDifferential);
	}
	public void print(PrintWriter writer) {
		writer.printf("%-38s GP: %2d      W: %2d  T: %2d  L: %2d     win pct.: %3.0f%%   GF: %4d  GA: %4d   +/-: %4d\r\n",name,GP,w,t,l,pct,GF,GA,goalDifferential);
	}
}
