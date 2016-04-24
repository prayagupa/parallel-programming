package main

import "fmt"

// https://gobyexample.com/non-blocking-channel-operations

func main() {
    packagesStream := make(chan string)
    signalsStream := make(chan bool)

    select {
    	case pkg := <- packagesStream:
        	fmt.Println("received package ", pkg)
    	default:
        	fmt.Println("no package received")
    }

    // A non-blocking send works similarly.
    msg := "Item 001 is Picked from reserve 001/ Isle 002."
    select {
    	case packagesStream <- pkg:
        	fmt.Println("sent package ", pkg)
    	default:
        	fmt.Println("no package sent")
    }

    select {
    	case pkg := <- packagesStream:
        	fmt.Println("received package", pkg)
    	case sig := <-signalsStream:
        	fmt.Println("received signal", sig)
    	default:
        	fmt.Println("no activity")
    }
}

