package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Dataset generator for graph testing.
 * Generates small, medium, and large datasets with varying structures.
 */
public class DatasetGenerator {

    private static class GraphData {
        boolean directed;
        int n;
        List<Edge> edges;
        int source;
        String weight_model;

        GraphData(boolean directed, int n, int source, String weightModel) {
            this.directed = directed;
            this.n = n;
            this.source = source;
            this.weight_model = weightModel;
            this.edges = new ArrayList<>();
        }
    }

    private static class Edge {
        int u, v, w;

        Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
    }

    /**
     * Generate a small graph (6-10 nodes).
     */
    public static void generateSmallGraphs(String outputDir) throws IOException {
        // Small 1: Simple DAG (7 nodes)
        generateSimpleDAG(outputDir + "/small_dag_1.json", 7, 0.4);
        
        // Small 2: Graph with 1-2 cycles (8 nodes)
        generateGraphWithCycles(outputDir + "/small_cyclic_1.json", 8, 2);
        
        // Small 3: Pure DAG (10 nodes)
        generatePureDAG(outputDir + "/small_dag_2.json", 10, 0.3);
    }

    /**
     * Generate medium graphs (10-20 nodes).
     */
    public static void generateMediumGraphs(String outputDir) throws IOException {
        // Medium 1: Mixed structure (12 nodes, sparse)
        generateMixedGraph(outputDir + "/medium_mixed_1.json", 12, 0.25, 2);
        
        // Medium 2: Multiple SCCs (15 nodes)
        generateGraphWithMultipleSCCs(outputDir + "/medium_scc_1.json", 15, 3);
        
        // Medium 3: Dense graph (18 nodes)
        generateDenseGraph(outputDir + "/medium_dense_1.json", 18, 0.6);
    }

    /**
     * Generate large graphs (20-50 nodes).
     */
    public static void generateLargeGraphs(String outputDir) throws IOException {
        // Large 1: Sparse DAG (25 nodes)
        generatePureDAG(outputDir + "/large_dag_1.json", 25, 0.15);
        
        // Large 2: Multiple SCCs (35 nodes)
        generateGraphWithMultipleSCCs(outputDir + "/large_scc_1.json", 35, 5);
        
        // Large 3: Performance test (50 nodes, dense)
        generateDenseGraph(outputDir + "/large_dense_1.json", 50, 0.4);
    }

    /**
     * Generate a simple DAG.
     */
    private static void generateSimpleDAG(String filename, int n, double density) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random(42);

        // Create edges only from lower to higher indices (ensures DAG)
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (rand.nextDouble() < density) {
                    int weight = rand.nextInt(10) + 1;
                    graph.edges.add(new Edge(i, j, weight));
                }
            }
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + ")");
    }

    /**
     * Generate a pure DAG with guaranteed connectivity.
     */
    private static void generatePureDAG(String filename, int n, double density) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random();

        // First create a path to ensure connectivity
        for (int i = 0; i < n - 1; i++) {
            int weight = rand.nextInt(10) + 1;
            graph.edges.add(new Edge(i, i + 1, weight));
        }

        // Add random edges (DAG property maintained)
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                if (rand.nextDouble() < density) {
                    int weight = rand.nextInt(10) + 1;
                    graph.edges.add(new Edge(i, j, weight));
                }
            }
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + ", DAG)");
    }

    /**
     * Generate a graph with cycles.
     */
    private static void generateGraphWithCycles(String filename, int n, int numCycles) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random();

        // Create some forward edges
        for (int i = 0; i < n - 1; i++) {
            int weight = rand.nextInt(10) + 1;
            graph.edges.add(new Edge(i, i + 1, weight));
        }

        // Add cycles
        for (int i = 0; i < numCycles; i++) {
            int start = rand.nextInt(n / 2);
            int end = start + rand.nextInt(n / 2) + 1;
            if (end < n) {
                int weight = rand.nextInt(10) + 1;
                graph.edges.add(new Edge(end, start, weight)); // Back edge creates cycle
            }
        }

        // Add some random edges
        for (int i = 0; i < n * 0.3; i++) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (u != v) {
                int weight = rand.nextInt(10) + 1;
                graph.edges.add(new Edge(u, v, weight));
            }
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + ", cyclic)");
    }

    /**
     * Generate a mixed graph (some cycles, some DAG parts).
     */
    private static void generateMixedGraph(String filename, int n, double density, int numCycles) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random();

        // Create forward edges
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (rand.nextDouble() < density) {
                    int weight = rand.nextInt(10) + 1;
                    graph.edges.add(new Edge(i, j, weight));
                }
            }
        }

        // Add some cycles
        for (int i = 0; i < numCycles; i++) {
            int v = rand.nextInt(n - 1) + 1;
            int u = rand.nextInt(v);
            int weight = rand.nextInt(10) + 1;
            graph.edges.add(new Edge(v, u, weight)); // Back edge
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + ", mixed)");
    }

    /**
     * Generate a graph with multiple SCCs.
     */
    private static void generateGraphWithMultipleSCCs(String filename, int n, int numSCCs) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random();

        int nodesPerSCC = n / numSCCs;
        
        // Create SCCs
        for (int scc = 0; scc < numSCCs; scc++) {
            int start = scc * nodesPerSCC;
            int end = (scc == numSCCs - 1) ? n : (scc + 1) * nodesPerSCC;
            
            // Create a cycle within SCC
            for (int i = start; i < end; i++) {
                int next = (i + 1 < end) ? i + 1 : start;
                int weight = rand.nextInt(10) + 1;
                graph.edges.add(new Edge(i, next, weight));
            }
            
            // Add internal edges
            for (int i = start; i < end; i++) {
                for (int j = start; j < end; j++) {
                    if (i != j && rand.nextDouble() < 0.3) {
                        int weight = rand.nextInt(10) + 1;
                        graph.edges.add(new Edge(i, j, weight));
                    }
                }
            }
        }

        // Add edges between SCCs (to form condensation DAG)
        for (int scc = 0; scc < numSCCs - 1; scc++) {
            int fromSCC = scc * nodesPerSCC + rand.nextInt(nodesPerSCC);
            int toSCC = (scc + 1) * nodesPerSCC + rand.nextInt(nodesPerSCC);
            int weight = rand.nextInt(10) + 1;
            graph.edges.add(new Edge(fromSCC, toSCC, weight));
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + 
                         ", SCCs=" + numSCCs + ")");
    }

    /**
     * Generate a dense graph.
     */
    private static void generateDenseGraph(String filename, int n, double density) throws IOException {
        GraphData graph = new GraphData(true, n, 0, "edge");
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && rand.nextDouble() < density) {
                    int weight = rand.nextInt(10) + 1;
                    graph.edges.add(new Edge(i, j, weight));
                }
            }
        }

        saveGraph(filename, graph);
        System.out.println("Generated: " + filename + " (n=" + n + ", edges=" + graph.edges.size() + ", dense)");
    }

    /**
     * Save graph to JSON file.
     */
    private static void saveGraph(String filename, GraphData graph) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(graph, writer);
        }
    }

    /**
     * Generate all datasets.
     */
    public static void generateAllDatasets(String outputDir) throws IOException {
        System.out.println("Generating datasets...");
        generateSmallGraphs(outputDir);
        generateMediumGraphs(outputDir);
        generateLargeGraphs(outputDir);
        System.out.println("All datasets generated successfully!");
    }

    public static void main(String[] args) {
        try {
            String outputDir = args.length > 0 ? args[0] : "data";
            generateAllDatasets(outputDir);
        } catch (IOException e) {
            System.err.println("Error generating datasets: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
