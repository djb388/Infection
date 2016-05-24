package infection;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class for the infection package.
 *
 * @author David Bell
 */
public class Main {

    // Scanner for reading user input
    private static final Scanner input = new Scanner(System.in);
    
    /**
     * Command line user interface for the infection package.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runUI();
    }

    /**
     * Build an array of randomized users with a unique user ID, random coaching
     * and coached-by relationships, and a random website version.
     *
     * The coaching and coached-by relationships are randomized
     *
     * @param size number of users in the array
     * @param numVersions number of versions active in the user base
     * @param maxClassSize maximum number of students a user can have
     * @param studentRatio change the student-coach ratio
     * @return
     */
    public static User[] getRandomUserBase(
            int size, int numVersions, int maxClassSize, int studentRatio) {

        User[] users = new User[size];

        // Initialize users
        for (int userID = 0; userID < size; userID++) {

            // Create a new user with a unique ID and a random website version
            users[userID] = new User(userID, (int) (Math.random() * numVersions));
        }
        
        // Randomize relationships
        for (User newUser : users) {

            // Get a random class size (number of students coached by this user)
            int classSize = maxClassSize;

            // Get a random class size (number of students coached by this user)
            for (int j = 0; j < studentRatio; j++) {

                // Creates log distribution of class sizes over all users
                classSize = (int) (Math.random() * classSize);
            }

            // Find classSize random users and create the coach relations
            for (int j = 0; j < classSize; j++) {

                int randID = (int) (Math.random() * size); // Get a random userID

                // Get a random userID that's not already a student of this user
                while (randID == newUser.id()
                        || newUser.getCoachesRelation().contains(users[randID])) {

                    randID = (int) (Math.random() * size); // Get a random userID
                }

                // Add the random user as a student of this user
                newUser.addCoachesRelation(users[randID]);

                // Add this user as a coach of the random user
                users[randID].addCoachedByRelation(newUser);
            }
        }

        return users;
    }
    
    /**
     * Run the command line user interface
     */
    public static void runUI() {
        int MAX_CLASS_SIZE = 64; // Students a coach can have
        int NUMBER_OF_USERS = 100000; // Total number of users
        int COACH_RATIO = 6; // Bigger = fewer coaches

        // Array containing every user
        User[] users = getRandomUserBase(NUMBER_OF_USERS, 1, MAX_CLASS_SIZE, COACH_RATIO);

        // Infection object containing clustered users
        Infection infection = new Infection(users);

        boolean run = true;

        System.out.println("#################################################");
        System.out.println("#              INFECTION ALGORITHM              #");
        System.out.println("#           Implemented by David Bell           #");
        System.out.println("#################################################");
        System.out.println("#                                               #");
        System.out.println("#  A demonstration of infection algorithms      #");
        System.out.println("#  which 'infect' a subset of a population.     #");
        System.out.println("#  The population represents the users of a     #");
        System.out.println("#  website, and the infection represents the    #");
        System.out.println("#  spread of updates to the website.            #");
        System.out.println("#                                               #");
        System.out.println("#################################################");
        System.out.println();

        do {
            System.out.println("Main menu; enter an option:");
            System.out.println();
            System.out.println(" 1) User population options");
            System.out.println();
            System.out.println(" 2) Print population variable values");
            System.out.println();
            System.out.println(" 3) Verify clustering algorithm functionality");
            System.out.println();
            System.out.println(" 4) Infection algorithm options");
            System.out.println();
            System.out.println(" Any other key to exit the program");

            System.out.print("> ");
            String line = input.nextLine();
            System.out.println();
            
            if (line.length() == 0) {
                line = "l";
            }
            
            switch (line.charAt(0)) {

                case '1':
                    boolean submenu1 = true;

                    do {
                        System.out.println("Population menu; enter an option:");
                        System.out.println();
                        System.out.println(" 1) Print population statistics");
                        System.out.println();
                        System.out.println(" 2) Cluster population and print cluster metadata");
                        System.out.println();
                        System.out.println(" 3) Change number of users");
                        System.out.println("    -Controls the user population size");
                        System.out.println();
                        System.out.println(" 4) Change max class size");
                        System.out.println("    -Sets the upper bound of the coaches relation");
                        System.out.println();
                        System.out.println(" 5) Change student ratio adjustment");
                        System.out.println("    -Bigger numbers reduce the number of large");
                        System.out.println("     coaches relations (3-6 are typically ideal)");
                        System.out.println();
                        System.out.println(" 6) Generate the new user population");
                        System.out.println();
                        System.out.println(" Any other key for the main menu");

                        System.out.print("> ");
                        line = input.nextLine();
                        System.out.println();

                        if (line.length() == 0) {
                            line = "l";
                        }
                        
                        switch (line.charAt(0)) {
                            case '1':
                                ComponentTests.testUserBaseCreation(users, MAX_CLASS_SIZE);
                                System.out.println();
                                promptGo();
                                break;

                            case '2':
                                if (infection == null) {
                                    System.out.println("Clustering users...");
                                    infection = new Infection(users);

                                    System.out.println(NUMBER_OF_USERS + " users clustered");
                                } else {
                                    System.out.println(NUMBER_OF_USERS + " users already clustered");
                                }
                                
                                infection = ensureNotNull(users, infection);
                                infection.printClusterData();
                                System.out.println();
                                promptGo();
                                break;
                                
                            case '3':
                                String message = "New number of users: ";
                                int value = getUnsignedInt(message);
                                
                                if (value > 0) {
                                    NUMBER_OF_USERS = value;
                                } else {
                                    NUMBER_OF_USERS = 0;
                                }
                                
                                System.out.println("Number of users = " + NUMBER_OF_USERS);
                                System.out.println();
                                
                                System.out.println("Generating users...");
                                users = getRandomUserBase(NUMBER_OF_USERS, 1, MAX_CLASS_SIZE, COACH_RATIO);
                                infection = null;

                                System.out.println("  " + users.length + " users created");
                                System.out.println();
                                break;

                            case '4':
                                message = "New max class size: ";
                                value = getUnsignedInt(message);

                                if (value > 0) {
                                    MAX_CLASS_SIZE = value;
                                } else {
                                    MAX_CLASS_SIZE = 0;
                                }
                                
                                System.out.println("Max class size = " + MAX_CLASS_SIZE);
                                System.out.println();
                                
                                System.out.println("Generating users...");
                                users = getRandomUserBase(NUMBER_OF_USERS, 1, MAX_CLASS_SIZE, COACH_RATIO);
                                infection = null;

                                System.out.println("  " + users.length + " users created");
                                System.out.println();
                                break;

                            case '5':
                                message = "New student ratio adjustment number: ";
                                value = getUnsignedInt(message);

                                if (value > 0) {
                                    COACH_RATIO = value;
                                } else {
                                    COACH_RATIO = 0;
                                }
                                
                                System.out.println("Student ratio adjustment number = " + COACH_RATIO);
                                System.out.println();
                                
                                System.out.println("Generating users...");
                                users = getRandomUserBase(NUMBER_OF_USERS, 1, MAX_CLASS_SIZE, COACH_RATIO);
                                infection = null;

                                System.out.println("  " + users.length + " users created");
                                System.out.println();
                                break;

                            case '6':
                                System.out.println("Generating users...");
                                users = getRandomUserBase(NUMBER_OF_USERS, 1, MAX_CLASS_SIZE, COACH_RATIO);
                                infection = null;

                                System.out.println("  " + users.length + " users created");
                                System.out.println();
                                break;

                            default:
                                submenu1 = false;
                                break;
                        }
                        System.out.println();
                    } while (submenu1);

                    break;

                case '2':
                    System.out.println("Current population variables:");
                    System.out.println(" Number of users = " + NUMBER_OF_USERS);
                    System.out.println("  Max class size = " + MAX_CLASS_SIZE);
                    System.out.println("   Student ratio = " + COACH_RATIO);
                    System.out.println();
                    promptGo();
                    break;

                case '3':
                    infection = ensureNotNull(users, infection);
                    ComponentTests.clusteringAlgorithmTest(infection);
                    System.out.println();
                    promptGo();
                    break;

                case '4':
                    boolean submenu4 = true;

                    do {
                        System.out.println("Infection menu; enter an option:");
                        System.out.println();
                        System.out.println(" 1) Print website version for a user's cluster");
                        System.out.println("    -Prints the website version of all members in a");
                        System.out.println("     specific user's cluster");
                        System.out.println();
                        System.out.println(" 2) Run total_infection on a specific user");
                        System.out.println();
                        System.out.println(" 3) Run limited_infection");
                        System.out.println();
                        System.out.println(" 4) Run tiered_infection");
                        System.out.println();
                        System.out.println("    -Infects all users, dividing 4 website versions among");
                        System.out.println("     approximately 5%, 10%, 15%, and 70% of the population.");
                        System.out.println();
                        System.out.println(" 5) Verify total_infection functionality");
                        System.out.println("    -Runs total_infection on 10 random users, comparing");
                        System.out.println("     its results to the clustering algorithm.");
                        System.out.println();
                        System.out.println(" 6) Verify limited_infection functionality");
                        System.out.println("    -Runs limited_infection 10 times, comparing its");
                        System.out.println("     results to the clustering algorithm.");
                        System.out.println();
                        System.out.println(" Any other key for the main menu");

                        System.out.print("> ");
                        line = input.nextLine();
                        
                        if (line.length() == 0) {
                            line = "l";
                        }

                        switch (line.charAt(0)) {
                            
                            case '1':
                                infection = ensureNotNull(users,infection);
                                int userID = getUserID(NUMBER_OF_USERS - 1);
                                
                                if (userID >= 0) {
                                    
                                    User user = users[userID];
                                    int label = user.getClusterLabel();
                                    ArrayList<Integer> cluster = infection.verticesInCluster(label);
                                    int myVersion = users[userID].websiteVersion();
                                    
                                    System.out.println("------------------------------------------------");
                                    System.out.println("User: " + userID);
                                    System.out.println("User's website version: " + myVersion);
                                    System.out.println("Cluster: " + label);
                                    System.out.println("Cluster size: " + cluster.size());
                                    
                                    int countSame = 0;
                                    int countNot = 0;
                                    
                                    for (int id : cluster) {
                                        if (users[userID].websiteVersion() == myVersion) {
                                            countSame++;
                                        } else {
                                            countNot++;
                                        }
                                    }
                                    
                                    System.out.println("Users with the same website version:  " + countSame);
                                    System.out.println("Users with different website version: " + countNot);
                                    System.out.println("------------------------------------------------");
                                } else {
                                    System.out.println("Invalid user ID or website version");
                                }
                                System.out.println();
                                promptGo();
                                break;

                            case '2':
                                infection = ensureNotNull(users,infection);
                                userID = getUserID(NUMBER_OF_USERS - 1);
                                
                                String message = "Enter website version number (positive integer):";
                                int version = getUnsignedInt(message);
                                
                                if (userID >= 0 && version >= 0) {
                                    User user = users[userID];
                                    int num = infection.total_infection(user, version);
                                    System.out.println("Infected " + num + " users");
                                } else {
                                    System.out.println("Invalid user ID or website version");
                                }
                                System.out.println();
                                promptGo();
                                break;

                            case '3':
                                infection = ensureNotNull(users,infection);
                                
                                message = "Enter website version number (positive integer):";
                                version = getUnsignedInt(message);
                                
                                message = "Enter target infection size number (positive integer):";
                                int target = getUnsignedInt(message);
                                
                                if (version >= 0 && target >= 0) {
                                    int num = infection.limited_infection(target, version);
                                    System.out.println("Infected " + num + " users");
                                } else {
                                    System.out.println("Invalid website version or infection size");
                                }
                                System.out.println();
                                promptGo();
                                break;
                                
                            case '4':
                                infection = ensureNotNull(users,infection);
                                
                                int[] versions = new int[4];
                                int[] populations = new int[4];
                                int[] percents = new int[]{ 5, 10, 15, 75 };
                                
                                System.out.println("Version number for x% of the population:");
                                
                                System.out.print(" ");
                                for (int i = 0; i < 4; i++) {
                                    System.out.print(" " + percents[i] + "%: ");
                                    versions[i] = getInt();
                                    populations[i] = (int)(users.length * percents[i] * 0.01);
                                }
                                
                                double[][] data = infection.tiered_infection(versions);
                                System.out.println("------------------------------------------------");
                                System.out.print(" ");
                                for (int i = 0; i < 4; i++) {
                                    System.out.println(
                                            percents[i] +
                                            "% population = " 
                                            + populations[i]
                                            + ", users infected = " 
                                            + data[1][i]
                                            + ", number of clusters = "
                                            + data[0][i]);
                                }
                                System.out.println("------------------------------------------------");
                                System.out.println();
                                promptGo();
                                break;
                                
                            case '5':
                                infection = ensureNotNull(users,infection);
                                ComponentTests.testTotalInfection(users, infection);
                                System.out.println();
                                promptGo();
                                break;

                            case '6':
                                infection = ensureNotNull(users,infection);
                                ComponentTests.testLimitedInfection(users, infection);
                                System.out.println();
                                promptGo();
                                break;

                            default:
                                submenu4 = false;
                                break;
                        }
                        System.out.println();
                    } while (submenu4);

                    break;

                default:
                    run = false;
                    break;
            }
        } while (run);

        System.out.println("---End infection program---");
        System.out.println();
    }

    private static Infection ensureNotNull(User[] users, Infection infection) {

        if (infection == null) {
            
            System.out.println("Clustering users...");
            infection = new Infection(users);

            System.out.println("  " + users.length + " users clustered");
        } 
        
        return infection;
    }

    /**
     * Get a userID from the scanner input
     * @param maxID max possible userID
     * @return a valid userID or -1 if input was invalid
     */
    private static int getUserID(int maxID) {
        
        String message = "Enter user ID between 0-" + maxID + ":";
        
        return getUnsignedInt(message);
    }
    
    /**
     * Get a positive integer from a string
     * @return a positive integer or -1 if input was invalid
     */
    private static int getUnsignedInt(String message) {
        
        System.out.println(message);
        System.out.print("> ");
        String line = input.nextLine();
        
        int value = -1;

        try {
            value = Integer.parseUnsignedInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number");
        }
        
        return value;
    }
    
    /**
     * Get a positive integer from a string
     * @return a positive integer or -1 if input was invalid
     */
    private static int getInt() {
        String line = input.nextLine();
        
        int value = -1;

        try {
            value = Integer.parseUnsignedInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid number");
        }
        
        return value;
    }
    
    /**
     * Prompts user to continue the program
     */
    private static void promptGo() {
        System.out.println("Enter any key to continue...");
        System.out.print("> ");
        String line = input.nextLine();
    }
}
