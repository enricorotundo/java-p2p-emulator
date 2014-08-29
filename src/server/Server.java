package server;

import gui.ServerFrame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public final class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = -2240153419231304793L;
	private static final String HOST = "localhost";
	private String serverNameString = "";
	private ServerFrame guiServerFrame = null;
	private Vector<ClientInterface> connectedClients = new Vector<ClientInterface>();
	private Vector<ServerInterface> connectedServers = new Vector<ServerInterface>();

	public Server(final String paramServerName) throws RemoteException {
		serverNameString = paramServerName;
		guiServerFrame = new ServerFrame(paramServerName);
		// update gui
		guiServerFrame.setConnectedClientsList(connectedClients);
		guiServerFrame.setConnectedServersList(connectedServers);
	}

	@Override
	// if connection success returns 1, if something wrong returns -1
	public Integer clientConnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		Boolean alreadyPresentInteger = false;
		for (int i = 0; i < connectedClients.size(); i++)
			if (connectedClients.elementAt(i).getClientName() == paramClient.getClientName())
				alreadyPresentInteger = true;
		if (alreadyPresentInteger == false) {
			connectedClients.add(paramClient);
			guiServerFrame.appendLogEntry(paramClient.getClientName() + " connected.");
			functionResultInteger = 1;
		}
		// update gui
		guiServerFrame.setConnectedClientsList(connectedClients);
		// guiServerFrame.setConnectedServersList(connectedServers);
		return functionResultInteger;
	}

	@Override
	// 0 if disconnection is done, -1 if something wrong
	public Integer clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		final Integer functionResultInteger = -1;

		return functionResultInteger;
	}

	@Override
	public Vector<ServerInterface> getAllServers() {
		return connectedServers;
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return connectedClients;
	}

	@Override
	public Vector<Resource> getClientsResources() throws RemoteException {
		final Vector<Resource> resources = new Vector<Resource>();
		for (final ClientInterface cli : connectedClients) {
			for (final Resource resource : cli.getResources()) {
				resources.add(resource);
			}
		}
		return resources;
	}

	@Override
	public Vector<ClientInterface> getRequest(final ResourceInterface paramResource) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerNameString() {
		return serverNameString;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return "rmi://" + HOST + "/Server/" + serverNameString;
	}
}
