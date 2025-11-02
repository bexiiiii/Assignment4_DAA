import common.Graph;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import utils.DatasetGenerator;

import java.io.IOException;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        try {
           
            if (args.length > 0 && args[0].equals("--generate")) {
                String outputDir = args.length > 1 ? args[1] : "data";
                DatasetGenerator.generateAllDatasets(outputDir);
                return;
            }

            
            String filename = args.length > 0 ? args[0] : "tasks (1).json";
            runCompleteAnalysis(filename);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            printUsage();
        }
    }

   
    public static void runCompleteAnalysis(String filename) throws IOException {
       
        System.out.println("\nAnalyzing dataset: " + filename);
        
       
        Graph graph = Graph.fromJsonFile(filename);
        int source = Graph.getSourceFromJson(filename);
        if (source == -1) {
            source = 0; 
        }

        System.out.println("\n" + graph);
        
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("STEP 1: STRONGLY CONNECTED COMPONENTS (Tarjan's Algorithm)");
        System.out.println("=".repeat(60));
        
        TarjanSCC sccFinder = new TarjanSCC(graph);
        List<List<Integer>> sccs = sccFinder.findSCCs();
        sccFinder.printSCCs();

    
        System.out.println("\n" + "=".repeat(60));
        System.out.println("CONDENSATION GRAPH (DAG of SCCs)");
        System.out.println("=".repeat(60));
        
        Graph condensation = sccFinder.buildCondensation();
        System.out.println(condensation);


        System.out.println("\n" + "=".repeat(60));
        System.out.println("STEP 2: TOPOLOGICAL SORT (Kahn's Algorithm)");
        System.out.println("=".repeat(60));
        
        TopologicalSort topoSort = new TopologicalSort(condensation);
        List<Integer> topoOrder = topoSort.kahnSort();
        topoSort.printTopologicalOrder(topoOrder);

  
        if (!topoOrder.isEmpty()) {
            System.out.println("\n--- Derived Task Order from SCC Condensation ---");
            System.out.print("SCC Order: ");
            for (int sccId : topoOrder) {
                System.out.print("SCC" + sccId + " ");
            }
            System.out.println();
            
            System.out.println("\nExpanded Task Order:");
            for (int sccId : topoOrder) {
                List<Integer> sccNodes = sccs.get(sccId);
                System.out.println("  SCC " + sccId + " contains tasks: " + sccNodes);
            }
        }


        if (!topoOrder.isEmpty()) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("STEP 3: DAG SHORTEST & LONGEST PATHS");
            System.out.println("=".repeat(60));
            

            int condensationSource = sccFinder.getSccId(source);
            
            DAGShortestPath dagSP = new DAGShortestPath(condensation);
            

            DAGShortestPath.PathResult shortestResult = dagSP.shortestPaths(condensationSource);
            dagSP.printShortestPaths(condensationSource, shortestResult);
            

            System.out.println("\n" + "-".repeat(60));
            DAGShortestPath.PathResult longestResult = dagSP.longestPaths(condensationSource);
            dagSP.printLongestPaths(condensationSource, longestResult);
            

            dagSP.printCriticalPath(condensationSource);
        } else {
            System.out.println("\n[WARNING] Cannot compute DAG paths: graph contains cycles");
            System.out.println("The condensation step should have resolved this, but the graph may be fully cyclic.");
        }


        System.out.println("\n" + "=".repeat(60));
        System.out.println("ANALYSIS COMPLETE");
        System.out.println("=".repeat(60));
        System.out.println("✓ SCCs found: " + sccFinder.getSccCount());
        System.out.println("✓ Condensation nodes: " + condensation.getVertexCount());
        System.out.println("✓ Condensation edges: " + condensation.getEdgeCount());
        System.out.println("✓ Topological order: " + (topoOrder.isEmpty() ? "N/A (cyclic)" : "Valid"));
        System.out.println("✓ Weight model: " + graph.getWeightModel());
    }


    private static void printUsage() {
        System.out.println("\n=== USAGE ===");
        System.out.println("Run analysis:");
        System.out.println("  java -jar assignment4.jar <graph-file.json>");
        System.out.println("  mvn exec:java -Dexec.args=\"<graph-file.json>\"");
        System.out.println("\nGenerate datasets:");
        System.out.println("  java -jar assignment4.jar --generate [output-dir]");
        System.out.println("  mvn exec:java -Dexec.args=\"--generate data\"");
        System.out.println("\nRun tests:");
        System.out.println("  mvn test");
    }
}
