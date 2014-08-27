package client;

import java.util.Vector;

import resource.Resource;

public interface ClientInterface {
	/**
	 * Connect the client to the p2p system.
	 * @return 1 if connection is done, 0 if disconnection is done, -1 if something goes wrong.
	 */
	public Integer connect();

	/**
	 * @return the client name identifier
	 */
	public String getClientName();

	/**
	 * @return client's resources.
	 */
	public Vector<Resource> getResources();

	/**
	 * @return true if and only if the connection is up.
	 */
	public Resource requestResource(Resource paramResquestedResource);
}
