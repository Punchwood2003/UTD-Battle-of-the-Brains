package main

import (
	"bufio"
	"fmt"
	"math"
	"math/rand"
	"os"
	"strconv"
	"strings"
)

var testCaseNumber int

var inputFile *os.File
var outputFile *os.File

var file *bufio.Reader
var out *bufio.Writer

func printf(f string, a ...interface{}) { fmt.Fprintf(out, f, a...) }
func scanf(f string, a ...interface{})  { fmt.Fscanf(file, f, a...) }

func readInt(in *bufio.Reader) int {
	nStr, _ := in.ReadString('\n')
	nStr = strings.ReplaceAll(nStr, "\r", "")
	nStr = strings.ReplaceAll(nStr, "\n", "")
	n, _ := strconv.Atoi(nStr)
	return n
}

func readLineNumbs(in *bufio.Reader) []string {
	line, _ := in.ReadString('\n')
	line = strings.ReplaceAll(line, "\r", "")
	line = strings.ReplaceAll(line, "\n", "")
	numbs := strings.Split(line, " ")
	return numbs
}

func readArrInt64(in *bufio.Reader) []int64 {
	numbs := readLineNumbs(in)
	arr := make([]int64, len(numbs))
	for i, n := range numbs {
		val, _ := strconv.ParseInt(n, 10, 64)
		arr[i] = val
	}
	return arr
}

func solve() {
	defer out.Flush()

	// Read in the number of graduates
	numGraduates := readInt(file)
	// Read in their grades
	grades := readArrInt64(file)
	// Define aloofness array
	aloofness := make([]int64, numGraduates)
	// Calculate aloofness
	for i := 0; i < numGraduates; i++ {
		for j := 0; j < numGraduates; j++ {
			if i != j {
				aloofness[i] += int64(math.Abs(float64(grades[i] - grades[j])))
			}
		}
	}
	// Find the minimum aloofness
	minAloofness := aloofness[0]
	minIndex := 0
	for i := 1; i < numGraduates; i++ {
		if aloofness[i] < minAloofness {
			minAloofness = aloofness[i]
			minIndex = i
		}
	}

	// Make sure that the minimum aloofness is unique
	unique := true
	for i := 0; i < numGraduates; i++ {
		if i != minIndex && aloofness[i] == minAloofness {
			unique = false
			break
		}
	}

	// Print the result
	if unique {
		printf("%d\n", minIndex)
	} else {
		printf("-1\n")
	}
}

const (
	START_TEST_CASE_NUMBER         = 0
	END_OF_HAND_CRAFTED_TEST_CASES = 6
	NUM_TEST_CASES                 = 15
	MAX_N                          = 10e3
	MAX_GRADE                      = 10e6
)

func main() {
	// Solve all the hand crafted test cases
	for testCaseNumber = START_TEST_CASE_NUMBER; testCaseNumber < END_OF_HAND_CRAFTED_TEST_CASES; testCaseNumber++ {
		inputFile, _ = os.Open(fmt.Sprintf("../input/input%02d.txt", testCaseNumber))
		outputFile, _ = os.Create(fmt.Sprintf("../output/output%02d.txt", testCaseNumber))
		file = bufio.NewReader(inputFile)
		out = bufio.NewWriter(outputFile)

		solve()

		inputFile.Close()
		outputFile.Close()
	}

	// Generate the random test cases
	for testCaseNumber = END_OF_HAND_CRAFTED_TEST_CASES; testCaseNumber < NUM_TEST_CASES; testCaseNumber++ {
		inputFile, _ = os.Create(fmt.Sprintf("../input/input%02d.txt", testCaseNumber))
		inputWriter := bufio.NewWriter(inputFile)

		// Generate the test case
		n := rand.Intn(MAX_N) + 1
		fmt.Fprintf(inputWriter, "%d\n", n)
		for i := 0; i < n; i++ {
			if i != n-1 {
				fmt.Fprintf(inputWriter, "%d ", rand.Intn(MAX_GRADE))
			} else {
				fmt.Fprintf(inputWriter, "%d\n", rand.Intn(MAX_GRADE))
			}
		}

		// Write all the input to the file
		inputWriter.Flush()
		inputFile.Close()

		outputFile, _ = os.Create(fmt.Sprintf("../output/output%02d.txt", testCaseNumber))
		inputFile, _ = os.Open(fmt.Sprintf("../input/input%02d.txt", testCaseNumber))
		file = bufio.NewReader(inputFile)
		out = bufio.NewWriter(outputFile)

		solve()

		inputFile.Close()
		outputFile.Close()
	}
}
