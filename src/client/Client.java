/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package client;

import gui.ClientFrame;

import java.util.Vector;

import javax.print.attribute.standard.Fidelity;

import resource.Resource;
import resource.part.ResourcePart;

/**
 * The Class Client.
 *
 * @author erotundo
 */
public final class Client implements ClientInterface {
	
	/** The my resources. */
	private Vector<Resource> myResources = new Vector<Resource>();
	
	/** The my downloading parts. */
	private Vector<ResourcePart> myDownloadingParts = new Vector<ResourcePart>();
	
	/** The my gui client frame. */
	private ClientFrame myGuiClientFrame;
	
	/** The my name. */
	private String myName="";
	
	/** The my download capaciy. */
	private Integer myDownloadCapaciy=0; // il client puo scaricare fino a myDownloadCapaciy PARTI DI RISORSE contemporaneamente
	
	/** The connection up boolean. */
	private Boolean connectionUpBoolean = false;
		
	/**
	 * Instantiates a new client.
	 *
	 * @param paramClientName the param client name
	 * @param paramDownloadCapacity the param download capacity
	 */
	public Client(final String paramClientName, final Integer paramDownloadCapacity) {
		setMyName(paramClientName);
		setMyDownloadCapaciy(paramDownloadCapacity);
		setMyResources(Resource.createRandomResourceVector());
		
		setMyGuiClientFrame(createClientFrame()); // should be the last one call (GUI could request a full object set)
	}
	
	/**
	 * Creates the client frame.
	 *
	 * @return the client frame
	 */
	private ClientFrame createClientFrame() {
		final ClientFrame clientFrame = new ClientFrame(getMyName(), this);
		clientFrame.appendLogEntry("Creating client... ");
		clientFrame.appendLogEntry("Starting creating resources...");
		loadRandomResources();
		setMyResources(loadRandomResources());
		clientFrame.appendLogEntry("Resources created.");
		return clientFrame;
	}
	
	/**
	 * Load random resources.
	 */
	private static Vector<Resource> loadRandomResources() {
		return Resource.createRandomResourceVector();
	}
	
	
	@Override
	public ClientFrame getMyGuiClientFrame() {
		return myGuiClientFrame;
	}

	@Override
	public void setMyGuiClientFrame(final ClientFrame myGuiClientFrame) {
		this.myGuiClientFrame = myGuiClientFrame;
	}
	
	/* (non-Javadoc)
	 * @see client.ClientInterface#getMyResources()
	 */
	@Override
	public Vector<Resource> getMyResources() {
		return myResources;
	}

	/* (non-Javadoc)
	 * @see client.ClientInterface#setMyResources(java.util.Vector)
	 */
	@Override
	public void setMyResources(final Vector<Resource> myResources) {
		this.myResources = myResources;
	}

	/* (non-Javadoc)
	 * @see client.ClientInterface#getMyName()
	 */
	@Override
	public final String getMyName() {
		return myName;
	}

	/* (non-Javadoc)
	 * @see client.ClientInterface#setMyName(java.lang.String)
	 */
	@Override
	public final void setMyName(final String myName) {
		this.myName = myName;
	}

	/* (non-Javadoc)
	 * @see client.ClientInterface#connect()
	 */
	@Override
	public Integer connect() {
		if (getConnectionStatus()) {
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

	/* (non-Javadoc)
	 * @see client.ClientInterface#requestResource(resource.Resource)
	 */
	@Override
	public Resource requestResource(final Resource paramResquestedResource) {
		if (getConnectionStatus()) {
			//TODO
				//if
			for (ResourcePart part : paramResquestedResource.getParts()) {
				getMyDownloadingParts().add(part);				
			}
			
//			part.setDownloadingStatus(TransfertStatus.Downloading);

			myGuiClientFrame.getDownloadQueueList().updateUI();	
		} else {
			System.out.println("Connection down.");
		}
		
		return paramResquestedResource;//stub
	}
	
	@Override
	public Integer getMyDownloadCapaciy() {
		return myDownloadCapaciy;
	}

	@Override
	public void setMyDownloadCapaciy(final Integer myDownloadCapaciy) {
		this.myDownloadCapaciy = myDownloadCapaciy;
	}

	/* (non-Javadoc)
	 * @see client.ClientInterface#cheackConnectionStatus()
	 */
	@Override
	public Boolean getConnectionStatus() {
		//TODO: gestire eccezz ecc..
		return connectionUpBoolean;
	}

	@Override
	public Vector<ResourcePart> getMyDownloadingParts() {
		return myDownloadingParts;
	}

	@Override
	public void setMyDownloadingParts(final Vector<ResourcePart> myDownloadingParts) {
		this.myDownloadingParts = myDownloadingParts;
	}
}
