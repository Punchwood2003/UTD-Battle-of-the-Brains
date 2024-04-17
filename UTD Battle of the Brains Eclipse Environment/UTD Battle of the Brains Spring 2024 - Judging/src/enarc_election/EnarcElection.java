package enarc_election;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class EnarcElection {
	public static void main(String[] args) throws IOException {
		new EnarcElection().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
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
