package bishop_attack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BishopAttack {
	public static void main(String[] args) throws IOException {
		new BishopAttack().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
		String[] line = file.readLine().split(" ");
		int col = line[0].charAt(0) - 'a' + 1;
		int row = Integer.parseInt(line[1]);
		file.close();
		
		int topLeft = Math.min(row, col) - 1;
		int topRight = Math.min(row, 9-col) - 1;
		int bottomRight = 8 - Math.max(row, 9-col);
		int bottomLeft = 8 - Math.max(row, col);
		out.println(topLeft + topRight + bottomRight + bottomLeft + 1);
		out.close();
	}
}
