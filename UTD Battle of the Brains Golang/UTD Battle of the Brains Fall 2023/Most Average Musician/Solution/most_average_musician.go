package main

import (
	"bufio"
	"fmt"
	"math"
	"os"
	"strconv"
	"strings"
)

var file *bufio.Reader = bufio.NewReader(os.Stdin)
var out *bufio.Writer = bufio.NewWriter(os.Stdout)

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

func main() {
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
