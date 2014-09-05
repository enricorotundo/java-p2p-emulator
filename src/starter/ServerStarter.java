package starter;

import java.rmi.Naming;

import controller.server.Server;
import controller.server.ServerInterface;

public class ServerStarter {
	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Invalid argument!");
		} else {
			try {
				Boolean alreadyRunningBoolean = false;

				final String[] list = Naming.list(Server.URL_STRING);
				for (final String string : list) {
					final ServerInterface serverInterface = (ServerInterface) Naming.lookup(string);
					if (serverInterface.getServerNameString().equals(args[0].toString())) {
						alreadyRunningBoolean = true;
					}
				}
				if (alreadyRunningBoolean == false) {
					final Server server = new Server(args[0]);
					Naming.rebind(server.getServerUrl(), server);
				} else {
					System.out.println("Namesake server running!");
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
