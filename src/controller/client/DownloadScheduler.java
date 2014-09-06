package controller.client;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultListModel;
import javax.swing.text.html.HTMLDocument.Iterator;

import model.client.ClientResources;

public class DownloadScheduler extends Thread {
	private final ClientResources resourceModel; // MODEL
	private ConcurrentHashMap<ClientInterface, AtomicBoolean> ownersClientsList;
	private String[] resourceToDownload; // il nome della risorsa da scaricare
	private Integer howManyPartsToDownload; // di quante parti e' composta la risorsa da scaricare
	private AtomicBoolean[] downloadedParts; // array di AtomicBoolean inizialmente tutti a FALSE
//	private Integer previousListModelSize = 0; //
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
		this.downloadedParts = new AtomicBoolean[howManyPartsToDownload];
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
		
		while (trueCounter(downloadedParts) < howManyPartsToDownload && !ownersClientsList.isEmpty()) {
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
				
				// chiamte atomiche perche' sync su currentDownloadNumber
				partToDownloadIndex();
				clientToDownloadFrom();
				
			}
		}
		Client lastClientAssigned = null;
	}
	
	private ClientInterface clientToDownloadFrom() {
		ClientInterface clientToDownladFrom = null;
		boolean found = false;
		
		/*
		 * even though all operations are thread-safe, 
		 * retrieval operations do not entail locking, 
		 * and there is not any support for locking the entire table in a way 
		 * that prevents all access.
		 */

		Iterator<Map.Entry<ClientInterface, AtomicBoolean>> iterator = ownersClientsList.entrySet().iterator();
		
//		for(Map.Entry<ClientInterface, AtomicBoolean> ownersEntry : ownersClientsList.entrySet()){
//		    
//			
//			ownersEntry.getKey();
//			ownersEntry.getValue();
//		}
		
		
		
		return clientToDownladFrom;
	}
	
	/**
	 * @return l'indice di una parte che non e' ancora stata scaricata
	 */
	private int partToDownloadIndex() {
		int result = -1;
		for (int i = 0; i < downloadedParts.length && result == -1; i++) {
			if (downloadedParts[i].get() == false) {
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
	
	private Integer trueCounter(AtomicBoolean[] values) {
		int count = 0;
		for (AtomicBoolean val : values) {
			count += (val.get() == true ? 1 : 0);
		}
		return count;
	}
}
