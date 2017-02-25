import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws NumberFormatException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader("PracticeInput.txt"));
        String input;
        outerloop:
        while ((input = reader.readLine()) != null && input.length() != 0) {
            String[] str1 = input.split(" ");
            int[][] matrix = new int[str1.length][];
            for (int i = 0; i < matrix.length; i++) {
                String[] str2 = str1[i].split("\\.");
                matrix[i] = new int[str2.length];
                for (int j = 0; j < matrix[i].length; j++) {
                    if (str2[j].equals("")) {
                        System.out.println("InValid");
                        continue outerloop; // FIX FIX FIX
                    }
                    matrix[i][j] = Integer.parseInt(str2[j]);
                }
            }

            // checking validity
            if (matrix[2].length != 4) {
                System.out.println("InValid");
                continue;
            }
            for (int i = 0; i < 4; i++) {
                if (matrix[2][i] < 0 || matrix[2][i] > 255) {
                    System.out.println("InValid");
                    continue outerloop;
                }
            }
            // checking bounds
            if (matrix[2][0] != 10 && matrix[2][0] != 11) {
                System.out.println("OutRange");
                continue;
            }
            if (matrix[2][0] == 10) {
                System.out.println("InRange");
                continue;
            }
            if (matrix[2][0] == 11) { // 10s all in bounds
                if (matrix[2][1] > 199) {
                    System.out.println("OutRange");
                    continue;
                }
                if (matrix[2][1] < 199) {
                    System.out.println("InRange");
                } else {
                    if (matrix[2][2] > 88) {
                        System.out.println("OutRange");
                        continue;
                    }
                    if (matrix[2][2] < 88) {
                        System.out.println("InRange");
                    } else {
                        if (matrix[2][2] > 254) {
                            System.out.println("OutRange");
                        } else {
                            System.out.println("InRange");
                        }
                    }
                }
            }
        }
        reader.close();
    }
}
