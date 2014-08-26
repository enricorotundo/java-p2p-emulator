package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;
import client.ClientInterface;

public interface ServerInterface extends Remote {

	public void clientConnect(ClientInterface paramClient)
			throws RemoteException;

	public void clientDisconnect(ClientInterface paramClient)
			throws RemoteException;

	public Vector<ClientInterface> getClients() throws RemoteException;

	public Vector<ClientInterface> getRequest(ResourceInterface paramResource)
			throws RemoteException;

	/**
	 *
	 * @return the server url like: "rmi://localhost/Server/" + server name;
	 * @throws RemoteException
	 */
	public String getServerUrl() throws RemoteException;

}
