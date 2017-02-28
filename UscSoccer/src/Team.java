class Team extends SeasonTemplate {
	private String name;

	Team(String opponent) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		name = "";
		goalDifferential = 0;
		name = opponent;
	}
}