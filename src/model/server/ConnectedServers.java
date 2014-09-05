package model.server;

import java.util.Vector;

import javax.swing.DefaultListModel;

import controller.client.ClientInterface;
import controller.server.ServerInterface;

public class ConnectedServers {

	private static final long serialVersionUID = 3312651043157668857L;
	private Vector<ServerInterface> connectedServers;
	private DefaultListModel modelConnectedServers;
	
	// chiamato da view.ServerFrame.updateConnectedServers;
	public DefaultListModel getConnectedServersModel() {
		synchronized (connectedServers) {
			// creare il model leggendo i server connessi
			return modelConnectedServers;
		}
	}
}
