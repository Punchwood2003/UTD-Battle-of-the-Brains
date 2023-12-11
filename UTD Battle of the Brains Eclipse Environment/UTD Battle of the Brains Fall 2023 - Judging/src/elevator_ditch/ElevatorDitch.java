package elevator_ditch;

import java.io.IOException;
import java.util.Scanner;

public class ElevatorDitch {
	public static void main(String[] args) throws IOException {
		new ElevatorDitch().run();
	}

	public void run() throws IOException {
		Scanner scan = new Scanner(System.in);

		// Yes, this problem can be solved in O(1) time, but for the sake of argument
		// we want to show that this can also be solved in O(log(n)) time.
		int numTimes = scan.nextInt();
		scan.nextLine();
		while(numTimes-->0) {
			long t = scan.nextLong();
			long w = scan.nextLong();
			long d = scan.nextLong();
			long M = scan.nextLong();
			scan.nextLine();

			M = (M / t + d - w) / d;
			// Now let's simulate a worse algorithm by performing 
			// binary search over 0 through the new value of M to
			// determine how many semesters remain.
			long left = 0, right = M, mid = 0;
			while(left <= right) {
				mid = (left + right) / 2;
				if(mid < M) {
					left = mid + 1;
				} else {
					right = mid - 1;
				} 
			}
			System.out.println(mid);
		}
		scan.close();
	}
}
