package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ServerStarter {

	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				// final Server server = new Server(args[0]);
				// final Server server = new Server();
				//
				// Naming.rebind("oo", server);

				final Server server = new Server();
				final ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
				final Registry registry = LocateRegistry.getRegistry();
				registry.rebind("ci", stub);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
