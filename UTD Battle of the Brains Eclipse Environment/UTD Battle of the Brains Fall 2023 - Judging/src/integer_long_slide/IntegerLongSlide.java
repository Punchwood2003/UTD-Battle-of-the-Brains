package integer_long_slide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class IntegerLongSlide {
	public static void main(String[] agrs) throws IOException {
		new IntegerLongSlide().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		int numTimes = Integer.parseInt(file.readLine());
		while(numTimes-->0) {
			StringTokenizer st = new StringTokenizer(file.readLine());
			int b = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int sumOfSquares = (b * b) + (h * h);
			int intHyp = (int) Math.sqrt(sumOfSquares);
			out.printf("%s\n", (intHyp * intHyp == sumOfSquares) ? "Yes" : "No");
		}
		file.close();
		out.close();
	}
}
