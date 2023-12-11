package temoc_learns_rop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class TemocLearnsROP {
	public static void main(String[] args) throws IOException {
		new TemocLearnsROP().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		int N = Integer.parseInt(file.readLine());
		long[] A = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Long.parseLong(str)).mapToLong(Long::longValue).toArray();
		int M = Integer.parseInt(file.readLine());
		int[] xy = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str) - 1).mapToInt(Integer::intValue).toArray();
		file.close();
		
	}
}
