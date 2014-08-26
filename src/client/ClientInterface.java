package client;

import gui.ClientFrame;

import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;

public interface ClientInterface {
	/**
	 * Connect the client to the p2p system.
	 * @return 1 if connection is done, 0 if disconnection is done, -1 if something goes wrong.
	 */
	public Integer connect();
	/**
	 * @return true if and only if the connection is up.
	 */
	public Boolean getConnectionStatus();
	public Integer getMyDownloadCapaciy();
	public Vector<ResourcePart> getMyDownloadingParts();
	public ClientFrame getMyGuiClientFrame();
	public String getMyName();
	public Vector<Resource> getMyResources();
	public Resource requestResource(Resource paramResquestedResource);
	public void setMyDownloadCapaciy(final Integer myDownloadCapaciy);
	public void setMyDownloadingParts(final Vector<ResourcePart> myDownloadingParts);
	public void setMyGuiClientFrame(final ClientFrame myGuiClientFrame);
	public void setMyName(final String myName);
	public void setMyResources(final Vector<Resource> myResources);
}
