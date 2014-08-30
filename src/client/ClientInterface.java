package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;

public interface ClientInterface extends Remote {

	/**
	 * @return the client name identifier
	 */
	public String getClientName() throws RemoteException;

	/**
	 * @return client's resources.
	 */
	public Vector<Resource> getResources() throws RemoteException;

}
