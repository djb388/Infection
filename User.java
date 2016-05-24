package infection;

import java.util.ArrayList;

/**
 * Model for a single user in the infection algorithm test.
 * 
 * @author David Bell
 */
public class User extends ClusterVertex {
    
    private final int userID; // Unique user ID
    private final ArrayList<User> coachedBy;  // This user's coaches
    private final ArrayList<User> coaches;  // This user's students
    private final ArrayList<ClusterVertex> edges; // Users connected to this user
    private int clusterLabel;   // Cluster this user is a member of
    private int websiteVersion;    // Version of the website this user sees
    
    /**
     * Constructor
     * @param newID unique user identification
     * @param webVersion version of the website this user sees
     */
    public User(int newID, int webVersion) {
        userID = newID;
        websiteVersion = webVersion;
        clusterLabel = -1;
        coachedBy = new ArrayList<>();
        coaches = new ArrayList<>();
        edges = new ArrayList<>();
    }
    
    @Override
    public int id() {
        return userID;
    }

    @Override
    public ArrayList<ClusterVertex> edges() {
        return edges;
    }

    @Override
    public int getClusterLabel() {
        return clusterLabel;
    }

    @Override
    public void setClusterLabel(int label) {
        clusterLabel = label;
    }
    
    /**
     * Update the version of the website this user sees
     * @param webVersion 
     */
    public void updateWebsiteVersion(int webVersion) {
        websiteVersion = webVersion;
    }
    
    /**
     * Add a user that this user is coached by
     * @param user 
     */
    public void addCoachedByRelation(User user) {
        coachedBy.add(user);
        edges.add(user);
    }
    
    /**
     * Add a user that this user coaches
     * @param user 
     */
    public void addCoachesRelation(User user) {
        coaches.add(user);
        edges.add(user);
    }
    
    /**
     * Get this user's website version 
     * @return 
     */
    public int websiteVersion() {
        return websiteVersion;
    }
    
    /**
     * Get this user's coaches
     * @return 
     */
    public ArrayList<User> getCoachedByRelation() {
        return coachedBy;
    }
    
    /**
     * Get the students coached by this user
     * @return 
     */
    public ArrayList<User> getCoachesRelation() {
        return coaches;
    }
    
    /**
     * Get the number of students coached by this user
     * @return 
     */
    public int getCoachesRelationSize() {
        return coaches.size();
    }
    
    /**
     * Get the number of coaches for this user
     * @return 
     */
    public int getCoachedByRelationSize() {
        return coachedBy.size();
    }
}