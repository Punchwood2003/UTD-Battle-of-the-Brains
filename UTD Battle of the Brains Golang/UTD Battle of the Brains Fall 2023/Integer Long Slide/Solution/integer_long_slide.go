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

func readArrInt(in *bufio.Reader) []int {
	numbs := readLineNumbs(in)
	arr := make([]int, len(numbs))
	for i, n := range numbs {
		val, _ := strconv.Atoi(n)
		arr[i] = val
	}
	return arr
}

func main() {
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
