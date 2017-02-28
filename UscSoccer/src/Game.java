import java.io.PrintWriter;
import java.util.Date;

class Game {
	private Date date; //includes time as well
	public String opponent;
	private String site; // could be char
	public String result;   // could be char    (remember F and PPD)
	public int goalsFor;
	public int goalsAgainst;
	private boolean conferenceGame; // include playoffs?
	public int goalDifferential;

	public Game(Date date_, String opponent_, String site_, String result_, int goalsFor_, int goalsAgainst_, boolean conferenceGame_) {
		date = date_;
		opponent = opponent_;
		site = site_;
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
		conferenceGame = conferenceGame_;
		goalDifferential = goalsFor_ - goalsAgainst_;
	}

	public void print() {
		System.out.printf("%32s%40s%32s%32s%32s\n\n%30s%44s%66s\r\n", "date: " + date, "opponent: " + opponent, "result: " + result, "goalsFor: " + goalsFor, "goalsAgainst: " + goalsAgainst, "conferenceGame: " + conferenceGame, "site: " + site, "gdiff:" + goalDifferential);
	}

	public void print(PrintWriter writer) {
		writer.printf("%32s%40s%32s%32s%32s\n\n%30s%44s%66s\r\n", "date: " + date, "opponent: " + opponent, "result: " + result, "goalsFor: " + goalsFor, "goalsAgainst: " + goalsAgainst, "conferenceGame: " + conferenceGame, "site: " + site, "gdiff:" + goalDifferential);
	}
}
