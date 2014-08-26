package client;

import resource.Resource;

public interface ClientInterface {
	/**
	 * Connect the client to the p2p system.
	 * @return 1 if connection is done, 0 if disconnection is done, -1 if something goes wrong.
	 */
	public Integer connect();
	public String getClientName();

	/**
	 * @return true if and only if the connection is up.
	 */
	public Resource requestResource(Resource paramResquestedResource);
}
