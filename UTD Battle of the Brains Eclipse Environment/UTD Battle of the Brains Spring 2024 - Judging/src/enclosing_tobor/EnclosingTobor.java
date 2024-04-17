package enclosing_tobor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class EnclosingTobor {
	public static void main(String[] args) throws IOException {
		new EnclosingTobor().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
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
