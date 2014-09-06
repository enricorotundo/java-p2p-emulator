package model.client;

import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultListModel;

import model.share.Resource;

public class ClientResources extends Observable {
	final private Vector<Resource> downloads = new Vector<Resource>();
	final private Vector<Resource> resources = new Vector<Resource>();
	
	// mantiene lo status sullo scaricamento delle parti associate alla risorsa in download
	private Vector<AtomicInteger> parts = new Vector<AtomicInteger>(); // array di AtomicInteger inizialmente tutti a 0; -1 = in scaricamento; 1 = scaricato
	
	public Vector<AtomicInteger> createParts(final int howManyPartsToDownload) {
		synchronized (parts) {
			this.parts = new Vector<AtomicInteger>(howManyPartsToDownload);
			// notifico alla VIEW le modifiche
			setChanged();  
			notifyObservers();
			return parts;			
		}
	}
	
	public int getPartStatus(final int index) {
		synchronized (parts) {
			return parts.elementAt(index).get();
		}
	}
	
	// status: 1 = completa, 0 = non scaricata, -1 = in scaricamento
	public void setPartStatus(final int index, final int status) {
		synchronized (parts) {
			parts.elementAt(index).set(status);		
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	public Integer getPartsLength() {
		synchronized (parts) {
			return parts.size();
		}
	}
	
	public Integer completePartsCounter() {
		synchronized (parts) {
			int count = 0;
			if (!parts.isEmpty()) {
				for (AtomicInteger val : parts) {
					count += (val.get() == 1 ? 1 : 0);
				}				
			}
			return count;			
		}
	}
	
	// chiamato da views.ClientFrame.updateResourceList()
	public DefaultListModel getDefaultListModelResources() {
		final DefaultListModel modelResources = new DefaultListModel();
		synchronized (resources) {
			for (Resource oneResource : resources) {
				modelResources.addElement(oneResource);
			}			
		}
		return modelResources;
	}
	
	// chiamato da views.ClientFrame.updateDownloadList()
	public DefaultListModel getDefaultListModelDownloads() {
		final DefaultListModel modelDownloads = new DefaultListModel();
		synchronized (downloads) {
			if (!downloads.isEmpty()) {
				// prendo il nome della prima risorsa in download
				final String downloadingResourceName = downloads.get(downloads.indexOf(downloads.firstElement())).toString();
				for (AtomicInteger partStatus : parts) {
					modelDownloads.addElement(downloadingResourceName + " " + partStatus);
				}			
			}
		}
		return modelDownloads;
		
		
//		final DefaultListModel modelDownloads = new DefaultListModel();
//		synchronized (downloads) {
//			for (Resource oneResource : downloads) {
//				modelDownloads.addElement(oneResource);
//			}			
//		}
//		return modelDownloads;
	}
	
	// chiamato da starter.ClientStarter prima di creare il controller.client.Client
	// chiamato da ...
	public void addAvailableResource(final String insertResourceName) {
		synchronized (resources) {
			resources.add(new Resource(insertResourceName.substring(0,1), Integer.parseInt(insertResourceName.substring(2,3))));
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	// chiamato da ...
	public void addDownloadingResource(final String insertResourceName) {
		synchronized (downloads) {
			downloads.add(new Resource(insertResourceName.substring(0,1), Integer.parseInt(insertResourceName.substring(2,3))));
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	public Boolean containsResource(final String resourceToSearchFor) {
		Boolean result = false;
		synchronized (resources) {
			for (Resource singleResource : resources) {
				if (singleResource.toString().equals(resourceToSearchFor)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	public Vector<Resource> getResources() {
		synchronized (resources) {
			return resources;
		}
	}
	
	public Vector<Resource> getDownloadningResources() {
		synchronized (downloads) {
			return downloads;
		}
	}

}
