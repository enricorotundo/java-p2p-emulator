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
	private String serverNameString;

	public Server(final String paramServerName) {
		myFrame = new ServerFrame(paramServerName);
		myFrame.appendLogEntry("Buliding server...");
		setServerNameString(paramServerName);

		try {
			Naming.rebind(getServerUrl(), this);
		} catch (final RemoteException e) {
			e.printStackTrace();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
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

	public String getServerNameString() {
		return serverNameString;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return "rmi://" + HOST + "/Server/" + getServerNameString();
	}

	public void setMyConnectedClients(
			final Vector<ClientInterface> myConnectedClients) {
		this.myConnectedClients = myConnectedClients;
	}

	public void setServerNameString(final String serverNameString) {
		this.serverNameString = serverNameString;
	}

}
