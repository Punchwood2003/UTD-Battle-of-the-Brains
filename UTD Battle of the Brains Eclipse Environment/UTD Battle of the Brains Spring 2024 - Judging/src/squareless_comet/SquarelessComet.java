package squareless_comet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class SquarelessComet {
	public static void main(String[] args) throws IOException {
		new SquarelessComet().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
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
