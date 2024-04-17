package skewer_scurry_hard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

public class SkewerScurryHardTestcaseGenerator {
	public static void main(String[] args) throws IOException {
		new SkewerScurryHardTestcaseGenerator().run();
	}

	private final String JUDGE_INPUT_FORMAT = "Skewer Scurry Hard/Input/input%02d.txt";
	private final String JUDGE_OUTPUT_FORMAT = "Skewer Scurry Hard/Output/output%02d.txt";

	private final int NUM_TESTCASES = 20;
	private final int MAX_RC = 50;
	private final int MIN_RC = 1;	
	private final int MAX_S_LEN = 1000;
	private final int MIN_S_LEN = 1;

	private final double PILLAR_CHANCE = 0.05;
	private final double NORTH_CHANCE = 0.25;
	private final double EAST_CHANCE = 0.5;
	private final double SOUTH_CHANCE = 0.75;
	private final double SUCCESS_CHANCE = 0.1;
	private final double SWAP_CHANCE = 0.3;

	private void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}

	public void generateTestcases() throws IOException {
		for(int tc = 3; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));

			R = (int) (Math.random() * (MAX_RC - MIN_RC + 1)) + MIN_RC;
			C = (int) (Math.random() * (MAX_RC - MIN_RC + 1)) + MIN_RC;
			startR = (int) (Math.random() * R);
			startC = (int) (Math.random() * C);
			out.printf("%d %d %d %d\n", R, C, startR, startC);

			board = new boolean[R][C];
			for(int r = 0; r < R; r++) {
				for(int c = 0; c < C; c++) {
					if(r == startR && c == startC) {
						board[r][c] = true;
						out.print('.');
						continue;
					}
					board[r][c] = Math.random() > PILLAR_CHANCE;
					out.print(board[r][c] ? '.' : '#');
				}
				out.println();
			}

			determineSets();
			getAllSourceShortestPath();

			boolean isSuccess = Math.random() < SUCCESS_CHANCE;

			int tempS = (int) (Math.random() * (MAX_S_LEN - MIN_S_LEN + 1)) + MIN_S_LEN;
			System.out.println(tempS + " " + isSuccess);
			int currS = 0;

			boolean notNorth = dNorth.get(new Pos(startR, startC)).isEmpty();
			boolean notEast = dEast.get(new Pos(startR, startC)).isEmpty();
			boolean notSouth = dSouth.get(new Pos(startR, startC)).isEmpty();
			boolean notWest = dWest.get(new Pos(startR, startC)).isEmpty();
			if(notNorth && notEast && notSouth && notWest) {
				System.out.println("No Safe Space Exists");
				while(currS < tempS) {
					int dist = (int) (Math.random() * 10);
					for(int i = 0; i < dist; i++) {
						out.print('-');
					}
					double rand = Math.random();
					if(rand < NORTH_CHANCE) {
						out.print('N');
					} else if(rand < EAST_CHANCE) {
						out.print('E');
					} else if(rand < SOUTH_CHANCE) {
						out.print('S');
					} else {
						out.print('W');
					}
					currS += dist+1;
				}
				out.close();
				continue;
			}

			Pos prevPos = new Pos(startR, startC);
			while(currS < tempS) {
				char dir = '-';
				int dist = 0;
				Pos currPos = null;
				boolean safePick = false;
				while(!safePick) {
					double rand = Math.random();
					if(rand < NORTH_CHANCE && !notNorth) {
						currPos = getRandomPos(safeNorthPos);
						dir = 'N';
						safePick = dNorth.get(prevPos).containsKey(currPos);
					} else if(rand < EAST_CHANCE && !notEast) {
						currPos = getRandomPos(safeEastPos);
						dir = 'E';
						safePick = dEast.get(prevPos).containsKey(currPos);
					} else if(rand < SOUTH_CHANCE && !notSouth) {
						currPos = getRandomPos(safeSouthPos);
						dir = 'S';
						safePick = dSouth.get(prevPos).containsKey(currPos);
					} else if(!notWest) {  
						currPos = getRandomPos(safeWestPos);
						dir = 'W';
						safePick = dWest.get(prevPos).containsKey(currPos);
					}
				}


				switch(dir) {
					case 'N': {
						dist = dNorth.get(prevPos).get(currPos);
						break;
					} case 'E': {
						dist = dEast.get(prevPos).get(currPos);
						break;
					} case 'S': {
						dist = dSouth.get(prevPos).get(currPos);
						break;
					} case 'W': {
						dist = dWest.get(prevPos).get(currPos);
						break;
					}
				}

				int rand = (int) (Math.random() * Math.sqrt(dist));
				int upperBound = isSuccess ? dist-1 : rand;
				for(int i = 0; i < upperBound; i++) {
					out.print('-');
				}
				if(isSuccess) {
					out.print(dir);
				} else {
					if(Math.random() < SWAP_CHANCE) {
						rand = (int) (Math.random() * 4);
						if(rand == 0) {
							out.print('N');
						} else if(rand == 1) {
							out.print('E');
						} else if(rand == 2) {
							out.print('S');
						} else {
							out.print('W');
						}
					}
				}

				prevPos = currPos;
				currS += upperBound+1;
			}

			out.close();
		}
	}

	private Pos getRandomPos(HashSet<Pos> pSet) {
		int i = (int) (Math.random() * pSet.size());
		Iterator<Pos> it = pSet.iterator();
		Pos p = null;
		while(it.hasNext() && i >= 0) {
			p = it.next();
			i--;
		}
		return p;
	}

	private boolean[][] board;
	private int R, C, startR, startC;
	public void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));

			StringTokenizer st = new StringTokenizer(file.readLine());
			R = Integer.parseInt(st.nextToken());
			C = Integer.parseInt(st.nextToken());
			startR = Integer.parseInt(st.nextToken());
			startC = Integer.parseInt(st.nextToken());

			board = new boolean[R][C];
			for(int i = 0; i < R; i++) {
				String line = file.readLine();
				for(int j = 0; j < C; j++) {
					board[i][j] = line.charAt(j) == '.';
				}
			}

			determineSets();
			getExtensions(file.readLine());
			getAllSourceShortestPath();
			out.println(getAns() ? "Go Comets!" : "Ouch!!!");

			file.close();
			out.close();
		}
	}

	private boolean getAns() {
		NoDuplicates<Query> toSearch = new NoDuplicates<Query>();
		toSearch.offer(new Query(new Pos(startR, startC), 0));
		while(!toSearch.isEmpty()) {
			Query currQuery = toSearch.poll();
			Pos currPos = currQuery.p;
			int i = currQuery.val;
			if(i == extensions.size()) {
				return true;
			}

			Extension currExtension = extensions.get(i);
			char w = currExtension.wall;
			HashMap<Pos, Integer> minDistances = null;
			switch(w) {
				case 'N': {
					minDistances = dNorth.containsKey(currPos) ? dNorth.get(currPos) : null;
					break;
				} case 'E': {
					minDistances = dEast.containsKey(currPos) ? dEast.get(currPos) : null;
					break;
				} case 'S': {
					minDistances = dSouth.containsKey(currPos) ? dSouth.get(currPos) : null;
					break;
				} case 'W': {
					minDistances = dWest.containsKey(currPos) ? dWest.get(currPos) : null;
					break;
				}
			}
			if(minDistances == null) {
				continue;
			}

			int maxDistance = currExtension.d;
			for(Pos p : minDistances.keySet()) {
				if(minDistances.get(p) <= maxDistance) {
					toSearch.offer(new Query(p, i+1));
				}
			}
		}

		return false;
	}

	private HashMap<Pos, HashMap<Pos, Integer>> dNorth, dEast, dSouth, dWest;
	private final int[] dr = new int[] {-1, 0, 1, 0};
	private final int[] dc = new int[] {0, -1, 0, 1};
	private void getAllSourceShortestPath() {
		dNorth = new HashMap<Pos, HashMap<Pos, Integer>>();
		dEast = new HashMap<Pos, HashMap<Pos, Integer>>();
		dSouth = new HashMap<Pos, HashMap<Pos, Integer>>();
		dWest = new HashMap<Pos, HashMap<Pos, Integer>>();

		Queue<Query> toExplore = new LinkedList<Query>();
		toExplore.add(new Query(new Pos(startR, startC), 0));
		boolean[][] explored = new boolean[R][C];
		explored[startR][startC] = true;
		while(!toExplore.isEmpty()) {
			Pos p = toExplore.poll().p;

			HashMap<Pos, Integer> distancesNorth = new HashMap<Pos, Integer>();
			HashMap<Pos, Integer> distancesEast = new HashMap<Pos, Integer>();
			HashMap<Pos, Integer> distancesSouth = new HashMap<Pos, Integer>();
			HashMap<Pos, Integer> distancesWest = new HashMap<Pos, Integer>();

			Queue<Query> toSearch = new LinkedList<Query>();
			boolean[][] visited = new boolean[R][C];
			visited[p.r][p.c] = true;
			toSearch.add(new Query(p, 0));
			while(!toSearch.isEmpty()) {
				Query currQuery = toSearch.poll();
				Pos currPos = currQuery.p;

				if(safeNorthPos.contains(currPos)) {
					distancesNorth.put(currPos, currQuery.val);
				}
				if(safeEastPos.contains(currPos)) {
					distancesEast.put(currPos, currQuery.val);
				}
				if(safeSouthPos.contains(currPos)) {
					distancesSouth.put(currPos, currQuery.val);
				}
				if(safeWestPos.contains(currPos)) {
					distancesWest.put(currPos, currQuery.val);
				}
				for(int i = 0; i < 4; i++) {
					int newR = currPos.r + dr[i];
					int newC = currPos.c + dc[i];
					if(inBounds(newR, newC) && !visited[newR][newC] && board[newR][newC]) {
						visited[newR][newC] = true;
						toSearch.add(new Query(new Pos(newR, newC), currQuery.val + 1));
						if(!explored[newR][newC]) {
							explored[newR][newC] = true;
							toExplore.add(new Query(new Pos(newR, newC), 0));
						}
					}
				}
			}

			dNorth.put(p, distancesNorth);
			dEast.put(p, distancesEast);
			dSouth.put(p, distancesSouth);
			dWest.put(p, distancesWest);
		}
	}

	private boolean inBounds(int r, int c) {
		return 0 <= r && r < R && 0 <= c && c < C;
	}

	private ArrayList<Extension> extensions;
	private void getExtensions(String line) {
		extensions = new ArrayList<Extension>();
		int time = 1;
		for(int i = 0; i < line.length(); i++) {
			char currChar = line.charAt(i);
			if(currChar != '-') {
				extensions.add(new Extension(currChar, time));
				time = 1;
			} else {
				time++;
			}
		}
	}

	private HashSet<Pos> safeNorthPos, safeEastPos, safeSouthPos, safeWestPos;
	private void determineSets() {
		safeNorthPos = new HashSet<Pos>();
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

		safeEastPos = new HashSet<Pos>();
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

		safeSouthPos = new HashSet<Pos>();
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

		safeWestPos = new HashSet<Pos>();
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
	}
}
