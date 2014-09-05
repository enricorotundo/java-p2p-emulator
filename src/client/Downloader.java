package client;

import java.rmi.RemoteException;
import java.util.Vector;

import resource.ResourceInterface;
import resource.part.ResourcePartInterface;
import resource.part.TransfertStatus;

class Downloader extends Thread implements DownloaderInterface {
	private ClientInterface clientInterface;
	private String nameString;
	/*
	 * viene caricata con le parti che compongono la singola risorsa in scaricamento
	 */
	private Vector<ResourcePartInterface> downloadingParts = new Vector<ResourcePartInterface>();

	/**** RISORSE CONDIVISE DA SINCRONIZZARE *****/
	private Vector<ResourcePartInterface> completeParts = new Vector<ResourcePartInterface>();
	private Vector<ResourceInterface> downloadingResources; // condiviso con Client, e' la lista che viene stampata nella gui
	private Vector<ResourceInterface> alreadyDownloadingResources = new Vector<ResourceInterface>();
	/*********************************************/
	
	public Downloader(final ClientInterface paramClientInterface, final String paramName, Vector<ResourceInterface> paramDownloadingResources) {
		clientInterface = paramClientInterface;
		nameString = paramName;
		// il lock e' sull oggetto non sul riferimento, il ref e' locale ma l'oggetto sta nel client! 
		downloadingResources = paramDownloadingResources;
	}
	
	private void updateGui() {
		// update gui (lock???)
		try {
			clientInterface.getGuiClientFrame().appendLogEntry(nameString + ": Updating gui...");
			clientInterface.getGuiClientFrame().setDownloadQueueList(downloadingResources);
			clientInterface.getGuiClientFrame().setResourceList(clientInterface.getResources());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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
					
					updateGui();
					
					// controllo che ci siano risorse da scaricare
					if (downloadingResources.size() <= 0) {
						clientInterface.getGuiClientFrame().appendLogEntry("No resources to download, see you later! by " + nameString);
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
						
						ResourceInterface resToDownload = null;
						do {
							// this cicla tra le risorse in coda di download
							if(resourceIndex.equals(downloadingResources.size() - 1))
								resourceIndex = 0;
							else 
								resourceIndex++;
							
							// recupero la risorsa da scaricare alla posizione resourceIndex della lista
							resToDownload = downloadingResources.get(resourceIndex);							
						} while (alreadyDownloadingResources.contains(resToDownload));
						alreadyDownloadingResources.add(resToDownload);
//						downloadingResources.remove((int) resourceIndex);
						
						synchronized (downloadingParts) {
							//aggiungo alla lista delle parti da scaricare TUTTE le parti della risorsa da scaricare
							downloadingParts.addAll(resToDownload.getParts());
							//risveglio eventuali thread in attesa di ulteriori parti da scaricare
							downloadingParts.notifyAll();
							
							System.out.println();
							for (ResourcePartInterface resourcePartInterface : downloadingParts) {
								System.out.println(nameString + ": downloadingParts:" + resourcePartInterface);
							}
							System.out.println();
							synchronized (completeParts) {
								for (ResourcePartInterface resourcePartInterface : completeParts) {
									System.out.println(nameString + ": completeParts:" + resourcePartInterface);
								}
								System.out.println();
								
								// elimino dall lista download le risorse di cui ho ottenuto TUTTE le parti			
								if (completeParts.containsAll(resToDownload.getParts())) {
									System.out.println(nameString + ": completeParts.containsAll(resToDownload.getParts())");
									completeParts.clear();
									downloadingResources.remove(resToDownload);
									clientInterface.getResources().add(resToDownload);
									clientInterface.getGuiClientFrame().setResourceList(clientInterface.getResources());
									//vedo se in downlodaing parts ci sono tutte le parti che compongono
									//una delle risorse che sta in downloadingResources
									//se si devo spostare la risorsa completamente scaricata tra le risorse
									//del client
								}
							}
						}
						
						
						sleep(100);
						
						Integer concurrencyLevel = clientInterface.getMinIndex(resToDownload); // prima del for per evitare eventuali chiamate multiple al metodo getMinIndex
						//comunico all'utente che sto per aprire getMinIndex(resToDownload) connessioni,
						if (concurrencyLevel > 0) {
							clientInterface.getGuiClientFrame().appendLogEntry("Opening " + concurrencyLevel + " connections. by " + nameString);							
						}
						/*
						 * avvio getMinIndex(resToDownload) numero di thread che dovranno
						 * scaricare concorrentemente "downloadingParts".
						 * se getMinIndex=0 significa che dovro attendere la prossima iterazione
						 * (siamo dentro a un while(true)!!!)
						 */
						for (int i = 0; i < concurrencyLevel; i++) {							 
							 
							 
							 /*
							  * scarica le "downloadingParts", ogni thread consuma un elemento (PARTE) di downloadingParts
							  */
							 new Thread() {
								 
								 /*
								  * controllo che tra i vari possessori della risorsa della PARTE
								  * non vi sia uno dei client che sta gia trasmettendo a me (getClientsBusyWithMe),
								  * chiamo download sul client che non e' gia impegnato con me,
								  * se tutti i client che possiedono la risorsa che voglio sono impegnati
								  * allora aspetto per un tempo pari al tempo di traserimento di una risorsa
								  * e poi ricontrollo se si e' liberto qualche client
								  */
								 private ClientInterface selectOwner(Vector<ClientInterface> owners, ResourcePartInterface partToDownload) {
									 	ClientInterface selecetdOwner = null;
									 	try {
											clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":prima di individiare il client a cui chiedere la parte " + partToDownload.toString() );
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
													}
												}
	//											sleep(Client.DOWNLOAD_TIME);
											}
											clientInterface.getGuiClientFrame().appendLogEntry(this.toString() + ":il client che mi dara' " + partToDownload.toString() + " e' " + selecetdOwner.getClientName());
									 	} catch (RemoteException e) {
									 		e.printStackTrace();
									 	}
										return selecetdOwner;
								 }
								 
								 private ResourcePartInterface selectPartToDownload() {
									 ResourcePartInterface partToDownload = null;
									 try {
										clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":prima del for che sceglie la parte da scaricare");
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									 while (partToDownload == null) {
											for (ResourcePartInterface part : downloadingParts) {
												if (part.getDownloadingStatus().equals(TransfertStatus.NotStarted)) {
													partToDownload = part;
												}
											}
//											 downloadingParts.wait();
										}
									 return partToDownload;
								 }
								 
								 private void download(ClientInterface selecetdOwner, ResourcePartInterface partToDownload) {
									 	try {
									 		long startTime = System.nanoTime();									 	
//											clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":cambio il flag di " + partToDownload.toString());
											partToDownload.setDownloadingStatus(TransfertStatus.Downloading);
											clientInterface.incrementCount();
											clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":prima di chiedere il download da " + selecetdOwner.getClientName() + " di " + partToDownload.toString());
//											updateGui();
											selecetdOwner.download(); //NOTA: chiamata BLOCCANTE!!!!!!
											// e' gia sincronizzato
											clientInterface.getClientsBusyWithMe().remove(selecetdOwner);
											clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":DOPO aver compleato il download da " + selecetdOwner.getClientName() + " di " + partToDownload.toString());
											clientInterface.decrementCount();
											partToDownload.setDownloadingStatus(TransfertStatus.Completed);
											
											// aggiungo la parte scaricata a una lista di scaricati cosi posso controllare quando in questa lista ho una risorsa intera
											synchronized (completeParts) {
												completeParts.add(partToDownload);												
											}
											
											synchronized (downloadingParts) {
												downloadingParts.remove(partToDownload);													
											}
											
											long endTime = System.nanoTime();
											long duration = (endTime - startTime);
											System.out.println("download(" + selecetdOwner.getClientName() + "," + partToDownload.toString() + ") completed in: " + (duration / 1000000000.0) + " seconds. Static download time is: " + Client.DOWNLOAD_TIME);
											updateGui();
										} catch (RemoteException e) {
											e.printStackTrace();
										}
								 }
								 
								 @Override
								 public void run() {
									 try {
											 ResourcePartInterface partToDownload = null;
											 synchronized (downloadingParts) {
												 clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":starting");
//												 clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ":prima del while (downloadingParts.isEmpty())");
												/*
												 *  viene risvegliato quando il thread padre aggiunge
												 *  una risorsa da scaricare e aggiunge in downloadingParts 
												 *  le sue parti 
												 */
												while (downloadingParts.isEmpty()) {
													 downloadingParts.wait();
												}
												/*
												 * qui devo scaricare le parti, quale scelgo?
												 * la prima che abbia downloadingStatus = notStarted
												 */
												partToDownload = selectPartToDownload();
											 }
												
											 	// recupero chi possiede la risorsa da scaricare
												final Vector<ClientInterface> owners =  clientInterface.getResourceOwners(partToDownload.getOwnerResource().toString(), this.getName());
							
												// selezione del client che mi inviera' la parte
												ClientInterface selecetdOwner = selectOwner(owners, partToDownload);
												// qui selecetdOwner != null prodedo con il download												  
												download(selecetdOwner, partToDownload);	
												
												clientInterface.getGuiClientFrame().appendLogEntry(this.getName() + ": ending");
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