package where_are_my_children;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class WhereAreMyChildrenTestcaseGenerator {
	public static void main(String[] args) throws IOException {
		new WhereAreMyChildrenTestcaseGenerator().run();
	}
	
	public final String JUDGE_INPUT_FORMAT = "Where Are My Children/Input/input%02d.txt";
	public final String JUDGE_OUTPUT_FORMAT = "Where Are My Children/Output/output%02d.txt";
	
	public final int NUM_TESTCASES = 20;
	public final int MAX_N = 20;
	public final int MIN_N = 1;
	public final int MIN_M = 1;
	
	public final long MAX_VI = Long.MAX_VALUE;
	public final long MIN_VI = 1;
	
	public final double REMOVE_CHANCE = 0.05;
	public final double MISSING_CHANCE = 0.2;
	
	public void run() throws IOException {
		generateTestcases();
		solveTestcases();
	}
	
	public void generateTestcases() throws IOException {
		for(int tc = 2; tc < NUM_TESTCASES; tc++) {
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_INPUT_FORMAT, tc)));
			
			int n = (int) (Math.random() * (MAX_N - MIN_N + 1)) + MIN_N;
			out.println(n);
			
			TestcaseBST bst;
			HashSet<Long> missing;
			do {
				bst = new TestcaseBST(n);
				missing = bst.messUpTree();
			} while(missing.size() == 0);
			out.println(bst.toString());
			out.println(missing.size());
			
			ArrayList<Long> missingShuffled = new ArrayList<Long>(missing);
			Collections.shuffle(missingShuffled);
			out.println(missingShuffled.toString().replaceAll("[\\[\\],]", ""));
			
			out.close();
		}
	}
	
	private class TestcaseBST {
		public Node<Long> root;
		
		public TestcaseBST(int n) {
			long currVal = (long) (Math.random() * (MAX_VI - MIN_VI)) + MIN_VI;
			root = new Node<Long>(currVal);
			if(n > 1) {
				constructorHelper(root, MIN_VI, currVal-1, true, 2, n);
				constructorHelper(root, currVal + 1, MAX_VI, false, 2, n);
			}
		}
		
		private void constructorHelper(Node<Long> parent, long low, long high, boolean isLeft, int currDepth, int targetDepth) {
			Node<Long> curr;
			long currVal;
			if(high <= low) {
				currVal = low;
				curr = new Node<Long>('X');
			} else {
				currVal = (long) (Math.random() * (high - low + 1)) + low;
				curr = new Node<Long>(currVal);
			}
			
			if(isLeft) {
				parent.left = curr;
			} else {
				parent.right = curr;
			}
			
			if(currDepth != targetDepth) {
				constructorHelper(curr, low, currVal-1, true, currDepth + 1, targetDepth);
				constructorHelper(curr, currVal+1, high, false, currDepth + 1, targetDepth);
			}
		}
		
		public HashSet<Long> messUpTree() {
			HashSet<Long> missing = new HashSet<Long>();
			if(Math.random() < MISSING_CHANCE) {
				root.sentinel = MISSING;
				missing.add(root.data);
				root.data = null;
			}
			if(root.left != null) {
				missing.addAll(messUpTreeHelper(root.left));
				missing.addAll(messUpTreeHelper(root.right));
			}
			return missing;
		}
		
		private HashSet<Long> messUpTreeHelper(Node<Long> curr) {
			HashSet<Long> missing = new HashSet<Long>();
			if(Math.random() < REMOVE_CHANCE) {
				removeHelper(curr);
				return missing;
			}
			if(Math.random() < MISSING_CHANCE) {
				curr.sentinel = MISSING;
				missing.add(curr.data);
				curr.data = null;
			}
			if(curr.left != null) {
				missing.addAll(messUpTreeHelper(curr.left));
				missing.addAll(messUpTreeHelper(curr.right));
			}
			return missing;
		}
		
		private void removeHelper(Node<Long> curr) {
			curr.sentinel = EMPTY;
			curr.data = null;
			if(curr.left != null) {
				removeHelper(curr.left);
				removeHelper(curr.right);
			}
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			Queue<Node<Long>> toSearch = new LinkedList<Node<Long>>();
			toSearch.offer(root);
			while(!toSearch.isEmpty()) {
				Node<Long> curr = toSearch.poll();
				if(curr.sentinel == EMPTY || curr.sentinel == MISSING) {
					sb.append(curr.sentinel);
				} else {
					sb.append(curr.data.toString());
				}
				sb.append(' ');
				if(curr.left != null) {
					toSearch.offer(curr.left);
					toSearch.offer(curr.right);
				}
			}
			
			return sb.toString().trim();
		}
	}
	
	public void solveTestcases() throws IOException {
		for(int tc = 0; tc < NUM_TESTCASES; tc++) {
			BufferedReader file = new BufferedReader(new FileReader(String.format(JUDGE_INPUT_FORMAT, tc)));
			PrintWriter out = new PrintWriter(new File(String.format(JUDGE_OUTPUT_FORMAT, tc)));
			
			file.readLine();
			String original = file.readLine();
			file.readLine();
			String missing = file.readLine();
			file.close();
			
			BST bst = new BST(original);
			bst.fillInMissing(missing);
			out.println(bst.toString());
			out.close();
		}
	}
	
	public final char MISSING = '_';
	public final char EMPTY = 'X';
	
	private class Node<T> {
		public T data;
		public char sentinel;
		public int subtreeMissing;
		public Node<T> left, right;
		
		public Node(T d) {
			this.data = d;
		}
		
		public Node(char sentinel) {
			this.sentinel = sentinel;
		}
		
		public void populateMissing() {
			this.populateMissingHelper();
		}
		
		private int populateMissingHelper() {
			if(this.sentinel == EMPTY) {
				return 0;
			}
			int numLeft = (left == null) ? 0 : left.populateMissingHelper();
			int numRight = (right == null) ? 0 : right.populateMissingHelper();
			this.subtreeMissing = numLeft + numRight + ((sentinel == MISSING) ? 1 : 0);
			return this.subtreeMissing;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			Queue<Node<T>> toSearch = new LinkedList<Node<T>>();
			toSearch.offer(this);
			while(!toSearch.isEmpty()) {
				Node<T> curr = toSearch.poll();
				if(curr.sentinel == EMPTY) {
					sb.append('X');
				} else {
					sb.append(curr.data.toString());
				}
				sb.append(' ');
				if(curr.left != null) {
					toSearch.offer(curr.left);
					toSearch.offer(curr.right);
				}
			}
			
			return sb.toString().trim();
		}
	}
	
	private class BST {
		public Node<Long> root;
		
		public BST(String line) {
			String[] vals = line.split(" ");
			
			char firstChar = vals[0].charAt(0);
			if(firstChar == MISSING || firstChar == EMPTY) {
				root = new Node<Long>(firstChar);
			} else {
				root = new Node<Long>(Long.parseLong(vals[0]));
			}
			
			if(vals.length > 1) {
				constructorHelper(vals, this.root, 1, true);
				constructorHelper(vals, this.root, 2, false);
			}
			
			this.root.populateMissing();
		}
		
		private void constructorHelper(String[] vals, Node<Long> parent, int i, boolean isLeft) {
			char firstChar = vals[i].charAt(0);
			Node<Long> curr;
			if(firstChar == MISSING || firstChar == EMPTY) {
				curr = new Node<Long>(firstChar);
			} else {
				curr = new Node<Long>(Long.parseLong(vals[i]));
			}
			
			if(isLeft) {
				parent.left = curr;
			} else {
				parent.right = curr;
			}
			
			if(2 * i + 1 < vals.length) {
				constructorHelper(vals, curr, 2*i + 1, true);
				constructorHelper(vals, curr, 2*i + 2, false);
			}
		}
		
		public void fillInMissing(String line) {
			long[] vals = Arrays.asList(line.split(" ")).stream().map(str -> Long.parseLong(str)).mapToLong(Long::longValue).toArray();
			Arrays.sort(vals);
			fillInMissingHelper(vals, this.root, 0, vals.length);
		}
		
		private void fillInMissingHelper(long[] vals, Node<Long> curr, int i, int j) {
			if(curr.left == null) {
				if(curr.sentinel == MISSING) {
					curr.data = vals[i];
				}
				return;
			}
			if(curr.left.subtreeMissing > 0) {
				fillInMissingHelper(vals, curr.left, i, i+curr.left.subtreeMissing);
			}
			int rightStart = i+curr.left.subtreeMissing;
			if(curr.sentinel == MISSING) {
				curr.data = vals[rightStart];
				rightStart++;
			}
			if(curr.right.subtreeMissing > 0) {
				fillInMissingHelper(vals, curr.right, rightStart, j);
			}
		}
		
		public String toString() {
			return this.root.toString();
		}
	}
}
