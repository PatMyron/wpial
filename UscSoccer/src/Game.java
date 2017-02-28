class Game {
	final String opponent;
	final String result;   // (remember F and PPD)
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
}