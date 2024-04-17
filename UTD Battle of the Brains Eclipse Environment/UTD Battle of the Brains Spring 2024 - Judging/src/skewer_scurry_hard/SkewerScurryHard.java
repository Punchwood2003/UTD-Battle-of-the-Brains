package skewer_scurry_hard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;

class Pos implements Comparable<Pos> {
    public int r, c;
    private int hashCode;
    private boolean hashed;

    public Pos(int r, int c) {
        this.r = r;
        this.c = c;
    }

    @Override
    public int hashCode() {
        if(hashed) {
            return hashCode;
        }
        hashCode = Arrays.hashCode(new int[] {r, c});
        hashed = true;
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        Pos other = (Pos) o;
        return this.r == other.r && this.c == other.c;
    }

    @Override
    public int compareTo(Pos other) {
        int comp = Integer.compare(this.r, other.r);
        return comp == 0 ? Integer.compare(this.c, other.c) : comp;
    }
}

class Extension {
    public char wall;
    public int d;

    public Extension(char w, int d) {
        this.wall = w;
        this.d = d;
    }
}

class Query implements Comparable<Query> {
    public Pos p;
    public int val;
    private int hashCode;
    private boolean hashed;

    public Query(Pos p, int v) {
        this.p = p;
        this.val = v;
    }
    
    @Override
    public int hashCode() {
        if(hashed) {
            return hashCode;
        }
        hashCode = Arrays.hashCode(new int[] {val, p.r, p.c});
        hashed = true;
        return hashCode;
    }
    
    @Override
    public boolean equals(Object o) {
        Query other = (Query) o;
        return this.val == other.val && this.p.equals(other.p);
    }
    
    @Override
    public int compareTo(Query other) {
        int comp = -Integer.compare(this.val, other.val);
        return comp == 0 ? p.compareTo(other.p) : comp;
    }
}

@SuppressWarnings("serial")
class NoDuplicates<E extends Comparable<E>> extends PriorityQueue<E> {
    private HashSet<E> uniqueElements;

    public NoDuplicates() {
        super();
        this.uniqueElements = new HashSet<E>();
    }

    @Override
    public boolean offer(E e) {
        boolean isAdded = false;
        if(!this.uniqueElements.contains(e)) {
            isAdded = super.offer(e);
            this.uniqueElements.add(e);
        }
        return isAdded;
    }

    @Override
    public E poll() {
        E elementRemoved = super.poll();
        this.uniqueElements.remove(elementRemoved);
        return elementRemoved;
    }
}

public class SkewerScurryHard {
    public static void main(String[] args) throws IOException {
        new SkewerScurryHard().run();
    }

    private boolean[][] board;
    private int R, C, startR, startC;
    public void run() throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);

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