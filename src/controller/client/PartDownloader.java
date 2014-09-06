package controller.client;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PartDownloader extends Thread {
	
	final private AtomicInteger currentDownloadsNumber;
	final private ConcurrentHashMap<ClientInterface, AtomicBoolean> ownersClientsList;
	final private AtomicInteger[] parts;
	final private String[] resourceToDownload; // il nome della risorsa da scaricare
	final private int partToDownloadIndex;
	final private ClientInterface clientToDownloadFrom;
	final private String clientName;

	public PartDownloader(final AtomicInteger currentDownloadsNumber, final ConcurrentHashMap<ClientInterface, AtomicBoolean> ownersClientsList, final AtomicInteger[] parts, final String[] resourceToDownload, final int partToDownloadIndex, final ClientInterface clientToDownloadFrom) throws RemoteException {
		setDaemon(true);
		this.currentDownloadsNumber = currentDownloadsNumber;
		this.ownersClientsList = ownersClientsList;
		this.parts = parts;
		this.resourceToDownload = resourceToDownload;
		this.partToDownloadIndex = partToDownloadIndex;
		this.clientToDownloadFrom = clientToDownloadFrom;
		
		/*
		 * se il client non c'e piu' lancia RemoteException
		 * cosi il DownloadScheduler sa che il client non e' piu 
		 * disponibile e agisce di conseguenza
		 */
		this.clientName = this.clientToDownloadFrom.getClientName();
	}
	
	@Override
	public void run() {
		System.out.println("Starting to download " + resourceToDownload[0] + " part " + partToDownloadIndex + " from " +clientName);
		try {
			clientToDownloadFrom.download(this.getName());
			parts[partToDownloadIndex].set(1);; // segno la parte come scaricata
			ownersClientsList.get(clientToDownloadFrom).set(false);// segno il client come libero
			currentDownloadsNumber.decrementAndGet();
			/*
			 *  risveglio il DownloadScheduler se era in wait a causa del
			 *  raggiungiemnto di capacita massima di download del client
			 */
			currentDownloadsNumber.notifyAll();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
