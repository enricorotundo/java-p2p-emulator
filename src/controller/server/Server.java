package controller.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import model.server.ConnectedClients;
import model.server.ConnectedServers;
import view.ServerFrame;
import controller.client.ClientChecker;
import controller.client.ClientInterface;
import controller.client.ServerChecker;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = 8405014344648483674L;
	private static final String HOST = "localhost";
	public static final String URL_STRING = "rmi://" + HOST + "/Server/";
	private final String serverNameString;
	private final ServerFrame gui; // VIEW

	/**** RISORSE CONDIVISE DA SINCRONIZZARE *****/
	private final ConnectedClients connectedClients = new ConnectedClients(); // MODEL
	private final ConnectedServers connectedServers = new ConnectedServers(); // MODEL
	/*********************************************/
	
	private final Object clientsMonitor = new Object();
	private final Object serversMonitor = new Object();
	private final ClientChecker clientsChecker;
	private final ServerChecker serversChecker;

	public Server(final String paramServerName) throws RemoteException {
		super();
		serverNameString = paramServerName;
		// creo gui
		gui = new ServerFrame(paramServerName, connectedClients, connectedServers);
		// dico al MODEL chi e' il suo Observer
		this.connectedClients.addObserver(gui);
		this.connectedServers.addObserver(gui);
		// set the java.rmi.server.hostname property to tell the RMI Registry which hostname or IP Adress to return in its RMI URLs: http://stackoverflow.com/questions/11343132/rmi-responding-very-slow
		String ipAddress = "127.0.0.1"; //Local IP address 
		System.setProperty("java.rmi.server.hostname",ipAddress);
		
		// quando si chiude il server si deve disconnettere dall rmi registry
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					disconnect();
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		// faccio partire i controllori dei client e server connessi
		clientsChecker = new ClientChecker(clientsMonitor, connectedClients);
		serversChecker = new ServerChecker(serversMonitor, connectedServers);
		clientsChecker.start();
		serversChecker.start();
	}

	@Override
	public Integer clientConnect(ClientInterface paramClient) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer clientDisconnect(ClientInterface paramClient) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
		Naming.unbind(Server.URL_STRING + serverNameString);
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerNameString() throws RemoteException {
		return serverNameString;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return Server.URL_STRING + serverNameString;
	}

	@Override
	public Vector<ClientInterface> resourceOwners(String paramResourceName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}
