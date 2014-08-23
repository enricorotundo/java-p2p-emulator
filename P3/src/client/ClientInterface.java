/**
 * 
 */
package client;

import java.util.Vector;
import resource.Resource;

/**
 * @author erotundo
 *
 */
public interface ClientInterface {
	
	/**
	 * 
	 * @return the client's resources.
	 */
	public Vector<Resource> getMyResources();

	/**
	 * 
	 * @param myResources
	 */
	public void setMyResources(Vector<Resource> myResources);
	
//	/**
//	 * @return the myDownloadingResources
//	 */
//	public Vector<Resource> getMyDownloadingResources();

//	/**
//	 * @param myDownloadingResources the myDownloadingResources to set
//	 */
//	public void setMyDownloadingResources(Vector<Resource> myDownloadingResources);
	
	/**
	 * 
	 * @return the client's name.
	 */
	public String getMyName();

	/**
	 * @param myName the myName to set
	 */
	public void setMyName(String myName);
	
	/**
	 * Connect the client to the p2p system.
	 * @return 1 if connection is done, 0 if disconnection is done, -1 if something goes wrong.
	 */
	public Integer connect();
	
	/**
	 * 
	 * @return true if and only if the connection is up.
	 */
	public Boolean getConnectionStatus();

	public Resource requestResource(Resource paramResquestedResource);
	
}
