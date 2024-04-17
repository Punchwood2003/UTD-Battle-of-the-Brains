package triangle_thesis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.StringTokenizer;

public class TriangleThesis {
	public static void main(String[] args) throws IOException {
		new TriangleThesis().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		StringTokenizer st = new StringTokenizer(file.readLine());
		BigInteger B = BigInteger.valueOf(Long.parseLong(st.nextToken()));
		BigInteger H = BigInteger.valueOf(Long.parseLong(st.nextToken()));
		int v = Integer.parseInt(st.nextToken());
		int h = Integer.parseInt(st.nextToken());
		
		BigInteger[] x = Arrays.asList(file.readLine().split(" ")).stream().map(str -> BigInteger.valueOf(Math.abs(Long.parseLong(str)))).toArray(BigInteger[]::new);
		BigInteger[] y = new BigInteger[h+1];
		st = new StringTokenizer(file.readLine());
		for(int i = 0; i < h; i++) {
			y[i] = BigInteger.valueOf(Long.parseLong(st.nextToken())).multiply(B);
		}
		y[h] = BigInteger.ZERO;
		Arrays.sort(y);
		
		
		BigInteger[] yIntercepts = new BigInteger[v];
		BigInteger h2 = H.multiply(BigInteger.valueOf(-2));
		BigInteger hb = H.multiply(B);
		for(int i = 0; i < x.length; i++) {
			yIntercepts[i] = h2.multiply(x[i]).add(hb);
		}
		
		BigInteger numTriangles = BigInteger.valueOf(h+1);
		h2 = BigInteger.valueOf(2 * y.length);
		for(int i = 0; i < x.length; i++) {
			if(x[i].equals(BigInteger.ZERO)) {
				numTriangles = numTriangles.add(h2);
			} else {
				int index = Arrays.binarySearch(y, yIntercepts[i]);
				if(index < 0) {
					index = -index - 1;
				}
				numTriangles = numTriangles.add(BigInteger.valueOf(index));
			}
		}
		
		out.println(numTriangles);
		out.close();
	}
}
