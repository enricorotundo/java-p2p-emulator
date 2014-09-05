package client;

import gui.ClientFrame;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;

public interface ClientInterface extends Remote {

	public long download() throws RemoteException;

	/**
	 * @return the client name identifier
	 */
	public String getClientName() throws RemoteException;

	public String getConnectedServer() throws RemoteException;

	/**
	 * @return client's resources.
	 */
	public Vector<ResourceInterface> getResources() throws RemoteException;
	
	public ClientFrame getGuiClientFrame() throws RemoteException;
	
	/**
	 * @param paramResourceToDownload
	 * @return il minimo tra 
	 * D' (capacita download), 
	 * K' num parti mancanti, 
	 * N' num client disponibili 
	 */
	public Integer getMinIndex(final ResourceInterface paramResourceToDownload) throws RemoteException;
	
	public Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString, final String callerName) throws RemoteException;
	
	public Integer getMaxDownloadCapacity() throws RemoteException;

	public void incrementCount() throws RemoteException;
	
	public void decrementCount() throws RemoteException;

	public int getCount() throws RemoteException;
	
	public Vector<ClientInterface> getClientsBusyWithMe() throws RemoteException;
}
