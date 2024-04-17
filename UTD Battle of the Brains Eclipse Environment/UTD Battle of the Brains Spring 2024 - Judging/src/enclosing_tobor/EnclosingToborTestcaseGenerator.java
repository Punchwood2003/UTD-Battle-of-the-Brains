package enclosing_tobor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnclosingToborTestcaseGenerator {
	public static void main(String[] args) throws IOException {
		new EnclosingToborTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Enclosing Tobor/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Enclosing Tobor/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_XY = 100;
	public final int MIN_XY = 0;
	
	public final double YES_CHANCE = 0.5;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	private void generateTestcases() throws IOException {
		for(int tc = 2; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));
			
			if(Math.random() < YES_CHANCE) {
				int localMaxX = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
				int localMinX = (int) (Math.random() * (localMaxX - MIN_XY + 1)) + MIN_XY;
				int localMaxY = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
				int localMinY = (int) (Math.random() * (localMaxY - MIN_XY + 1)) + MIN_XY;
				
				int[][] nums = new int[4][4];
				nums[0][0] = (int) (Math.random() * (localMaxX - localMinX + 1)) + localMinX;
				nums[0][1] = (int) (Math.random() * (localMaxY - localMinY + 1)) + localMinY;
				nums[0][2] = localMaxX;
				nums[0][3] = localMaxY;
				nums[1][0] = localMinX;
				nums[1][1] = localMinY;
				nums[1][2] = (int) (Math.random() * (localMaxX - localMinX + 1)) + localMinX;
				nums[1][3] = (int) (Math.random() * (localMaxY - localMinY + 1)) + localMinY;
				
				for(int i = 2; i < 4; i++) {
					int r1 = (int) (Math.random() * (localMaxX - localMinX + 1)) + localMinX;
					int c1 = (int) (Math.random() * (localMaxY - localMinY + 1)) + localMinY;
					int r2 = (int) (Math.random() * (localMaxX - localMinX + 1)) + localMinX;
					int c2 = (int) (Math.random() * (localMaxY - localMinY + 1)) + localMinY;
					nums[i] = new int[] {r1, c1, r2, c2};
				}
				
				List<int[]> asList = Arrays.asList(nums);
				Collections.shuffle(asList);
				for(int i = 0; i < 4; i++) {
					out.println(Arrays.toString(asList.get(i)).replaceAll("[\\[\\],]", ""));
				}
			} else {
				for(int i = 0; i < 4; i++) {
					int r1 = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
					int c1 = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
					int r2 = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
					int c2 = (int) (Math.random() * (MAX_XY - MIN_XY + 1)) + MIN_XY;
					out.printf("%d %d %d %d\n", r1, c1, r2, c2);
				}
			}
			
			out.close();
		}
	}
	
	private void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));
			
			int leftMin = Integer.MAX_VALUE, rightMax = Integer.MIN_VALUE, 
					downMin = Integer.MAX_VALUE, upMax = Integer.MIN_VALUE;
			int[][] points = new int[4][4];
			for(int i = 0; i < 4; i++) {
				int[] nums = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
				points[i] = nums;
				if(nums[0] < leftMin) {
					leftMin = nums[0];
				}
				if(nums[0] > rightMax) {
					rightMax = nums[0];
				}
				if(nums[2] < leftMin) {
					leftMin = nums[2];
				}
				if(nums[2] > rightMax) {
					rightMax = nums[2];
				}
				if(nums[1] < downMin) {
					downMin = nums[1];
				}
				if(nums[1] > upMax) {
					upMax = nums[1];
				}
				if(nums[3] < downMin) {
					downMin = nums[3];
				}
				if(nums[3] > upMax) {
					upMax = nums[3];
				}
			}
			file.close();
			
			boolean ans = enclosingPossible(points, leftMin, rightMax, downMin, upMax);
			
			out.println(ans ? "Yes" : "No");
			out.close();
		}
	}
	
	private boolean enclosingPossible(int[][] points, int leftMin, int rightMax, int downMin, int upMax) {
		for(int i = 0; i < 4; i++) {
			boolean iIsBottomLeft = points[i][0] == leftMin && points[i][1] == downMin;
			boolean iIsTopRight = points[i][2] == rightMax && points[i][3] == upMax;
			if(!iIsBottomLeft && !iIsTopRight) {
				continue;
			}
			for(int j = i + 1; j < 4; j++) {
				boolean jIsBottomLeft = points[j][0] == leftMin && points[j][1] == downMin;
				boolean jIsTopRight = points[j][2] == rightMax && points[j][3] == upMax;
				if((iIsBottomLeft && jIsTopRight) || (iIsTopRight && jIsBottomLeft)) {
					return true;
				}
			}
		}
		return false;
	}
}
