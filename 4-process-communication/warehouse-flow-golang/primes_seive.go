package main

import ( "math")

func IsPrime(n int) bool {
	if n < 2 {
		return false
	}
	if n == 2 {
		return true
	}
	if n & 1 == 0 {
		return false
	}
	s := int(math.Sqrt(float64(n)))
	for i := 3; i <= s; i += 2 {
		if n % i == 0 {
			return false
		}
	}
	return true
}

func main() {
	primeStream1:= make(chan int)
	
	go func() {
		for number := 0; number < 10; number++ {
			if IsPrime(number) {
				primeStream1 <- number
			}
		}
		close(primeStream1)
	}()
	
	for primeNumber := range primeStream1 {
		println(primeNumber)	
	}
}

