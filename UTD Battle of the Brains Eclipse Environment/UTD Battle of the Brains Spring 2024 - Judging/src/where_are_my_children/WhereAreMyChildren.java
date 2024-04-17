package where_are_my_children;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class WhereAreMyChildren {
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
				constructorHelper(vals, this.root, this.root.left, 1, true);
				constructorHelper(vals, this.root, this.root.right, 2, false);
			}
			
			this.root.populateMissing();
		}
		
		private void constructorHelper(String[] vals, Node<Long> parent, Node<Long> curr, int i, boolean isLeft) {
			char firstChar = vals[i].charAt(0);
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
				constructorHelper(vals, curr, curr.left, 2*i + 1, true);
				constructorHelper(vals, curr, curr.right, 2*i + 2, false);
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
	
	public static void main(String[] args) throws IOException {
		new WhereAreMyChildren().run();
	}
	
	public void run() throws IOException {
		BufferedReader file = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
		
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
