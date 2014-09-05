package model.client;

import java.util.Vector;

import javax.swing.DefaultListModel;

import model.share.Resource;

public class ClientResources {
	DefaultListModel modelResources = new DefaultListModel();
	DefaultListModel modelDownloads = new DefaultListModel();
	Vector<Resource> downloads = new Vector<Resource>();
	Vector<Resource> resources = new Vector<Resource>();
	
	// chiamato da views.ClientFrame.updateResourceList()
	public DefaultListModel getModelResources() {
		return modelResources;
	}
	
	// chiamato da views.ClientFrame.updateDownloadList()
	public DefaultListModel getModelDownloads() {
		return modelDownloads;
	}
	
	// chiamato da starter.ClientStarter prima di creare il controller.client.Client
	// chiamato da ...
	public void addAvailableResource(final Resource insertResource) {
		synchronized (resources) {
			resources.add(insertResource);
		}
	}
	
	// chiamato da ...
	public void addDownloadingResource(final Resource insertResource) {
		synchronized (downloads) {
			downloads.add(insertResource);
		}
	}
	
//	public Vector<Resource> getResources() {
//		return resources;
//	}
//	
//	public Vector<Resource> getDownloads() {
//		return downloads;
//	}

}
