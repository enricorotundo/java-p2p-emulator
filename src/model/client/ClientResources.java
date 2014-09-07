package model.client;

import java.util.Observable;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultListModel;
import javax.swing.text.html.HTMLDocument.Iterator;

import model.share.Resource;

public class ClientResources extends Observable {
	final private Vector<Resource> downloads = new Vector<Resource>();
	final private Vector<Resource> resources = new Vector<Resource>();
	
	// mantiene lo status sullo scaricamento delle parti associate alla risorsa in download
	private Vector<AtomicInteger> parts = new Vector<AtomicInteger>(); // array di AtomicInteger inizialmente tutti a 0; -1 = in scaricamento; 1 = scaricato
	
	public Vector<AtomicInteger> createParts(final int howManyPartsToDownload) {
		synchronized (parts) {
			parts.clear();
			for (int i = 0; i < howManyPartsToDownload; i++) {
				parts.add(new AtomicInteger(0));
			}
			// notifico alla VIEW le modifiche
			setChanged();  
			notifyObservers();
			return parts;			
		}
	}
	
	public void resetDownloadDatas() {
		synchronized (parts) {
			parts.clear();
		}
		synchronized (downloads) {
			downloads.clear();
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	public int getPartStatus(final int index) {
		synchronized (parts) {
			return parts.elementAt(index).get();
		}
	}
	
	// status: 1 = completa, 0 = non scaricata, -1 = in scaricamento
	public void setPartStatus(final int index, final int status) {
		synchronized (parts) {
			if (!parts.isEmpty()) {
				parts.elementAt(index).set(status);						
			}
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
				for (Resource downloadingResource : downloads) {
					int index = 1;
					for (AtomicInteger partStatus : parts) {
						modelDownloads.addElement(downloadingResource + " [" + index + "/" + parts.size()+ "] " + partStatusInterpreter(partStatus));
						index++;
					}			
				}				
			}
		}
		return modelDownloads;
	}
	
	private String partStatusInterpreter(final AtomicInteger partStatus) {
		final String result;
		switch (partStatus.get()) {
			case 2: result = "[Failed]"; break;
			case 1: result = "[Completed]"; break;
			case -1: result = "[Downloading]"; break;
			default: result = "[Not started]"; break;
		}
		return result;
	}
	
	// chiamato da starter.ClientStarter prima di creare il controller.client.Client
	// chiamato da ...
	public void addAvailableResource(final String insertResourceName) {
		final Resource toInsertResource = new Resource(insertResourceName.substring(0,1), Integer.parseInt(insertResourceName.substring(2,3)));
		synchronized (downloads) {
			/*
			 * posso perche e' richiesto il download di SOLO una risorsa alla volta
			 */
			downloads.clear(); 
		}
		synchronized (resources) {
			resources.add(toInsertResource);
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
