package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public interface ServerInterface extends Remote {

	public void clientConnect(ClientInterface paramClient)
			throws RemoteException;

	public void clientDisconnect(ClientInterface paramClient)
			throws RemoteException;

	/**
	 * @return all servers connected to the system
	 */
	public Vector<ServerInterface> getAllServers();

	/**
	 * @return Vector<ClientInterface> of clients registered and connected to
	 *         this server
	 * @throws RemoteException
	 */
	public Vector<ClientInterface> getClients() throws RemoteException;

	/**
	 * @return Vector<Resource> of client's resources
	 * @throws RemoteException
	 */
	public Vector<Resource> getClientsResources() throws RemoteException;

	public Vector<ClientInterface> getRequest(ResourceInterface paramResource)
			throws RemoteException;

	/**
	 * @return the server name string, different from getServerUrl()
	 */
	public String getServerNameString() throws RemoteException;

	/**
	 * @return the server url like: "rmi://localhost/Server/" + server name;
	 * @throws RemoteException
	 */
	public String getServerUrl() throws RemoteException;

}
