package client;

import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;
import resource.part.ResourcePartInterface;
import resource.part.TransfertStatus;

class Downloader extends Thread implements DownloaderInterface {
	private ClientInterface clientInterface;
	private String nameString;
	private Vector<ResourceInterface> downloadingResources;
	private Vector<ResourcePartInterface> downloadingParts = new Vector<ResourcePartInterface>();

	//vedo se in downlodaing parts ci sono tutte le parti che compongono
	//una delle risorse che sta in downloadingResources
	//se si devo spostare la risorsa completamente scaricata tra le risorse
	//del client
	private void refreshDownloadingLists() {
		try {
			for (ResourceInterface res : downloadingResources) {
				if (downloadingParts.containsAll(res.getParts())) {
					downloadingParts.removeAll(res.getParts());
					downloadingResources.remove(res);
						clientInterface.getResources().add(res);
					// update gui
					clientInterface.getGuiClientFrame().setDownloadQueueList(downloadingResources);
					clientInterface.getGuiClientFrame().setResourceList(clientInterface.getResources());
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public Downloader(final ClientInterface paramClientInterface, final String paramName, Vector<ResourceInterface> paramDownloadingResources) {
		clientInterface = paramClientInterface;
		nameString = paramName;
		downloadingResources = paramDownloadingResources;
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println(nameString + ": begin");
				synchronized (downloadingResources) {
					System.out.println(nameString + ": lock on " + downloadingResources);
					// update gui
					clientInterface.getGuiClientFrame().setDownloadQueueList(downloadingResources);

					if (downloadingResources.size() <= 0) {
						clientInterface.getGuiClientFrame().appendLogEntry("No parts to download, see you later! by " + nameString);
						downloadingResources.wait();
					}
					
					// qui ce qualcosa da scaricare
					if (clientInterface.getCurrentDownloads().equals(clientInterface.getMaxDownloadCapacity())) {
						clientInterface.getGuiClientFrame().appendLogEntry("Download capacity reached... wait.");
						downloadingResources.wait();
					}
					
					// qui devo scaricare
					if (!downloadingResources.isEmpty()) {
						// recupero la prima risorsa della lista da scaricare
						final ResourceInterface resToDownload = downloadingResources.get(downloadingResources.indexOf(downloadingResources.firstElement()));
						
						synchronized (downloadingParts) {
							//aggiungo alla lista delle parti da scaricare TUTTE le parti della risorsa da scaricare
							downloadingParts.addAll(resToDownload.getParts());
							//risveglio eventuali thread in attesa di ulteriori parti da scaricare
							downloadingParts.notifyAll();								
						}
						
						//comunico all'utente che sto per aprire getMinIndex(resToDownload) connessioni,
						clientInterface.getGuiClientFrame().appendLogEntry("Opening " + clientInterface.getMinIndex(resToDownload) + " connections.");
						/*
						 * avvio getMinIndex(resToDownload) numero di thread che dovranno
						 * scaricare concorrentemente "downloadingParts".
						 * se getMinIndex=0 significa che dovro attendere la prossima iterazione
						 * (siamo dentro a un while(true)!!!)
						 */
						for (int i = 0; i < clientInterface.getMinIndex(resToDownload); i++) {
							 final int j=i;
							 
							 
							 /*
							  * deve svuotare "downloadingParts"
							  */
							 new Thread() {
							 @Override
								 public void run() {
									 try {
										while (true) {
											 synchronized (downloadingParts) {
												 
												 
												 
												while (downloadingParts.isEmpty()) {
													 downloadingParts.wait();
												}
												/*
												 * qui devo scaricare le parti, quale scelgo?
												 * la prima che abbia downloadingStatus = notStarted
												 */
												ResourcePartInterface partToDownload = null;
												for (ResourcePartInterface part : downloadingParts) {
													if (part.getDownloadingStatus().equals(TransfertStatus.NotStarted)) {
														partToDownload = part;
													}
												}
												
												if(partToDownload == null)
													downloadingParts.wait();
												
												
												
												
												partToDownload.setDownloadingStatus(TransfertStatus.Downloading);
												// recupero chi possiede la risorsa da scaricare
												final Vector<ClientInterface> owners =  clientInterface.getResourceOwners(partToDownload.getOwnerResource().toString());
												clientInterface.incrementCurrentDownloadsCounter();
												/* contatto il primo client che possiede la risorsa (TODO:giusto?)
												 * TODO: siamo sicuri che non debba controllare che io non stia gia 
												 * scaricando da lui?
												 */ 
												owners.elementAt(owners.indexOf(owners.firstElement())).download();
												clientInterface.decrementCurrentDownloadsCounter();
												partToDownload.setDownloadingStatus(TransfertStatus.Completed);
//												
												// elimino dall lista download le risorse di cui ho ottenuto TUTTE le parti
												refreshDownloadingLists();
											 }
										}
									 } catch (InterruptedException | RemoteException e) {
										 e.printStackTrace();
									 }
							 	}
							 }.start();
						 }
					}
				}
			} catch (final InterruptedException | RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}