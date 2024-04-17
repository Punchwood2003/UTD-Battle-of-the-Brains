package niceness;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NicenessTestcaseGenerator {
	public static void main(String[] args) throws IOException {
		new NicenessTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Niceness/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Niceness/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_N = 10_000;
	public final int MIN_N = 1;
	public final int MAX_SI = 2_000_000_000;
	public final int MIN_SI = 1;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	private void generateTestcases() throws IOException {
		for(int tc = 2; tc < NUM_TESTCASES; tc++) {
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
			
			int lisLen = getLisLen(nums, n);
			int ldsLen = getLdsLen(nums, n);
			
			out.println(lisLen > ldsLen ? lisLen : ldsLen);
			out.close();
		}
	}
	
	private int getLisLen(int[] nums, int n) {
        ArrayList<Integer> ans = new ArrayList<Integer>();
 
        ans.add(nums[0]);
 
        for (int i = 1; i < n; i++) {
            if (nums[i] > ans.get(ans.size() - 1)) {
                ans.add(nums[i]);
            } else {

                int low = Collections.binarySearch(ans, nums[i]);
 
                if (low < 0) {
                    low = -(low + 1);
                }
                ans.set(low, nums[i]);
            }
        }
 
        return ans.size();
	}
	
	private int getLdsLen(int[] nums, int n) {
		int[] reverse = new int[n];
		for(int i = 0; i <= n/2; i++) {
			reverse[i] = nums[n-i-1];
			reverse[n-i-1] = nums[i];
		}
		return getLisLen(reverse, n);
	}
}
