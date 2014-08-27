package client;

import gui.ClientFrame;

import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;

public final class Client implements ClientInterface {

	private static Vector<Resource> loadRandomResources() {
		return Resource.createRandomResourceVector();
	}

	public static void main(final String[] args) {
		if (args.length == 0) {
			System.out.println("invalid argument!");
		} else {
			try {
				final Vector<Resource> argResources = new Vector<Resource>();
				for (int i=3; i<args.length; i+=2) {
					argResources.add(new Resource(args[i].toCharArray()[0], Integer
							.parseInt(args[i + 1])));
				}
				// if no resources args fill it random
				if (argResources.size() == 0)
					argResources.addAll(Resource.createRandomResourceVector());

				// Schedule a job for the event dispatch thread:
				// creating and showing this application's GUI.
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							final Client c = new Client(args[0], args[1],
									Integer.parseInt(args[2]), argResources);
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				});
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Vector<Resource> resources = new Vector<Resource>();

	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
	private final ClientFrame guiClientFrame;

	private String name = "";
	private Integer downloadCapacitInteger = 0; // il client puo scaricare
	// fino a myDownloadCapaciy
	// PARTI DI RISORSE
	// contemporaneamente
	private Boolean connectionUpBoolean = new Boolean(false);


	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<Resource> paramResources) {
		name = paramClientName;
		downloadCapacitInteger = paramDownloadCapacity;

		resources = paramResources;


		guiClientFrame = createClientFrame();
	}

	@Override
	public Integer connect() {
		if (connectionUpBoolean) {
			//disconnection
			connectionUpBoolean = false;
			return Integer.valueOf(0);
			//TODO: tornare -1 se qualcosa va storno
		} else {
			//connection
			connectionUpBoolean = true;
			return Integer.valueOf(1);
			//TODO: tornare -1 se qualcosa va storno
		}
	}

	private ClientFrame createClientFrame() {
		final ClientFrame clientFrame = new ClientFrame(name, this);

		clientFrame.appendLogEntry("Creating client... ");
		clientFrame.appendLogEntry("Starting creating resources...");
		loadRandomResources();
		resources = loadRandomResources();
		clientFrame.appendLogEntry("Resources created.");
		return clientFrame;
	}

	@Override
	public String getClientName() {
		return name;
	}

	public Boolean getConnectionUpBoolean() {
		return connectionUpBoolean;
	}

	public Vector<ResourcePart> getDownloadingParts() {
		return downloadingParts;
	}

	public Vector<Resource> getResources() {
		return resources;
	}

	@Override
	public Resource requestResource(final Resource paramResquestedResource) {
		if (connectionUpBoolean) {
			// TODO
			// if
			for (final ResourcePart part : paramResquestedResource.getParts()) {
				downloadingParts.add(part);
			}

			// part.setDownloadingStatus(TransfertStatus.Downloading);

			guiClientFrame.getDownloadQueueList().updateUI();
		} else
			System.out.println("Connection down.");

		return paramResquestedResource;// stub
	}
}
