package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;
import resource.part.ResourcePart;

public interface ClientInterface extends Remote {

	// public boolean clientCompare(final Object other) throws RemoteException;

	/**
	 * @param paramClient
	 * @param paramResource
	 * @param paramPartNumber
	 * @return the paramPartNumber resource of paramResource, if paramClient
	 *         isn't already downloading parts from this client
	 * @throws RemoteException
	 */
	public ResourcePart downloadPart(final ClientInterface paramClient, final ResourceInterface paramResource, final Integer paramPartNumber) throws RemoteException;

	/**
	 * @return the client name identifier
	 */
	public String getClientName() throws RemoteException;

	public String getConnectedServer() throws RemoteException;

	/**
	 * @return client's resources.
	 */
	public Vector<ResourceInterface> getResources() throws RemoteException;
}
