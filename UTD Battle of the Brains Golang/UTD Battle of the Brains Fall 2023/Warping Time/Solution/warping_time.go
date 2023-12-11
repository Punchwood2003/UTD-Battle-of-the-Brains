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

func readArrInt(in *bufio.Reader) []int {
	numbs := readLineNumbs(in)
	arr := make([]int, len(numbs))
	for i, n := range numbs {
		val, _ := strconv.Atoi(n)
		arr[i] = val
	}
	return arr
}

const MAX_INTEGER = (1 << 31) - 1

func main() {
	defer out.Flush()

	NK := readArrInt(file)
	N := NK[0]
	K := NK[1]
	A := readArrInt(file)

	dp := make([]int, N)
	for i := range dp {
		dp[i] = MAX_INTEGER
	}
	dp[0] = 0

	for i := 1; i <= N; i++ {
		numTunrnsToCurrDist := dp[i-1]
		if numTunrnsToCurrDist == MAX_INTEGER {
			continue
		}
		for j := 0; j < K; j++ {
			Aj := A[j]
			newDist := int64(i + Aj)
			if newDist <= int64(N) && numTunrnsToCurrDist+1 < dp[int(newDist-1)] {
				dp[int(newDist-1)] = numTunrnsToCurrDist + 1
			}
			newDist = int64(i * Aj)
			if newDist <= int64(N) && numTunrnsToCurrDist+1 < dp[int(newDist-1)] {
				dp[int(newDist-1)] = numTunrnsToCurrDist + 1
			}
		}
	}
	if dp[N-1] == MAX_INTEGER {
		printf("-1\n")
	} else {
		printf("%d\n", dp[N-1])
	}
}
