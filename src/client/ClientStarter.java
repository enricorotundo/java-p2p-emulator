package client;

import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;


public class ClientStarter {
	public static void main(final String[] args) {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				final Vector<ResourceInterface> argResources = new Vector<ResourceInterface>();
				for (int i = 3; i < args.length; i += 2)
					argResources.add(new Resource(args[i].toCharArray()[0], Integer.parseInt(args[i + 1])));
				final Client c = new Client(args[0], args[1], Integer.parseInt(args[2]), argResources);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
