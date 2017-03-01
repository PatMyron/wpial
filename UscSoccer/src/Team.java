class Team extends SeasonTemplate {
	private final String schoolName;

	Team(String opponent) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		schoolName = opponent;
	}
}