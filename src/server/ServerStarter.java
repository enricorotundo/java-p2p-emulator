package server;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

public class ServerStarter {

	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				final Server server = new Server(args[0]);
				// Naming.rebind(server.getServerUrl(), server);
				final ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
				Naming.rebind("ciao", stub);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
