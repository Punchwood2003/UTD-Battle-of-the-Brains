package enarc_election;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class EnarcElectionTestcaseGenerator {
	
	public static void main(String[] args) throws IOException {
		new EnarcElectionTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Enarc Election/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Enarc Election/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_N = 1_000_00;
	public final int MIN_N = 1;
	public final int MAX_SF = Integer.MAX_VALUE;
	public final int MIN_SF = 0;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	public void generateTestcases() throws IOException {
		for(int tc = 3; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));
			
			int n = (int) (Math.random() * (MAX_N - MIN_N + 1)) + MIN_N;
			out.println(n);
			
			int[] s = new int[n];
			int[] f = new int[n];
			for(int i = 0; i < n; i++) {
				int fi = (int) (Math.random() * MAX_SF) + MIN_SF;
				int si = (int) (Math.random() * fi) + MIN_SF;
				
				s[i] = si;
				f[i] = fi;
			}
			
			out.println(Arrays.toString(s).replaceAll("[\\[\\],]", ""));
			out.println(Arrays.toString(f).replaceAll("[\\[\\],]", ""));
			
			out.close();
		}
	}
	
	public void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));

			int n = Integer.parseInt(file.readLine());
			int[] s = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
			int[] f = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
			file.close();
			
			Arrays.sort(s);
			Arrays.sort(f);
			
			int j = 0, e = 0;
			for(int i = 0; i < n; i++) {
				if(s[i] < f[j]) {
					e++;
				} else {
					j++;
				}
			}
			
			out.println(e);
			out.close();
		}
	}
}
