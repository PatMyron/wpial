import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		File template = new File("/Users/Patrick/Downloads/index-template.html");
		Scanner sc = new Scanner(template);
		List<String> lines = new ArrayList<>();
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		String[] arr = lines.toArray(new String[0]);
		sc.close();

		String endingString = "<script>addRow(\"..\",\"..\",1,\"0 B\",\"3/8/15, 3:28:52 AM\");</script>\n<script>addRow(\"opponentsABC.html\",\"opponentsABC.html\",0,\"6.0 kB\",\"12/18/14, 2:13:03 AM\");</script>\n<script>addRow(\"opponentsGP.html\",\"opponentsGP.html\",0,\"6.0 kB\",\"12/18/14, 2:13:03 AM\");</script>\n<script>addRow(\"seasons.html\",\"seasons.html\",0,\"2.9 kB\",\"12/17/14, 6:37:52 PM\");</script>\n";
		String middle1 = "<script>start(\"patmyron.com\\\\wpial\\\\data\\\\specificData\\\\";
		String middle2 = "\");</script>";
		final File folder = new File("/Users/Patrick/Documents/GitHub/WPIAL/data/specificData");
		listFilesForFolder(folder, arr, endingString, middle1, middle2);
	}

	public static void listFilesForFolder(final File folder, String[] arr, String endingString, String middle1, String middle2) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, arr, endingString, middle1, middle2);
			}
		}
		if (folder.equals(new File("/Users/Patrick/Documents/GitHub/WPIAL/data/specificData"))) {
			return;
		}
		//////////////////////////////////////////////////
		File file = null;
		try {
			file = new File(folder + "/index.html");
			if (file.createNewFile()) {
				System.out.println("File (" + file + ") is created!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//////////////////////////////////////////////////
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)))) {
			for (String s : arr)
				out.println(s);
			out.print(middle1);
			out.print(folder.getName());
			out.println(middle2);
			// print middle1 then custom part then middle2
			out.println(endingString);
		} catch (IOException ignored) {
		}
	}
}
