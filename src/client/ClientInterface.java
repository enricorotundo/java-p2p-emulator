package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;

public interface ClientInterface extends Remote {

	// public boolean clientCompare(final Object other) throws RemoteException;

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
