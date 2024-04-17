package temoc_reach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class TemocReachTestcaseGenerator {
	private class Pos {
		public int r, c, d;
		
		public Pos(int r, int c, int d) {
			this.r = r;
			this.c = c;
			this.d = d;
		}
		
		@Override
		public boolean equals(Object o) {
			Pos other = (Pos) o;
			return this.r == other.r && this.c == other.c;
		}
	}
	
	public static void main(String[] args) throws IOException {
		new TemocReachTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Temoc Reach/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Temoc Reach/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_N = 5000;
	public final int MIN_N = 4;
	public final int MIN_RC = 1;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	public void generateTestcases() throws IOException {
		for(int tc = 3; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));
			
			int n = (int) (Math.random() * (MAX_N - MIN_N + 1)) + MIN_N;
			out.println(n);
			
			int a1 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			int a2 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			out.println(String.format("%d %d", a1, a2));
			
			int b1 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			int b2 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			out.println(String.format("%d %d", b1, b2));
			
			int d1 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			int d2 = (int) (Math.random() * (n - MIN_RC + 1)) + MIN_RC;
			out.println(String.format("%d %d", d1, d2));
			
			out.close();
		}
	}
	
	public void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));
			StringTokenizer st;
			
			int n = Integer.parseInt(file.readLine());
			
			st = new StringTokenizer(file.readLine());
			int a1 = Integer.parseInt(st.nextToken())-1;
			int a2 = Integer.parseInt(st.nextToken())-1;
			
			st = new StringTokenizer(file.readLine());
			int b1 = Integer.parseInt(st.nextToken())-1;
			int b2 = Integer.parseInt(st.nextToken())-1;
			
			st = new StringTokenizer(file.readLine());
			int d1 = Integer.parseInt(st.nextToken())-1;
			int d2 = Integer.parseInt(st.nextToken())-1;
			
			file.close();
			
			int minDist1 = getMinDist(a1, a2, d1, d2, n);
			int minDist2 = getMinDist(b1, b2, d1, d2, n);
			
			out.println(minDist1 == minDist2 ? 0 : (minDist1 < minDist2 ? 1 : 2));
			
			out.close();
		}
	}
	
	private final int[] dr = new int[] {-1, 1, 2, 2, 1, -1, -2, -2};
	private final int[] dc = new int[] {-2, -2, -1, 1, 2, 2, 1, -1};
	private int getMinDist(int r1, int c1, int r2, int c2, int n) {
		Pos start = new Pos(r1, c1, 0);
		Pos finish = new Pos(r2, c2, 0);
		boolean[][] visited = new boolean[n][n];
		visited[r1][c1] = true;
		
		Queue<Pos> toSearch = new LinkedList<Pos>();
		
		toSearch.offer(start);
		while(!toSearch.isEmpty()) {
			Pos curr = toSearch.poll();
			if(curr.equals(finish)) {
				return curr.d;
			}
			
			for(int i = 0; i < 8; i++) {
				int newR = curr.r + dr[i];
				int newC = curr.c + dc[i];
				if(inRange(newR, newC, n) && !visited[newR][newC]) {
					toSearch.offer(new Pos(newR, newC, curr.d + 1));
					visited[newR][newC] = true;
				}
			}
		}
		
		return -1;
	}
	
	private boolean inRange(int r, int c, int n) {
		return 0 <= r && r < n && 0 <= c && c < n;
	}
}
