package client;

import gui.ClientFrame;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;

public interface ClientInterface extends Remote {

	public void download() throws RemoteException;

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
	
	public Integer getMinIndex(final ResourceInterface paramResourceToDownload) throws RemoteException;
	
	public Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString) throws RemoteException;
	
	public void incrementCurrentDownloadsCounter() throws RemoteException;
	
	public void decrementCurrentDownloadsCounter() throws RemoteException;
	
	public Integer getCurrentDownloads() throws RemoteException;

	public void setCurrentDownloads(Integer currentDownloads) throws RemoteException ;
	
	public Integer getMaxDownloadCapacity() throws RemoteException;
}
