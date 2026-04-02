# Virtual Threads in Java 21

## Table of Contents
1. [Platform Threads — Internal Working](#1-platform-threads--internal-working)
2. [Virtual Threads — Internal Working](#2-virtual-threads--internal-working)
3. [Platform vs Virtual — Side-by-Side](#3-platform-vs-virtual--side-by-side)
4. [When to Use Virtual Threads](#4-when-to-use-virtual-threads)
5. [When NOT to Use Virtual Threads](#5-when-not-to-use-virtual-threads)
6. [Benchmark](#6-benchmark)
7. [Code Examples](#7-code-examples)

---

## 1. Platform Threads — Internal Working

A **platform thread** is a thin Java wrapper around a native OS thread.

```mermaid
graph TD
    A[Java Application] --> B["java.lang.Thread\n(Platform Thread)"]
    B -->|1-to-1 mapped| C["OS Kernel Thread\n(managed by OS scheduler)"]

    style A fill:#4A90D9,color:#fff
    style B fill:#2E7D32,color:#fff
    style C fill:#6A1B9A,color:#fff
```

### Lifecycle & Cost
| Property                  | Detail                                                   |
|---------------------------|----------------------------------------------------------|
| **Stack size**            | ~1 MB reserved per thread (configurable via `-Xss`)      |
| **Creation cost**         | ~1 ms OS syscall + memory allocation                     |
| **Context switch**        | OS-level, ~1–10 µs, saves/restores full CPU register set |
| **Max practical threads** | ~10,000 before memory/scheduling pressure                |
| **Blocking behaviour**    | Blocks the OS thread — carrier is idle, CPU wasted       |

### What happens on a blocking call (platform thread)

```mermaid
sequenceDiagram
    participant App
    participant PT as Platform Thread
    participant OS as OS Kernel Thread
    participant IO as Blocking I/O

    App->>PT: submit task
    PT->>OS: mount (1-to-1)
    OS->>IO: blocking call (e.g. DB query)
    Note over OS,IO: OS thread PARKED 💤<br/>kernel stack held<br/>thread slot consumed
    IO-->>OS: response (eventually)
    OS-->>PT: resume
    PT-->>App: task done
```

The OS thread is **suspended** but still occupies a thread slot in the thread pool. With a fixed pool of N threads and N+ blocking tasks, tasks queue up — throughput collapses.

---

## 2. Virtual Threads — Internal Working

A **virtual thread** is a lightweight thread managed entirely by the JVM. Many virtual threads are **multiplexed** onto a small pool of OS carrier threads (ForkJoinPool, default size = number of CPU cores).

```mermaid
graph LR
    VT1["Virtual Thread 1"] --> JVMS
    VT2["Virtual Thread 2"] --> JVMS
    VT3["Virtual Thread 3"] --> JVMS
    VTN["Virtual Thread N …"] --> JVMS

    JVMS["JVM Scheduler\n(mount / unmount)"]

    JVMS --> CT1["Carrier OS Thread 1"]
    JVMS --> CT2["Carrier OS Thread 2"]
    JVMS --> CTK["Carrier OS Thread #cores …"]

    subgraph FJP["ForkJoinPool (carrier pool)"]
        CT1
        CT2
        CTK
    end

    style JVMS fill:#E65100,color:#fff
    style FJP fill:#1565C0,color:#fff
    style VT1 fill:#2E7D32,color:#fff
    style VT2 fill:#2E7D32,color:#fff
    style VT3 fill:#2E7D32,color:#fff
    style VTN fill:#2E7D32,color:#fff
    style CT1 fill:#6A1B9A,color:#fff
    style CT2 fill:#6A1B9A,color:#fff
    style CTK fill:#6A1B9A,color:#fff
```

### Key Concepts

#### Mount / Unmount
- When a virtual thread is **scheduled**, the JVM **mounts** it onto a carrier OS thread.
- When it hits a **blocking point** (I/O, `sleep`, `lock`), the JVM **unmounts** it — the carrier is freed immediately to run another virtual thread.
- The virtual thread's stack is stored on the Java **heap** (~few KB, grows as needed).

```mermaid
sequenceDiagram
    participant C1 as Carrier-1 (OS thread)
    participant C2 as Carrier-2 (OS thread)
    participant VT1 as Virtual Thread 1
    participant VT2 as Virtual Thread 2
    participant VT3 as Virtual Thread 3
    participant VT4 as Virtual Thread 4
    participant IO as Blocking I/O

    C1->>VT1: mount
    C2->>VT2: mount
    VT1->>IO: blocking call
    Note over VT1: unmount → stack parked on heap (~KB)
    C1->>VT3: mount (carrier reused immediately)
    VT2->>IO: blocking call
    Note over VT2: unmount → stack parked on heap (~KB)
    C2->>VT4: mount (carrier reused immediately)
    IO-->>VT1: I/O done → re-queue
    C1->>VT1: re-mount & resume
```

#### Stack Storage
|                | Platform Thread          | Virtual Thread             |
|----------------|--------------------------|----------------------------|
| Stack location | Native memory (off-heap) | Java heap                  |
| Stack size     | ~1 MB fixed              | ~few KB, grows dynamically |
| GC managed     | ❌ No                     | ✅ Yes                      |

#### Scheduler
- Built on `ForkJoinPool` in FIFO mode (not work-stealing for virtual threads).
- Configurable via system property: `-Djdk.virtualThreadScheduler.parallelism=N`

#### Pinning (watch out!)
A virtual thread becomes **pinned** to its carrier and cannot be unmounted when:
- Inside a `synchronized` block/method (use `ReentrantLock` instead)
- Calling a native method or foreign function

```java
// ❌ Pins the carrier — avoid for long blocking ops
synchronized (lock) {
    Thread.sleep(1000); // carrier blocked!
}

// ✅ Does not pin
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    Thread.sleep(1000); // carrier freed during sleep
} finally {
    lock.unlock();
}
```

---

## 3. Platform vs Virtual — Side-by-Side

| Aspect              | Platform Thread             | Virtual Thread                             |
|---------------------|-----------------------------|--------------------------------------------|
| Mapping             | 1 Java thread = 1 OS thread | N Java threads = ~#cores OS threads        |
| Stack memory        | ~1 MB/thread (native)       | ~few KB/thread (heap)                      |
| Creation cost       | High (OS syscall)           | Very low (heap allocation)                 |
| Context switch      | OS-level (~µs)              | JVM-level (cheaper)                        |
| Max practical count | ~10 K                       | Millions                                   |
| Blocking behaviour  | Parks OS thread             | Unmounts, frees carrier                    |
| Best for            | CPU-bound tasks             | I/O-bound / blocking tasks                 |
| `synchronized`      | Fine                        | Causes pinning — prefer `ReentrantLock`    |
| Thread locals       | Supported                   | Supported (prefer `ScopedValue` in future) |
| Debugging           | Mature tooling              | Improving (JDK 21+)                        |

---

## 4. When to Use Virtual Threads

✅ **Use virtual threads when your tasks spend most time blocking:**

| Use Case                                | Why Virtual Threads Help                                            |
|-----------------------------------------|---------------------------------------------------------------------|
| HTTP servers (many concurrent requests) | Each request can block on DB/API without consuming an OS thread     |
| Database query handlers                 | JDBC calls block; virtual threads unmount while waiting             |
| REST/gRPC clients                       | Network I/O dominates; thousands of concurrent calls become trivial |
| File I/O pipelines                      | Reading/writing files blocks; virtual threads handle fan-out easily |
| Message queue consumers                 | Blocking poll/consume operations park cheaply                       |
| Scheduled/batch jobs                    | Thousands of small tasks with sleep/wait intervals                  |

### Rule of Thumb
> **If your thread spends more time waiting than computing, virtual threads are the right tool.**

---

## 5. When NOT to Use Virtual Threads

❌ **Avoid virtual threads when:**

| Scenario                                                | Reason                                                                                                                        |
|---------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| CPU-intensive tasks (encryption, image processing, ML)  | Blocking frees the carrier — but if you never block, there's no benefit. Use a fixed platform thread pool sized to CPU cores. |
| Heavy use of `synchronized` with long critical sections | Pinning prevents unmount; defeats the purpose                                                                                 |
| Code that relies on `ThreadLocal` for large caches      | Each virtual thread can have its own `ThreadLocal`; millions of threads = memory pressure                                     |
| Real-time / latency-sensitive code                      | JVM scheduling adds non-determinism                                                                                           |

---

## 6. Benchmark

Results from [`PthreadVthreadComparison.java`](PthreadVthreadComparison.java) — Java 21, Apple M-series, 10 CPU cores.

### Scenario 1 — Equal Pool Size (100 tasks, 500 ms blocking, pool = 100)

| Thread Type      | Time     |
|------------------|----------|
| Platform threads | 525 ms   |
| Virtual threads  | 516 ms   |
| **Speedup**      | **1.0×** |

> Pool is sized equal to task count so all platform threads run in parallel — parity is expected. The gap only opens when the pool is **constrained**.

### Scenario 2 — Constrained Platform Pool (1,000 tasks, 200 ms blocking, pool = 20)

| Thread Type      | Time                                  |
|------------------|---------------------------------------|
| Platform threads | 10,176 ms                             |
| Virtual threads  | 208 ms                                |
| **Speedup**      | **48.9× faster with virtual threads** |

> Platform threads must process 1,000 tasks in batches of 20 (50 rounds × 200 ms = ~10 s). Virtual threads unmount on every block, completing all tasks in a single ~200 ms wave.

### Scenario 3 — Massive Load (100,000 tasks, 50 ms blocking, pool = 200)

| Thread Type      | Time                                  |
|------------------|---------------------------------------|
| Platform threads | 26,536 ms                             |
| Virtual threads  | 370 ms                                |
| **Speedup**      | **71.7× faster with virtual threads** |

> 100k tasks processed in 500 rounds of 200 by platform threads (~26 s total). Virtual threads handle all 100k concurrently with trivial overhead.

```mermaid
xychart-beta
    title "Execution Time: Platform vs Virtual Threads (ms, lower is better)"
    x-axis ["Scenario 1 (equal pool)", "Scenario 2 (constrained)", "Scenario 3 (massive load)"]
    y-axis "Time (ms)" 0 --> 27000
    bar [525, 10176, 26536]
    bar [516, 208, 370]
```

### Memory Footprint

| Thread Count | Platform Threads   | Virtual Threads |
|--------------|--------------------|-----------------|
| 1,000        | ~1 GB native stack | ~10 MB heap     |
| 10,000       | OOM / OS limit     | ~100 MB heap    |
| 100,000      | Not feasible       | ~1 GB heap      |

---

## 7. Code Examples

See [`VirtualThreadExample.java`](VirtualThreadExample.java) and [`PthreadVthreadComparison.java`](PthreadVthreadComparison.java)

### Quick Start

```bash
# Virtual thread demos
./gradlew run --args="vthreads.VirtualThreadExample"

# Platform vs virtual benchmark
./gradlew run --args="vthreads.PthreadVthreadComparison"
```

### Minimum Requirements
- Java 21+ (virtual threads are GA since JDK 21)
- Gradle 8+

