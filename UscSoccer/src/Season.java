class Season extends SeasonTemplate {
	private final int year;

	Season(int year_) {
		GP = 0;
		w = 0;
		t = 0;
		l = 0;
		GF = 0;
		GA = 0;
		goalDifferential = 0;
		year = year_;
	}
}