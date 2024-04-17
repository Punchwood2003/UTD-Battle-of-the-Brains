package skewer_scurry_easy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class SkewerScurryEasy {
	private class Pos implements Comparable<Pos> {
		public int r, c;
		
		public Pos(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		public int getID(int R) {
			return (r * R) + c;
		}
		
		@Override
		public int compareTo(Pos other) {
			int comp = Integer.compare(this.r, other.r);
			return comp == 0 ? Integer.compare(this.c, other.c) : comp;
		}
	}
	
	public static void main(String[] args) throws IOException {
		new SkewerScurryEasy().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		int n = Integer.parseInt(file.readLine());
		for(int tc = 1; tc <= n; tc++) {
			StringTokenizer st = new StringTokenizer(file.readLine());
			int R = Integer.parseInt(st.nextToken());
			int C = Integer.parseInt(st.nextToken());
			
			boolean[][] board = new boolean[R][C];
			for(int i = 0; i < R; i++) {
				String line = file.readLine();
				for(int j = 0; j < C; j++) {
					board[i][j] = line.charAt(j) == '.';
				}
			}
			
			determineOutput(out, board, R, C, tc);
		}
		
		file.close();
		out.close();
	}
	
	public void determineOutput(PrintWriter out, boolean[][] board, int R, int C, int tc) {
		TreeSet<Pos> safeNorthPos = new TreeSet<Pos>();
		for(int j = 0; j < C; j++) {
			boolean wallSeen = false;
			for(int i = 0; i < R; i++) {
				if(!wallSeen) {
					wallSeen = !board[i][j];
				} else if(board[i][j]) {
					safeNorthPos.add(new Pos(i, j));
				}
			}
		}
		out.printf("Testcase %d NORTH: %s\n", tc, Arrays.toString(safeNorthPos.stream().map(pos -> pos.getID(R)).mapToInt(Integer::intValue).toArray()).replaceAll("[\\[\\],]", ""));
		
		TreeSet<Pos> safeEastPos = new TreeSet<Pos>();
		for(int i = 0; i < R; i++) {
			boolean wallSeen = false;
			for(int j = C-1; j >= 0; j--) {
				if(!wallSeen) {
					wallSeen = !board[i][j];
				} else if(board[i][j]) {
					safeEastPos.add(new Pos(i, j));
				}
			}
		}
		out.printf("Testcase %d EAST : %s\n", tc, Arrays.toString(safeEastPos.stream().map(pos -> pos.getID(R)).mapToInt(Integer::intValue).toArray()).replaceAll("[\\[\\],]", ""));
		
		TreeSet<Pos> safeSouthPos = new TreeSet<Pos>();
		for(int j = 0; j < C; j++) {
			boolean wallSeen = false;
			for(int i = R-1; i >= 0; i--) {
				if(!wallSeen) {
					wallSeen = !board[i][j];
				} else if(board[i][j]) {
					safeSouthPos.add(new Pos(i, j));
				}
			}
		}
		out.printf("Testcase %d SOUTH: %s\n", tc, Arrays.toString(safeSouthPos.stream().map(pos -> pos.getID(R)).mapToInt(Integer::intValue).toArray()).replaceAll("[\\[\\],]", ""));
		
		TreeSet<Pos> safeWestPos = new TreeSet<Pos>();
		for(int i = 0; i < R; i++) {
			boolean wallSeen = false;
			for(int j = 0; j < C; j++) {
				if(!wallSeen) {
					wallSeen = !board[i][j];
				} else if(board[i][j]) {
					safeWestPos.add(new Pos(i, j));
				}
			}
		}
		out.printf("Testcase %d WEST : %s\n", tc, Arrays.toString(safeWestPos.stream().map(pos -> pos.getID(R)).mapToInt(Integer::intValue).toArray()).replaceAll("[\\[\\],]", ""));
	}
}
