package most_average_musician;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class MostAverageMusician {
	public static void main(String[] args) throws IOException {
		new MostAverageMusician().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		int numInts = Integer.parseInt(file.readLine());
		long[] grades = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Long.parseLong(str)).mapToLong(Long::longValue).toArray();
		long[] aloofness = new long[numInts];
		for(int i = 0; i < numInts; i++) {
			for(int j = 0; j < numInts; j++) {
				long grade = grades[i];
				if(i != j) {
					aloofness[i] += Math.abs(grade - grades[j]);
				}
			}
		}
		file.close();
		
		int minIndex = 0;
		long min = aloofness[0];
		for(int i = 1; i < numInts; i++) {
			if(aloofness[i] < min) {
				minIndex = i;
				min = aloofness[i];
			}
		}
		
		boolean isUnique = true;
		for(int i = 0; i < numInts; i++) {
			if(aloofness[i] == min && i != minIndex) {
				isUnique = false;
				break;
			}
		}
		out.println(isUnique ? minIndex : -1);
		out.close();
	}
}
