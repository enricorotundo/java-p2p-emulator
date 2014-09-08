package controller.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import model.server.ConnectedClients;
import model.server.ConnectedServers;
import view.ServerFrame;
import controller.client.ClientInterface;

public class Server extends UnicastRemoteObject implements ServerInterface {
	
	private static final long serialVersionUID = 8405014344648483674L;
	private static final String HOST = "localhost";
	public static final String URL_STRING = "rmi://" + HOST + "/Server/";
	private final String serverNameString;
	private ServerFrame gui; // VIEW

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
		
		// crea e mostra gui
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// creo gui
				gui = new ServerFrame(paramServerName, connectedClients, connectedServers);
				// dico al MODEL chi e' il suo Observer
				connectedClients.addObserver(gui);
				connectedServers.addObserver(gui);
		        gui.setVisible(true);
		    }
		});
	}

	@Override
	public Integer clientConnect(ClientInterface clientToConnect) throws RemoteException {
		gui.appendLogEntry(clientToConnect.getClientName() + " try to connect");
		Integer functionResultInteger = -1;
		synchronized (clientsMonitor) {
			// check if client is already connected
			if (!connectedClients.getConnectedClients().contains(clientToConnect)) {
				connectedClients.addClient(clientToConnect);
				functionResultInteger = 1;
				gui.appendLogEntry(clientToConnect.getClientName() + " succesfully connected!");
			}
			// risveglia il thread clientChecker
			clientsMonitor.notifyAll();
		}
		return functionResultInteger;
	}

	@Override
	public Integer clientDisconnect(ClientInterface clientToDisconnect) throws RemoteException {
		gui.appendLogEntry(clientToDisconnect.getClientName() + " try to disconnect");
		Integer functionResultInteger = -1;
		synchronized (clientsMonitor) {
			// check if client is connected
			if (connectedClients.getConnectedClients().contains(clientToDisconnect)) {
				connectedClients.removeClient(clientToDisconnect);
				functionResultInteger = 0;
				gui.appendLogEntry(clientToDisconnect.getClientName() + " succesfully disconnected!");
			}
			// risveglia il thread clientChecker
			clientsMonitor.notifyAll();
		}
		return functionResultInteger;
	}

	@Override
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
		gui.appendLogEntry("Disconneting from the rmiregistry... bye bye");
		Naming.unbind(Server.URL_STRING + serverNameString);
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return connectedClients.getConnectedClients();			
	}

	@Override
	public String getServerNameString() throws RemoteException {
		return serverNameString;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return Server.URL_STRING + serverNameString;
	}

	/**
	 * ritorna un elenco dei client connessi a me che possiedono paramResourceName
	 */
	@Override
	public Vector<ClientInterface> getLocalResourceOwners(final String paramResourceName, final String clientCaller)  throws RemoteException {
		final Vector<ClientInterface> searchedResourceOwners = new Vector<ClientInterface>();
		gui.appendLogEntry("Looking for local clis that owns " + paramResourceName + ". Request made by " + clientCaller);
		try {
			
			// per ogni client connesso ad un particolare server della rete
			for (final ClientInterface cli : connectedClients.getConnectedClients()) {
				gui.appendLogEntry("Looking for " + paramResourceName + " in " + cli.getClientName() + "@" + serverNameString);
				
				// chiedo al client se possiede la risorsa
				if (cli.checkResourcePossession(paramResourceName, clientCaller)) {
					
								try {
									gui.appendLogEntry(cli.getClientName() + "@" + serverNameString + " has " + paramResourceName);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
								searchedResourceOwners.add(cli);								
				}
			}
		} catch (RemoteException e) {
			gui.appendLogEntry("Error during local client asking for " + paramResourceName + ". Request made by " + clientCaller);
		}		
		return searchedResourceOwners;
	}

	/**
	 * ritorna i client possessori di paramResourceName.
	 * cerca tra i client connessi a questo server e chiede agli altri server
	 * di chiedere ai loro client se sono possessori di paramResourceName
	 */
	@Override
	public Vector<ClientInterface> getResourceOwners(final String paramResourceName, final String clientCaller) throws RemoteException {
		final Vector<ClientInterface> searchedResourceOwners = new Vector<ClientInterface>();
		gui.appendLogEntry("Looking for every network connected cli that owns " + paramResourceName + ". Request made by " + clientCaller);
		
		// cerco tra i client locali
		searchedResourceOwners.addAll(getLocalResourceOwners(paramResourceName, clientCaller));
		
		synchronized (serversMonitor) {
			// per ogni server connesso alla rete
			for (final ServerInterface remoteServerInterface : connectedServers.getConnectedServers()) {
				
				// escludo questo server dalla ricerca
				if (!remoteServerInterface.getServerNameString().equals(serverNameString)) {
					gui.appendLogEntry("Asking " + remoteServerInterface.getServerNameString() + " to ask his clients for " + paramResourceName + ". Request made by " + clientCaller);
					
								// chiamo il metodo remoto di un altro server che mi torna i suoi client possessori di paramResourceName
								try {
									searchedResourceOwners.addAll(remoteServerInterface.getLocalResourceOwners(paramResourceName, serverNameString));
								} catch (RemoteException e) {
									e.printStackTrace();
								}													
				}
			}
		}
		return searchedResourceOwners;
	}
	
	
}