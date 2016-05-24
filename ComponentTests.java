package infection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class containing test methods for every component of the project.
 * 
 * @author David Bell
 */
public class ComponentTests {

    /**
     * Test and debug the randomization of the user base
     *
     * @param users array containing every user
     * @param maxClassSize max number of students a user can coach
     */
    public static void testUserBaseCreation(User[] users, int maxClassSize) {

        System.out.println("------------------------------------------------");
        System.out.println("---------- User population statistics ----------");
        System.out.println("------------------------------------------------");

        // Numbers of users who coach <index> number of other users
        int[] coachesRelations = new int[maxClassSize];

        // Numbers of users who have <index> number of coaches
        int[] coachedByRelations = new int[maxClassSize];

        int coachesAndCoachedBy = 0;    // Users who are both coaches and students
        int justCoaches = 0;    // Users who are just coaches
        int justCoachedBy = 0;  // Users who are just students

        // Calculate coach/coached-by statistics
        for (User user : users) {

            // size of user coaches list
            int coachesSize = user.getCoachesRelationSize();

            // size of user coachedBy list
            int coachedBySize = user.getCoachedByRelationSize();
            
            coachesRelations[coachesSize] += 1;
            coachedByRelations[coachedBySize] += 1;

            boolean isStudent = coachedBySize > 0;
            boolean isCoach = coachesSize > 0;

            if (isCoach & isStudent) {
                coachesAndCoachedBy++;
            } else if (isCoach) {
                justCoaches++;
            } else if (isStudent) {
                justCoachedBy++;
            }
        }
        int notCoachOrStudent = users.length
                - coachesAndCoachedBy - justCoachedBy - justCoaches;

        System.out.println("------------------------------------------------");
        
        // Get the max indices of the coaches and coached by relations
        int maxCoachesIndex = 1;
        int maxCoachedByIndex = 1;
        
        for (int i = 0; i < maxClassSize; i++) {
            
            if (coachesRelations[i] != 0) {
                maxCoachesIndex = i;
            }
            
            if (coachedByRelations[i] != 0) {
                maxCoachedByIndex = i;
            }
        }
        

        System.out.println("number of students a user coaches, number of users");

        for (int i = 0; i < maxCoachesIndex; i++) {
            
            System.out.println("               " + i + ", " + coachesRelations[i]);
        }
        
        System.out.println("------------------------------------------------");
        
        System.out.println("number of coaches a user is coached by, number of users");

        for (int i = 0; i < maxCoachedByIndex; i++) {
            
            System.out.println("               " + i + ", " + coachedByRelations[i]);
        }
        
        System.out.println("------------------------------------------------");

        System.out.println("Users who are both students and coaches: "
                + coachesAndCoachedBy);

        System.out.println("Users who are students, but not coaches: "
                + justCoachedBy);

        System.out.println("Users who are coaches, but not students: "
                + justCoaches);

        System.out.println("Users who are not coaches or students:   "
                + notCoachOrStudent);

        System.out.println("------------------------------------------------");
        System.out.println();
    }

    /**
     * Test clustering algorithm
     *
     * @param cluster Clusters object that will be re-clustered multiple times
     */
    public static void clusteringAlgorithmTest(Clusters cluster) {

        System.out.println("------------------------------------------------");
        System.out.println("------ Clustering algorithm verification -------");
        System.out.println("------------------------------------------------");
        

        // Experiment variables
        int[] meanSizes = new int[10];
        int[] minSizes = new int[10];
        int[] maxSizes = new int[10];

        for (int i = 0; i < 10; i++) {
            
            int runNumber = i + 1;
            
            System.out.println("  Randomized clustering algorithm, run #" + runNumber);
            // HashMap containing <userID,getClusterLabel> key-value pair
            HashMap<Integer, ArrayList<Integer>> clusters
                    = cluster.buildAllClustersRandomly();

            long averageClusterSize = 0;
            int maxClusterSize = 0;
            int minClusterSize = Integer.MAX_VALUE;

            // Calculate mean average, max, and min cluster sizes
            for (int j = 0; j < clusters.size(); j++) {
                
                int size = clusters.get(j).size();
                averageClusterSize += size;
                
                if (size > maxClusterSize) {
                    maxClusterSize = size;
                } else if (size < minClusterSize) {
                    minClusterSize = size;
                }
            }

            averageClusterSize /= clusters.size();
            
            meanSizes[i] += averageClusterSize;
            minSizes[i] += minClusterSize;
            maxSizes[i] += maxClusterSize;

            System.out.println("         Largest cluster: " + maxClusterSize);
            System.out.println("        Smallest cluster: " + minClusterSize);
            System.out.println("    Average cluster size: " + averageClusterSize);

        }

        System.out.println("------------------------------------------------");
        
        // Calculate error between runs
        
        double averageMinSize = 0;
        double averageMaxSize = 0;
        double averageMeanSize = 0;

        for (int i = 0; i < 10; i++) {
            averageMinSize += minSizes[i];
            averageMaxSize += maxSizes[i];
            averageMeanSize += meanSizes[i];
        }

        averageMinSize /= 10;
        averageMaxSize /= 10;
        averageMeanSize /= 10;
        
        double absErrorMinSize = 0;
        double absErrorMaxSize = 0;
        double absErrorMeanSize = 0;

        for (int i = 0; i < 10; i++) {
            absErrorMinSize += Math.abs(minSizes[i] - averageMinSize);
            absErrorMaxSize += Math.abs(maxSizes[i] - averageMaxSize);
            absErrorMeanSize += Math.abs(meanSizes[i] - averageMeanSize);
        }

        absErrorMinSize /= 10;
        absErrorMaxSize /= 10;
        absErrorMeanSize /= 10;
        
        System.out.println("------------------------------------------------");
        
        System.out.println("  Mean absolute error of test runs:");
        System.out.println("    Maximum cluster size error: " + absErrorMaxSize);
        System.out.println("    Minimum cluster size error: " + absErrorMinSize);
        System.out.println("    Average cluster size error: " + absErrorMeanSize);
        
        System.out.println();
        
        System.out.println("  Conclusion:");
        
        if (absErrorMaxSize + absErrorMeanSize + absErrorMinSize == 0) {
            System.out.println("    Clustering algorithm is functioning properly");
        } else {
            System.out.println("    Clustering algorithm is not functioning properly");
        }
        
        System.out.println("------------------------------------------------");
        System.out.println();
    }

    /**
     * Test total infection
     *
     * @param users array containing every user
     * @param infection
     */
    public static void testTotalInfection(User[] users, Infection infection) {

        System.out.println("------------------------------------------------");
        System.out.println("---- total_infection algorithm verification ----");
        System.out.println("------------------------------------------------");
        
        HashMap<Integer, ArrayList<Integer>> clusterPopulations
                = infection.getClusterPopulations();
        
        HashMap<Integer, Integer> clusterLookup
                = infection.getClusterLookup();
        boolean failed = false;
        
        // Test total_infection
        for (int i = 1; i <= 10; i++) {
            
            int randID = (int) (Math.random() * users.length);
            User testUser = users[randID];
            
            int numInfected = infection.total_infection(testUser, 99);
            int cluster = clusterLookup.get(randID);
            int expectedNum = clusterPopulations.get(cluster).size();
            
            System.out.println("  total_infection run " + i + ":");
            System.out.println("    expected infected population: " + expectedNum);
            System.out.println("      actual infected population: " + numInfected);
            
            if (expectedNum != numInfected) {
                failed = true;
            }
        }

        System.out.println("------------------------------------------------");
        
        System.out.println("  Conclusion:");
        
        if (failed) {
            System.out.println("    Infection is not functioning properly");
        } else {
            System.out.println("    Infection is functioning properly");
        }
        
        System.out.println("------------------------------------------------");
        System.out.println();
    }
    
    /**
     * Test limited infection
     *
     * @param users array containing every user
     * @param infection
     */
    public static void testLimitedInfection(User[] users, Infection infection) {

        System.out.println("------------------------------------------------");
        System.out.println("------- Infection algorithm verification -------");
        System.out.println("------------------------------------------------");

        System.out.println("------------------------------------------------");
        
        // Test limited_infection
        for (int i = 1; i < 10; i++) {
            
            int randomTarget = (int) (Math.random() * 2000);
            int numInfected = infection.limited_infection(randomTarget, 99);
            
            System.out.println("  limited_infection test " + i + ":");
            System.out.println("    target infected population: " + randomTarget);
            System.out.println("    actual infected population: " + numInfected);
        }
        
        System.out.println("------------------------------------------------");
        System.out.println();
    }
}
