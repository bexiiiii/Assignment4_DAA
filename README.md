# Assignment 4: Smart City/Campus Scheduling

**Course:** Algorithms and Data Structures  
**Topic:** Strongly Connected Components, Topological Ordering, and Shortest Paths in DAGs

## Overview

This project implements three fundamental graph algorithms applied to a "Smart City/Campus Scheduling" scenario:

1. **Strongly Connected Components (SCC)** - Tarjan's Algorithm
2. **Topological Sort** - Kahn's Algorithm (BFS-based)
3. **Shortest & Longest Paths in DAGs** - Dynamic Programming over Topological Order

## Project Structure

```
assignment4/
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
├── data/                            # Generated datasets
│   ├── small_dag_1.json
│   ├── small_cyclic_1.json
│   ├── small_dag_2.json
│   ├── medium_mixed_1.json
│   ├── medium_scc_1.json
│   ├── medium_dense_1.json
│   ├── large_dag_1.json
│   ├── large_scc_1.json
│   └── large_dense_1.json
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── Main.java            # Main entry point
│   │       ├── common/              # Common utilities
│   │       │   ├── Graph.java       # Graph data structure
│   │       │   ├── Metrics.java     # Metrics interface
│   │       │   └── MetricsImpl.java # Metrics implementation
│   │       ├── graph/
│   │       │   ├── scc/             # SCC package
│   │       │   │   └── TarjanSCC.java
│   │       │   ├── topo/            # Topological sort package
│   │       │   │   └── TopologicalSort.java
│   │       │   └── dagsp/           # DAG shortest path package
│   │       │       └── DAGShortestPath.java
│   │       └── utils/
│   │           └── DatasetGenerator.java
│   └── test/
│       └── java/
│           └── GraphAlgorithmsTest.java
└── tasks (1).json                   # Original dataset
```

## Algorithms Implemented

### 1. Strongly Connected Components (Tarjan's Algorithm)

**File:** `src/main/java/graph/scc/TarjanSCC.java`

- **Time Complexity:** O(V + E)
- **Space Complexity:** O(V)
- **Features:**
  - Finds all SCCs in a directed graph
  - Assigns SCC IDs to vertices
  - Builds condensation graph (DAG of SCCs)
  - Tracks metrics: DFS visits, edges explored, stack operations

**Key Method:**
```java
List<List<Integer>> findSCCs()
Graph buildCondensation()
```

### 2. Topological Sort (Kahn's Algorithm)

**File:** `src/main/java/graph/topo/TopologicalSort.java`

- **Time Complexity:** O(V + E)
- **Space Complexity:** O(V)
- **Features:**
  - BFS-based (Kahn's algorithm)
  - Alternative DFS-based implementation
  - Cycle detection (returns empty list if cycle exists)
  - Order validation

**Key Methods:**
```java
List<Integer> kahnSort()
List<Integer> dfsSort()
boolean isValidTopologicalOrder(List<Integer> order)
```

### 3. DAG Shortest & Longest Paths

**File:** `src/main/java/graph/dagsp/DAGShortestPath.java`

- **Time Complexity:** O(V + E)
- **Space Complexity:** O(V)
- **Features:**
  - Single-source shortest paths
  - Single-source longest paths (critical path)
  - Path reconstruction
  - Supports both edge weights and node weights

**Key Methods:**
```java
PathResult shortestPaths(int source)
PathResult longestPaths(int source)
CriticalPathResult findCriticalPath(int source)
List<Integer> reconstructPath(int source, int target, int[] parent)
```

## Weight Model

The implementation supports two weight models (specified in JSON):

1. **`edge`** (default): Uses edge weights for distance calculations
2. **`node`**: Uses destination node weights (node durations)

This is documented in the JSON field `weight_model`.

## Build & Run Instructions

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Build the Project

```bash
# Clone or navigate to project directory
cd assignment4

# Compile the project
mvn clean compile

# Run tests
mvn test

# Build JAR file
mvn clean package
```

### Run the Program

#### Option 1: Using Maven

```bash
# Run analysis on the provided dataset
mvn exec:java -Dexec.mainClass="Main" -Dexec.args="tasks (1).json"

# Generate datasets
mvn exec:java -Dexec.mainClass="utils.DatasetGenerator" -Dexec.args="data"
```

#### Option 2: Using JAR

```bash
# Run analysis
java -jar target/smart-city-scheduling-1.0-SNAPSHOT.jar "tasks (1).json"

# Generate datasets
java -cp target/smart-city-scheduling-1.0-SNAPSHOT.jar utils.DatasetGenerator data
```

#### Option 3: Direct Java Execution

```bash
# Compile
javac -cp "target/classes:lib/*" -d target/classes src/main/java/**/*.java

# Run
java -cp "target/classes:lib/*" Main "tasks (1).json"
```

### Generate Datasets

```bash
# Generate all 9 datasets (3 small, 3 medium, 3 large)
mvn exec:java -Dexec.mainClass="utils.DatasetGenerator"

# Or specify output directory
mvn exec:java -Dexec.mainClass="utils.DatasetGenerator" -Dexec.args="data"
```

### Run Tests

```bash
# Run all JUnit tests
mvn test

# Run with verbose output
mvn test -X

# Run specific test
mvn test -Dtest=GraphAlgorithmsTest#testSCCOnDAG
```

## Dataset Summary

### Small Graphs (6-10 nodes)

| File | Nodes | Edges | Type | Description |
|------|-------|-------|------|-------------|
| `small_dag_1.json` | 7 | ~10 | DAG | Simple DAG with moderate density |
| `small_cyclic_1.json` | 8 | ~12 | Cyclic | Contains 2 cycles |
| `small_dag_2.json` | 10 | ~13 | DAG | Pure DAG with guaranteed connectivity |

### Medium Graphs (10-20 nodes)

| File | Nodes | Edges | Type | Description |
|------|-------|-------|------|-------------|
| `medium_mixed_1.json` | 12 | ~18 | Mixed | Sparse, with 2 cycles |
| `medium_scc_1.json` | 15 | ~30 | Multiple SCCs | 3 distinct SCCs with inter-SCC edges |
| `medium_dense_1.json` | 18 | ~100 | Dense | High connectivity (60% density) |

### Large Graphs (20-50 nodes)

| File | Nodes | Edges | Type | Description |
|------|-------|-------|------|-------------|
| `large_dag_1.json` | 25 | ~45 | DAG | Sparse DAG for performance testing |
| `large_scc_1.json` | 35 | ~120 | Multiple SCCs | 5 SCCs with complex structure |
| `large_dense_1.json` | 50 | ~500 | Dense | Dense graph (40% density) for stress testing |

## JSON Format

```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2}
  ],
  "source": 4,
  "weight_model": "edge"
}
```

**Fields:**
- `directed`: Boolean indicating if graph is directed
- `n`: Number of vertices
- `edges`: Array of edges with source (u), destination (v), and weight (w)
- `source`: Starting vertex for path algorithms (optional)
- `weight_model`: Either "edge" or "node" (optional, defaults to "edge")

## Algorithm Analysis

### SCC (Tarjan's Algorithm)

**Bottlenecks:**
- DFS traversal: O(V + E)
- Stack operations: O(V) in worst case

**Effect of Structure:**
- **Dense graphs:** More edges to explore, but same asymptotic complexity
- **Multiple SCCs:** More independent components to process
- **Single large SCC:** All vertices visited in one DFS tree

**Metrics Tracked:**
- DFS visits
- Edges explored
- Stack pops

### Topological Sort (Kahn's Algorithm)

**Bottlenecks:**
- In-degree calculation: O(E)
- Queue operations: O(V)

**Effect of Structure:**
- **DAG:** Completes successfully
- **Cyclic:** Detects cycle early
- **Long chains:** More sequential processing

**Metrics Tracked:**
- Queue pushes/pops
- Edge relaxations

### DAG Shortest/Longest Paths

**Bottlenecks:**
- Topological sort: O(V + E)
- Edge relaxations: O(E)

**Effect of Structure:**
- **Sparse graphs:** Fewer relaxations
- **Dense graphs:** More relaxations per vertex
- **Long paths:** More parent pointer traversals for reconstruction

**Metrics Tracked:**
- Vertices processed
- Edge relaxations
- Successful relaxations

## Performance Results

Sample execution times (on provided dataset with n=8):

| Algorithm | Time (ms) | Operations |
|-----------|-----------|------------|
| SCC (Tarjan) | ~0.5 | 8 DFS visits, 7 edges |
| Topological Sort | ~0.3 | 8 queue ops |
| DAG Shortest Path | ~0.4 | 7 edge relaxations |
| DAG Longest Path | ~0.4 | 7 edge relaxations |

*Note: Times may vary based on hardware and JVM optimization*

## Testing

The project includes comprehensive JUnit 5 tests covering:

-  SCC on DAGs, cyclic graphs, and multi-SCC graphs
-  Condensation graph validation
-  Topological sort on DAGs and cyclic graphs
-  DFS-based topological sort
-  Shortest path correctness
-  Longest path (critical path) correctness
-  Path reconstruction
-  Edge cases (single node, disconnected graphs)
-  Metrics recording

**Test Coverage:** All major algorithms and edge cases

## Practical Recommendations

### When to Use Each Algorithm

1. **SCC (Tarjan's Algorithm)**
   - Detecting dependency cycles in task scheduling
   - Analyzing strongly connected web pages
   - Finding mutual reachability in networks
   - **Best for:** Graphs where you need to identify cyclic dependencies

2. **Topological Sort**
   - Task scheduling with precedence constraints
   - Build systems (compile order)
   - Course prerequisite planning
   - **Best for:** Ordering tasks with dependencies (DAGs only)

3. **DAG Shortest Paths**
   - Finding minimum cost paths in task dependencies
   - Critical path method (CPM) for project management
   - Resource optimization
   - **Best for:** DAGs where you need optimal paths (works in O(V+E) vs Dijkstra's O((V+E)logV))

### Choosing Between Algorithms

- **For cyclic graphs:** First apply SCC to build condensation, then use topological sort on condensation
- **For DAGs:** Direct topological sort + DAG shortest path
- **For general graphs:** Use Dijkstra/Bellman-Ford instead

## Code Quality

### Design Principles

- **Modularity:** Separated into packages (scc, topo, dagsp)
- **Reusability:** Common Graph and Metrics interfaces
- **Testability:** Comprehensive JUnit tests
- **Documentation:** Javadoc comments for all public methods
- **Instrumentation:** Built-in metrics tracking

### Key Design Decisions

1. **Graph representation:** Adjacency list for efficiency
2. **Weight model flexibility:** Support both edge and node weights
3. **JSON parsing:** Gson library for easy data loading
4. **Metrics interface:** Unified performance tracking
5. **Error handling:** Graceful cycle detection and reporting

## Dependencies

- **Gson 2.10.1:** JSON parsing
- **JUnit 5.9.3:** Unit testing

## Author

Assignment 4 Implementation  
Algorithms and Data Structures Course

## License

Academic use only.
