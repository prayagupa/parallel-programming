package main

import "fmt"

func sum(array []int, someChannel chan int) {
	sum := 0
	for _, v := range array {
		sum += v
	}
	someChannel <- sum // send sum to someChannel
}

func main() {
	array := []int { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }

	someChannel := make(chan int)

	go sum(array[:len(array)/2], someChannel)
	go sum(array[len(array)/2:], someChannel)
	x, y := <- someChannel, <- someChannel // receive from someChannel

	fmt.Println(x, y, x+y)
}
