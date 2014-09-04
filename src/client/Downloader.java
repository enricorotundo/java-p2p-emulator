package client;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;

import resource.ResourceInterface;
import resource.part.ResourcePartInterface;
import resource.part.TransfertStatus;

class Downloader extends Thread implements DownloaderInterface {
	private ClientInterface clientInterface;
	private String nameString;
	private Vector<ResourcePartInterface> downloadingParts = new Vector<ResourcePartInterface>();
	private Vector<ResourcePartInterface> completeParts = new Vector<ResourcePartInterface>();

	/**** RISORSE CONDIVISE DA SINCRONIZZARE *****/
	private Vector<ResourceInterface> downloadingResources; // condiviso con Client
	/*********************************************/
	
	public Downloader(final ClientInterface paramClientInterface, final String paramName, Vector<ResourceInterface> paramDownloadingResources) {
		clientInterface = paramClientInterface;
		nameString = paramName;
		// il lock e' sull oggetto non sul riferimento, il ref e' locale ma l'oggetto sta nel client! 
		downloadingResources = paramDownloadingResources;
	}

	@Override
	public void run() {
		Integer resourceIndex = 0;
		while (true) {
			/*
			 * esegue finche' ci sono downloadingResources
			 * viene risvegliato dal client quando accerta che l'utente non
			 * possiede la risorsa ed esistono client che la possiedono 
			 */
			try {
				synchronized (downloadingResources) {
					
					// controllo che ci siano risorse da scaricare
					if (downloadingResources.size() <= 0) {
						clientInterface.getGuiClientFrame().appendLogEntry("No parts to download, see you later! by " + nameString);
						/*
						 *  viene risvegliato da Client.performSearch() che aggiunge 
						 *  a downloadingResources le risorse da scaricare e risveglia
						 *  i thread in wait su downloadingResources  
						 */
						downloadingResources.wait();
					}
					
					// qui ce qualcosa da scaricare, controllo se lo posso scaricare
					if (clientInterface.getCount() >= clientInterface.getMaxDownloadCapacity()) {
						clientInterface.getGuiClientFrame().appendLogEntry("Download capacity reached... wait.");			
						
						downloadingResources.wait();
					}
					
					// qui devo scaricare
					if (!downloadingResources.isEmpty()) {
						
						// update gui
						clientInterface.getGuiClientFrame().setDownloadQueueList(downloadingResources);
						clientInterface.getGuiClientFrame().repaint();
						
						// this cicla tra le risorse in coda di download
						if(resourceIndex.equals(downloadingResources.size() - 1))
							resourceIndex = 0;
						else {
							resourceIndex++;
						}
						
						// recupero la risorsa da scaricare alla posizione resourceIndex della lista
						final ResourceInterface resToDownload = downloadingResources.get(resourceIndex);
						
						synchronized (downloadingParts) {
							//aggiungo alla lista delle parti da scaricare TUTTE le parti della risorsa da scaricare
							downloadingParts.addAll(resToDownload.getParts());
							//risveglio eventuali thread in attesa di ulteriori parti da scaricare
							downloadingParts.notifyAll();
							
							// elimino dall lista download le risorse di cui ho ottenuto TUTTE le parti			
							if (completeParts.containsAll(resToDownload.getParts())) {
								downloadingResources.remove(resToDownload);
								clientInterface.getResources().add(resToDownload);
								clientInterface.getGuiClientFrame().setResourceList(clientInterface.getResources());
								//vedo se in downlodaing parts ci sono tutte le parti che compongono
								//una delle risorse che sta in downloadingResources
								//se si devo spostare la risorsa completamente scaricata tra le risorse
								//del client
							}
						}
						
						
						sleep(100);
						
						Integer concurrencyLevel = clientInterface.getMinIndex(resToDownload); // prima del for per evitare eventuali chiamate multiple al metodo getMinIndex
						//comunico all'utente che sto per aprire getMinIndex(resToDownload) connessioni,
						clientInterface.getGuiClientFrame().appendLogEntry("Opening " + concurrencyLevel + " connections. by " + nameString);
						/*
						 * avvio getMinIndex(resToDownload) numero di thread che dovranno
						 * scaricare concorrentemente "downloadingParts".
						 * se getMinIndex=0 significa che dovro attendere la prossima iterazione
						 * (siamo dentro a un while(true)!!!)
						 */
						for (int i = 0; i < concurrencyLevel; i++) {							 
							 
							 
							 /*
							  * svuota "downloadingParts", ogni thread deve consumare una PARTE di downloadingParts
							  */
							 new Thread() {
								 
								 @Override
								 public void run() {
									 try {
//										while (true) {
											 ResourcePartInterface partToDownload = null;
											 synchronized (downloadingParts) {
												 clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":starting");
												 clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima del while (..)");
												/*
												 *  viene risvegliato quando il thread padre aggiunge
												 *  una risorsa da scaricare e aggiunge in downloadingParts 
												 *  le sue parti 
												 */
												while (downloadingParts.isEmpty()) {
													 downloadingParts.wait();
												}
												
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima del for che sceglie la parte da scaricare");
												/*
												 * qui devo scaricare le parti, quale scelgo?
												 * la prima che abbia downloadingStatus = notStarted
												 */
												while (partToDownload == null) {
													for (ResourcePartInterface part : downloadingParts) {
														if (part.getDownloadingStatus().equals(TransfertStatus.NotStarted)) {
															partToDownload = part;
														}
													}
//													 downloadingParts.wait();
												}
//												if(partToDownload == null) // forse non serve ce gia il controllo a inizio blocco synch
//													downloadingParts.wait();
												
											 }
												
												
												
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":cambio il flag di " + partToDownload.toString());
												partToDownload.setDownloadingStatus(TransfertStatus.Downloading);
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima di recuperare tutti i client che hanno " + partToDownload.toString());
												// recupero chi possiede la risorsa da scaricare
												final Vector<ClientInterface> owners =  clientInterface.getResourceOwners(partToDownload.getOwnerResource().toString());
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":ho recuperato tutti client che possiedono " + partToDownload.toString());
												
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima di individiare il client a cui chiedere la parte " + partToDownload.toString() );
												ClientInterface selecetdOwner = null;
												/*
												 * controllo che tra i vari possessori della risorsa della PARTE
												 * non vi sia uno dei client che sta gia trasmettendo a me (getClientsBusyWithMe),
												 * chiamo download sul client che non e' gia impegnato con me,
												 * se tutti i client che possiedono la risorsa che voglio sono impegnati
												 * allora aspetto per un tempo pari al tempo di traserimento di una risorsa
												 * e poi ricontrollo se si e' liberto qualche client
												 */
												while (selecetdOwner == null) {		
													for (int j = 0; j < owners.size() && selecetdOwner == null; j++) {
														/*
														 *  per ogni owner controllo se NON e' contenuto nell insieme dei client
														 *  con me impegnati
														 */
														if (clientInterface.getClientsBusyWithMe().contains(owners.elementAt(j))) {
															clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ": " + owners.elementAt(j).getClientName() + " e' tra i client impegnati con me");
														} else {
															selecetdOwner = owners.elementAt(j);
															// e' gia sincronizzato
															clientInterface.getClientsBusyWithMe().add(selecetdOwner);
															clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":il client che mi dara' " + partToDownload.toString() + " e' " + selecetdOwner.getClientName());
														}
													}
//													sleep(Client.DOWNLOAD_TIME);
												}
												
												/* 
												 * qui selecetdOwner != null
												 * prodedo con il download
												 */ 
												clientInterface.incrementCount();
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima di chiedere il download da " + selecetdOwner.getClientName());
												selecetdOwner.download(); //NOTA: chiamata BLOCCANTE!!!!!!
												// e' gia sincronizzato
												clientInterface.getClientsBusyWithMe().remove(selecetdOwner);
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":DOPO aver compleato il download da " + selecetdOwner.getClientName());
												clientInterface.decrementCount();
												// aggiungo la parte scaricata a una lista di scaricati cosi posso controllare quando in questa lista ho una risorsa intera
												completeParts.add(partToDownload);
												synchronized (downloadingParts) {
													downloadingParts.remove(partToDownload);													
												}
												partToDownload.setDownloadingStatus(TransfertStatus.Completed);
												
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima di ridisegnare la finestra");
												// update gui
												clientInterface.getGuiClientFrame().repaint();
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":dopo aver prima di ridisegato la finestra");

												
												clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ": ending");
//										}
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