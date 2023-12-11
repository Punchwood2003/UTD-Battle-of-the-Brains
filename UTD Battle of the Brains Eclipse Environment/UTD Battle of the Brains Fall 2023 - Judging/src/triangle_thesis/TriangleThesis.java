package triangle_thesis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class TriangleThesis {
	
	//I keep test and out gen separate so I can edit a couple cases by hand
	enum Mode {SOLVE,TESTGEN,OUTGEN};
	public static final String INPUT_FNAME_PREFIX = "input";
	public static final String OUTPUT_FNAME_PREFIX = "output";
	public static final String FNAME_FORMAT = "%s%02d.txt";
	public static final String INPUT_PATH = "Triangle_Thesis_Testcases/Input/";
	public static final String OUTPUT_PATH = "Triangle_Thesis_Testcases/Output/";
	public static final int NUM_CASES = 30;

	public static void main(String[] args) throws Exception {
		new TriangleThesis().run(Mode.OUTGEN);
	}
	
	public void run(Mode mode) throws Exception{
		if(mode == Mode.SOLVE)
			run(new BufferedReader(new InputStreamReader(System.in)), new PrintWriter(System.out));
		else if(mode == Mode.TESTGEN)
			genData();
		else if(mode == Mode.OUTGEN)
			for(int i = 0; i < NUM_CASES; i++) {
				long millis = System.currentTimeMillis();
				run(new BufferedReader(new InputStreamReader(System.in)), new PrintWriter(System.out));
				System.out.println("finished case "+ i +" in "+(System.currentTimeMillis() - millis)+" millis");
			}
	}
	
	public void genData() throws Exception {
		// First case is sample case 
		for(int i = 1; i < NUM_CASES; i++) {
			String outputName = String.format(INPUT_PATH + FNAME_FORMAT, INPUT_FNAME_PREFIX, i); 
			PrintWriter pout = new PrintWriter(new FileWriter(new File(outputName)));
			int B = (int)(Math.random() * 200000) + 1;
			int H = (int)(Math.random() * 200000) + 1;
			ArrayList<Integer> vertical = new ArrayList<Integer>();
			ArrayList<Integer> horizontal = new ArrayList<Integer>();
			double lineChance = (i+0.0) / NUM_CASES;
			for(int j = -B / 2 + 1; j < B / 2; j++) {
				if(Math.random() < lineChance)
					vertical.add(j);
			}
			for(int j = 1; j < H; j++) {
				if(Math.random() < lineChance)
					horizontal.add(j);
			}
			pout.println(B + " " + H + " " + vertical.size() + " " + horizontal.size());
			StringBuilder vert = new StringBuilder();
			for(int j: vertical)
				vert.append(j+" ");
			pout.println(vert.toString().trim());
			StringBuilder horz = new StringBuilder();
			for(int j: horizontal)
				horz.append(j+" ");
			pout.println(horz.toString().trim());
			pout.flush();
			pout.close();
		}
	}

	public void run(BufferedReader file, PrintWriter pout) throws Exception {
		StringTokenizer st = new StringTokenizer(file.readLine());
		long B = Long.parseLong(st.nextToken());
		long H = Long.parseLong(st.nextToken());
		int v = Integer.parseInt(st.nextToken());
		int h = Integer.parseInt(st.nextToken());
		long[] X = new long[v];
		ArrayList<Rational> Y = new ArrayList<>();
		st = new StringTokenizer(file.readLine());
		for(int i = 0;i<X.length;i++)
			X[i] = Long.parseLong(st.nextToken());
		st = new StringTokenizer(file.readLine());
		for(int i = 0;i<h;i++)
			Y.add(new Rational(Long.parseLong(st.nextToken()),1));
		Collections.sort(Y);
		long ans = h + 1;
		for(long x:X) {
			if(x < 0) {
				Rational intersection = new Rational(2*x + B,2).multiply(new Rational(2*H, B));
				ans += countLess(Y, intersection) + 1;
			} else if(x > 0) {
				Rational intersection = new Rational(B * H - 2 * H * x, B);
				ans += countLess(Y,intersection) + 1;
			} else {
				ans += (h+1) * 2;
			}
		}
		pout.println(ans);
		pout.flush();
		pout.close();
	}
	
	public int countLess(ArrayList<Rational> list, Rational limit) {
		int L = -1;
		int R = list.size();
		int M = (L + R) / 2;
		while(R - L > 1) {
			Rational test = list.get(M);
			if(test.compareTo(limit) < 0) {
				L = M;
			} else {
				R = M;
			}
			M = (L + R) / 2;
		}
		return R;
	}
	
	private class Rational implements Comparable<Rational> {
		private BigInteger numerator;
		private BigInteger denominator;
		
		public Rational(long num, long denom) {
			this(BigInteger.valueOf(num), BigInteger.valueOf(denom));
		}
		
		private Rational(BigInteger num, BigInteger denom) {
			this.numerator = num;
			this.denominator = denom;
			reduce();
		}
		
		public void reduce() {
			BigInteger gcd = numerator.gcd(denominator);
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
			if(denominator.compareTo(BigInteger.ZERO) < 0) {
				denominator = denominator.negate();
				numerator = numerator.negate();
			}
		}
		
		public Rational multiply(Rational other) {
			return new Rational(numerator.multiply(other.numerator), denominator.multiply(other.denominator));
		}
		
		public int compareTo(Rational other) {
			return numerator.multiply(other.denominator).compareTo(other.numerator.multiply(denominator));
		}
	}
}
