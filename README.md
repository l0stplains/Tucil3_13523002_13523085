# Rush Hour Puzzle Solver
> Tugas Kecil 3 - IF2211 Strategi Algoritma 2025
<p align="center">
    <img width="150px" src="https://github.com/user-attachments/assets/d5660c11-66cf-4cda-a1d2-31a6086a6798">
</p>
    <h3 align="center">Rush Hour Puzzle Solver</h3>
<p align="center">
    A Rush Hour puzzle solver in Java with configurable search algorithms and heuristics.
    <br />
    <br />
    <a href="https://github.com/l0stplains/Tucil3_13523002_13523085/releases/">Releases</a>
    ·
    <a href="https://github.com/l0stplains/Tucil3_13523002_13523085/tree/main/docs/">Project Report & Specification (Bahasa Indonesia)</a>
</p>

## Table of Contents <a name="table-of-contents"></a>

- [The Team](#team)
- [About](#about)
- [Puzzle Description](#puzzle-description)
- [Algorithms](#algorithms)
- [Heuristics](#heuristics)
- [The App](#the-app)
- [How to Run](#how-to-run)
- [Benchmark](#benchmark)
- [Optimizations](#optimizations)
- [Acknowledgements](#acknowledgements)
---

## The Team <a name="team"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div> 

<table>
       <tr align="left">
         <td><b>NIM</b></td>
         <td><b>Name</b></td>
         <td align="center"><b>GitHub</b></td>
       </tr>
       <tr align="left">
         <td>13523002</td>
         <td>Refki Alfarizi</td>
         <td align="center" >
           <div style="margin-right: 20px;">
           <a href="https://github.com/l0stplains" ><img src="https://avatars.githubusercontent.com/u/78079998?v=4" width="48px;" alt=""/> <br/> <sub><b> @l0stplains </b></sub></a><br/>
           </div>
         </td>
       </tr>
       <tr align="left">
         <td>13523085</td>
         <td>Muhammad Jibril Ibrahim</td>
         <td align="center" >
           <div style="margin-right: 20px;">
           <a href="https://github.com/BoredAngel" ><img src="https://avatars.githubusercontent.com/u/168176400?v=4" width="48px;" alt=""/> <br/> <sub><b> @BoredAngel </b></sub></a><br/>
           </div>
         </td>
       </tr>
</table>


---

## About <a name="about"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

This project is the third "small" milestone project of 2025 IF2211 Algorithm Strategy course at [Institut Teknologi Bandung](https://itb.ac.id). We implemented various path routing algorithm as well as heuristics to solve Rush Hour Puzzle.

<p align="center">
    <img width="300px" src="https://github.com/user-attachments/assets/d12ada3a-2f58-49b8-b3fd-5a9997c7d198">
</p>
<p align="center"><i>Indonesia’s traffic is a real-life brainteaser, so we made a solver for the virtual one that (hopefully) has a solution.</i></p>


---

## Puzzle Description <a name="puzzle-description"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

<p align="center">
    <img width="300px" src="https://www.michaelfogleman.com/static/rush/solution.gif">
</p>
<p align="center"><i>Rush Hour Puzzle example<br/> (<a href="https://www.michaelfogleman.com/rush/">https://www.michaelfogleman.com/rush/</a>)</i></p>


The Rush Hour puzzle puts a primary car in a gridlocked parking lot full of cars. As a player, you must slide vehicles forward or backward to clear a path and free the target car.  Each move shifts one vehicle along its orientation, and the goal is to navigate the red car out through the exit.  It’s a simple set of rules but a fiendishly tricky logic challenge.

### Input Format

Below is the expected `.txt` layout for any puzzle instance (at least for this project scope):

```txt
# Board size: (rows cols)
# Then number of vehicle (not counting the player's).
# Then each block

# Sample:

6 6
11
GBB.L.
GHI.LM
GHIPPMK
CCCZ.M
..JZDD
EEJFF.
```
> [!IMPORTANT]
> Primary car always using the letter "P" and exit "K"


---

## Algorithms <a name="algorithms"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

Solving Rush Hour means searching a sprawling state space of car configurations, some paths lead to dead ends, while others offer a clear route to solution. We provide (or rather choose) four distinct strategies to explore this space, each balancing optimality, speed, and resource use in different ways.

### Uniform Cost Search (UCS)
UCS expands states in order of cumulative move cost, ensuring that the first time it reaches the goal is via the least-cost path. It’s guaranteed optimal and complete, but may consume large amounts of memory on harder puzzles as it explores uniformly in all directions.

### Greedy Best-First Search (GBFS)
GBFS prioritizes states that appear closest to the exit, based solely on the heuristic at each node generation. This can dramatically reduce explored nodes and runtime, but because it ignores path cost, it can wander into long or even dead-end paths and is neither optimal nor always complete.

### A\*
A\* combines UCS’s cost tracking with GBFS’s heuristic guidance, expanding states by the sum of path cost and estimated cost to goal. When using an admissible, consistent heuristic, A\* finds an optimal solution and often explores far fewer states than UCS, though its memory use remains substantial.

### Beam Search
Beam Search restricts the frontier to the best _k_ candidates at each depth level, dramatically cutting memory usage. It runs quickly on large puzzles but sacrifices both completeness and optimality, since promising states outside the beam are discarded. This algorithm can be an alternative to GBFS.

Below is a comparison of these algorithms across key performance metrics:

| Algorithm | Optimality     | Completeness | Memory Usage (relatively) | Key Traits                                      |
| --------- | -------------- | ------------ | ------------ | ----------------------------------------------- |
| UCS       | ✔️              | ✔️            | High         | Explores by path cost, exhaustive               |
| GBFS      | ❌              | ❌            | Medium       | Heuristic-driven, fast but myopic               |
| A\*       | ✔️ (w/ valid h) | ✔️            | Medium-High  | Balances cost + heuristic for efficient optimal |
| Beam      | ❌              | ❌            | Low          | Bounded beam width, very fast, may miss paths   |

---

## Heuristics <a name="heuristics"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

Guiding the search efficiently depends on good cost-to-go estimates. We implement four admissible heuristics, ranging from trivial to high-overhead, to trade off between search speed and solution optimality.

### Zero Heuristic
Returns 0 for every state. Guarantees admissibility but provides no guidance, forcing the search to behave like uninformed UCS.

### *Distance Heuristic
Returns 0 if the path to exit is not blocked and returns 1 if blocked. This "distance" heuristic will just force the searching to just finish the search if the exit is not blocked.

<details>
    <summary>
        <i>Why not using Manhattan Distance❓</i>
    </summary>
    <br/>
    The short answer is that <b>it's not admissible</b>. <i>well at least for this project</i>
    <br/>
    <br/>
    In this Rush Hour, any <i>k</i> unit of displacement that a car can do is counted as a single movement. So calculating the Manhattan Distance from the car to the exit will be an over-estimation thus breaks the definition of admissible.
</details>

### Blocking Heuristic
Counts the number of vehicles directly blocking the target car’s path. Still admissible, it captures immediate obstacles with minimal computation, often trimming many unnecessary expansions.

### Recursive Blocking Heuristic
Extends the blocking count by also summing blockers of those blocking cars recursively. More informed than simple blocking, but with higher per-node cost. This approach still admissible because to make the blocking car "not blocking", at least one movement need to be made and it applies to another car that blocking the blocker so it will never over-estimates the cost. This approach is really good when handling a relatively simple puzzle (from human perspective) but have a chaining blocking cars.

Below is a comparison of these heuristics across key characteristics:

| Heuristic               | Admissible | Informedness   | Computation Cost | Key Traits                                    |
| ----------------------- | ---------- | -------------- | ---------------- | --------------------------------------------- |
| Zero                    | ✔️          | None           | Low              | Uninformed baseline                           |
| *Distance                | ✔️          | Low        | Low              | Force to finish when exit is not blocked           |
| Blocking                | ✔️          | Medium-High    | Low              | Immediate obstacle count                      |
| Recursive Blocking      | ✔️          | High           | Medium           | Recursive obstacle dependency analysis   |

> [!NOTE]
> View the source code for the algorithms and heuristics [here](https://github.com/l0stplains/Tucil3_13523002_13523085/tree/main/src/java/tucil_3_stima/strategy)

---

## The App <a name="the-app"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  


A user-friendly, chaotic (as it should be), and immersive JavaFX interface that brings the Rush Hour solver to life. Load puzzles, choose algorithms and heuristics, and watch the solution animate step by step (with _blazingly fast ⚡_ solver of course).

### Screenshot
<p align="center">
  <img src="https://github.com/user-attachments/assets/caf5ad22-9cc1-4f93-92d8-eed24752d573" alt="Rush Hour Solver App GUI" width="500px"/>
</p>  

### Key Features
- **Puzzle Loader:** Open `.txt` puzzle files and visualize the board (with robust validation).
- **Algorithm & Heuristic Selection:** Dropdown menus to pick UCS, GBFS, A\*, or Beam search and any of the four heuristics.
- **Animated Solution:** Step through each move with play/pause controls or jump to any steps as you want.
- **Performance Stats:** Display of nodes expanded, nodes generated, moves taken, and execution time.
- **Save Result:** Save your result to a rich information `.txt` result report.
- **Immersive Experience:** Feel the madness of Indonesia's traffic from it's background image and sounds.

---

## How to Run <a name="how-to-run"></a>

<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

### Requirements
- Java 23 or later
- Gradle 8.10 or later (if building from source)

### Running the Application

1. **Download the JAR file** from the [releases page](https://github.com/l0stplains/Tucil3_13523002_13523085/releases/).
2. Open a terminal and navigate to the directory containing the downloaded JAR.
3. Run the following command to start the application:
   ```bash
   java -jar rush-hour-solver_13523002_13523085.jar
   ```
   Alternatively, if you have cloned the repository and want to run it using Gradle:
   ```bash
   ./gradlew run
   ```
> [!TIP]
> You can double click the jar to run it!


---

## Benchmark <a name="benchmark"></a>

<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

_Coming Soon_

---

## Optimizations <a name="optimizations"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

> _Blazingly Fast ⚡⚡⚡_

To squeeze maximum performance out of our solver, we layered in several low-level and algorithmic optimizations. Each tackles a different bottleneck, memory churn, lookup speed, or bitwise arithmetic, to make even the "hardest" puzzles run in milliseconds.

### BitSet Board Representation
Each vehicle position is represented by a `BitSet` mask over the flattened grid. Bitwise `and`, `or`, and `intersects` let us **test collisions and occupancy in a few CPU cycles**, rather than iterating over each cell. This pays off hugely when generating thousands of neighbor states.

### Precomputed Vehicle Masks
Instead of rebuilding masks on the fly, we precompute for every vehicle and every valid base position a `BitSet` of its footprint. During search, we simply retrieve a mask from `vehicleMasks.get(i).get(pos)`, no loops or allocations, just a quick map lookup.

### Priority Queue for Open Set
We use a `PriorityQueue<State>` ordered by each state’s `f = g + h` (or just `g` for UCS). This gives us O(log n) insertion and extraction of the next-best node, keeping the frontier operations blazing fast even when thousands of states are in flight.

### HashMap for Best-Seen Costs
A `Map<State,Integer>` tracks the best `g`-score seen so far per state. Before adding a neighbor to the open set, we check `best.get(nxt)` in O(1) time to prune any paths that aren’t improvements. This avoids redundant expansions and keeps memory usage under control.

### Efficient Neighbor Generation
By removing a vehicle’s mask from the occupancy `BitSet` before sliding, we can test each incremental move (`while` loop) in isolation. We only copy & mutate a `State` when a valid move is found, then lazily compute its new heuristic. Together with tight reuse of `State.copy()`, this minimizes object churn and GC pressure.

<br>
<p align="center">
  <em><strong>“Bad programmers worry about code.<br>
  Good programmers worry about data structures and their relationships.”</strong></em><br>
  — Linus Torvalds
</p>

---

## Acknowledgements <a name="acknowledgements"></a>
<div align="right">(<a href="#table-of-contents">back to top</a>)</div>  

We gratefully acknowledge:

- **Dr. Nur Ulfa Maulidevi, S.T, M.Sc.**, for expert guidance and deep dives into search theory.
- **Dr. Ir. Rinaldi Munir, M.T.**, for always giving "Kabar Baik" on his tweet.
- Our **Course Assistants**, for preparing the milestone and answering questions (faster response please hehe).
- Kostan Jibril at **Azalea**, for late-night sprints.
- **Friends & Classmates**, whose spirited “just one more feature” environment kept us motivated.
- **ThinkFun** and the Rush Hour community, for creating the original puzzle that inspired this solver.
- And **Michael Fogleman** for the techniques described in his article which are advance the current "state of the art" for solving the game of Rush Hour which heavily inspired us.


---