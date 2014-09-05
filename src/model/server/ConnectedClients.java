package model.server;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;

import model.client.ClientResources;
import model.share.Resource;
import controller.client.Client;
import controller.client.ClientInterface;
import controller.server.ServerInterface;


public class ConnectedClients extends Observable {

	private static final long serialVersionUID = 1147723973129182931L;
	private Vector<ClientInterface> connectedClients = new Vector<ClientInterface>();
		
	// chiamato da view.ServerFrame.updateConnectedClients;
	public DefaultListModel getConnectedClientsModel() {
		final DefaultListModel modelConnectedClients = new DefaultListModel();
		synchronized (connectedClients) {
			try {
				for (ClientInterface oneConnectedClient : connectedClients) {
						modelConnectedClients.addElement(oneConnectedClient.getClientName());
				}			
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return modelConnectedClients;			
	}
	
	public Vector<ClientInterface> getConnectedClients() {
		synchronized (connectedClients) {
			return connectedClients;
		}
	}
	
	public void removeClient(final ClientInterface clientToRemove) {
		synchronized (connectedClients) {
			connectedClients.remove(clientToRemove);
		}
		// notifico alla VIEW view.ServerFrame le modifiche
		setChanged();  
		notifyObservers();
	}
	
	public void addClient(ClientInterface clientToInsert) {
		synchronized (connectedClients) {
			connectedClients.add(clientToInsert);
		}
		// notifico alla VIEW view.ServerFrame le modifiche
		setChanged();  
		notifyObservers();
	}
} 
