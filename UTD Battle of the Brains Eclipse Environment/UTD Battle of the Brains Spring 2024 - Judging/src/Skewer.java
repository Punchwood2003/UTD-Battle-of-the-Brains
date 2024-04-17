import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Skewer {

	static String print(String dir, int test, TreeSet<Integer> set)
	{
		StringBuilder ans = new StringBuilder();
		while(!set.isEmpty())
		{
			ans.append(set.first()).append(" "); 
			set.remove(set.first()); 
		}
		return String.format("Testcase %d %-5s: %s", test, dir, ans.toString());
	}

	static void parse(boolean[][][] safe, int dir, TreeSet<Integer> set)
	{
		for(int row = 0; row < safe[dir].length; row++)
			for(int col = 0; col < safe[dir][0].length; col++)
				if(safe[dir][row][col])
					set.add(row * safe[dir].length + col);
	}

	public static void main(String... args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(new File("testing.txt"));
		int T = Integer.parseInt(br.readLine());
		for(int t = 1; t <= T; t++) {
			StringTokenizer st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			char[][] grid = new char[r][c];
			boolean[][][] safe = new boolean[4][r][c];
			for(int i = 0; i < r; i++) {
				String line = br.readLine();
				for(int j = 0; j < c; j++) {
					grid[i][j] = line.charAt(j);
				}
			}

			TreeSet<Integer> list = new TreeSet<>();
			for (int j = 0; j < c; j++) {
				boolean s = false;
				for (int i = 0; i < r; i++) {
					if (grid[i][j] == '#') {
						s = true;
					}
					safe[0][i][j] = s && grid[i][j] != '#';
				}
			}
			parse(safe, 0, list);
			out.println(print("NORTH", t, list)); 
			list.clear();
			for (int i = 0; i < r; i++) {
				boolean s = false;
				for (int j = c - 1; j >= 0; j--) {
					if (grid[i][j] == '#') {
						s = true;
					}
					safe[1][i][j] = s && grid[i][j] != '#';
				}
			}
			parse(safe, 1, list);
			out.println(print("EAST", t, list)); 
			list.clear();
			for (int j = 0; j < c; j++) {
				boolean s = false;
				for (int i = r - 1; i >= 0; i--) {
					if (grid[i][j] == '#') {
						s = true;
					}
					safe[2][i][j] = s && grid[i][j] != '#';
				}
			}
			parse(safe, 2, list);
			out.println(print("SOUTH", t, list)); 
			list.clear();
			for (int i = 0; i < r; i++) {
				boolean s = false;
				for (int j = 0; j < c; j++) {
					if (grid[i][j] == '#') {
						s = true;
					}
					safe[3][i][j] = s && grid[i][j] != '#';
				}
			}
			parse(safe, 3, list);
			out.println(print("WEST", t, list)); 
			list.clear();
		}
		
		out.close();
	}
}
