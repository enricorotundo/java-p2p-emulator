package model.server;

import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;

import controller.client.ClientInterface;
import controller.server.Server;
import controller.server.ServerInterface;

public class ConnectedServers  extends Observable {

	private static final long serialVersionUID = 3312651043157668857L;
	private Vector<ServerInterface> connectedServers = new Vector<ServerInterface>();
	
	public ConnectedServers() {
		
	}
	
	// chiamato da view.ServerFrame.updateConnectedServers;
	public DefaultListModel getConnectedServersModel() {
		final DefaultListModel modelConnectedServers = new DefaultListModel();
		synchronized (connectedServers) {
			try {
			for (ServerInterface oneConnectedServer : connectedServers) {
					modelConnectedServers.addElement(oneConnectedServer.getServerNameString());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}			
		}
		return modelConnectedServers;
	}
	
	public Vector<ServerInterface> getConnectedServers() {
		synchronized (connectedServers) {
			return connectedServers;
		}
	}
	
	public void addServer(final ServerInterface serverToInsert) {
		System.out.println("model.ConnectedServer");
		synchronized (connectedServers) {
			connectedServers.add(serverToInsert);
		}
		// notifico alla VIEW view.ServerFrame le modifiche
		setChanged();  
		notifyObservers();
	}
	
	public void clearServers() {
		synchronized (connectedServers) {
			connectedServers.clear();
		}
		// notifico alla VIEW view.ServerFrame le modifiche
		setChanged();  
		notifyObservers();
	}
}
