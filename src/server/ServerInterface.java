package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public interface ServerInterface extends Remote {

	/**
	 * @param paramClient
	 * @return if connection success returns 1, if something wrong returns -1
	 * @throws RemoteException
	 */
	public Integer clientConnect(ClientInterface paramClient) throws RemoteException;

	/**
	 * @param paramClient
	 * @return 0 if disconnection is done, -1 if something wrong
	 * @throws RemoteException
	 */
	public Integer clientDisconnect(ClientInterface paramClient) throws RemoteException;

	/**
	 * @return all servers connected to the system
	 */
	public Vector<ServerInterface> getAllServers() throws RemoteException;

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

	public Vector<ClientInterface> getRequest(ResourceInterface paramResource) throws RemoteException;

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
