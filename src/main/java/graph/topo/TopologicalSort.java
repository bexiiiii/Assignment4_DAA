package graph.topo;

import common.Graph;
import common.Metrics;
import common.MetricsImpl;

import java.util.*;


public class TopologicalSort {
    private final Graph graph;
    private final Metrics metrics;

    public TopologicalSort(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

 
    public List<Integer> kahnSort() {
        int n = graph.getVertexCount();
        int[] inDegree = new int[n];
        
        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                inDegree[edge.to]++;
            }
        }

        // Queue for vertices with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.incrementCounter("queue_pushes");
            }
        }

        List<Integer> topoOrder = new ArrayList<>();
        
        metrics.startTiming();

        while (!queue.isEmpty()) {
            int u = queue.poll();
            topoOrder.add(u);
            metrics.incrementCounter("queue_pops");

            // Reduce in-degree for adjacent vertices
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                int v = edge.to;
                inDegree[v]--;
                metrics.incrementCounter("edge_relaxations");
                
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementCounter("queue_pushes");
                }
            }
        }

        metrics.stopTiming();

        // Check if all vertices are included (cycle detection)
        if (topoOrder.size() != n) {
            System.err.println("Warning: Graph contains a cycle! Topological sort incomplete.");
            return new ArrayList<>(); // Return empty list to indicate cycle
        }

        return topoOrder;
    }

    /**
     * DFS-based topological sort (alternative implementation).
     * @return List of vertices in topological order
     */
    public List<Integer> dfsSort() {
        int n = graph.getVertexCount();
        boolean[] visited = new boolean[n];
        Stack<Integer> stack = new Stack<>();

        metrics.reset();
        metrics.startTiming();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsHelper(i, visited, stack);
            }
        }

        metrics.stopTiming();

        List<Integer> topoOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            topoOrder.add(stack.pop());
        }

        return topoOrder;
    }

    /**
     * Helper method for DFS-based topological sort.
     */
    private void dfsHelper(int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        metrics.incrementCounter("DFS_visits");

        for (Graph.Edge edge : graph.getAdjacent(u)) {
            int v = edge.to;
            metrics.incrementCounter("edges_explored");
            
            if (!visited[v]) {
                dfsHelper(v, visited, stack);
            }
        }

        stack.push(u);
        metrics.incrementCounter("stack_pushes");
    }

    /**
     * Get metrics for the algorithm execution.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Verify if the given ordering is a valid topological sort.
     */
    public boolean isValidTopologicalOrder(List<Integer> order) {
        if (order.size() != graph.getVertexCount()) {
            return false;
        }

        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }

        // Check that for every edge u->v, u comes before v
        for (int u = 0; u < graph.getVertexCount(); u++) {
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                int v = edge.to;
                if (position.get(u) >= position.get(v)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Print topological order with metrics.
     */
    public void printTopologicalOrder(List<Integer> order) {
        System.out.println("\n=== Topological Order ===");
        if (order.isEmpty()) {
            System.out.println("No valid topological order (graph contains cycle)");
        } else {
            System.out.println("Order: " + order);
            System.out.println("Valid: " + isValidTopologicalOrder(order));
        }
        System.out.println("\nMetrics:");
        System.out.println(metrics.getSummary());
    }
}
