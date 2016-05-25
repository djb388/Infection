package infection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

/**
 * Class for finding clusters of ClusterVertex vertices
 * 
 * @author David Bell
 */
public class Clusters {
    
    /**
     * Array containing every vertex.
     */
    protected final ClusterVertex[] vertices;
    
    /**
     * HashMap containing <getClusterLabel,clusterPopulation> key-value pair. 
     * Maps a cluster's label to its vertices.
     */
    protected final HashMap<Integer,ArrayList<Integer>> clusterPopulations;

    /**
     * HashMap containing <vertexID,getClusterLabel> key-value pair. 
     * Maps vertices to their respective clusters.
     */
    protected final HashMap<Integer,Integer> clusterLookup;
    
    /**
     * Constructor builds clusters.
     * @param vertices array containing all vertices in the graph
     */
    public Clusters(ClusterVertex[] vertices) {
        
        this.vertices = vertices;
        clusterPopulations = buildAllClusters(vertices);
        clusterLookup = Clusters.createClusterLookup(clusterPopulations);
    }
    
    /**
     * Clusters algorithm assigns cluster labels to every vertex, selecting
 vertices at random.
     * 
     * @return HashMap with <vertexID,getClusterLabel> key-value pair
     */
    public HashMap<Integer,ArrayList<Integer>> buildAllClustersRandomly() {
        
        // Create a list containing every vertex ID
        ArrayList<Integer> vertexIDs = new ArrayList<>();
        
        for (int i = 0; i < vertices.length; i++) {
            vertexIDs.add(i, i);
        }
        
        // HashMap containing all clusters 
        HashMap<Integer,ArrayList<Integer>> clusters = new HashMap<>();
        
        // Array for indicating when vertices have been clustered
        boolean[] closedSet = new boolean[vertices.length];
        
        int clusterLabel = 0;
        
        // For each vertex...
        for (int i = 0; i < vertices.length; i++) {
            
            // Remove a vertexID from the ArrayList
            int vertexID = vertexIDs.remove((int) (Math.random() * vertexIDs.size()));
            ClusterVertex vertex = vertices[vertexID];
            
            // If the vertex is in the closed set, skip this iteration
            if (closedSet[vertex.id()]) {
                continue;
            }
            
            // Create a new cluster
            ArrayList<Integer> cluster = buildCluster(vertex, closedSet, clusterLabel);
            
            // Add the cluster to the clusters hash
            clusters.put(clusterLabel, cluster);
            
            clusterLabel++;
        }
        
        return clusters;
    } 
    
    /**
     * Clusters algorithm assigns cluster labels to vertices. 
     * Clusters labels represent groups of connected vertices.
     * 
     * @return HashMap with <vertexID,getClusterLabel> key-value pair
     */
    public HashMap<Integer,ArrayList<Integer>> buildAllClusters() {
        
        return buildAllClusters(vertices);
    }
    
    /**
     * Clusters algorithm assigns cluster labels to vertices. 
     * Clusters labels represent groups of connected vertices.
     * 
     * @param vertices array containing all vertices
     * @return HashMap with <vertexID,getClusterLabel> key-value pair
     */
    public static HashMap<Integer,ArrayList<Integer>> buildAllClusters(
            ClusterVertex[] vertices) {
        
        // HashMap containing all clusters 
        HashMap<Integer,ArrayList<Integer>> clusters = new HashMap<>();
        
        // Array for indicating when vertices have been clustered
        boolean[] closedSet = new boolean[vertices.length];
        
        int clusterLabel = 0;
        
        // For each vertex...
        for (ClusterVertex vertex : vertices) {
            
            // If the vertex is in the closed set, skip this iteration
            if (closedSet[vertex.id()]) {
                continue;
            }
            
            // Create a new cluster
            ArrayList<Integer> cluster = buildCluster(vertex, closedSet, clusterLabel);
            
            // Add the cluster to the clusters hash
            clusters.put(clusterLabel, cluster);
            
            clusterLabel++;
        }
        
        return clusters;
    } 

    /**
     * Build a single cluster that contains a given vertex.
     * 
     * @param rootVertex root member of the cluster
     * @param closedSet set of vertices that have been clustered
     * @param clusterLabel label for this cluster
     * @return HashMap with <vertexID,getClusterLabel> key-value pair
     */
    public static ArrayList<Integer> buildCluster(
            ClusterVertex rootVertex,
            boolean[] closedSet,
            int clusterLabel) {
        
        // Initialize open set
        Stack<ClusterVertex> openSet = new Stack<>();
        
        // Push the root vertex onto the open set
        openSet.push(rootVertex);
        
        ArrayList<Integer> cluster = new ArrayList<>();
        
        while (!openSet.empty()) {
            
            ClusterVertex vertex = openSet.pop();
            
            if (closedSet[vertex.id()]) {
                continue;
            }
            
            /* Push every vertex this vertex is connected to onto the open 
            set stack if the vertex is not already in the stack. */
            vertex.edges().stream().filter((coach) 
                    -> (!closedSet[coach.id()])).forEach((coach) -> {
                openSet.add(coach);
            });
            
            // Add this vertex to the closed set and cluster
            cluster.add(vertex.id());
            vertex.setClusterLabel(clusterLabel);
            closedSet[vertex.id()] = true;
        }
        
        return cluster;
    }
    
    /**
     * Create a table mapping vertex IDs to cluster labels
     * @param clusters HashMap with <getClusterLabel,verticesInCluster> key-value pair
     * @return HashMap with <vertexID,getClusterLabel> key-value pair
     */
    public static HashMap<Integer,Integer> createClusterLookup(
            HashMap<Integer,ArrayList<Integer>> clusters) {
        
        // HashMap for the cluster lookup table
        HashMap<Integer,Integer> lookup = new HashMap<>();
        
        for (int i = 0; i < clusters.size(); i++) {
            
            // Get the next cluster
            ArrayList<Integer> cluster = clusters.get(i);
            
            // Add every vertexID in the cluster as a key to the lookup HashMap
            for (Integer vertexID : cluster) {
                lookup.put(vertexID, i);
            }
        }
        
        return lookup;
    }
    
    /**
     * Getter method for the clusterLookup HashMap
     * @return 
     */
    protected HashMap<Integer,Integer> getClusterLookup() {
        return clusterLookup;
    }
    
    /**
     * Getter method for the clusterPopulations HashMap
     * @return 
     */
    protected HashMap<Integer,ArrayList<Integer>> getClusterPopulations() {
        return clusterPopulations;
    }
    
    /**
     * Get the total number of vertices in this set of clusters
     * @return 
     */
    public int size() {
        return vertices.length;
    }
    
    /**
     * Print info for this cluster
     */
    public void printClusterData() {
        Integer[] labels = new Integer[clusterPopulations.size()];
        labels = clusterPopulations.keySet().toArray(labels);
        
        int[] sizes = new int[labels.length];
        
        double meanSize = 0;
        int maxSize = 0;
        int minSize = Integer.MAX_VALUE;
        HashMap<Integer,Integer> numOfSizes = new HashMap<>();
        
        for (int i = 0; i < labels.length; i++) {
            
            int size = clusterPopulations.get(labels[i]).size();
            
            int numOfSize = numOfSizes.getOrDefault(size, 0) + 1;
            numOfSizes.put(size, numOfSize);
            
            meanSize += size;
            sizes[i] = size;
            
            if (size > maxSize) {
                maxSize = size;
            }
            
            if (size < minSize) {
                minSize = size;
            }
        }
        
        Arrays.sort(sizes);
        
        meanSize /= labels.length;
        double sizeStdDev = 0;

        for (int i = 0; i < sizes.length; i++) {
            
            double error = sizes[i] - meanSize;
            sizeStdDev += error * error;
        }
        
        sizeStdDev = Math.sqrt(sizeStdDev / sizes.length);
        
        System.out.println("------------------------------------------------");
        
        System.out.println("Size and number of every cluster:");
        System.out.println();
        int lastSize = -1;
        
        for (int i = 0; i < sizes.length; i++) {
            
            if (numOfSizes.containsKey(sizes[i])) {
                int nextSize = numOfSizes.remove(sizes[i]);
                System.out.format("%10d,%d%n", sizes[i],nextSize);
            }
        }
        
        System.out.println();
        
        System.out.println("------------------------------------------------");
        
        System.out.println("  Max cluster size: " + maxSize);
        System.out.println("  Min cluster size: " + minSize);
        System.out.println(" Mean cluster size: " + meanSize);
        System.out.println("Standard deviation: " + sizeStdDev);
        System.out.println("Number of clusters: " + clusterPopulations.size());
        
        System.out.println("------------------------------------------------");
    }
    
    /**
     * Get the cluster that contains a specific vertex
     * @param vertex
     * @return cluster containing vertex or -1 if the vertex is not in a cluster
     */
    public int clusterContainingVertex(ClusterVertex vertex) {
        
        return clusterLookup.getOrDefault(vertex.id(), -1);
    }
    
    /**
     * Get the vertices of a specific cluster
     * @param clusterLabel cluster identifier
     * @return vertices in cluster clusterLabel or null if label is invalid
     */
    public ArrayList<Integer> verticesInCluster(int clusterLabel) {
        
        return clusterPopulations.getOrDefault(clusterLabel, null);
    }
    
    /**
     * Get the size of a specific cluster
     * @param clusterLabel cluster identifier
     * @return size of cluster clusterLabel or -1 if label is invalid
     */
    public int sizeOfCluster(int clusterLabel) {
        
        int size = -1;
        
        if (clusterPopulations.containsKey(clusterLabel)) {
            size = clusterPopulations.get(clusterLabel).size();
        }
        
        return size;
    }
}