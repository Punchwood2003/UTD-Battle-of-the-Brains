package two_rectangles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class TwoRectanglesTestCaseGenerator {
	public static void main(String[] args) throws IOException {
		new TwoRectanglesTestCaseGenerator().run();
	}
	
	public void run() throws IOException {
		for(int i = 1; i <= 100; i++) {
			generateInput(i, i * 100);
			generateOutput(i);
		}
	}
	
	public void generateInput(int testCaseNumber, int T) throws IOException {
		PrintWriter out = new PrintWriter(new File(String.format("Two_Rectangles_Test_Cases/input%03d.txt", testCaseNumber)));
		out.println(T);
		for(int i = 0; i < T; i++) {
			ArrayList<Point> points = new ArrayList<Point>();
			// Choose a random number of valid rectangles [0, 3) ==> [0, 2]
			int numberOfValidRectangles = (int) (Math.random() * 3);
			// Generate valid rectangles
			for(int j = 0; j < numberOfValidRectangles; j++) {
				// Generate two random points
				int a = (int) (Math.random() * 10000);
				int b = (int) (Math.random() * 10000);
				// Make a rectangle out of those points
				points.add(new Point(a, a));
				points.add(new Point(a, b));
				points.add(new Point(b, a));
				points.add(new Point(b, b));
			}
			// Generate invalid rectangles
			for(int j = numberOfValidRectangles; j < 2; j++) {
				// Four random points to a rectangle
				for(int k = 0; k < 4; k++) {
					points.add(new Point((int) (Math.random() * 10000), (int) (Math.random() * 10000)));
				}
			}
			// Randomize the order of the points
			Collections.shuffle(points);
			// Print the points
			points.stream().forEach(point -> out.printf("%d %d ", point.x, point.y));
			// New line
			out.println();
		}
		out.close();
	}
	
	public void generateOutput(int testCaseNumber) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(String.format("Two_Rectangles_Test_Cases/input%03d.txt", testCaseNumber)));
		PrintWriter out = new PrintWriter(new File(String.format("Two_Rectangles_Test_Cases/output%03d.txt", testCaseNumber)));
		new TwoRectangles().run(file, out);
	}
}
