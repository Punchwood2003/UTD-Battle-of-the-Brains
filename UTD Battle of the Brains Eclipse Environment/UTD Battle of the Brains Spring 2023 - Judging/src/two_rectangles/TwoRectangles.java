package two_rectangles;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

class Point implements Comparable<Point> {
	public int x, y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int compareTo(Point other) {
		int comp = Integer.compare(this.y, other.y);
		return comp == 0 ? Integer.compare(this.x, other.x) : comp;
	}
	public String toString() {
		return String.format("(%d, %d)", this.x, this.y);
	}
}

public class TwoRectangles {
	class Pairing {
		public HashMap<Integer, Integer> x, y;
		public Pairing(HashMap<Integer, Integer> x, HashMap<Integer, Integer> y) {
			this.x = x;
			this.y = y;
		}
		public int hashCode() {
			return this.x.hashCode() + this.y.hashCode();
		}
	}

	public static void main(String[] args) throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		new TwoRectangles().run(file, out);
	}

	public void run(BufferedReader file, PrintWriter out) throws IOException {
		int numTimes = Integer.parseInt(file.readLine());
		while(numTimes-->0) {
			int[] ints = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
			Point[] points = new Point[8];
			for(int i = 0; i < ints.length/2; i++) {
				points[i] = new Point(ints[i*2], ints[i*2+1]);
			}
			out.println(valid(points) ? "1" : "0");
		}

		file.close();
		out.close();
	}

	public boolean valid(Point[] points) {
		HashMap<Integer, Integer> xPosFreq = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> yPosFreq = new HashMap<Integer, Integer>();
		for(Point p : points) {
			int x = p.x;
			int y = p.y;
			int xFreq = 1;
			int yFreq = 1;

			if(xPosFreq.containsKey(x)) {
				xFreq += xPosFreq.get(x);
			}
			xPosFreq.put(x, xFreq);

			if(yPosFreq.containsKey(y)) {
				yFreq += yPosFreq.get(y);
			}
			yPosFreq.put(y, yFreq);
		}

		for(int key : xPosFreq.keySet()) {
			if(xPosFreq.get(key) % 2 == 1) {
				return false;
			}
		}
		for(int key : yPosFreq.keySet()) {
			if(yPosFreq.get(key) % 2 == 1) {
				return false;
			}
		}
		return true;
	}
}