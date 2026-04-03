# Synchronous Parallelism

> **Audience:** L6+ engineers reasoning about coordination, shared-state correctness, and synchronisation overhead at scale.  
> **Context:** Chapter 6 of the parallel programming series — how threads safely share state and synchronise progress without deadlock, livelock, or starvation.

---

## Table of Contents

1. [What is Synchronous Parallelism?](#1-what-is-synchronous-parallelism)
2. [The Java Memory Model — Why Synchronisation Exists](#2-the-java-memory-model--why-synchronisation-exists)
3. [Mutex — Exclusive Access](#3-mutex--exclusive-access)
4. [ReentrantLock — Structured Locking](#4-reentrantlock--structured-locking)
5. [Semaphore — Counted Access](#5-semaphore--counted-access)
6. [Monitor — Mutex + Condition](#6-monitor--mutex--condition)
7. [Barrier Synchronisation](#7-barrier-synchronisation)
8. [Lock Contention & Performance](#8-lock-contention--performance)
9. [Synchronisation in the LLM Era](#9-synchronisation-in-the-llm-era)
10. [L6+ Design Trade-offs](#10-l6-design-trade-offs)
11. [Key Decision Framework](#11-key-decision-framework)
12. [Further Reading](#12-further-reading)

---

## 1. What is Synchronous Parallelism?

**Synchronous parallelism** is parallelism with coordination — multiple threads execute simultaneously but must agree on ordering at certain points to maintain correctness over shared state.

```mermaid
graph LR
    subgraph Unsynchronised["❌ Unsynchronised — Race Condition"]
        T1A["Thread A\nread x=0\nwrite x=1"] 
        T2A["Thread B\nread x=0\nwrite x=1"]
        T1A & T2A -->|"interleaved writes"| SRA["x = 1\n(lost update)"]
    end

    subgraph Synchronised["✅ Synchronised — Correct"]
        T1B["Thread A\nlock → read x=0\nwrite x=1 → unlock"]
        T2B["Thread B\nwaits for lock\nread x=1\nwrite x=2 → unlock"]
        T1B -->|"sequential via lock"| T2B
        T2B --> SRB["x = 2 ✓"]
    end
```

**The core trade-off:**

| | No synchronisation | Full synchronisation |
|---|---|---|
| **Throughput** | Maximum | Reduced (serialised critical sections) |
| **Correctness** | Undefined (data races) | Guaranteed |
| **Complexity** | Low | High (deadlock, priority inversion) |

> **L6+ framing:** synchronisation is a form of *intentional serialisation*. Every lock is a bottleneck you are accepting. The goal is to make critical sections as narrow as possible and to eliminate shared mutable state entirely where feasible.

---

## 2. The Java Memory Model — Why Synchronisation Exists

Without synchronisation, the JVM and CPU are free to **reorder** instructions and keep values in **per-CPU caches** — a thread's write may not be visible to another thread at all.

```mermaid
sequenceDiagram
    participant CPU0 as CPU 0 (Thread A)
    participant L1A as L1 Cache (CPU 0)
    participant RAM as Main Memory
    participant L1B as L1 Cache (CPU 1)
    participant CPU1 as CPU 1 (Thread B)

    CPU0->>L1A: write x = 1
    Note over L1A,RAM: ⚠️ not flushed yet
    CPU1->>L1B: read x → 0 (stale!)
    Note over L1A,RAM: sync / volatile / lock flushes cache
    L1A->>RAM: flush x = 1
    RAM->>L1B: invalidate, reload x = 1
    CPU1->>L1B: read x → 1 ✓
```

**Key JMM guarantees (`jsr-133`):**

| Mechanism | Visibility guarantee | Ordering guarantee |
|---|---|---|
| `volatile` read/write | Yes — flushes to main memory | Prevents reordering across the volatile access |
| `synchronized` block | Yes — on entry (read) and exit (write) | Full happens-before between lock/unlock pairs |
| `java.util.concurrent.locks.Lock` | Yes — equivalent to `synchronized` | Full happens-before |
| `final` field (after construction) | Yes — after object is published safely | Constructor writes visible to all readers |

References:  
- [JSR-133 FAQ — reordering](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html#reordering)  
- [Java 8 in Action — Chapter 11](https://livebook.manning.com/book/java-8-in-action/chapter-11/1)

---

## 3. Mutex — Exclusive Access

A **Mutex** (MUTual EXclusion) allows exactly **one** thread to own a resource at a time. All other threads block until the owner releases it.

```mermaid
sequenceDiagram
    participant A as Thread A
    participant M as Mutex (lock)
    participant B as Thread B

    A->>M: lock()
    Note over M: owned by A
    B->>M: lock() — blocks
    Note over B: waiting in entry queue
    A->>M: unlock()
    M->>B: granted
    Note over M: owned by B
    B->>M: unlock()
```

**Analogy:** a concert stage — only one artist performs at a time; others wait in the wings.

**Reentrant Mutex (Recursive Mutex):** a thread that already holds the lock can re-acquire it without deadlocking itself. The JVM's `synchronized` and `ReentrantLock` are both reentrant.

```
A non-reentrant mutex: Thread A holds lock → calls method that also tries to acquire same lock → deadlock.
A reentrant mutex:     Thread A holds lock → re-acquires → increments hold count → safe.
```

---

## 4. ReentrantLock — Structured Locking

`ReentrantLock` gives explicit, unstructured control over lock acquisition and release — spanning method boundaries if needed — unlike `synchronized` which is block-scoped.

### 4.1 The Deadlock Pattern in `ShipPackagesUsingLock`

The repo's `ShipPackagesUsingLock.java` demonstrates **lock ordering** — a critical correctness requirement:

```java
// ShipPackagesUsingLock.java
Lock listLock  = new ReentrantLock();
Lock stackLock = new ReentrantLock();

// pushToStackAsync: acquires listLock THEN stackLock
listLock.lock();
    var value = packageList.remove(packageList.size() - 1);
    stackLock.lock();
        releaseStack.push(value);
    stackLock.unlock();
listLock.unlock();

// popFromStackAsync: acquires stackLock THEN listLock  ← opposite order!
stackLock.lock();
    var value = releaseStack.pop();
    listLock.lock();             // ← DEADLOCK RISK if push holds listLock here
        packageList.add(value);
    listLock.unlock();
stackLock.unlock();
```

```mermaid
graph TD
    subgraph Deadlock["⚠️ Deadlock — Circular Wait"]
        TA["Thread A\n(pushToStack)\nholds listLock\nwaits for stackLock"]
        TB["Thread B\n(popFromStack)\nholds stackLock\nwaits for listLock"]
        TA -->|"waiting for"| LB["stackLock"]
        TB -->|"waiting for"| LA["listLock"]
        LB -->|"held by"| TB
        LA -->|"held by"| TA
    end

    subgraph Fix["✅ Fix — Consistent Lock Ordering"]
        FA["Thread A\nlistLock → stackLock"]
        FB["Thread B\nlistLock → stackLock"]
        FA & FB -->|"same order\nno cycle"| OK["No deadlock"]
    end
```

**Deadlock conditions (Coffman, 1971) — all four must hold:**

| Condition | Description | Break it by... |
|---|---|---|
| **Mutual exclusion** | Resource held exclusively | Use lock-free / immutable structures |
| **Hold and wait** | Thread holds one lock while waiting for another | Acquire all locks atomically (tryLock) |
| **No preemption** | Locks can't be forcibly taken | Use `tryLock(timeout)` with backoff |
| **Circular wait** | A → waits for B → waits for A | Enforce global lock ordering |

**`ReentrantLock` advantages over `synchronized`:**

| Feature | `synchronized` | `ReentrantLock` |
|---|---|---|
| Interruptible wait | No | `lockInterruptibly()` |
| Timed tryLock | No | `tryLock(time, unit)` |
| Fairness policy | No | `new ReentrantLock(true)` |
| Multiple conditions | No (one per object) | `lock.newCondition()` — many per lock |
| Cross-method locking | No | Yes |

---

## 5. Semaphore — Counted Access

A **Semaphore** controls access to a pool of N identical resources. It generalises a Mutex: a Mutex is a semaphore with N=1 that enforces ownership.

```mermaid
graph TD
    subgraph Pool["DB Connection Pool (Semaphore permits = 10)"]
        P["10 permits available"]
    end

    T1["Thread 1\nacquire()"] & T2["Thread 2\nacquire()"] & T3["Thread 3..10\nacquire()"] --> P
    P -->|"permit granted"| C1["Connection 1"]
    P -->|"permit granted"| C2["Connection 2"]
    P -->|"permit granted"| C3["Connection 3..10"]

    T11["Thread 11\nacquire()"] -->|"blocks — no permits"| WAIT["waiting queue"]
    C1 -->|"release()"| P
    WAIT -->|"permit freed → unblocked"| C1

    style P fill:#4a90d9,color:#fff,stroke:#2c6fad
    style WAIT fill:#e8a838,color:#fff,stroke:#b07d20
```

### 5.1 `DatabaseConnectionsUsingSemaphore` — Read/Write Pool

The repo models **asymmetric access**: 10 concurrent readers, 1 exclusive writer — a read/write semaphore:

```java
// DatabaseConnectionsUsingSemaphore.java
private Semaphore readLock  = new Semaphore(10);  // 10 concurrent readers
private Semaphore writeLock = new Semaphore(1);   // 1 exclusive writer

public void getWriteLock()  throws InterruptedException { writeLock.acquire(); }
public void releaseWriteLock()                          { writeLock.release(); }
public void getReadLock()   throws InterruptedException { readLock.acquire();  }
public void releaseReadLock()                           { readLock.release();  }
```

**Mutex vs. Semaphore in one line:**
```
Mutex:     exclusive-member access to a resource     (N = 1, ownership enforced)
Semaphore: N-member concurrent access to a resource  (N ≥ 1, no ownership)
```

**Real-world sizing references:**
- MongoDB `net.maxIncomingConnections` — semaphore over incoming TCP connections
- Cassandra `native_transport_max_concurrent_connections` — default unlimited (danger: unbounded)
- LLM inference server `--max-concurrent-requests` — semaphore on GPU KV cache slots

---

## 6. Monitor — Mutex + Condition

A **monitor** combines a mutex with one or more **condition variables** — threads can atomically release the lock and wait for a signal, avoiding busy-waiting.

```mermaid
sequenceDiagram
    participant P as Producer Thread
    participant M as Monitor (mutex + condition)
    participant C as Consumer Thread

    C->>M: lock() → check: queue empty?
    Note over C: queue is empty
    C->>M: condition.await() — atomically releases lock + sleeps
    P->>M: lock() → enqueue item
    P->>M: condition.signal() — wake one waiter
    P->>M: unlock()
    M->>C: re-acquire lock → re-check condition
    C->>M: dequeue item → unlock()
```

**Java monitor internals:**
- Every `Object` in the JVM is implicitly a monitor
- `synchronized (obj)` acquires the monitor lock
- `obj.wait()` → releases lock + enters **wait set**
- `obj.notify()` / `notifyAll()` → moves thread(s) from wait set to **entry set**

```mermaid
graph LR
    subgraph Monitor["Java Monitor (Object)"]
        ES["Entry Set\n(threads competing for lock)"]
        LOCK["Lock\n(one owner at a time)"]
        WS["Wait Set\n(threads that called wait())"]
    end

    NEW["New Thread"] -->|"tries to enter"| ES
    ES -->|"granted"| LOCK
    LOCK -->|"wait()"| WS
    WS -->|"notify()"| ES
    LOCK -->|"exit / unlock"| ES
```

**Hoare vs. Mesa monitors:**

| | Hoare | Mesa (Java) |
|---|---|---|
| On `signal()` | Signaller yields; woken thread runs immediately | Woken thread re-enters entry set; signaller continues |
| Re-check condition? | No — guaranteed true on wake | **Yes** — always wrap `wait()` in a `while` loop |
| Simpler to implement? | No | Yes (Java uses Mesa) |

```java
// ✅ Correct Java pattern — always while, never if
synchronized (monitor) {
    while (!conditionMet()) {   // re-check after spurious wakeup
        monitor.wait();
    }
    // safe to proceed
}
```

---

## 7. Barrier Synchronisation

A **barrier** is a rendezvous point — no thread may pass until **all** participating threads have arrived. It coordinates the boundary between parallel phases.

```mermaid
sequenceDiagram
    participant I0 as Item-0 (Thread 0)
    participant I1 as Item-1 (Thread 1)
    participant I2 as Item-2 (Thread 2)
    participant B  as CyclicBarrier(3)

    I0->>I0: conveyor (1000ms)
    I1->>I1: conveyor (1200ms)
    I2->>I2: conveyor (1400ms)

    I0->>B: await() — 1st arrival, blocks
    I1->>B: await() — 2nd arrival, blocks
    I2->>B: await() — 3rd arrival
    Note over B: all 3 arrived → barrier opens
    B-->>I0: released
    B-->>I1: released
    B-->>I2: released
    I0 & I1 & I2->>I0: "arrived at packing lane"
```

### 7.1 `PackageItemsBarrier` — Warehouse Lane

```java
// PackageItemsBarrier.java
CyclicBarrier packageBarrierWaitingForAllItems = new CyclicBarrier(NO_OF_ITEMS); // 3

// Each ItemConveyor thread:
packageBarrierWaitingForAllItems.await(); // blocks until all 3 arrive
// only then: readyToPack = true
```

### 7.2 `CyclicBarrier` vs. `CountDownLatch` vs. `Phaser`

| | `CountDownLatch` | `CyclicBarrier` | `Phaser` |
|---|---|---|---|
| **Reusable?** | No (single use) | Yes (resets after trip) | Yes (multiple phases) |
| **Who counts down?** | Any thread | Participating threads | Registered parties |
| **Barrier action?** | No | Yes — runs after all arrive | Yes (per phase) |
| **Dynamic parties?** | No | No | Yes (`register`/`deregister`) |
| **Best for** | One-shot start gun / completion | Iterative parallel phases | Variable-phase pipelines |

```mermaid
graph LR
    subgraph CDL["CountDownLatch(1) — Car Race Start Gun"]
        GUN["countdown.countDown()"] -->|"count = 0"| R1["Racer 1 unblocks"]
        GUN --> R2["Racer 2 unblocks"]
        GUN --> R3["Racer 3 unblocks"]
    end

    subgraph CB["CyclicBarrier(N) — Iterative Phases"]
        PH1["Phase 1\n(all threads work)"] -->|"all arrive"| BAR1["Barrier"]
        BAR1 --> PH2["Phase 2\n(all threads work)"]
        PH2 -->|"all arrive"| BAR2["Barrier"]
        BAR2 --> PH3["Phase 3 ..."]
    end
```

---

## 8. Lock Contention & Performance

Every synchronised section is a serialisation point. At scale, lock contention is a primary throughput killer.

```mermaid
xychart-beta
    title "Throughput vs Thread Count Under Lock Contention"
    x-axis "Thread Count" [1, 2, 4, 8, 16, 32, 64]
    y-axis "Relative Throughput" 0 --> 10
    line "Lock-free (CAS)"          [1, 1.95, 3.8, 7.4, 9.5, 9.8, 9.9]
    line "ReentrantLock (low crit)" [1, 1.90, 3.5, 6.2, 7.8, 8.1, 8.2]
    line "synchronized (med crit)"  [1, 1.70, 2.8, 3.9, 4.1, 4.0, 3.9]
    line "Coarse lock (high crit)"  [1, 1.10, 1.2, 1.1, 1.0, 0.9, 0.8]
```

**Strategies to reduce contention:**

| Strategy | Mechanism | Trade-off |
|---|---|---|
| **Lock striping** | Partition resource into N independent locks (e.g. `ConcurrentHashMap` has 16 stripes) | Memory overhead; complexity |
| **Lock-free (CAS)** | `AtomicLong`, `AtomicReference` — compare-and-swap in hardware | Retry loops under high contention; no blocking |
| **Immutability** | Shared state is read-only — no synchronisation needed | Requires copying on update |
| **Thread-local state** | `ThreadLocal<T>` — no sharing at all | Aggregation needed at boundary |
| **Read/Write lock** | `ReentrantReadWriteLock` — many readers, one writer | Writer starvation if readers never release |
| **StampedLock** | Optimistic reads — validate without acquiring write lock | Complex API; not reentrant |

---

## 9. Synchronisation in the LLM Era

The same primitives — mutex, semaphore, barrier, monitor — appear throughout modern LLM infrastructure, often under different names.

### 9.1 KV Cache Slot Management — Semaphore

An LLM inference server's **KV cache** has a finite number of slots (proportional to GPU HBM). A semaphore gates admission:

```mermaid
graph TD
    subgraph KVPool["KV Cache Pool\n(Semaphore — N slots)"]
        SEM["N permits\n(e.g. 256 concurrent sequences)"]
    end

    R1["Request 1\nacquire()"] & R2["Request 2\nacquire()"] --> SEM
    SEM -->|"permit granted"| GPU["GPU\nKV Cache Block"]
    R257["Request 257\nacquire()"] -->|"no permits → queue / reject"| WAIT["Waiting Queue\n(or 429 Too Many Requests)"]
    GPU -->|"sequence complete\nrelease()"| SEM
    WAIT -->|"permit freed"| GPU

    style SEM fill:#4a90d9,color:#fff,stroke:#2c6fad
    style WAIT fill:#e8a838,color:#fff,stroke:#b07d20
```

**vLLM's PagedAttention** is a virtual memory system for KV cache — it replaces a monolithic semaphore with fine-grained page allocation, analogous to how OS virtual memory replaced fixed partition allocation.

### 9.2 Batch Barrier — Continuous Batching

**Continuous batching** (Orca) removes a global barrier that legacy static batching imposed: all sequences in a batch had to finish before the next batch started. The barrier was the throughput bottleneck.

```mermaid
sequenceDiagram
    participant S0 as Seq 0 (short)
    participant S1 as Seq 1 (medium)
    participant S2 as Seq 2 (long)
    participant GPU as GPU Kernel

    Note over S0,GPU: ❌ Static Batching — barrier after each batch
    GPU->>S0: decode token 1
    GPU->>S1: decode token 1
    GPU->>S2: decode token 1
    Note over S0: S0 finished at step 4
    Note over S0,GPU: ⚠️ barrier: wait for S2 to finish (step 20)
    GPU->>S0: idle (wasted)

    Note over S0,GPU: ✅ Continuous Batching — no global barrier
    GPU->>S0: decode steps 1..4
    S0-->>GPU: EOS — slot freed
    GPU->>S3: new sequence S3 inserts immediately
```

### 9.3 Gradient Synchronisation — Distributed Barrier

In **data-parallel** distributed training, all workers must synchronise gradients before the next forward pass. This is a distributed barrier + all-reduce:

```mermaid
sequenceDiagram
    participant W0 as Worker 0
    participant W1 as Worker 1
    participant W2 as Worker 2
    participant NCCL as NCCL All-Reduce

    W0->>W0: forward + backward pass
    W1->>W1: forward + backward pass
    W2->>W2: forward + backward pass

    Note over W0,NCCL: Distributed barrier — all must arrive before all-reduce begins
    W0->>NCCL: allreduce(grad_shard)
    W1->>NCCL: allreduce(grad_shard)
    W2->>NCCL: allreduce(grad_shard)
    NCCL-->>W0: averaged gradients
    NCCL-->>W1: averaged gradients
    NCCL-->>W2: averaged gradients
    W0 & W1 & W2->>W0: optimizer.step()
    Note over W0,NCCL: Next iteration begins — barrier trips again
```

**Stragglers break the barrier:** one slow worker delays all others. Mitigations:
- **Gradient compression** (PowerSGD) — reduces all-reduce volume
- **Async SGD** — removes the barrier entirely; accepts stale gradients
- **Backup workers** (Google's approach) — redundant workers; use the fastest N-of-M

### 9.4 Tool-Call Concurrency in Agents — Semaphore + Monitor

A multi-agent LLM system calling external tools needs bounded concurrency (rate limits, cost) and result coordination:

```mermaid
graph TD
    subgraph Agent["Agent Orchestrator"]
        ORC["Orchestrator Thread"]
    end

    subgraph ToolSem["Tool Call Semaphore (permits = 5)"]
        SEM5["max 5 concurrent\nexternal API calls"]
    end

    subgraph Tools["Tool Threads"]
        T1["Search API\n(acquire → call → release)"]
        T2["Code Executor\n(acquire → call → release)"]
        T3["DB Lookup\n(acquire → call → release)"]
    end

    subgraph Latch["CountDownLatch(3)\n— wait for all tool results"]
        CDL["countdown on each\ntool completion"]
    end

    ORC -->|"scatter tool calls"| T1 & T2 & T3
    T1 & T2 & T3 --> SEM5
    T1 & T2 & T3 -->|"countDown()"| CDL
    CDL -->|"count = 0\nall tools done"| ORC
```

---

## 10. L6+ Design Trade-offs

### 10.1 Prefer Immutability and Isolation

```mermaid
flowchart TD
    Q["Is the shared state\nactually necessary?"]
    Q -->|No| A1["Eliminate sharing:\nthread-local, actor model,\nimmutable value objects"]
    Q -->|Yes| Q2{Read-heavy\nor write-heavy?}
    Q2 -->|"Read-heavy\n(> 90% reads)"| A2["StampedLock optimistic read\nor ReadWriteLock"]
    Q2 -->|"Write-heavy\nor balanced"| Q3{Single value\nor compound?}
    Q3 -->|"Single counter\nor reference"| A3["Lock-free:\nAtomicLong / AtomicReference CAS"]
    Q3 -->|"Compound state\n(multiple fields)"| A4["ReentrantLock\nor synchronized block\n(keep critical section minimal)"]
```

### 10.2 The Synchronisation Hierarchy — Cost vs. Strength

```mermaid
graph TD
    L0["Immutable / no sharing\n⚡ zero synchronisation cost"]
    L1["ThreadLocal\n⚡ no contention — per-thread state"]
    L2["volatile\n🔶 visibility only — no atomicity"]
    L3["Lock-free CAS\n🔶 non-blocking — retry on conflict"]
    L4["ReentrantLock / synchronized\n🔴 blocking — thread parks on contention"]
    L5["Distributed lock (Redis / ZooKeeper)\n🔴🔴 network round-trip per acquire"]

    L0 -->|"need mutable\nshared scalar"| L2
    L2 -->|"need atomicity\non single value"| L3
    L3 -->|"need atomicity\non compound state"| L4
    L4 -->|"need coordination\nacross JVMs"| L5

    style L0 fill:#5ba85a,color:#fff
    style L1 fill:#5ba85a,color:#fff
    style L2 fill:#e8a838,color:#fff
    style L3 fill:#e8a838,color:#fff
    style L4 fill:#d9534f,color:#fff
    style L5 fill:#d9534f,color:#fff
```

### 10.3 Failure Modes Checklist

| Failure | Cause | Detection | Fix |
|---|---|---|---|
| **Deadlock** | Circular lock acquisition order | Thread dump — BLOCKED threads in cycle | Enforce global lock ordering; use `tryLock(timeout)` |
| **Livelock** | Threads retry indefinitely, yielding to each other | CPU 100%, no progress | Randomised backoff; break symmetry |
| **Starvation** | Low-priority thread never acquires fair lock | Thread dump — one BLOCKED thread, very long | `new ReentrantLock(true)` (fair mode) |
| **Missed signal** | `notify()` before `wait()` — signal lost | Thread waits forever | Use `CountDownLatch` or check condition in `while` loop |
| **Broken barrier** | One thread in `CyclicBarrier` throws — barrier trips with exception | `BrokenBarrierException` on all | Reset barrier; handle exception; don't reuse after break |
| **Spurious wakeup** | `wait()` returns without `notify()` (JVM spec allows) | Incorrect state after wake | Always `while (!condition) wait()` — never `if` |

---

## 11. Key Decision Framework

```mermaid
flowchart TD
    START["What are you synchronising?"]

    START --> SH{Shared\nmutable state?}
    SH -->|No| SH0["No synchronisation needed\n— immutable / message passing"]

    SH -->|Yes| SC{How many threads\nneed access?}
    SC -->|"Exactly 1\nat a time"| MU{Single value\nor compound?}
    MU -->|"Single counter\nor pointer"| MU1["AtomicLong / AtomicReference\n(lock-free CAS)"]
    MU -->|"Compound state"| MU2["ReentrantLock\nor synchronized"]

    SC -->|"Up to N\nat a time"| SEM["Semaphore(N)\n— connection pool,\nGPU slot, rate limit"]

    SC -->|"Many readers\nfew writers"| RW["ReadWriteLock\nor StampedLock"]

    START --> BAR{Phase\ncoordination?}
    BAR -->|"One-shot\nstart or completion"| CDL["CountDownLatch"]
    BAR -->|"Iterative phases\n(training step loop)"| CB["CyclicBarrier"]
    BAR -->|"Variable parties\nor phases"| PH["Phaser"]

    START --> DIST{Cross-JVM\nor cross-node?}
    DIST -->|Yes| DL["Distributed lock\nRedis Redlock / ZooKeeper\n(accept network latency cost)"]

    style SH0 fill:#5ba85a,color:#fff
    style MU1 fill:#5ba85a,color:#fff
    style MU2 fill:#4a90d9,color:#fff
    style SEM fill:#4a90d9,color:#fff
    style RW fill:#4a90d9,color:#fff
    style CDL fill:#e8a838,color:#fff
    style CB fill:#e8a838,color:#fff
    style PH fill:#e8a838,color:#fff
    style DL fill:#d9534f,color:#fff
```

---

## 12. Further Reading

| Topic | Link |
|---|---|
| JSR-133: Java Memory Model FAQ | https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html |
| Java 8 in Action — Ch.11 (CompletableFuture) | https://livebook.manning.com/book/java-8-in-action/chapter-11/1 |
| JVM Lock Objects | https://docs.oracle.com/javase/tutorial/essential/concurrency/newlocks.html |
| Java 8 StampedLock vs ReadWriteLock | http://blog.takipi.com/java-8-stampedlocks-vs-readwritelocks-and-synchronized/ |
| ReentrantLock and Dining Philosophers | https://dzone.com/articles/reentrantlock-and-dining-philo |
| Barrier Synchronisation — Rice University | https://cs.anu.edu.au/courses/comp8320/lectures/aux/comp422-Lecture21-Barriers.pdf |
| JVM CyclicBarrier | https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CyclicBarrier.html |
| CountDownLatch vs CyclicBarrier | https://stackoverflow.com/a/4168861/432903 |
| Orca: continuous batching (OSDI 2022) | https://www.usenix.org/conference/osdi22/presentation/yu |
| vLLM: PagedAttention | https://arxiv.org/abs/2309.06180 |
| Mutex vs Semaphore | https://blog.feabhas.com/2009/09/mutex-vs-semaphores-%E2%80%93-part-1-semaphores/ |
| Parallel Programming with Barrier Sync (JVM) | http://blogs.sourceallies.com/2012/03/parallel-programming-with-barrier-synchronization/ |

