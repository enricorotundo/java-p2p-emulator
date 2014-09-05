package controller.client;

import java.rmi.RemoteException;

import view.ClientFrame;

import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import model.client.ClientResources;
import model.share.Resource;

public class Client extends UnicastRemoteObject  implements ClientInterface  {
	
	/**************** TEMPO DI DONWLOAD COSTANTE (PER PARTE) **************/
	public static final long UPLOAD_TIME = 4000;
	/**********************************************************************/
	
	private static final long serialVersionUID = -3445312807782067423L;
	private final String clientName;
	private final String serverName;
	private AtomicInteger currentDownloads = new AtomicInteger(0);
	private final Integer maxDownloadCapacity;
	private final ClientResources resources; // MODEL
	private final ClientFrame gui; // VIEW
	
	public Client(final String clientName, String serverName, int maxDownloadCapacity, final ClientResources argResources) throws RemoteException {
		this.clientName = clientName;
		this.serverName = serverName;
		this.maxDownloadCapacity = maxDownloadCapacity;
		this.resources = argResources;
		gui = new ClientFrame(clientName + "@" + serverName, resources);
		// dico al MODEL chi e' il suo Observer
		this.resources.addObserver(gui);
	}

	@Override
	public Boolean download(String callerName) throws RemoteException {
		try {
			Thread.sleep(Client.UPLOAD_TIME);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getClientName() throws RemoteException {
		return clientName;
	}

	@Override
	public String getConnectedServer() throws RemoteException {
		return serverName;
	}

	@Override
	public Boolean test() throws RemoteException {
		return true;
	}

	@Override
	public Vector<String[]> getResourceList() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
