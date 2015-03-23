import java.io.PrintWriter;
import java.util.Date;


public class Game {
	public Date date; //includes time as well
	public int year;
	public String opponent;
	public String site; // could be char
	public String result;   // could be char    (remember F and PPD)
	public int goalsFor;
	public int goalsAgainst;
	public String matchType; //include later
	public boolean conferenceGame; // include playoffs?
	public int goalDifferential;
	public boolean playoffGame;

	public Game(){
		goalsFor = 0;
		goalsAgainst = 0;
		goalDifferential = 0;
	}

	public Game(Date date_, String opponent_, String site_, String result_, int goalsFor_, int goalsAgainst_, boolean conferenceGame_) {
		date=date_;
		year=date.getYear();
		opponent=opponent_;
		site=site_;
		result=result_;
		goalsFor=goalsFor_;
		goalsAgainst=goalsAgainst_;
		conferenceGame=conferenceGame_;   
		goalDifferential = goalsFor_-goalsAgainst_;
	}


	public void print() {
		System.out.printf("%32s%40s%32s%32s%32s\n\n%30s%44s%66s\r\n","date: "+date,"opponent: "+opponent,"result: "+result,"goalsFor: "+goalsFor,"goalsAgainst: "+goalsAgainst,"conferenceGame: "+conferenceGame,"site: "+site,"gdiff:"+goalDifferential);
	}
	public void print(PrintWriter writer) {
		writer.printf("%32s%40s%32s%32s%32s\n\n%30s%44s%66s\r\n","date: "+date,"opponent: "+opponent,"result: "+result,"goalsFor: "+goalsFor,"goalsAgainst: "+goalsAgainst,"conferenceGame: "+conferenceGame,"site: "+site,"gdiff:"+goalDifferential);
	}
}