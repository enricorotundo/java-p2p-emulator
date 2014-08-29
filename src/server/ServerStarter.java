package server;

import java.rmi.Naming;


public class ServerStarter {

	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				final Server server = new Server(args[0]);
				Naming.rebind(server.getServerUrl(), server);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
