class Game {
	final String opponent;
	final String result;
	int goalsFor;
	int goalsAgainst;

	Game(String opponent_, String result_, int goalsFor_, int goalsAgainst_) {
		opponent = opponent_;
		result = result_;
		assert (goalsFor_ >= 0 && goalsAgainst_ >= 0);
		goalsFor = goalsFor_;
		goalsAgainst = goalsAgainst_;
	}
}