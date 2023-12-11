package main

import (
	"bufio"
	"fmt"
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

func readLineNumbs(in *bufio.Reader) []string {
	line, _ := in.ReadString('\n')
	line = strings.ReplaceAll(line, "\r", "")
	line = strings.ReplaceAll(line, "\n", "")
	numbs := strings.Split(line, " ")
	return numbs
}

func min(a, b int) int {
	if a < b {
		return a
	}
	return b
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}

func solve() {
	defer out.Flush()
	// Read in input
	line := readLineNumbs(file)
	col := int(line[0][0]-'a') + 1
	row, _ := strconv.Atoi(line[1])

	// Determine the number of squares that can be attacked by a bishop
	// placed at the given position on a standard chessboard.
	topLeft := min(row, col) - 1
	topRight := min(row, 9-col) - 1
	bottomLeft := 8 - max(row, 9-col)
	bottomRight := 8 - max(row, col)

	// Print the Results
	printf("%d\n", topLeft+topRight+bottomLeft+bottomRight+1)
}

const (
	START_TEST_CASE_NUMBER         = 0
	END_OF_HAND_CRAFTED_TEST_CASES = 2
	NUM_TEST_CASES                 = 10
)

func main() {
	// Used to determine which test cases have already been covered
	unique := make(map[rune]map[int]bool)
	for i := 'a'; i <= 'h'; i++ {
		unique[i] = make(map[int]bool)
	}
	// Mark the two hand crafted test cases as already covered
	unique['d'][5] = true
	unique['a'][1] = true

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

		// Generate the column and the row
		col := rune(rand.Intn(8) + 'a')
		row := rand.Intn(8) + 1
		for unique[col][row] {
			col = rune(rand.Intn(8) + 'a')
			row = rand.Intn(8) + 1
		}
		unique[col][row] = true
		fmt.Fprintf(inputWriter, "%c %d\n", col, row)

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
