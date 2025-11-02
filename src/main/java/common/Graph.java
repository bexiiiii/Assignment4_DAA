package common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adjList;
    private final String weightModel;
    private final int[] nodeWeights;

    
    public static class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("->%d(w=%d)", to, weight);
        }
    }

   
    public Graph(int n, boolean directed, String weightModel) {
        this.n = n;
        this.directed = directed;
        this.weightModel = weightModel != null ? weightModel : "edge";
        this.adjList = new ArrayList<>(n);
        this.nodeWeights = new int[n];
        
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
            nodeWeights[i] = 1; 
        }
    }

    
    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        adjList.get(from).add(new Edge(to, weight));
    }

   
    public void setNodeWeight(int node, int weight) {
        if (node < 0 || node >= n) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        nodeWeights[node] = weight;
    }

    
    public List<Edge> getAdjacent(int v) {
        if (v < 0 || v >= n) {
            throw new IllegalArgumentException("Invalid vertex index");
        }
        return adjList.get(v);
    }

    
    public int getVertexCount() {
        return n;
    }

   
    public boolean isDirected() {
        return directed;
    }

   
    public String getWeightModel() {
        return weightModel;
    }

   
    public int getNodeWeight(int node) {
        return nodeWeights[node];
    }

    
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjList) {
            count += edges.size();
        }
        return directed ? count : count / 2;
    }

   
    public static Graph fromJsonFile(String filename) throws IOException {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(new FileReader(filename), JsonObject.class);

        boolean directed = json.get("directed").getAsBoolean();
        int n = json.get("n").getAsInt();
        String weightModel = json.has("weight_model") ? 
                            json.get("weight_model").getAsString() : "edge";

        Graph graph = new Graph(n, directed, weightModel);

        
        JsonArray edges = json.getAsJsonArray("edges");
        for (JsonElement edgeElement : edges) {
            JsonObject edge = edgeElement.getAsJsonObject();
            int u = edge.get("u").getAsInt();
            int v = edge.get("v").getAsInt();
            int w = edge.has("w") ? edge.get("w").getAsInt() : 1;
            graph.addEdge(u, v, w);
        }

        
        if (json.has("node_weights")) {
            JsonArray nodeWeights = json.getAsJsonArray("node_weights");
            for (int i = 0; i < nodeWeights.size() && i < n; i++) {
                graph.setNodeWeight(i, nodeWeights.get(i).getAsInt());
            }
        }

        return graph;
    }

  
    public static int getSourceFromJson(String filename) throws IOException {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(new FileReader(filename), JsonObject.class);
        return json.has("source") ? json.get("source").getAsInt() : -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Graph: n=%d, edges=%d, directed=%b, weightModel=%s%n",
                n, getEdgeCount(), directed, weightModel));
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%d: %s%n", i, adjList.get(i)));
        }
        return sb.toString();
    }
}
