package main

import (
	"bufio"
	"container/heap"
	"fmt"
	"os"
	"strconv"
	"strings"
)

var file *bufio.Reader = bufio.NewReader(os.Stdin)
var out *bufio.Writer = bufio.NewWriter(os.Stdout)

func printf(f string, a ...interface{}) { fmt.Fprintf(out, f, a...) }
func scanf(f string, a ...interface{})  { fmt.Fscanf(file, f, a...) }

func readInt64(in *bufio.Reader) int64 {
	nStr, _ := in.ReadString('\n')
	nStr = strings.ReplaceAll(nStr, "\r", "")
	nStr = strings.ReplaceAll(nStr, "\n", "")
	n, _ := strconv.ParseInt(nStr, 10, 64)
	return n
}

func readLine(in *bufio.Reader) string {
	line, _ := in.ReadString('\n')
	line = strings.ReplaceAll(line, "\r", "")
	line = strings.ReplaceAll(line, "\n", "")
	return line
}

// Object for the priority queue
type Item struct {
	value    int // The floor
	priority int // The priority of the item in the queue.
	index    int // The index of the item in the heap.
}

// Priority queue implementation
type PriorityQueueNoDuplicates struct {
	items        []*Item
	isMinHeap    bool
	uniqueValues map[int]bool
}

func (pq PriorityQueueNoDuplicates) Len() int { return len(pq.items) }

func (pq PriorityQueueNoDuplicates) Less(i, j int) bool {
	if pq.isMinHeap {
		return pq.items[i].priority < pq.items[j].priority
	} else {
		return pq.items[i].priority > pq.items[j].priority
	}
}

func (pq PriorityQueueNoDuplicates) Swap(i, j int) {
	pq.items[i], pq.items[j] = pq.items[j], pq.items[i]
	pq.items[i].index = i
	pq.items[j].index = j
}

func (pq *PriorityQueueNoDuplicates) Push(x interface{}) {
	item := x.(*Item)
	if pq.uniqueValues[item.value] {
		return
	}
	pq.uniqueValues[item.value] = true
	item.index = len(pq.items)
	pq.items = append(pq.items, item)
}

func (pq *PriorityQueueNoDuplicates) Pop() interface{} {
	old := pq.items
	n := len(old)
	item := old[n-1]
	old[n-1] = nil  // avoid memory leak
	item.index = -1 // for safety
	pq.items = old[0 : n-1]
	delete(pq.uniqueValues, item.value)
	return item
}

func (pq *PriorityQueueNoDuplicates) Peek() interface{} {
	return pq.items[0]
}

func (pq *PriorityQueueNoDuplicates) IsEmpty() bool {
	return pq.Len() == 0
}

type Direction int

const (
	IDLE Direction = 0
	UP   Direction = 1
	DOWN Direction = 2
)

type Request struct {
	startingFloor int // The floor the request is made on
	endingFloor   int // The floor the request wants to go to
}

type Elevator struct {
	floorsInCurrentDirection  PriorityQueueNoDuplicates // The floors in the current direction
	floorsInOppositeDirection PriorityQueueNoDuplicates // The floors in the opposite direction
	addToOppositeAfterChange  PriorityQueueNoDuplicates // The floors to add to the opposite direction after the current turn
	unfulfilledRequests       map[int]map[Request]int   // Starting floor -> the request -> the number of that request
	processingRequests        map[int]map[Request]int   // Destination floor -> the request -> the number of that request
	requestsToProcess         []Request                 // The requests to process
	firstRequest              Request                   // The first request to start heading towards ater having been idle
	direction                 Direction                 // The direction the elevator is going in
	currFloor                 int                       // The current floor the elevator is on
	wasPreviouslyIdle         bool                      // Whether the elevator was previously idle
}

func (elevator *Elevator) getRelativeDirection(request Request) (Direction, Direction, Direction, bool) {
	startingFloorDir, destinationFloorDir, requestDir := IDLE, IDLE, DOWN
	onCurrentPass := true

	if request.startingFloor > elevator.currFloor {
		startingFloorDir = UP
	} else if request.startingFloor < elevator.currFloor {
		startingFloorDir = DOWN
	}

	if request.endingFloor > elevator.currFloor {
		destinationFloorDir = UP
	} else if request.endingFloor < elevator.currFloor {
		destinationFloorDir = DOWN
	}

	if request.startingFloor-request.endingFloor < 0 {
		requestDir = UP
	}

	if requestDir == elevator.direction {
		if requestDir == UP && request.startingFloor < elevator.currFloor {
			onCurrentPass = false
		} else if requestDir == DOWN && request.startingFloor > elevator.currFloor {
			onCurrentPass = false
		}
	}

	return startingFloorDir, destinationFloorDir, requestDir, onCurrentPass
}

func (elevator *Elevator) determineCorrectQueue(request Request) {
	startingFloorDir, _, requestDir, onCurrentPass := elevator.getRelativeDirection(request)
	if onCurrentPass {
		if requestDir == elevator.direction {
			if startingFloorDir != IDLE {
				heap.Push(&elevator.floorsInCurrentDirection, &Item{value: request.startingFloor, priority: request.startingFloor})
			}
			heap.Push(&elevator.floorsInCurrentDirection, &Item{value: request.endingFloor, priority: request.endingFloor})
		} else {
			heap.Push(&elevator.floorsInOppositeDirection, &Item{value: request.startingFloor, priority: request.startingFloor})
			heap.Push(&elevator.floorsInOppositeDirection, &Item{value: request.endingFloor, priority: request.endingFloor})
		}

		if startingFloorDir == elevator.direction && startingFloorDir != IDLE {
			heap.Push(&elevator.floorsInCurrentDirection, &Item{value: request.startingFloor, priority: request.startingFloor})
		}
	} else {
		heap.Push(&elevator.floorsInOppositeDirection, &Item{value: request.startingFloor, priority: request.startingFloor})
		heap.Push(&elevator.addToOppositeAfterChange, &Item{value: request.startingFloor, priority: request.startingFloor})
		heap.Push(&elevator.addToOppositeAfterChange, &Item{value: request.endingFloor, priority: request.endingFloor})
	}
}

func (elevator *Elevator) addRequest(request Request) {
	if elevator.direction == IDLE && elevator.wasPreviouslyIdle {
		elevator.firstRequest = request
		elevator.wasPreviouslyIdle = false
	}

	if elevator.direction == IDLE {
		elevator.requestsToProcess = append(elevator.requestsToProcess, request)
	} else {
		elevator.determineCorrectQueue(request)
	}

	floor, ok := elevator.unfulfilledRequests[request.startingFloor]
	if !ok {
		floor = make(map[Request]int)
	}
	floor[request]++
	elevator.unfulfilledRequests[request.startingFloor] = floor
}

func (elevator *Elevator) exit() int {
	numExited := 0
	floor, ok := elevator.processingRequests[elevator.currFloor]
	if ok {
		for request := range floor {
			numExited += floor[request]
		}
		delete(elevator.processingRequests, elevator.currFloor)
	}

	if !elevator.floorsInCurrentDirection.IsEmpty() && elevator.floorsInCurrentDirection.Peek().(*Item).value == elevator.currFloor {
		heap.Pop(&elevator.floorsInCurrentDirection)
	}
	return numExited
}

func (elevator *Elevator) isCase2_1() bool {
	return len(elevator.processingRequests) != 0 || !elevator.floorsInCurrentDirection.IsEmpty()
}

func (elevator *Elevator) isCase2_2() bool {
	return len(elevator.unfulfilledRequests) == 0
}

func (elevator *Elevator) isCase2_3() bool {
	return elevator.direction == IDLE && (len(elevator.processingRequests) != 0 || len(elevator.unfulfilledRequests) != 0)
}

func (elevator *Elevator) processQueuedRequests() {
	for _, request := range elevator.requestsToProcess {
		elevator.determineCorrectQueue(request)
	}
	elevator.requestsToProcess = make([]Request, 0)
}

func (elevator *Elevator) determineDirection() {
	if elevator.isCase2_1() {
		// Continue moving in current direction
	} else if elevator.isCase2_2() {
		elevator.direction = IDLE
		elevator.wasPreviouslyIdle = true
	} else if elevator.isCase2_3() {
		startingDir, destinationDir, _, _ := elevator.getRelativeDirection(elevator.firstRequest)
		elevator.direction = startingDir
		if elevator.direction == IDLE {
			elevator.direction = destinationDir
		}

		if elevator.direction == UP {
			elevator.floorsInCurrentDirection.isMinHeap = true
			elevator.floorsInOppositeDirection.isMinHeap = false
			elevator.addToOppositeAfterChange.isMinHeap = true
		} else {
			elevator.floorsInCurrentDirection.isMinHeap = false
			elevator.floorsInOppositeDirection.isMinHeap = true
			elevator.addToOppositeAfterChange.isMinHeap = false
		}

		elevator.processQueuedRequests()
	} else {
		if elevator.direction == UP {
			elevator.direction = DOWN
		} else {
			elevator.direction = UP
		}

		// Prepare the state of addToOppositeAfterChange
		temp := elevator.floorsInCurrentDirection
		temp.isMinHeap = !temp.isMinHeap

		// Swap the three queues
		elevator.floorsInCurrentDirection = elevator.floorsInOppositeDirection
		elevator.floorsInOppositeDirection = elevator.addToOppositeAfterChange
		elevator.addToOppositeAfterChange = temp
	}
	// Edge case
	if !elevator.floorsInCurrentDirection.IsEmpty() && elevator.floorsInCurrentDirection.Peek().(*Item).value == elevator.currFloor {
		heap.Pop(&elevator.floorsInCurrentDirection)
	}
}

func (elevator *Elevator) board() int {
	numBoarded := 0
	currFloor, ok := elevator.unfulfilledRequests[elevator.currFloor]
	if ok {
		toRemove := make([]Request, 0)
		for request := range currFloor {
			_, destinationDir, _, _ := elevator.getRelativeDirection(request)
			if destinationDir == elevator.direction {
				numBoarded += currFloor[request]
				destinationFloor, ok := elevator.processingRequests[request.endingFloor]
				if !ok {
					destinationFloor = make(map[Request]int)
				}
				destinationFloor[request] += currFloor[request]
				elevator.processingRequests[request.endingFloor] = destinationFloor
				toRemove = append(toRemove, request)
			}
		}
		for _, request := range toRemove {
			delete(currFloor, request)
		}
		if len(currFloor) == 0 {
			delete(elevator.unfulfilledRequests, elevator.currFloor)
		} else {
			elevator.unfulfilledRequests[elevator.currFloor] = currFloor
		}
	}
	return numBoarded
}

func (elevator *Elevator) handleOutput(currTurn int64, isDoneWithInput bool) (int64, bool) {
	if isDoneWithInput {
		if !elevator.floorsInCurrentDirection.IsEmpty() {
			nextImportantFloor := elevator.floorsInCurrentDirection.Peek().(*Item).value
			var numTurnsToGetToNextFloor int
			if elevator.direction == UP {
				numTurnsToGetToNextFloor = nextImportantFloor - elevator.currFloor
			} else {
				numTurnsToGetToNextFloor = elevator.currFloor - nextImportantFloor
			}
			elevator.currFloor = nextImportantFloor
			return currTurn + int64(numTurnsToGetToNextFloor), elevator.direction == IDLE
		}
		return currTurn, elevator.direction == IDLE
	} else if elevator.direction != IDLE {
		delta := 1
		if elevator.direction == DOWN {
			delta = -1
		}
		elevator.currFloor += delta
		return currTurn + 1, false
	} else {
		return currTurn + 1, true
	}
}

func (elevator *Elevator) handleTurn(currTurn int64, isDoneWithInput bool) (int64, bool) {
	numExited := elevator.exit()
	elevator.determineDirection()
	numBoarded := elevator.board()

	if numExited != 0 || numBoarded != 0 {
		printf("%d: %d, %d, %d\n", currTurn, elevator.currFloor, numExited, numBoarded)
	}

	return elevator.handleOutput(currTurn, isDoneWithInput)
}

func main() {
	defer out.Flush()

	elevator := Elevator{
		floorsInCurrentDirection:  PriorityQueueNoDuplicates{isMinHeap: true, uniqueValues: make(map[int]bool)},
		floorsInOppositeDirection: PriorityQueueNoDuplicates{isMinHeap: false, uniqueValues: make(map[int]bool)},
		addToOppositeAfterChange:  PriorityQueueNoDuplicates{isMinHeap: true, uniqueValues: make(map[int]bool)},
		unfulfilledRequests:       make(map[int]map[Request]int),
		processingRequests:        make(map[int]map[Request]int),
		requestsToProcess:         make([]Request, 0),
		direction:                 IDLE,
		currFloor:                 0,
		wasPreviouslyIdle:         true,
	}

	minNumTurns := readInt64(file)
	var currTurn int64
	currTurn = 1
	isDoneWithInput := false
	for true {
		if currTurn <= minNumTurns {
			line := readLine(file)
			if line != "None" {
				pairs := strings.Split(line, ",")
				for _, pair := range pairs {
					nums := strings.Split(pair, "->")
					startingFloor, _ := strconv.Atoi(nums[0])
					endingFloor, _ := strconv.Atoi(nums[1])
					elevator.addRequest(Request{startingFloor, endingFloor})
				}
			}
		} else if !isDoneWithInput {
			isDoneWithInput = true
		}

		var isIdle bool
		currTurn, isIdle = elevator.handleTurn(currTurn, isDoneWithInput)

		if isDoneWithInput && isIdle {
			break
		}
	}
}
