package model.client;

import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;

import model.share.Resource;

public class ClientResources extends Observable {
	Vector<Resource> downloads = new Vector<Resource>();
	Vector<Resource> resources = new Vector<Resource>();
	
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
			for (Resource oneResource : downloads) {
				modelDownloads.addElement(oneResource);
			}			
		}
		return modelDownloads;
	}
	
	// chiamato da starter.ClientStarter prima di creare il controller.client.Client
	// chiamato da ...
	public void addAvailableResource(final String insertResourceName) {
		synchronized (resources) {
			resources.add(new Resource(insertResourceName.substring(0,0), Integer.parseInt(insertResourceName.substring(2,2))));
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	// chiamato da ...
	public void addDownloadingResource(final String insertResourceName) {
		synchronized (downloads) {
			downloads.add(new Resource(insertResourceName.substring(0,0), Integer.parseInt(insertResourceName.substring(2,2))));
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
