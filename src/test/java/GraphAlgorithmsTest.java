import common.Graph;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmsTest {

   
    private Graph createSimpleDAG() {
        Graph g = new Graph(6, true, "edge");
        g.addEdge(0, 1, 3);
        g.addEdge(0, 3, 2);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 5, 1);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 5, 3);
        return g;
    }

   
    private Graph createCyclicGraph() {
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 0, 1);
        return g;
    }

    
    private Graph createMultiSCCGraph() {
        Graph g = new Graph(8, true, "edge");
        // SCC 1: 0->1->2->0
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        // SCC 2: 3->4->3
        g.addEdge(3, 4, 1);
        g.addEdge(4, 3, 1);
        // SCC 3: 5->6->7->5
        g.addEdge(5, 6, 1);
        g.addEdge(6, 7, 1);
        g.addEdge(7, 5, 1);
        // Connect SCCs
        g.addEdge(2, 3, 1);
        g.addEdge(4, 5, 1);
        return g;
    }

    @Test
    public void testSCCOnDAG() {
        Graph g = createSimpleDAG();
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> sccs = scc.findSCCs();
        
        // DAG should have n SCCs (each node is its own SCC)
        assertEquals(6, sccs.size(), "DAG should have 6 SCCs");
        
        for (List<Integer> component : sccs) {
            assertEquals(1, component.size(), "Each SCC should have size 1 in a DAG");
        }
    }

    @Test
    public void testSCCOnCyclicGraph() {
        Graph g = createCyclicGraph();
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> sccs = scc.findSCCs();
        
        // Should find 1 SCC containing all 4 vertices
        assertEquals(1, sccs.size(), "Should find 1 SCC");
        assertEquals(4, sccs.get(0).size(), "SCC should contain all 4 vertices");
    }

    @Test
    public void testSCCOnMultiSCCGraph() {
        Graph g = createMultiSCCGraph();
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> sccs = scc.findSCCs();
        
        // Should find 3 SCCs
        assertEquals(3, sccs.size(), "Should find 3 SCCs");
        
        // Check SCC sizes
        boolean hasSize3 = false;
        boolean hasSize2 = false;
        for (List<Integer> component : sccs) {
            if (component.size() == 3) hasSize3 = true;
            if (component.size() == 2) hasSize2 = true;
        }
        assertTrue(hasSize3, "Should have one SCC of size 3");
        assertTrue(hasSize2, "Should have one SCC of size 2");
    }

    @Test
    public void testCondensationIsDAG() {
        Graph g = createMultiSCCGraph();
        TarjanSCC scc = new TarjanSCC(g);
        scc.findSCCs();
        
        Graph condensation = scc.buildCondensation();
        
        // Condensation should be a DAG - check via topological sort
        TopologicalSort topo = new TopologicalSort(condensation);
        List<Integer> order = topo.kahnSort();
        
        assertFalse(order.isEmpty(), "Condensation should be a DAG");
        assertEquals(condensation.getVertexCount(), order.size(), 
                    "Topological sort should include all nodes");
    }

    @Test
    public void testTopologicalSortOnDAG() {
        Graph g = createSimpleDAG();
        TopologicalSort topo = new TopologicalSort(g);
        List<Integer> order = topo.kahnSort();
        
        assertEquals(6, order.size(), "Should include all 6 vertices");
        assertTrue(topo.isValidTopologicalOrder(order), "Order should be valid");
        
        // Check specific ordering constraints
        int pos0 = order.indexOf(0);
        int pos1 = order.indexOf(1);
        int pos2 = order.indexOf(2);
        int pos3 = order.indexOf(3);
        int pos5 = order.indexOf(5);
        
        assertTrue(pos0 < pos1, "0 should come before 1");
        assertTrue(pos1 < pos2, "1 should come before 2");
        assertTrue(pos0 < pos3, "0 should come before 3");
        assertTrue(pos2 < pos5, "2 should come before 5");
    }

    @Test
    public void testTopologicalSortOnCyclicGraph() {
        Graph g = createCyclicGraph();
        TopologicalSort topo = new TopologicalSort(g);
        List<Integer> order = topo.kahnSort();
        
        // Should return empty list due to cycle
        assertTrue(order.isEmpty(), "Cyclic graph should return empty topological order");
    }

    @Test
    public void testDFSTopologicalSort() {
        Graph g = createSimpleDAG();
        TopologicalSort topo = new TopologicalSort(g);
        List<Integer> order = topo.dfsSort();
        
        assertEquals(6, order.size(), "Should include all 6 vertices");
        assertTrue(topo.isValidTopologicalOrder(order), "DFS order should be valid");
    }

    @Test
    public void testShortestPathInDAG() {
        Graph g = createSimpleDAG();
        DAGShortestPath dagSP = new DAGShortestPath(g);
        DAGShortestPath.PathResult result = dagSP.shortestPaths(0);
        
        assertNotNull(result, "Result should not be null");
        assertEquals(6, result.distances.length, "Should have distances for all vertices");
        
        // Check some distances
        assertEquals(0, result.distances[0], "Distance to source should be 0");
        assertEquals(3, result.distances[1], "Distance to vertex 1");
        assertEquals(7, result.distances[2], "Distance to vertex 2");
        
        // Reconstruct path to vertex 5
        List<Integer> path = dagSP.reconstructPath(0, 5, result.parent);
        assertFalse(path.isEmpty(), "Path should exist");
        assertEquals(0, path.get(0), "Path should start at source");
        assertEquals(5, path.get(path.size() - 1), "Path should end at target");
    }

    @Test
    public void testLongestPathInDAG() {
        Graph g = createSimpleDAG();
        DAGShortestPath dagSP = new DAGShortestPath(g);
        DAGShortestPath.PathResult result = dagSP.longestPaths(0);
        
        assertNotNull(result, "Result should not be null");
        
        // Longest path should be greater than or equal to shortest
        assertTrue(result.distances[5] >= 8, "Longest path to vertex 5 should be at least 8");
    }

    @Test
    public void testCriticalPath() {
        Graph g = createSimpleDAG();
        DAGShortestPath dagSP = new DAGShortestPath(g);
        DAGShortestPath.CriticalPathResult critical = dagSP.findCriticalPath(0);
        
        assertNotNull(critical, "Critical path should not be null");
        assertFalse(critical.path.isEmpty(), "Critical path should not be empty");
        assertTrue(critical.length > 0, "Critical path length should be positive");
        assertEquals(0, critical.path.get(0), "Critical path should start at source");
    }

    @Test
    public void testSingleNodeGraph() {
        Graph g = new Graph(1, true, "edge");
        
        // Test SCC
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> sccs = scc.findSCCs();
        assertEquals(1, sccs.size(), "Single node should be one SCC");
        
        // Test topological sort
        TopologicalSort topo = new TopologicalSort(g);
        List<Integer> order = topo.kahnSort();
        assertEquals(1, order.size(), "Single node topological sort");
        
        // Test shortest path
        DAGShortestPath dagSP = new DAGShortestPath(g);
        DAGShortestPath.PathResult result = dagSP.shortestPaths(0);
        assertEquals(0, result.distances[0], "Distance to self should be 0");
    }

    @Test
    public void testDisconnectedGraph() {
        Graph g = new Graph(4, true, "edge");
        g.addEdge(0, 1, 1);
        g.addEdge(2, 3, 1);
        // Vertices 0-1 and 2-3 are disconnected
        
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> sccs = scc.findSCCs();
        assertEquals(4, sccs.size(), "Disconnected graph should have 4 SCCs");
    }

    @Test
    public void testMetricsRecording() {
        Graph g = createSimpleDAG();
        
        // Test SCC metrics
        TarjanSCC scc = new TarjanSCC(g);
        scc.findSCCs();
        assertTrue(scc.getMetrics().getCounter("DFS_visits") > 0, "Should record DFS visits");
        assertTrue(scc.getMetrics().getElapsedTimeNanos() > 0, "Should record time");
        
        // Test topological sort metrics
        TopologicalSort topo = new TopologicalSort(g);
        topo.kahnSort();
        assertTrue(topo.getMetrics().getCounter("queue_pops") > 0, "Should record queue operations");
        
        // Test DAG shortest path metrics
        DAGShortestPath dagSP = new DAGShortestPath(g);
        dagSP.shortestPaths(0);
        assertTrue(dagSP.getMetrics().getCounter("edge_relaxations") > 0, "Should record relaxations");
    }
}
