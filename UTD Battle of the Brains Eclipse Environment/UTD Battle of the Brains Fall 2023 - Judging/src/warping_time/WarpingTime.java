package warping_time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;

public class WarpingTime {
	public static void main(String[] args) throws IOException {
		new WarpingTime().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		StringTokenizer st = new StringTokenizer(file.readLine());
		int N = Integer.parseInt(st.nextToken());
		int K = Integer.parseInt(st.nextToken());
		int[] A = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
		file.close();
		
		int[] dp = new int[N];
		Arrays.fill(dp, Integer.MAX_VALUE);
		dp[0] = 0;
		for(int i = 1; i <= N; i++) {
			int numTurnsToCurrDist = dp[i-1];
			if(numTurnsToCurrDist == Integer.MAX_VALUE) {
				continue;
			}
			for(int j = 0; j < K; j++) {
				long Aj = A[j];
				// Do the addition step
				long newDist = i + Aj;
				if(newDist <= N && numTurnsToCurrDist + 1 < dp[(int) (newDist-1)]) {
					dp[(int) (newDist-1)] = numTurnsToCurrDist + 1;
				}
				// Do the multiplication step
				newDist = i * Aj;
				if(newDist <= N && numTurnsToCurrDist + 1 < dp[(int) (newDist-1)]) {
					dp[(int) (newDist-1)] = numTurnsToCurrDist + 1;
				}
			}
		}
		out.println(dp[N-1] == Integer.MAX_VALUE ? -1 : dp[N-1]);
		out.close();
	}
}
