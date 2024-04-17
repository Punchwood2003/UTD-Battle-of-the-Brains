package temoc_reach;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class TemocReach {
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
		new TemocReach().run();
	}

	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
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