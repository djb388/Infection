package infection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * total_infection and limited_infection algorithm implementations. The meat of
 * the algorithms are in the extended class Clusters, where all
 * coaches/coached-by relation graphs are efficiently clustered.
 *
 * @author David Bell
 */
public class Infection extends Clusters {

    /**
     * Constructor.
     *
     * @param users array containing every user
     */
    public Infection(User[] users) {
        super(users);
    }

    /**
     * Total infection algorithm. Finds and labels the entire connected graph
     * for the coached/coached-by relation of a random root user.
     *
     * @param newWebsiteVersion version ID that users will be 'infected' with
     * @return number of users infected
     */
    public int total_infection(int newWebsiteVersion) {

        int randID = (int) (Math.random() * vertices.length);
        int numInfected = total_infection((User) vertices[randID], newWebsiteVersion);

        return numInfected;
    }

    /**
     * Total infection algorithm. Given a root user, finds and labels the entire
     * graph of the coached/coached-by relation to which the root user is
     * connected.
     *
     * @param epicenter root user where the infection will spread from
     * @param newWebsiteVersion version ID that users will be 'infected' with
     * @return number of users infected
     */
    public int total_infection(User epicenter, int newWebsiteVersion) {

        boolean[] closedSet = new boolean[vertices.length];

        // Build the cluster
        ArrayList<Integer> cluster = Clusters.buildCluster(
                epicenter,
                closedSet,
                epicenter.getClusterLabel());

        // Update every user's website version
        cluster.stream().forEach((userID) -> {
            User user = (User) vertices[userID];
            user.updateWebsiteVersion(newWebsiteVersion);
        });

        // Return the number of users infected
        int numInfected = cluster.size();

        return numInfected;
    }

    /**
     * Limited infection algorithm with default target error. Given a root user
     * and a target vertices size, finds and labels a graph of the
     * coached/coached-by relation.
     *
     * @param targetPop target size of the infected vertices
     * @param newWebsiteVersion version ID that users will be 'infected' with
     * @return actual number of users infected
     */
    public int limited_infection(int targetPop, int newWebsiteVersion) {

        int numInfected = limited_infection(targetPop, newWebsiteVersion, 0.05f);
        return numInfected;
    }

    /**
     * Limited infection algorithm. Given a root user and a target vertices
     * size, finds and labels a graph of the coached/coached-by relation.
     *
     * @param targetPop target size of the infected vertices
     * @param newWebsiteVersion version ID that users will be 'infected' with
     * @param error allowable error (target +- target * float in range (0,1))
     * @return actual number of users infected
     */
    public int limited_infection(int targetPop, int newWebsiteVersion, float error) {

        int numInfected = 0;    // Running total users infected
        int numClustersInfected = 0;    // Running total clusters infected

        int iterationThresh = vertices.length; // Max allowable iterations
        int meanClusterSize = 0; // Mean cluster size to estimate iterationThresh

        // HashMap aggregates infected clusters
        HashMap<Integer, ArrayList<Integer>> infectedClusters = new HashMap<>();

        boolean[] closedSet = new boolean[vertices.length];

        // Until we iterate an unreasonable number of times, try to hit targetPop
        for (int i = 0; i < iterationThresh; i++) {

            int randID = (int) (Math.random() * vertices.length);

            // While the random user is infected, get another random user
            while (closedSet[randID]) {
                randID = (int) (Math.random() * vertices.length);
            }

            User epicenter = (User) vertices[randID];

            // Build the cluster and add its contents to the closed set
            ArrayList<Integer> cluster = Clusters.buildCluster(
                    epicenter,
                    closedSet,
                    epicenter.getClusterLabel());

            int clusterSize = cluster.size();
            meanClusterSize += clusterSize;

            // Check to see if this new cluster will put us over the target error
            float infectionPercent = clusterSize + numInfected;
            infectionPercent /= (float) targetPop;

            if (infectionPercent <= 1 + error) {

                infectedClusters.put(numClustersInfected, cluster);

                numInfected += clusterSize;
                numClustersInfected++;
            }

            float infectionError = Math.abs(1 - infectionPercent);

            if (infectionError <= error) {
                break;
            }

            // Get a better idea as to how long this search should take
            if (i == 100 && iterationThresh == vertices.length) {

                int numUsersNeeded = targetPop - numInfected;
                meanClusterSize /= 100;
                float needToMean = (float) numUsersNeeded / meanClusterSize;
                iterationThresh = (int) (100.0f * needToMean);
            }
        }

        // Get all of the userIDs in the clusters HashMap
        Integer[] userIDs = new Integer[infectedClusters.size()];
        userIDs = infectedClusters.keySet().toArray(userIDs);

        // Update every user's website version
        for (int userID : userIDs) {
            User user = (User) vertices[userID];
            user.updateWebsiteVersion(newWebsiteVersion);
        }

        // Return the number of users infected
        return numInfected;
    }

    /**
     * Tiered infection utilizes the full clustering algorithm, sorting the
     * results and distributing website versions among the user population.
     *
     * @param versions
     * @return actual infected populations
     */
    public double[][] tiered_infection(int[] versions) {

        // Get cluster labels
        Integer[] labels = new Integer[clusterPopulations.size()];
        labels = clusterPopulations.keySet().toArray(labels);

        HashMap<Integer, ArrayList<Integer>> sizesToLabels
                = new HashMap<>();

        for (int label : labels) {

            int size = clusterPopulations.get(label).size();

            if (sizesToLabels.containsKey(size)) {
                sizesToLabels.get(size).add(label);
            } else {
                ArrayList<Integer> labelList = new ArrayList<>();
                labelList.add(label);
                sizesToLabels.put(size, labelList);
            }
        }

        // Get a sorted array of all sizes of clusters
        Integer[] sizes = new Integer[sizesToLabels.size()];
        sizes = sizesToLabels.keySet().toArray(sizes);
        Arrays.sort(sizes);

        // Output data
        double[] clustersAdded = new double[4];
        double[] numInfected = new double[4];
        
        // Determine target populations
        double[] targetPercent = new double[] { 0.05, 0.10, 0.15, 0.70 };
        
        // Track current tier size
        int tier = 3;

        // Iterate from biggest to smallest cluster size
        for (int i = sizes.length; --i >= 0;) {

            // Get the next set of clusters
            int size = sizes[i];
            ArrayList<Integer> clusters = sizesToLabels.get(sizes[i]);

            // Iterate through all clusters of the current size
            for (int clusterLabel : clusters) {

                // Get the list of users in the next cluster
                ArrayList<Integer> cluster = clusterPopulations.get(clusterLabel);
                
                // If almost met the quota for this tier, do the next tier
                if (numInfected[tier] >= targetPercent[tier] * vertices.length) {
                    
                    tier -= 1;
                    
                    if (tier < 0) {
                        
                        // Find the tier that is least full (percent-wise)
                        for (int j = 0; j < 3; j++) {
                            
                            double tierJ = numInfected[j] / vertices.length;
                            tierJ = tierJ / targetPercent[j];
                            
                            for (int k = j+1; k < 4; k++) {
                                
                                double tierK = numInfected[k] / vertices.length;
                                tierK /= targetPercent[k];
                                
                                if (tierJ > tierK) {
                                    tier = j;
                                } else {
                                    tier = k;
                                }
                            }
                        }
                    }
                }
                
                numInfected[tier] += size;
                clustersAdded[tier] += 1;

                // Update website versions of all users in the cluster
                for (int userID : cluster) {
                    User user = (User) vertices[userID];
                    user.updateWebsiteVersion(versions[tier]);
                }
            }
        }
        
        return new double[][]{ clustersAdded, numInfected };
    }
}
