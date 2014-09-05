package model.client;

import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;

import model.share.Resource;

public class ClientResources extends Observable {
	Vector<Resource> downloads = new Vector<Resource>();
	Vector<Resource> resources = new Vector<Resource>();
	
	public ClientResources() {
		// STUB
		addDownloadingResource(new Resource("A", 1));
		addAvailableResource(new Resource("B", 2));
	}
	
	// chiamato da views.ClientFrame.updateResourceList()
	public DefaultListModel getModelResources() {
		final DefaultListModel modelResources = new DefaultListModel();
		synchronized (resources) {
			for (Resource oneResource : resources) {
				modelResources.addElement(oneResource);
			}			
		}
		return modelResources;
	}
	
	// chiamato da views.ClientFrame.updateDownloadList()
	public DefaultListModel getModelDownloads() {
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
	public void addAvailableResource(final Resource insertResource) {
		synchronized (resources) {
			resources.add(insertResource);
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}
	
	// chiamato da ...
	public void addDownloadingResource(final Resource insertResource) {
		synchronized (downloads) {
			downloads.add(insertResource);
		}
		// notifico alla VIEW le modifiche
		setChanged();  
		notifyObservers();
	}

}
