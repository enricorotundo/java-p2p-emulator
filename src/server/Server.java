package server;

import gui.ServerFrame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;
import client.ClientInterface;

public final class Server implements ServerInterface {
	private ServerFrame myFrame = null;
	private static final String HOST = "localhost";
	private Vector<ClientInterface> myConnectedClients;
	private final String serverNameString;

	public Server(final String paramServerName) {
		myFrame = new ServerFrame(paramServerName);
		myFrame.appendLogEntry("Buliding server...");
		serverNameString = paramServerName;

		try {
			Naming.rebind(getServerUrl(), this);
		} catch (final RemoteException e) {
			e.printStackTrace();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}

		for (final ClientInterface clientInterface : myConnectedClients) {
			myFrame.appendLogEntry(clientInterface.getMyName());
		}

	}

	@Override
	public void clientConnect(final ClientInterface paramClient)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientDisconnect(final ClientInterface paramClient)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return myConnectedClients;
	}

	@Override
	public Vector<ClientInterface> getRequest(
			final ResourceInterface paramResource) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return "rmi://" + HOST + "/Server/" + serverNameString;
	}

}
