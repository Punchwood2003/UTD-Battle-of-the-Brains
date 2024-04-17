package niceness;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class Niceness {
	public static void main(String[] args) throws IOException {
		new Niceness().run();
	}

	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);

		int n = Integer.parseInt(file.readLine());
		int[] nums = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
		file.close();

		int lisLen = getLisLen(nums, n);
		int ldsLen = getLdsLen(nums, n);

		out.println(lisLen > ldsLen ? lisLen : ldsLen);
		out.close();
	}

	private int getLisLen(int[] nums, int n) {
		int[] dp = new int[n+1];
		Arrays.fill(dp, Integer.MAX_VALUE);
		dp[0] = Integer.MIN_VALUE;

		for (int i = 0; i < n; i++) {
			int l = Arrays.binarySearch(dp, nums[i]);
			if (l < 0) {
				l = -(l + 1);
			}
			if(nums[i] < dp[l]) {
				dp[l] = nums[i];
			}
		}
		
		int ans = 0;
		for(int l = 0; l <= n; l++) {
			if(dp[l] < Integer.MAX_VALUE) {
				ans = l;
			}
		}

		return ans;
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
