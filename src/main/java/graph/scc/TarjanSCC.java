package graph.scc;

import common.Graph;
import common.Metrics;
import common.MetricsImpl;

import java.util.*;


public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;
    
    private int time;
    private int[] disc;      
    private int[] low;       
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;
    private int[] sccId;     
    private int sccCount;

    public TarjanSCC(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    
    public List<List<Integer>> findSCCs() {
        int n = graph.getVertexCount();
        time = 0;
        sccCount = 0;
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        sccId = new int[n];
        
        Arrays.fill(disc, -1);
        Arrays.fill(sccId, -1);

        metrics.startTiming();

        // Run DFS from all unvisited vertices
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i);
            }
        }

        metrics.stopTiming();

        return sccs;
    }

    
    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        
        metrics.incrementCounter("DFS_visits");

       
        for (Graph.Edge edge : graph.getAdjacent(u)) {
            int v = edge.to;
            metrics.incrementCounter("edges_explored");

            if (disc[v] == -1) {
               
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                
                low[u] = Math.min(low[u], disc[v]);
            }
        }

       
        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
                sccId[v] = sccCount;
                metrics.incrementCounter("stack_pops");
            } while (v != u);
            
            sccs.add(scc);
            sccCount++;
        }
    }

    
    public int getSccId(int vertex) {
        return sccId[vertex];
    }

   
    public int getSccCount() {
        return sccCount;
    }

    
    public Graph buildCondensation() {
        Graph condensation = new Graph(sccCount, true, graph.getWeightModel());
        
        Set<String> addedEdges = new HashSet<>();
        
        // For each edge in original graph
        for (int u = 0; u < graph.getVertexCount(); u++) {
            int sccU = sccId[u];
            
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                int v = edge.to;
                int sccV = sccId[v];
                
                // Add edge between different SCCs (avoid duplicates)
                if (sccU != sccV) {
                    String edgeKey = sccU + "->" + sccV;
                    if (!addedEdges.contains(edgeKey)) {
                        condensation.addEdge(sccU, sccV, edge.weight);
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
        
        // Set node weights for condensation based on SCC sizes
        for (int i = 0; i < sccCount; i++) {
            condensation.setNodeWeight(i, sccs.get(i).size());
        }
        
        return condensation;
    }

    /**
     * Get metrics for the algorithm execution.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Get SCCs with their sizes.
     */
    public Map<Integer, Integer> getSccSizes() {
        Map<Integer, Integer> sizes = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            sizes.put(i, sccs.get(i).size());
        }
        return sizes;
    }

    
    public void printSCCs() {
        System.out.println("\n=== Strongly Connected Components ===");
        System.out.println("Total SCCs found: " + sccCount);
        
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            System.out.printf("SCC %d (size %d): %s%n", i, scc.size(), scc);
        }
        
        System.out.println("\nSCC Sizes: " + getSccSizes());
        System.out.println("\nMetrics:");
        System.out.println(metrics.getSummary());
    }
}
