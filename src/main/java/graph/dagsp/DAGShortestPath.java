package graph.dagsp;

import common.Graph;
import common.Metrics;
import common.MetricsImpl;
import graph.topo.TopologicalSort;

import java.util.*;


public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;
    private final String weightModel;

    public static class PathResult {
        public final int[] distances;
        public final int[] parent;
        public final List<Integer> topoOrder;

        public PathResult(int[] distances, int[] parent, List<Integer> topoOrder) {
            this.distances = distances;
            this.parent = parent;
            this.topoOrder = topoOrder;
        }
    }

    public DAGShortestPath(Graph graph) {
        this.graph = graph;
        this.metrics = new MetricsImpl();
        this.weightModel = graph.getWeightModel();
    }

    
    public PathResult shortestPaths(int source) {
        int n = graph.getVertexCount();
        
      
        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.kahnSort();
        
        if (topoOrder.isEmpty()) {
            throw new IllegalArgumentException("Graph contains a cycle - not a DAG!");
        }

        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        metrics.startTiming();

        
        for (int u : topoOrder) {
            metrics.incrementCounter("vertices_processed");
            
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getAdjacent(u)) {
                    int v = edge.to;
                    int weight = getEdgeWeight(u, edge);
                    
                    metrics.incrementCounter("edge_relaxations");
                    
                
                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                        metrics.incrementCounter("successful_relaxations");
                    }
                }
            }
        }

        metrics.stopTiming();

        return new PathResult(dist, parent, topoOrder);
    }

    
    public PathResult longestPaths(int source) {
        int n = graph.getVertexCount();
        
       
        TopologicalSort topoSort = new TopologicalSort(graph);
        List<Integer> topoOrder = topoSort.kahnSort();
        
        if (topoOrder.isEmpty()) {
            throw new IllegalArgumentException("Graph contains a cycle - not a DAG!");
        }

        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        metrics.startTiming();

       
        for (int u : topoOrder) {
            metrics.incrementCounter("vertices_processed");
            
            if (dist[u] != Integer.MIN_VALUE) {
                for (Graph.Edge edge : graph.getAdjacent(u)) {
                    int v = edge.to;
                    int weight = getEdgeWeight(u, edge);
                    
                    metrics.incrementCounter("edge_relaxations");
                    
                   
                    if (dist[u] + weight > dist[v]) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                        metrics.incrementCounter("successful_relaxations");
                    }
                }
            }
        }

        metrics.stopTiming();

        return new PathResult(dist, parent, topoOrder);
    }

   
    private int getEdgeWeight(int u, Graph.Edge edge) {
        if ("node".equals(weightModel)) {
           
            return graph.getNodeWeight(edge.to);
        } else {
           
            return edge.weight;
        }
    }

    public List<Integer> reconstructPath(int source, int target, int[] parent) {
        if (parent[target] == -1 && source != target) {
            return new ArrayList<>(); 
        }

        List<Integer> path = new ArrayList<>();
        int current = target;
        
        while (current != -1) {
            path.add(current);
            if (current == source) break;
            current = parent[current];
        }
        
        Collections.reverse(path);
        return path;
    }

    
    public CriticalPathResult findCriticalPath(int source) {
        PathResult result = longestPaths(source);
        
       
        int maxDist = Integer.MIN_VALUE;
        int endVertex = -1;
        
        for (int i = 0; i < result.distances.length; i++) {
            if (result.distances[i] != Integer.MIN_VALUE && result.distances[i] > maxDist) {
                maxDist = result.distances[i];
                endVertex = i;
            }
        }
        
        List<Integer> criticalPath = reconstructPath(source, endVertex, result.parent);
        
        return new CriticalPathResult(criticalPath, maxDist, endVertex);
    }

    public static class CriticalPathResult {
        public final List<Integer> path;
        public final int length;
        public final int endVertex;

        public CriticalPathResult(List<Integer> path, int length, int endVertex) {
            this.path = path;
            this.length = length;
            this.endVertex = endVertex;
        }

        @Override
        public String toString() {
            return String.format("Critical Path: %s, Length: %d, End: %d", path, length, endVertex);
        }
    }

   
    public Metrics getMetrics() {
        return metrics;
    }

    
    public void printShortestPaths(int source, PathResult result) {
        System.out.println("\n=== Shortest Paths from Source " + source + " ===");
        System.out.println("Weight model: " + weightModel);
        
        for (int i = 0; i < result.distances.length; i++) {
            if (result.distances[i] == Integer.MAX_VALUE) {
                System.out.printf("Vertex %d: unreachable%n", i);
            } else {
                List<Integer> path = reconstructPath(source, i, result.parent);
                System.out.printf("Vertex %d: distance=%d, path=%s%n", 
                                i, result.distances[i], path);
            }
        }
        
        System.out.println("\nMetrics:");
        System.out.println(metrics.getSummary());
    }

    
    public void printLongestPaths(int source, PathResult result) {
        System.out.println("\n=== Longest Paths from Source " + source + " ===");
        System.out.println("Weight model: " + weightModel);
        
        for (int i = 0; i < result.distances.length; i++) {
            if (result.distances[i] == Integer.MIN_VALUE) {
                System.out.printf("Vertex %d: unreachable%n", i);
            } else {
                List<Integer> path = reconstructPath(source, i, result.parent);
                System.out.printf("Vertex %d: distance=%d, path=%s%n", 
                                i, result.distances[i], path);
            }
        }
        
        System.out.println("\nMetrics:");
        System.out.println(metrics.getSummary());
    }

    
    public void printCriticalPath(int source) {
        CriticalPathResult result = findCriticalPath(source);
        System.out.println("\n=== Critical Path Analysis ===");
        System.out.println(result);
        System.out.println("\nMetrics:");
        System.out.println(metrics.getSummary());
    }
}
