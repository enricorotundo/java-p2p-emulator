package client;

import java.io.Serializable;
import java.util.Vector;

import resource.Resource;

public interface ClientInterface extends Serializable {

	/**
	 * Connect the client to the p2p system.
	 */
	public void connectToServer();

	/**
	 * @return the client name identifier
	 */
	public String getClientName();

	/**
	 * @return client's resources.
	 */
	public Vector<Resource> getResources();

	public void performSearch();

	/**
	 * @return true if and only if the connection is up.
	 */
	public Resource requestResource(Resource paramResquestedResource);

	@Override
	public String toString();
}
