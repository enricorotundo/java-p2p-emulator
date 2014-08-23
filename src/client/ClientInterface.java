/**
 * 
 */
package client;

import gui.ClientFrame;

import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;

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
	public void setMyResources(final Vector<Resource> myResources);
	
	/**
	 * 
	 * @return the client's name.
	 */
	public String getMyName();

	/**
	 * Set the client name.
	 * @param myName the myName to set
	 */
	public void setMyName(final String myName);
	
	/**
	 * Connect the client to the p2p system.
	 * @return 1 if connection is done, 0 if disconnection is done, -1 if something goes wrong.
	 */
	public Integer connect();
	
	/**
	 * @return true if and only if the connection is up.
	 */
	public Boolean getConnectionStatus();

	/**
	 * Request a Resource from the network.
	 * @param paramResquestedResource
	 * @return
	 */
	public Resource requestResource(Resource paramResquestedResource);
	
	/**
	 * Gets the my download capaciy.
	 *
	 * @return the myDownloadCapaciy
	 */
	public Integer getMyDownloadCapaciy();
	
	/**
	 * Sets the my download capaciy.
	 *
	 * @param myDownloadCapaciy the myDownloadCapaciy to set
	 */
	public void setMyDownloadCapaciy(final Integer myDownloadCapaciy);
	
	/**
	 * Gets the my downloading parts.
	 *
	 * @return the myDownloadingParts
	 */
	public Vector<ResourcePart> getMyDownloadingParts();
	
	/**
	 * Sets the my downloading parts.
	 *
	 * @param myDownloadingParts the myDownloadingParts to set
	 */
	public void setMyDownloadingParts(final Vector<ResourcePart> myDownloadingParts);
	
	/**
	 * Sets the my gui client frame.
	 *
	 * @param myGuiClientFrame the myGuiClientFrame to set
	 */
	public void setMyGuiClientFrame(final ClientFrame myGuiClientFrame);
	
	/**
	 * Gets the my gui client frame.
	 *
	 * @return the myGuiClientFrame
	 */
	public ClientFrame getMyGuiClientFrame();
}
