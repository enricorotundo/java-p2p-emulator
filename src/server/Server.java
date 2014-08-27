package server;

import gui.ServerFrame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public final class Server implements ServerInterface {
	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			// Schedule a job for the event dispatch thread:
			// creating and showing this application's GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						final Server server = new Server(args[0]);
						final String rmiObjName = server.getServerUrl();
						Naming.rebind(rmiObjName, server);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	}
	private ServerFrame myFrame = null;
	private static final String HOST = "localhost";
	private Vector<ClientInterface> myConnectedClients;
	Vector<ServerInterface> servers = new Vector<ServerInterface>();

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
			myFrame.appendLogEntry(clientInterface.getClientName());
		}

	}

	//	@Override
	@Override
	public void clientConnect(final ClientInterface paramClient)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	//	@Override
	@Override
	public void clientDisconnect(final ClientInterface paramClient)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<ServerInterface> getAllServers() {
		return servers;
	}

	//	@Override
	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return myConnectedClients;
	}

	@Override
	public Vector<Resource> getClientsResources()
			throws RemoteException {
		final Vector<Resource> resources = new Vector<Resource>();
		for (final ClientInterface cli : myConnectedClients) {
			for (final Resource resource : cli.getResources()) {
				resources.add(resource);
			}
		}
		return resources;
	}

	//	@Override
	@Override
	public Vector<ClientInterface> getRequest(
			final ResourceInterface paramResource) throws RemoteException {
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
