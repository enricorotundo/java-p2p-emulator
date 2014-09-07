package controller.server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import controller.client.ClientInterface;

public interface ServerInterface extends Remote {
	public Integer clientConnect(ClientInterface clientToConnect) throws RemoteException;
	public Integer clientDisconnect(ClientInterface clientToDisconnect) throws RemoteException;
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException;
	public Vector<ClientInterface> getClients() throws RemoteException;
	public String getServerNameString() throws RemoteException;
	public String getServerUrl() throws RemoteException;
	public Vector<ClientInterface> getResourceOwners(final String paramResourceName, final String caller) throws RemoteException;
	public Vector<ClientInterface> getLocalResourceOwners(String paramResourceName, String clientCaller)  throws RemoteException ;

}
