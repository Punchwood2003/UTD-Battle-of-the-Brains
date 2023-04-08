import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.PriorityQueue;
import java.util.TreeSet;

public class BruteForce {
	public static void main(String[] args) throws IOException, MatrixDimensionMismatchException, MatrixDimensionSizeException {
		new BruteForce().run();
	}

	public void run() throws IOException, MatrixDimensionMismatchException, MatrixDimensionSizeException {
		BufferedReader file = new BufferedReader(new FileReader("input.txt"));
		PrintWriter out = new PrintWriter(new File("output.txt"));
		double start = System.currentTimeMillis();

		int n = Integer.parseInt(file.readLine());
		file.close();

		Matrix initial = new Matrix(n, n, generateInitial(n));
		int[][] individualSwaps = generateListOfIndividualSwaps(n);
		TreeSet<Matrix> solutions = getAllSolutions(initial, n, individualSwaps);
		
		double total = ((double) System.currentTimeMillis() - start) / 1000;
		out.println("Time taken: " + total + " seconds.");
		
		int count = 0;
		for(Matrix m : solutions) {
			out.println("Solution " + (++count) + ":\n" + m);
		}
		
		out.close();
	}
	
	public TreeSet<Matrix> getAllSolutions(Matrix initial, int n, int[][] individualSwaps) throws MatrixDimensionMismatchException, MatrixDimensionSizeException {
		TreeSet<Matrix> solutions = new TreeSet<Matrix>();
		PriorityQueue<Query> toSearch = new PriorityQueue<Query>();
		toSearch.add(new Query(initial, n));

		while(!toSearch.isEmpty()) {
			Query currQuery = toSearch.poll();
			Matrix currMat = currQuery.getMatrix();
			int currDepthRemaining = currQuery.getRemainingDepth();

			
			if(currDepthRemaining - 2 == 0) {
				for(int[] swap : individualSwaps) {
					Matrix newMat = currMat.rowSwap(swap[0], swap[1]);
					solutions.add(newMat);
				}
			} else {
				for(int[] swap : individualSwaps) {
					Matrix newMat = currMat.rowSwap(swap[0], swap[1]);
					Query newQuery = new Query(newMat, currDepthRemaining - 1);
					if(!toSearch.contains(newQuery)) {
						toSearch.offer(newQuery);
					}
				}
			}
		}
		
		return solutions;
	}

	public int[][] generateListOfIndividualSwaps(int n) {
		int[][] swaps = new int[n * n][2];
		int index = 0;
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				swaps[index++] = new int[] {i, j};
			}
		}
		return swaps;
	}

	public int[][] generateInitial(int n) {
		int[][] shifts = new int[n][n];
		for(int i = 0; i < n; i++) {
			shifts[0][i] = (i+1);
		}
		for(int i = 1; i < n; i++) {
			shifts[i] = shift(shifts[i-1], 1);
		}
		return shifts;
	}

	private int[] shift(int[] arr, int s) {
		int l = arr.length;
		int[] shifted = new int[l];
		for(int i = 0; i < l; i++) {
			shifted[i] = arr[(i+s) % l];
		}
		return shifted;
	}	
}

class Query implements Comparable<Query> {
	private Matrix matrix;
	private int remainingDepth;

	public Query(Matrix mat, int depth) {
		this.matrix = mat;
		this.remainingDepth = depth;
	}

	public int getRemainingDepth() {
		return this.remainingDepth;
	}

	public Matrix getMatrix() {
		return this.matrix;
	}

	public int compareTo(Query other) {
		int comp = Integer.compare(this.remainingDepth, other.remainingDepth);
		if(comp != 0) {
			return comp;
		} else {
			return this.matrix.compareTo(other.matrix);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Current Depth Remaining: ");
		sb.append(this.remainingDepth);
		sb.append("\n");
		sb.append("Current Matrix:\n");
		sb.append(this.matrix.toString());
		
		return sb.toString();
	}
}

class Matrix implements Comparable<Matrix> {
	private int[][] matrix;
	private int m, n;

	public Matrix(int m, int n, int[][] mat) throws MatrixDimensionMismatchException, MatrixDimensionSizeException {
		if(m != n) {
			throw new MatrixDimensionMismatchException("Error: Dimension 'm' must be equivalent to dimension 'n'."
					+ "\nm := " + m + ", n := " + n + "\n");
		} else if(m < 1 || n < 1) {
			throw new MatrixDimensionSizeException("Error: Dimensions 'm' and 'n' must be greater than or equal to 1."
					+ "\nm := " + m + ", n := " + n + "\n");
		}
		this.m = m;
		this.n = n;
		this.matrix = mat;
	}

	public Matrix rowSwap(int r1, int r2) throws MatrixDimensionMismatchException, MatrixDimensionSizeException {
		int[][] newMatrix = new int[m][n];

		for(int i = 0; i < m; i++) {
			newMatrix[i] = (i == r1) ? matrix[r2] : ((i == r2) ? matrix[r1] : matrix[i]);
		}

		return new Matrix(m, n, newMatrix);
	}

	public Matrix columnSwap(int c1, int c2) throws MatrixDimensionMismatchException, MatrixDimensionSizeException {
		int[][] newMatrix = new int[m][n];

		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				newMatrix[i][j] = (j == c1) ? matrix[i][c2] : ((j == c2) ? matrix[i][c1] : matrix[i][j]);
			}
		}

		return new Matrix(m, n, newMatrix);
	}

	public int compareTo(Matrix other) {
		if(this.m < other.m || this.n < other.n) {
			return -1;
		} else if (this.m > other.m || this.n > other.n) {
			return 1;
		}
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				int comp = Integer.compare(this.matrix[i][j], other.matrix[i][j]);
				if(comp != 0) {
					return comp;
				}
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < m; i++) {
			for(int j = 0; j < n; j++) {
				sb.append(matrix[i][j]);
				sb.append((j != n-1) ? "\t" : (i != m-1) ? "\n" : "");
			}
		}
		sb.append("\n");
		return sb.toString();
	}
}

class MatrixDimensionMismatchException extends Exception {
	public MatrixDimensionMismatchException(String errorMessage) {
		super(errorMessage);
	}
}

class MatrixDimensionSizeException extends Exception {
	public MatrixDimensionSizeException(String errorMessage) {
		super(errorMessage);
	}
}