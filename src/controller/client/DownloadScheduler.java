package controller.client;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import model.client.ClientResources;

public class DownloadScheduler extends Thread {
	private final ClientResources resourceModel; // MODEL
	private ConcurrentHashMap<ClientInterface, AtomicBoolean> ownersClientsList;
	private String[] resourceToDownload; // il nome della risorsa da scaricare
	private Integer howManyPartsToDownload; // di quante parti e' composta la risorsa da scaricare
	private final Integer maxDownloadCapacity; // capacita' massima di download del client
	private final AtomicInteger currentDownloadsNumber; //
	
	public DownloadScheduler(final ClientResources resourceModel, final Vector<ClientInterface> owners, final String[] resource, final Integer maxDownloadCapacity, AtomicInteger currentDownloadsNumber) {
		setDaemon(true);
		this.resourceModel = resourceModel;
		this.ownersClientsList = new ConcurrentHashMap<ClientInterface, AtomicBoolean>(owners.size());
		for (ClientInterface client : owners) {
			this.ownersClientsList.put(client, new AtomicBoolean(false));
		}
		this.resourceToDownload = resource;
		this.howManyPartsToDownload = Integer.parseInt(resource[1]);
		this.maxDownloadCapacity = maxDownloadCapacity;
		this.currentDownloadsNumber = currentDownloadsNumber;
		
		this.resourceModel.createParts(howManyPartsToDownload);
	}
	
	@Override
	public void run() {
		/*
		 * finche' la lista delle risorse da scaricare e' vuota aspetto,
		 * Client.performSearch(..) chiama la notify su resourceModel
		 * quando aggiunge una risorsa da scaricare
		 */
		try {
			while (resourceModel.getDownloadningResources().isEmpty()) {
					resourceModel.wait();
			}
		} catch (InterruptedException e) {
			System.out.println("DownloadScheduler waiting interrupted.");
		}
		
		ClientInterface lastClientSelected = null;
		int maxConcurrentDownload = min(maxDownloadCapacity, howManyPartsToDownload, ownersClientsList.size());
		// qui ce' almeno una risorsa da scaricare
		while (!ownersClientsList.isEmpty() && resourceModel.completePartsCounter() < howManyPartsToDownload) {
			/*
			 * qui ci sono ancora parti della risorsa da scaricare
			 * inoltre ce' almeno un possessore
			 */	
			synchronized (currentDownloadsNumber) {
			
				while (currentDownloadsNumber.get() >= maxConcurrentDownload) {
					try {
						// verra risvegliato quando PartDownloader completa il downloand di una parte
						currentDownloadsNumber.wait();
					} catch (InterruptedException e) {
						System.out.println("DownloadScheduler waiting interrupted.");
					}
				}
				
				//qui ho il via libera per scaricare
				int partToDownloadIndex = -1;
				try {
					
					/*
					 * qui sono all'interno di un while che cicla finche' ci sono client per la risorsa
					 * e finche' ci non ho scaricato tutte le parti di cui la risorsa e' composta.
					 * Ora cerco, a partire dalla parte #0, quella che ha status 0=not started
					 * oppure anche failed=2 
					 */
					for (int i = 0; i < resourceModel.getPartsLength() && partToDownloadIndex == -1; i++) {
						if (resourceModel.getPartStatus(i) == 0 || resourceModel.getPartStatus(i) == 2) {
							partToDownloadIndex = i;
							
							boolean found = false;						
							for(Map.Entry<ClientInterface, AtomicBoolean> ownersEntry : ownersClientsList.entrySet()){
							    if (ownersEntry.getValue().get() == false && found == false) {
									// qui ownersEntry non e' impegnato
							    	found = true;
							    	// segno il client come impegnato (true)
							    	ownersEntry.setValue(new AtomicBoolean(true));
							    	lastClientSelected = ownersEntry.getKey();
							    	resourceModel.setPartStatus(partToDownloadIndex, -1); // -1 = in scaricamento
							    	currentDownloadsNumber.incrementAndGet();
							    	new PartDownloader(currentDownloadsNumber, ownersClientsList, resourceModel, resourceToDownload, partToDownloadIndex, lastClientSelected).start();
								}
							}
						}
					}

				} catch (RemoteException e) {
					// gestico il caso in cui un client va down
					currentDownloadsNumber.decrementAndGet();
					resourceModel.setPartStatus(partToDownloadIndex, 0);
					ownersClientsList.remove(lastClientSelected);
					System.out.println("Error while contacting a client no more available.");
				} 					
			}
		}
		/*
		 * 	qui la condizione
		 * 	(!ownersClientsList.isEmpty() && resourceModel.completePartsCounter() < howManyPartsToDownload)
		 * 	non e' piu' vera, vale cioe' la sua negazione:
		 * 	
		 * per semplificare pongo !ownersClientsList.isEmpty() = ciSonoClientDisponibili
		 * 
		 * 		   ciSonoClientDisponibili		| 	completePartsCounter() < howManyPartsToDownload
		 * 		----------------------------	|	-----------------------------------------------
		 * 					0					|						0
		 * 					1					|						0
		 * 					0					|						1
		 * 
		 * 	1) non ci sono piu' client possessori AND  ho completato il download di tutte le parti
		 * 	2) ci sono client possessori AND ho completato il download di tutte le parti
		 * 	3) non ci sono piu' client possessori AND mancano delle parti da scaricare
		 */
		
		if (	!(resourceModel.completePartsCounter() < howManyPartsToDownload)) {
//			1) or 2)
			resourceModel.addAvailableResource(resourceToDownload[0] + " " + resourceToDownload[1]);
			System.out.println(resourceToDownload[0] + " " + resourceToDownload[1] + ": scaricata!");
		} else {
//			3)
			System.out.println("Unable to download entirely " + resourceToDownload[0] + " " + resourceToDownload[1]);
//			//DEVO ELIMINARE DALLA CODA DOWNLOAD
			resourceModel.resetDownloadDatas();			
		}
	}
	
	/**
	 * torna il minimo tra D' K' N' come da specifica
	 */
	private Integer min(final Integer maxDownloadCapacity, final Integer parts, final Integer clientListSize) {
		final Vector<Integer> dkn = new Vector<Integer>();
		dkn.add(maxDownloadCapacity);
		dkn.add(parts);
		dkn.add(clientListSize);
		return Collections.min(dkn);
	}
	
	
}
