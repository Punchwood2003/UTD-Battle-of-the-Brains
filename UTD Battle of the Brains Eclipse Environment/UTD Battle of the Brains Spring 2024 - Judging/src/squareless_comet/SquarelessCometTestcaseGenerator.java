package squareless_comet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class SquarelessCometTestcaseGenerator {
	public static void main(String[] args) throws IOException {
		new SquarelessCometTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Squareless Comet/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Squareless Comet/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_N = 1_000;
	public final int MIN_N = 1;
	public final int MAX_SI = 1_000;
	public final int MIN_SI = 2;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	private void generateTestcases() throws IOException {
		for(int tc = 1; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));
			
			int n = (int) (Math.random() * (MAX_N - MIN_N + 1)) + MIN_N;
			out.println(n);
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < n; i++) {
				sb.append(String.format("%d ", (int) (Math.random() * (MAX_SI - MIN_SI + 1)) + MIN_SI));
			}
			out.println(sb.toString().trim());
			
			out.close();
		}
	}
	
	private void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));
			
			int n = Integer.parseInt(file.readLine());
			int[] nums = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
			file.close();
			
			int numSquareless = 0;
			for(int i = 0; i < n; i++) {
				numSquareless += isSquareless(nums[i]) ? 1 : 0;
			}
			out.println(numSquareless);
			out.close();
		}
	}
	
	private boolean isSquareless(int num) {
		int sqrtNum = (int) Math.sqrt(num);
		for(int i = 2; i <= sqrtNum; i++) {
			if(num % (i * i) == 0) {
				return false;
			}
		}
		return true;
	}
}
