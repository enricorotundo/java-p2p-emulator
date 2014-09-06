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
	private AtomicInteger[] parts; // array di AtomicInteger inizialmente tutti a 0; -1 = in scaricamento; 1 = scaricato
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
		this.parts = new AtomicInteger[howManyPartsToDownload];
		this.maxDownloadCapacity = maxDownloadCapacity;
		this.currentDownloadsNumber = currentDownloadsNumber;
	}
	
	@Override
	public void run() {
		int maxConcurrentDownload = min(maxDownloadCapacity, howManyPartsToDownload, ownersClientsList.size());
		
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
			e.printStackTrace();
		}
		// qui ce' almeno una risorsa da scaricare
		
		while (trueCounter(parts) < howManyPartsToDownload && !ownersClientsList.isEmpty()) {
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
						e.printStackTrace();
					}
				}
				//qui ho il via libera per scaricare
				
				final int partToDownloadIndex = partToDownloadIndex();
				final ClientInterface clientToDownloadFrom = clientToDownloadFrom(partToDownloadIndex);
				try {
					new PartDownloader(currentDownloadsNumber, ownersClientsList, parts, resourceToDownload, partToDownloadIndex, clientToDownloadFrom);
				} catch (RemoteException e) {
					// gestico il caso in cui un client va down
					currentDownloadsNumber.decrementAndGet();
					parts[partToDownloadIndex].set(0);
					ownersClientsList.remove(clientToDownloadFrom);
					System.out.println("Error while contacting a client no more available.");
				} 
			}
		}
	}
	
	private ClientInterface clientToDownloadFrom(final Integer partToDownloadIndex) {
		ClientInterface clientToDownladFrom = null;
		boolean found = false;						
		for(Map.Entry<ClientInterface, AtomicBoolean> ownersEntry : ownersClientsList.entrySet()){
		    if (ownersEntry.getValue().get() == false && found == false) {
				// qui ownersEntry non e' impegnato
		    	found = true;
		    	// segno il client come impegnato (true)
		    	ownersEntry.setValue(new AtomicBoolean(true));
		    	clientToDownladFrom = ownersEntry.getKey();
		    	parts[partToDownloadIndex].set(-1); // -1 = in scaricamento
		    	currentDownloadsNumber.incrementAndGet();
			}
		}
		return clientToDownladFrom;
	}
	
	/**
	 * @return l'indice di una parte che non e' ancora stata scaricata
	 */
	private int partToDownloadIndex() {
		int result = -1;
		for (int i = 0; i < parts.length && result == -1; i++) {
			if (parts[i].get() == 0) {
				result = i;
			}
		}		
		return result;
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
	
	private Integer trueCounter(AtomicInteger[] values) {
		int count = 0;
		for (AtomicInteger val : values) {
			count += (val.get() == 1 ? 1 : 0);
		}
		return count;
	}
}
