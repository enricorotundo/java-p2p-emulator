package model.server;

import java.util.Vector;

import javax.swing.DefaultListModel;

import controller.client.ClientInterface;
import controller.server.ServerInterface;


public class ConnectedClients {

	private static final long serialVersionUID = 1147723973129182931L;
	private Vector<ClientInterface> connectedClients;
	private DefaultListModel modelConnectedClients;
	
	// chiamato da view.ServerFrame.updateConnectedClients;
	public DefaultListModel getConnectedClientsModel() {
		synchronized (connectedClients) {
			// creare il model leggendo i client connessi
			return modelConnectedClients;			
		}
	}
}
