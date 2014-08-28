//package client;
//
//import gui.ClientFrame;
//
//import java.util.Vector;
//
//import resource.part.ResourcePart;
//
//
//public final class Client implements ClientInterface {
//
//	private Vector<Resource> resources = new Vector<Resource>();
//
//	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
//	private final ClientFrame guiClientFrame;
//
//	private String name = "";
//	/* download capacity */
//	private Integer downloadCapacitInteger = 0;
//	private Boolean connectionUpBoolean = new Boolean(false);
//	private String serverName;
//	private static final String HOST = "localhost";
//
//	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<Resource> paramResources) {
//		name = paramClientName;
//		serverName = paramServerName;
//		downloadCapacitInteger = paramDownloadCapacity;
//		resources = paramResources;
//		guiClientFrame = createClientFrame(); // last one
//	}
//
//	@Override
//	public Integer connect() {
//		if (connectionUpBoolean) {
//			//disconnection
//			connectionUpBoolean = false;
//			return Integer.valueOf(0);
//			//TODO: tornare -1 se qualcosa va storno
//		} else {
//			//connection
//
//			try {
//				// guiClientFrame.appendLogEntry("Trying to connect with " +
//				// "rmi://" + HOST + "/Server/" + serverName);
//				// final ServerInterface serverRemoteInterface =
//				// (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/"
//				// + serverName);
//				//
//				// if (serverRemoteInterface.clientConnect(this) == 1) {
//				// guiClientFrame.appendLogEntry("Succesfully connected to " +
//				// serverRemoteInterface.getServerUrl());
//				// } else {
//				// guiClientFrame.appendLogEntry("Unable to connect with " +
//				// serverRemoteInterface.getServerUrl());
//				// }
//
//			} catch (final Exception e) {
//				e.printStackTrace();
//			}
//
//			// serverConnected = ref;
//			// ref.appendLog("Client " + getClientName() + " connesso");
//			// sc = new ServerChecker(); sc.start();
//			//
//			connectionUpBoolean = true;
//			return Integer.valueOf(1);
//			//TODO: tornare -1 se qualcosa va storno
//		}
//	}
//
//	private ClientFrame createClientFrame() {
//		final ClientFrame clientFrame = new ClientFrame(name, this);
//
//		// clientFrame.appendLogEntry("Creating client... ");
//		// clientFrame.appendLogEntry("Starting creating resources...");
//		// resources = Resource.createRandomResourceVector();
//		// clientFrame.appendLogEntry("Resources created.");
//		return clientFrame;
//	}
//
//	@Override
//	public String getClientName() {
//		return name;
//	}
//
//	public Boolean getConnectionUpBoolean() {
//		return connectionUpBoolean;
//	}
//
//	public Vector<ResourcePart> getDownloadingParts() {
//		return downloadingParts;
//	}
//
//	@Override
//	public Vector<Resource> getResources() {
//		return resources;
//	}
//
//	@Override
//	public Resource requestResource(final Resource paramResquestedResource) {
//		if (connectionUpBoolean) {
//			// TODO
//			// if
//			for (final ResourcePart part : paramResquestedResource.getParts()) {
//				downloadingParts.add(part);
//			}
//
//			// part.setDownloadingStatus(TransfertStatus.Downloading);
//
//			guiClientFrame.getDownloadQueueList().updateUI();
//		} else
//			System.out.println("Connection down.");
//
//		return paramResquestedResource;// stub
//	}
// }
