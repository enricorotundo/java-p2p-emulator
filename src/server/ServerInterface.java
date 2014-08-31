package server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

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

	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException;

	public Vector<ClientInterface> getClients() throws RemoteException;

	/**
	 * @return the server name string, different from getServerUrl()
	 */
	public String getServerNameString() throws RemoteException;

	/**
	 * @return the server url like: "rmi://localhost/Server/" + server name;
	 * @throws RemoteException
	 */
	public String getServerUrl() throws RemoteException;

	/**
	 * il server S a cui C e connesso dovraa indicare a C l elenco dei client
	 * che sono attualmente connessi al sistema e in possesso di una copia di RK
	 *
	 * @param paramResource
	 * @return
	 * @throws RemoteException
	 */
	public Vector<ClientInterface> resourceOwners(String paramResourceName) throws RemoteException;
}
