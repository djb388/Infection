package infection;

import java.util.ArrayList;

/**
 * Class models vertices in a graph
 * @author David Bell
 */
public abstract class ClusterVertex {

    /**
     * Get the unique ID of this vertex
     * @return
     */
    public abstract int id();

    /**
     * Get the label for the cluster this vertex is a member of
     * @return
     */
    public abstract int getClusterLabel(); 
    
    /**
     * Set the cluster label for this vertex
     * @param label new cluster label
     */
    public abstract void setClusterLabel(int label); 

    /**
     * Get the vertices this vertex is connected to
     * @return
     */
    public abstract ArrayList<ClusterVertex> edges(); 
}
