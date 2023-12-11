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

func readArrInt(in *bufio.Reader) []int {
	numbs := readLineNumbs(in)
	arr := make([]int, len(numbs))
	for i, n := range numbs {
		val, _ := strconv.Atoi(n)
		arr[i] = val
	}
	return arr
}

func solve() {
	defer out.Flush()

	// Read in the number of test cases
	numTimes := readInt(file)
	for i := 0; i < numTimes; i++ {
		// Read in the two numbers
		numbs := readArrInt(file)
		b := numbs[0]
		h := numbs[1]

		// Calculate the hypotenuse
		sumOfSquares := (b * b) + (h * h)
		intHyp := int(math.Sqrt(float64(sumOfSquares)))
		// Determine if the hypotenuse is an integer
		if intHyp*intHyp == sumOfSquares {
			printf("Yes\n")
		} else {
			printf("No\n")
		}

	}
}

const (
	START_TEST_CASE_NUMBER         = 0
	END_OF_HAND_CRAFTED_TEST_CASES = 2
	NUM_TEST_CASES                 = 10
	MAX_TBH                        = 1000
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
		T := rand.Intn(MAX_TBH) + 1
		fmt.Fprintf(inputWriter, "%d\n", T)
		for i := 0; i < T; i++ {
			b := rand.Intn(MAX_TBH) + 1
			h := rand.Intn(MAX_TBH) + 1
			fmt.Fprintf(inputWriter, "%d %d\n", b, h)
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
