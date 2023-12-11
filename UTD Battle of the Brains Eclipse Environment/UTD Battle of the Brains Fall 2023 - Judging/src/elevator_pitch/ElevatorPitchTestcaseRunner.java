package elevator_pitch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;

public class ElevatorPitchTestcaseRunner {
	private class Request {
		public int startingFloor, destinationFloor;
		private int hashCode;

		public Request(int s, int d) {
			this.startingFloor = s;
			this.destinationFloor = d;
			this.hashCode = Objects.hash(this.startingFloor, this.destinationFloor);
		}

		@Override
		public boolean equals(Object o) {
			if(o == this) {
				return true;
			}

			if(!(o instanceof Request)) {
				return false;
			}

			Request r = (Request) o;

			return this.startingFloor == r.startingFloor && this.destinationFloor == r.destinationFloor;
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public String toString() {
			return String.format("%d->%d", this.startingFloor, this.destinationFloor);
		}
	}

	private enum Direction {
		IDLE, UP, DOWN;
	}

	private class TurnOutput {
		public long turn;
		public boolean isIdle;

		public TurnOutput(long t, boolean i) {
			this.turn = t;
			this.isIdle = i;
		}
	}

	@SuppressWarnings("serial")
	private class NoDuplicates<E extends Comparable<E>> extends PriorityQueue<E> {
		private HashSet<E> uniqueElements;
		private boolean isMinHeap;

		public NoDuplicates() {
			super();
			this.uniqueElements = new HashSet<E>();
		}

		public NoDuplicates(boolean isMinHeap) {
			super(isMinHeap ? (E f1, E f2) -> {
				return f1.compareTo(f2);
			} : (E f1, E f2) -> {
				return -1 * f1.compareTo(f2);
			});
			this.isMinHeap = isMinHeap;
			this.uniqueElements = new HashSet<E>();
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			if (c == null)
				throw new NullPointerException();
			if (c == this)
				throw new IllegalArgumentException();
			boolean modified = false;
			for (E e : c)
				if (this.offer(e))
					modified = true;
			return modified;
		}

		@Override
		public boolean offer(E e) {
			boolean isAdded = false;
			if(!this.uniqueElements.contains(e)) {
				isAdded = super.offer(e);
				this.uniqueElements.add(e);
			}
			return isAdded;
		}

		@Override
		public E poll() {
			E elementRemoved = super.poll();
			this.uniqueElements.remove(elementRemoved);
			return elementRemoved;
		}
	}

	private class Elevator {
		private class DirOutput {
			public Direction startingFloorDir, destinationFloorDir, requestDir;
			public boolean onCurrentPass;
			
			public DirOutput(Direction s, Direction d, Direction r, boolean o) {
				this.startingFloorDir = s;
				this.destinationFloorDir = d;
				this.requestDir = r;
				this.onCurrentPass = o;
			}
		}

		private HashMap<Integer, HashMap<Request, Integer>> unfulfilledRequests;
		private HashMap<Integer, HashMap<Request, Integer>> processingRequests;
		private PriorityQueue<Integer> floorsInCurrentDirection;
		private PriorityQueue<Integer> floorsInOppositeDirection;
		private PriorityQueue<Integer> addToOppositeAfterChange;
		private Queue<Request> requestsToProcess;
		private Request firstRequest;
		private Direction direction;
		private int currentFloor;
		private boolean wasPreviouslyIdle;

		public Elevator() {
			this.unfulfilledRequests = new HashMap<Integer, HashMap<Request, Integer>>();
			this.processingRequests = new HashMap<Integer, HashMap<Request, Integer>>();
			this.floorsInCurrentDirection = new NoDuplicates<Integer>();
			this.floorsInOppositeDirection = new NoDuplicates<Integer>();
			this.addToOppositeAfterChange = new NoDuplicates<Integer>();
			this.requestsToProcess = new LinkedList<Request>();
			this.direction = Direction.IDLE;
			this.currentFloor = 0;
			this.wasPreviouslyIdle = true;
		}

		private DirOutput getRelativeDirection(Request request) {
			Direction startingFloorDir = (request.startingFloor > this.currentFloor) ? 
					Direction.UP : (request.startingFloor < this.currentFloor) ? 
							Direction.DOWN : Direction.IDLE;

			Direction destinationFloorDir = (request.destinationFloor > this.currentFloor) ? 
					Direction.UP : (request.destinationFloor < this.currentFloor) ? 
							Direction.DOWN : Direction.IDLE;

			Direction requestDir = (request.startingFloor - request.destinationFloor < 0) ?
					Direction.UP : Direction.DOWN;

			boolean onCurrentPass = !((requestDir == this.direction) && 
					((requestDir == Direction.UP && request.startingFloor < this.currentFloor) || 
							(requestDir == Direction.DOWN && request.startingFloor > this.currentFloor)));
			
			return new DirOutput(startingFloorDir, destinationFloorDir, requestDir, onCurrentPass);
		}
		
		private void determineCorrectQueue(Request request) {
			DirOutput dirs = this.getRelativeDirection(request);
			if(dirs.onCurrentPass) {
				if(dirs.requestDir == this.direction) {
					if(dirs.startingFloorDir != Direction.IDLE) {
						this.floorsInCurrentDirection.offer(request.startingFloor);
					}
					this.floorsInCurrentDirection.offer(request.destinationFloor);
				} else {
					this.floorsInOppositeDirection.offer(request.startingFloor);
					this.floorsInOppositeDirection.offer(request.destinationFloor);
				}
				
				if(dirs.startingFloorDir == this.direction && dirs.startingFloorDir != Direction.IDLE) {
					this.floorsInCurrentDirection.offer(request.startingFloor);
				}
			} else {
				this.floorsInOppositeDirection.offer(request.startingFloor);
				this.addToOppositeAfterChange.offer(request.startingFloor);
				this.addToOppositeAfterChange.offer(request.destinationFloor);
			}
		}

		public void addRequest(Request request) {
			if(this.direction == Direction.IDLE && wasPreviouslyIdle) {
				this.firstRequest = request;
				this.wasPreviouslyIdle = false;
			}

			if(this.direction == Direction.IDLE) {
				this.requestsToProcess.offer(request);
			} else {
				this.determineCorrectQueue(request);
			}

			// Get the requests corresponding to the current floor
			HashMap<Request, Integer> floor = (this.unfulfilledRequests.containsKey(request.startingFloor))
					? this.unfulfilledRequests.get(request.startingFloor)
							: new HashMap<Request, Integer>();
			if(floor.containsKey(request)) {
				floor.put(request, floor.get(request)+1);
			} else {
				floor.put(request, 1);
			}
			this.unfulfilledRequests.put(request.startingFloor, floor);
		}
		
		private int exit() {
			int numExited = 0;
			if(this.processingRequests.containsKey(this.currentFloor)) {
				HashMap<Request, Integer> floor = this.processingRequests.get(this.currentFloor);
				for(Request request : floor.keySet()) {
					numExited += floor.get(request);
				}
				floor.clear();
				this.processingRequests.remove(this.currentFloor);
			}
			
			if(!this.floorsInCurrentDirection.isEmpty() && this.floorsInCurrentDirection.peek() == this.currentFloor) {
				this.floorsInCurrentDirection.poll();
			}
			return numExited;
		}
		
		private boolean isCase2_1() {
			return !this.processingRequests.isEmpty() || !this.floorsInCurrentDirection.isEmpty();
		}
		
		private boolean isCase2_2() {
			return this.unfulfilledRequests.isEmpty();
		}
		
		private boolean isCase2_3() {
			return this.direction == Direction.IDLE && (!this.processingRequests.isEmpty() || !this.unfulfilledRequests.isEmpty());
		}
		
		private void processQueuedRequests() {
			while(!this.requestsToProcess.isEmpty()) {
				this.determineCorrectQueue(this.requestsToProcess.poll());
			}
		}
		
		private void determineDirection() {
			if(isCase2_1()) {
				// Continue moving in the current direction
			} else if(isCase2_2()) {
				this.direction = Direction.IDLE;
				this.wasPreviouslyIdle = true;
			} else if(isCase2_3()) {
				DirOutput dirs = this.getRelativeDirection(this.firstRequest);
				this.direction = (dirs.startingFloorDir == Direction.IDLE) ? 
						dirs.destinationFloorDir : dirs.startingFloorDir;
				
				if(this.direction == Direction.UP) {
					this.floorsInCurrentDirection = new NoDuplicates<Integer>(true);
					this.floorsInOppositeDirection = new NoDuplicates<Integer>(false);
					this.addToOppositeAfterChange = new NoDuplicates<Integer>(true);
				} else {
					this.floorsInCurrentDirection = new NoDuplicates<Integer>(false);
					this.floorsInOppositeDirection = new NoDuplicates<Integer>(true);
					this.addToOppositeAfterChange = new NoDuplicates<Integer>(false); 
				}
				
				this.processQueuedRequests();
			} else {
				this.direction = (this.direction == Direction.UP) ? 
						Direction.DOWN : Direction.UP;
				
				PriorityQueue<Integer> temp = new NoDuplicates<Integer>(this.direction == Direction.UP);
				
				this.floorsInCurrentDirection = this.floorsInOppositeDirection;
				this.floorsInOppositeDirection = this.addToOppositeAfterChange;
				this.addToOppositeAfterChange = temp;
			}
			
			if(!this.floorsInCurrentDirection.isEmpty() && this.floorsInCurrentDirection.peek() == this.currentFloor) {
				this.floorsInCurrentDirection.poll();
			}
		}
		
		private int board() {
			int numBoarded = 0;
			if(this.unfulfilledRequests.containsKey(this.currentFloor)) {
				HashSet<Request> toRemove = new HashSet<Request>();
				HashMap<Request, Integer> floor = this.unfulfilledRequests.get(this.currentFloor);
				for(Request request : floor.keySet()) {
					DirOutput dirs = this.getRelativeDirection(request);
					if(dirs.destinationFloorDir == this.direction) {
						numBoarded += floor.get(request);
						boolean containsKey = this.processingRequests.containsKey(request.destinationFloor);
						HashMap<Request, Integer> destinationFloor = (containsKey) ? 
								this.processingRequests.get(request.destinationFloor) : new HashMap<Request, Integer>();
						destinationFloor.put(request, destinationFloor.containsKey(request) ? 
								destinationFloor.get(request) + floor.get(request) : floor.get(request));
						this.processingRequests.put(request.destinationFloor, destinationFloor);
						toRemove.add(request);
					}
				}
				for(Request request : toRemove) {
					floor.remove(request);
				}
				if(floor.isEmpty()) {
					this.unfulfilledRequests.remove(this.currentFloor);
				} else {
					this.unfulfilledRequests.put(this.currentFloor, floor);
				}
			}
			return numBoarded;
		}
		
		private TurnOutput handleOutput(long currTurn, boolean isDoneWithInput) {
			if(isDoneWithInput) {
				if(!this.floorsInCurrentDirection.isEmpty()) {
					if(!this.floorsInCurrentDirection.isEmpty()) {
						int nextImportantFloor = this.floorsInCurrentDirection.peek();
						int numTurnsToGetToNextFloor = (this.direction == Direction.UP) ? 
								nextImportantFloor - this.currentFloor : this.currentFloor - nextImportantFloor;
						this.currentFloor = nextImportantFloor;
						return new TurnOutput(currTurn + numTurnsToGetToNextFloor, this.direction == Direction.IDLE);
					}
				}
				return new TurnOutput(currTurn, this.direction == Direction.IDLE);
			} else if(this.direction != Direction.IDLE) {
				this.currentFloor += (this.direction == Direction.UP) ? 1 : -1;
				return new TurnOutput(currTurn + 1, false);
			} else {
				return new TurnOutput(currTurn + 1, true);
			}
		}
		
		public TurnOutput handleTurn(PrintWriter out, long currTurn, boolean isDoneWithInput) {
			int numExited = this.exit();
			this.determineDirection();
			int numBoarded = this.board();
			
			if(numExited != 0 || numBoarded != 0) {
				out.printf("%d: %d, %d, %d\n", currTurn, this.currentFloor, numExited, numBoarded);
			}
			
			return this.handleOutput(currTurn, isDoneWithInput);
		}
	}
	
	public static void main(String[] args) throws IOException {
		new ElevatorPitchTestcaseRunner().run();
	}
	
	public void run() throws IOException {
		runTestCases();
	}
	
	public void runTestCases() throws IOException {
		for(int testCaseNumber = 0; testCaseNumber <= 15; testCaseNumber++) {
			double start = System.currentTimeMillis();
			String inputName = String.format("Elevator_Pitch_Testcases/Input/input%02d.txt", testCaseNumber);
			String outputName = String.format("Elevator_Pitch_Testcases/Output/output%02d.txt", testCaseNumber);
			
			BufferedReader file = new BufferedReader(new FileReader(inputName));
			PrintWriter out = new PrintWriter(new FileWriter(outputName));

			long minNumTurns = Integer.parseInt(file.readLine());
			long currTurn = 1;
			boolean isDoneWithInput = false;
			Elevator elevator = new Elevator();
			while(true) {
				if(!isDoneWithInput && currTurn <= minNumTurns) {
					int numRequests = Integer.parseInt(file.readLine());
					if(numRequests != 0) {
						int[] requests = Arrays.asList(file.readLine().split(" ")).stream().map(str -> Integer.parseInt(str)).mapToInt(Integer::intValue).toArray();
						for(int i = 0; i < numRequests; i++) {
							elevator.addRequest(new Request(requests[2 * i], requests[(2 * i) + 1]));
						}
					} else {
						file.readLine();
					}
				} else if(!isDoneWithInput) {
					file.close();
					isDoneWithInput = true;
				}

				TurnOutput output = elevator.handleTurn(out, currTurn, isDoneWithInput);

				if(isDoneWithInput && output.isIdle) {
					break;
				}
				currTurn = output.turn;
			}
			
			out.close();
			System.out.printf("Testcase %d: %f seconds\n", testCaseNumber, ((double) System.currentTimeMillis() - start) / 1000);
		}
	}
}
