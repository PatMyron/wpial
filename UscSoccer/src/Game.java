class Game {
	final String opponent;
	final String result;   // (remember F and PPD)
	int goalsFor;
	int goalsAgainst;

	Game(String opponent_, String result_, int goalsFor_, int goalsAgainst_) {
		opponent = opponent_;
		result = result_;
		if (goalsFor_ >= 0 && goalsAgainst_ >= 0) {
			goalsFor = goalsFor_;
			goalsAgainst = goalsAgainst_;
		} else {
			System.out.println("ERROR. NEGATIVE GOALS");
		}
	}
}