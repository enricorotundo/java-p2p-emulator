package model.server;

import java.util.Vector;

import javax.swing.DefaultListModel;

import controller.client.ClientInterface;
import controller.server.ServerInterface;

public class ConnectedServers {

	private static final long serialVersionUID = 3312651043157668857L;
	private DefaultListModel modelConnectedServers;
	private Vector<ServerInterface> connectedServers;
	
	// chiamato da view.ServerFrame.updateConnectedServers;
	public DefaultListModel getConnectedServersModel() {
		synchronized (connectedServers) {
			// TODO creare il model leggendo i server connessi
			return modelConnectedServers;
		}
	}
	
	public Vector<ServerInterface> getConnectedServers() {
		synchronized (connectedServers) {
			return connectedServers;
		}
	}
	
	public void addServer(final ServerInterface serverToInsert) {
		synchronized (connectedServers) {
			connectedServers.add(serverToInsert);
		}
	}
	
	public void clearServers() {
		synchronized (connectedServers) {
			connectedServers.clear();
		}
	}
}
