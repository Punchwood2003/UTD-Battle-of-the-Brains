package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

var file *bufio.Reader = bufio.NewReader(os.Stdin)
var out *bufio.Writer = bufio.NewWriter(os.Stdout)

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

func main() {
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
