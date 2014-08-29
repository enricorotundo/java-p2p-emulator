package server;

import java.rmi.Naming;


public class ServerStarter {
	public static void main(final String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				Boolean alreadyRunningBoolean = false;

				final String[] list = Naming.list("rmi://" + HOST + "/Server/");
				for (final String string : list) {
					final ServerInterface srvInterface = (ServerInterface) Naming.lookup(string);
					if (srvInterface.getServerNameString().equals(args[0].toString())) {
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

	private static final String HOST = "localhost";

}
