package starter;

import java.util.Vector;

import controller.client.Client;
import model.client.ClientResources;
import model.share.Resource;

public class ClientStarter {
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.out.println("Invalid argument!");
		} else {
			try {
				// creo l'oggetto di tipo ClientResources da passare al client come model
				final ClientResources argResources = new ClientResources(); // MODEL
				for (int i = 3; i < args.length; i+=2) {
					String resNameString = (args[i] + " " + args[i+1]);
					argResources.addAvailableResource(resNameString);
				}
				// creo il controller.client.Client
				final Client clientController = new Client(args[0], args[1], Integer.parseInt(args[2]), argResources);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
