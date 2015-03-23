import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
// possibly add longest winning and losing streak 
// change throws IOException to print error to error file
public class Main {
	public static void main(String[] args) throws IOException {
		Map<Integer,String>                       sportNumbers  = new TreeMap<Integer,String>();     				 // enum 1=FOOTBALL etc.
		// TreeMap<Integer,TreeMap<String,String>>        teamids  = new TreeMap<Integer,TreeMap<String,String>>();     // Keys: sport#,school   V: website code
		TreeSet<String>						    allSchoolNames  = new TreeSet<String>();          					 // all WPIAL schools (142 of them)
		TreeSet<Integer> 						  allSportNums  = new TreeSet<Integer>();
		//teamIdsFiller(teamids,allSchoolNames,eWriter);                              // fills schools set and teamids double map ( for new data)
		String line;
		BufferedReader reader = new BufferedReader(new FileReader("WPIAL schools.txt"));
		while((line = reader.readLine()) != null)
			allSchoolNames.add(line);
		reader.close();
		fillSportsNumber(sportNumbers,allSportNums);						// fills enum map sport # and set
		org.jsoup.nodes.Element table=null;
		double pctDone = 0;
		for(String schoolName: allSchoolNames){ 		// iterates through all schools
			PrintWriter writerSchool  = new PrintWriter("dataBySchool/"+schoolName+".html", "UTF-8");
			tableBeginning(writerSchool,"Sport");
			List<TotalRecord> totalRecords = new ArrayList<TotalRecord>();
			for ( Integer teamtypeid: allSportNums ) {		// iterates through all sports
				if(schoolName.contains("Apollo") && teamtypeid==9) // idk whats up with this team haha
					continue;
				System.out.printf("%3.1f",pctDone+=0.1);System.out.println(" %");
				//PrintWriter writerAlpha = new PrintWriter("specificData/"+schoolName+"+"+sportNumbers.get(teamtypeid)+"/opponentsABC.html", "UTF-8");
				//PrintWriter writerSort = new PrintWriter("specificData/"+schoolName+"+"+sportNumbers.get(teamtypeid)+"/opponentsGP.html", "UTF-8");
				//writerSpecificSeasons.println(schoolName+" "+sportNumbers.get(teamtypeid));writerSpecificSeasons.println();
				ArrayList<Game> g = new ArrayList<Game>();  // all games a team has played
				Season seasons[] = new Season[15];
				totalRecords.add( new TotalRecord(schoolName,teamtypeid) );
				int totalGameCounter = 0;
				for (int year = 3; year < 15; year++) { // go from '03-'04 to '14-'15
					if(year==14 && teamtypeid!=1 && teamtypeid!=8 && teamtypeid!=9) // hasn't happened yet
						continue; 
					seasons[year] = new Season(year+2000);
					File f = new File("tables/"+schoolName+sportNumbers.get(teamtypeid)+year+".html");
					if(f.exists() && !f.isDirectory()) {  
						org.jsoup.nodes.Document doc = Jsoup.parse(f,"UTF-8");
						table = doc.select("table").first();
					}
					else
						continue;
					if(table==null)
						continue;
					int[] gameRow = new int[60];
					Elements trs = table.select("tr");
					String[][] trtd = new String[trs.size()][];
					int gamesInSeason = getTableData(trs,trtd,gameRow);   // puts table in trtd[][] and gameRow[] give rows where games are
					totalGameCounter = addGames(trtd,gameRow,year,seasons,g,gamesInSeason,totalRecords,totalRecords.size()-1,totalGameCounter); // adds to g, individual season, and total record
					seasons[year].endOfSeason();
				} 		// END YEAR LOOP
				if (totalRecords.get(totalRecords.size()-1).GP==0){
					totalRecords.remove( totalRecords.size()-1 );
				}
				else {
					totalRecords.get(totalRecords.size()-1).endOfSeason();
					totalRecords.get(totalRecords.size()-1).printSeasonToTable(writerSchool,sportNumbers.get(teamtypeid));
					TreeSet<String> 		opponents	= new TreeSet<String>();
					for (Game games: g) {
						if (!games.result.contains("PPD"))
							opponents.add(games.opponent);  // initializing with all opponents
					}
					TreeMap<String, Team> 	teamMap	= new TreeMap<String, Team>();
					List<Team>				opposingTeams		= new ArrayList<Team>(opponents.size());
					for(int i=0;i<opponents.size();i++)
						opposingTeams.add(new Team());
					setupOpponentAlphabetically(g, g.size(), opponents, teamMap,opposingTeams);  // alphabetical into opposingTeams
					minMaxHighestScoringGames(g,g.size()); // can be used with/without writer
					sortOpponentsByGP(opposingTeams);  // sorts by games played into opposingTeams
				}
			} 			// end inner for loop
			/*
			sortTotalRecords(totalRecords);
			for(TotalRecord tr: totalRecords)
				tr.printSeasonToTable(writerSportSorted,Boolean.TRUE);
			*/
			writerSchool.println("</table>");
			writerSchool.close();
		} 			// end outer for loop
	} 			// end main
	private static int addGames(String[][] trtd, int[] gameRow, int year,Season[] seasons, ArrayList<Game> g, int gamesInSeason,
			List<TotalRecord> totalRecords, int schoolNum,int totalGameCounter) {
		for (int i = 0; i < gamesInSeason; i++) {
			if(trtd[gameRow[i]][3].contains("W") || trtd[gameRow[i]][3].contains("T") || trtd[gameRow[i]][3].contains("L") ){
				if (!trtd[gameRow[i]][3].contains("PPD")) {
					g.add( gameInformation(trtd,i,year,gameRow) ); // ( opponent, site, result, goalsFor, goalsAgainst, conferenceGame )
					seasons[year].addGame( g.get(totalGameCounter) );
					totalRecords.get(schoolNum).addGame( g.get(totalGameCounter) );   // for total count
					totalGameCounter++;
				}
			}
		}
		return totalGameCounter;
	}
	private static void tableBeginning(PrintWriter writer,String firstColumnTitle) {
		writer.print("<table style=\"width:100%\">\n  <tr>\n    <th>"+firstColumnTitle+"</th>\n    <th>Games</th>\n    <th>W</th>\n    <th>T</th>\n    <th>L</th>\n    <th>Pct</th>\n    <th>For</th>\n    <th>Against</th>\n    <th>Diff</th>\n    <th>F/GP</th>\n    <th>A/GP</th>\n    <th>+/-</th>\n  </tr>");
	}
	private static void minMaxHighestScoringGames(ArrayList<Game> g, int totalGameCounter, PrintWriter writer) {
		int min  = minGame(g,totalGameCounter);
		int max  = maxGame(g,totalGameCounter);
		int high = highestScoringGame(g,totalGameCounter);
		writer.println("BEST GAME:");
		/*
		g[max].print(writer);
		writer.println("WORST GAME:");
		g[min].print(writer);
		writer.println("BEST OFFENSIVE GAME:");
		g[high].print(writer);
		 */
	}
	private static void minMaxHighestScoringGames(ArrayList<Game> g,	int totalGameCounter) {
		int min  = minGame(g,totalGameCounter);
		int max  = maxGame(g,totalGameCounter);
		int high = highestScoringGame(g,totalGameCounter);
		// System.out.println("BEST GAME:");
		// g[max].print();
		// System.out.println("WORST GAME:");
		// g[min].print();
		// System.out.println("BEST OFFENSIVE GAME:");
		// g[high].print();
	}
	private static int minGame(ArrayList<Game> g, int totalGameCounter) {
		int minimum=0;
		for(Game game: g){
			if((game.goalDifferential)<g.get(minimum).goalDifferential)
				minimum=g.indexOf(game);
		}
		return minimum;
	}
	private static int highestScoringGame(ArrayList<Game> g, int totalGameCounter) {
		int max=0;	
		for(Game game: g){
			if((game.goalsFor)>g.get(max).goalsFor) 
				max=g.indexOf(game);
		}
		return max;
	}
	private static int maxGame(ArrayList<Game> g, int totalGameCounter) {
		int max=0;	
		for(Game game: g){
			if((game.goalDifferential)>g.get(max).goalDifferential) 
				max=g.indexOf(game);
		}
		return max;
	}
	private static int getTableData(Elements trs, String[][] trtd, int[] gameRow) {
		int gameRowCounter = 0;
		for (int i = 0; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			trtd[i] = new String[tds.size()];
			for (int j = 0; j < tds.size(); j++) {
				trtd[i][j] = tds.get(j).text();
				if (!tds.get(j).text().isEmpty() && j == 0) {
					gameRow[gameRowCounter++] = i; // skip <th> columns and checks to make sure game not already in gameRow[]
				}
			}
		}
		return gameRowCounter;
	}
	private static void fillSportsNumber(Map<Integer, String> sportNumbers, TreeSet<Integer> allSportNums) {
		sportNumbers.put(1, "FOOTBALL");
		sportNumbers.put(2, "BASEBALL");
		sportNumbers.put(3, "BASKETBALL");
		sportNumbers.put(8, "SOCCER");
		sportNumbers.put(4, "WOMENS BASKETBALL");
		sportNumbers.put(5, "WOMENS SOFTBALL");
		sportNumbers.put(9, "WOMENS SOCCER");
		allSportNums.add(1);
		allSportNums.add(2);
		allSportNums.add(3);
		allSportNums.add(4);
		allSportNums.add(5);
		allSportNums.add(8);
		allSportNums.add(9);
	}
	private static Game gameInformation(String[][] trtd, int i, int year,
			int[] gameRow) {
		Date d1 = DateTimeGetter(trtd,i,year,gameRow);
		////  OPPONENT ///////////////////////////////////////////////////////////////////////////
		String delims = "[*]+";
		String[] tokenOpponent = trtd[gameRow[i]][1].split(delims);
		String[] tokenOpponent2 = tokenOpponent[0].split("at ");
		String opponent = tokenOpponent2[tokenOpponent2.length - 1]; // OPPONENT

		if (opponent.contains("Seton-La"))
			opponent = "Seton-La Salle";

		// RESULT GOALS FOR/AGAINST      // /////////////////////////////////////
		String result;
		int goalsFor, goalsAgainst;
		delims = "[\\(]";
		String[] tokens3 = trtd[gameRow[i]][3].split(delims);
		delims = "[ -]+";
		tokens3 = tokens3[0].split(delims);
		result = tokens3[0];
		if (tokens3[0].contains("PPD")) {
			goalsFor = 0;
			goalsAgainst = 0;
		} 
		else {
			if(result.contains("W")){
				int goals1 = Integer.parseInt(tokens3[1]);
				int goals2 = Integer.parseInt(tokens3[2]);
				goalsFor = Math.max(goals1,goals2);
				goalsAgainst = Math.min(goals1, goals2);
			}
			else if (result.contains("L")){
				int goals1 = Integer.parseInt(tokens3[1]);
				int goals2 = Integer.parseInt(tokens3[2]);
				goalsFor = Math.min(goals1,goals2);
				goalsAgainst = Math.max(goals1, goals2);
			}
			else{
				goalsFor = Integer.parseInt(tokens3[1]);
				goalsAgainst = Integer.parseInt(tokens3[2]);
			}
		}

		// /////////////////////////////////////////////////////////////////////

		boolean conferenceGame = false; // CONFERENCE GAME
		if (trtd[gameRow[i]][1].contains("*"))
			conferenceGame = true;
		String site = trtd[gameRow[i]][2]; // SITE

		return new Game(d1, opponent, site, result, goalsFor, goalsAgainst, conferenceGame); // MISSING MATCHTYPE
	}
	private static Date DateTimeGetter(String[][] trtd, int i,int year,int[] gameRow) {
		String delims = "[ /]+";
		String[] tokens0 = trtd[gameRow[i]][0].split(delims);
		int hrs,month,day;
		int min = 0;
		if(trtd[gameRow[i]][0].equals("TBA")){
			month=0;
			day=0;
			hrs = 0;
			min = 0;
		}
		else{ 
			if (!tokens0[2].contains(":")){
				if(tokens0[2].contains("TBA"))
					hrs = 0;
				else
					hrs = Integer.parseInt(tokens0[2]);
			}
			else { // not on the hour
				delims = "[: ]+";
				String[] timeToken = tokens0[2].split(delims);
				hrs = Integer.parseInt(timeToken[0]);
				min = Integer.parseInt(timeToken[1]);
			}
			month = Integer.parseInt(tokens0[0]) - 1;
			day = Integer.parseInt(tokens0[1]);
		}
		return new Date(year + 100,month,day, hrs, min);
	}
	public static void teamIdsFiller(TreeMap<Integer,TreeMap<String,String>> teamids,TreeSet<String> allSchoolNames,PrintWriter errorWriter) { // for new data
		// only call when getting new data
		org.jsoup.nodes.Document lookupDoc=null;

		for(int sportNum=1;sportNum<10;sportNum++){
			if(sportNum==6||sportNum==7) continue; //dont know why... but no sports for #6 or #7
			teamids.put(sportNum, new TreeMap<String,String>());
			try {
				lookupDoc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_lookup.asp?teamtypeid="+sportNum).get();
			} catch (IOException e) {
				errorWriter.println("Missed entire sport for getting allSchoolNames. Sport #: "+sportNum);
				System.out.println("Missed entire sport for getting allSchoolNames. Sport #: "+sportNum);
				continue;
			}
			org.jsoup.nodes.Element table = lookupDoc.select("table").first();
			Elements trs = table.select("tr");
			String[][] trtd = new String[trs.size()][];
			for (int r = 0; r < trs.size(); r++) {
				Elements tds = trs.get(r).select("td");
				trtd[r] = new String[tds.size()];
				for (int c = 0; c < tds.size(); c++){ 
					trtd[r][c] = tds.get(c).html();
					String teamName = tds.get(c).text();
					if(teamName.length()<3) continue; //skips blanks
					String delims = "[{}]+";
					String[] splitupTeamId= trtd[r][c].split(delims);
					String teamid = splitupTeamId[1];
					teamids.get(sportNum).put(teamName,teamid);
					allSchoolNames.add(teamName);
				}
			}
		}
	}
	public static org.jsoup.nodes.Element getTable(int year,TreeMap<Integer,TreeMap<String,String>> teamids, int teamtypeid,String schoolName,PrintWriter eWriter){ // for new data
		// gets Table from site.. only use when there is new data
		// // called this way
		//		table = getTable(year,teamids,teamtypeid,schoolName,eWriter);
		org.jsoup.nodes.Document doc;
		String teamid = teamids.get(teamtypeid).get(schoolName);
		try{
			if (year < 10)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid="+teamtypeid+"&teamid={"+teamid+"}&py=200"
						+ year).timeout(10*4000).get();
			else if (year < 14)
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/statsPrevYears/team_record.asp?teamtypeid="+teamtypeid+"&teamid={"+teamid+"}&py=20"
						+ year).timeout(10*4000).get();
			else
				doc = Jsoup.connect("http://old.post-gazette.com/highschoolsports/stats/team_record.asp?teamtypeid="+teamtypeid+"&teamid={"+teamid+"}&py=20"
						+ year).timeout(10*4000).get();
		} catch(IOException e) {
			eWriter.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			System.out.println("error message: "+e.getMessage()+" school: "+schoolName+" year: "+year+" sport: "+teamtypeid);
			return null;
		}
		org.jsoup.nodes.Element table = doc.select("table").first();
		return table;
	}
	public static void setupOpponentAlphabetically(ArrayList<Game> g, int totalGameCounter, TreeSet<String> opponents,TreeMap<String, Team> teamMap,List<Team> teams){
		Iterator<String> it1 = opponents.iterator();
		Iterator<Team> it2 = teams.iterator();
		while(it1.hasNext() && it2.hasNext()) {
			String s = it1.next();
			Team t = it2.next();
			teamMap.put(s,t);
			teamMap.get(s).name=s;
		}
		for(Game games: g){
			teamMap.get(games.opponent).addGame(games);
		}
		for(Map.Entry<String, Team> entry : teamMap.entrySet()){
			entry.getValue().endCalcs();
		}
	}
	public static void sortOpponentsByGP(List<Team> teams) {
		Collections.sort(teams, new Comparator<Team>(){
			public int compare(Team o1, Team o2){
				return o2.GP - o1.GP;
			}
		});
	}
	private static void sortTotalRecords(List<TotalRecord> totalRecords) {
		Collections.sort(totalRecords, new Comparator<TotalRecord>(){
			public int compare(TotalRecord o1, TotalRecord o2){
				if(o1.winPct == o2.winPct)
					return 0;
				return (o1.winPct > o2.winPct) ? -1 : 1;

			}
		});
	}
	public static boolean contains(final int[] array, final int key) {
		Arrays.sort(array);
		return Arrays.binarySearch(array, key) != -1;
	}
}