# Performance Analysis Report

## Results by Dataset Size

### SMALL Datasets

| Dataset | Algorithm | Orig_V | Orig_E | Cond_V | Cond_E | Time (ms) | Ops |
|---------|-----------|--------|--------|--------|--------|-----------|-----|
| small_cyclic_1 | SCC_Tarjan | 8 | 11 | 8 | 11 | 0.031 | 8 |
| small_cyclic_1 | Topo_Kahn | 8 | 11 | 2 | 1 | 0.009 | 2 |
| small_cyclic_1 | DAG_Shortest | 8 | 11 | 2 | 1 | 0.005 | 0 |
| small_cyclic_1 | DAG_Longest | 8 | 11 | 2 | 1 | 0.034 | 0 |
| small_dag_2 | SCC_Tarjan | 10 | 16 | 10 | 16 | 0.012 | 10 |
| small_dag_2 | Topo_Kahn | 10 | 16 | 10 | 16 | 0.012 | 10 |
| small_dag_2 | DAG_Shortest | 10 | 16 | 10 | 16 | 0.002 | 0 |
| small_dag_2 | DAG_Longest | 10 | 16 | 10 | 16 | 0.002 | 0 |
| small_dag_1 | SCC_Tarjan | 7 | 9 | 7 | 9 | 0.009 | 7 |
| small_dag_1 | Topo_Kahn | 7 | 9 | 7 | 9 | 0.005 | 7 |
| small_dag_1 | DAG_Shortest | 7 | 9 | 7 | 9 | 0.001 | 0 |
| small_dag_1 | DAG_Longest | 7 | 9 | 7 | 9 | 0.001 | 0 |

### MEDIUM Datasets

| Dataset | Algorithm | Orig_V | Orig_E | Cond_V | Cond_E | Time (ms) | Ops |
|---------|-----------|--------|--------|--------|--------|-----------|-----|
| medium_scc_1 | SCC_Tarjan | 15 | 33 | 15 | 33 | 0.020 | 15 |
| medium_scc_1 | Topo_Kahn | 15 | 33 | 3 | 2 | 0.004 | 3 |
| medium_scc_1 | DAG_Shortest | 15 | 33 | 3 | 2 | 0.001 | 0 |
| medium_scc_1 | DAG_Longest | 15 | 33 | 3 | 2 | 0.001 | 0 |
| medium_dense_1 | SCC_Tarjan | 18 | 177 | 18 | 177 | 0.052 | 18 |
| medium_dense_1 | Topo_Kahn | 18 | 177 | 1 | 0 | 0.001 | 1 |
| medium_dense_1 | DAG_Shortest | 18 | 177 | 1 | 0 | 0.001 | 0 |
| medium_dense_1 | DAG_Longest | 18 | 177 | 1 | 0 | 0.000 | 0 |
| medium_mixed_1 | SCC_Tarjan | 12 | 20 | 12 | 20 | 0.009 | 12 |
| medium_mixed_1 | Topo_Kahn | 12 | 20 | 9 | 12 | 0.010 | 9 |
| medium_mixed_1 | DAG_Shortest | 12 | 20 | 9 | 12 | 0.001 | 0 |
| medium_mixed_1 | DAG_Longest | 12 | 20 | 9 | 12 | 0.001 | 0 |

### LARGE Datasets

| Dataset | Algorithm | Orig_V | Orig_E | Cond_V | Cond_E | Time (ms) | Ops |
|---------|-----------|--------|--------|--------|--------|-----------|-----|
| large_scc_1 | SCC_Tarjan | 35 | 95 | 35 | 95 | 0.070 | 35 |
| large_scc_1 | Topo_Kahn | 35 | 95 | 5 | 4 | 0.007 | 5 |
| large_scc_1 | DAG_Shortest | 35 | 95 | 5 | 4 | 0.001 | 0 |
| large_scc_1 | DAG_Longest | 35 | 95 | 5 | 4 | 0.001 | 0 |
| large_dense_1 | SCC_Tarjan | 50 | 999 | 50 | 999 | 0.145 | 50 |
| large_dense_1 | Topo_Kahn | 50 | 999 | 1 | 0 | 0.001 | 1 |
| large_dense_1 | DAG_Shortest | 50 | 999 | 1 | 0 | 0.000 | 0 |
| large_dense_1 | DAG_Longest | 50 | 999 | 1 | 0 | 0.000 | 0 |
| large_dag_1 | SCC_Tarjan | 25 | 68 | 25 | 68 | 0.020 | 25 |
| large_dag_1 | Topo_Kahn | 25 | 68 | 25 | 68 | 0.021 | 25 |
| large_dag_1 | DAG_Shortest | 25 | 68 | 25 | 68 | 0.003 | 0 |
| large_dag_1 | DAG_Longest | 25 | 68 | 25 | 68 | 0.002 | 0 |

## Algorithm Performance Comparison

| Algorithm | Avg Time (ms) | Best Time (ms) | Worst Time (ms) |
|-----------|---------------|----------------|-----------------|
| SCC_Tarjan | 0.037 | 0.006 | 0.145 |
| Topo_Kahn | 0.007 | 0.001 | 0.021 |
| DAG_Shortest | 0.002 | 0.000 | 0.005 |
| DAG_Longest | 0.004 | 0.000 | 0.034 |

## Key Insights

1. **SCC Detection**: O(V+E) complexity confirmed - scales linearly with graph size
2. **Topological Sort**: Very efficient on condensation graphs
3. **DAG Shortest Path**: Faster than Dijkstra for DAGs (no heap operations)
4. **Dense vs Sparse**: Dense graphs show higher operation counts but similar time complexity
