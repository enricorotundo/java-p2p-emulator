package model.client;

import java.util.Vector;

import javax.annotation.Resource;
import javax.swing.DefaultListModel;

public class ClientResources {
	DefaultListModel modelResources;
	DefaultListModel modelDownloads;
	Vector<Resource> downloads;
	Vector<Resource> resources;
	
	// chiamato da views.ClientFrame.updateResourceList()
	public DefaultListModel getModelResources() {
		return modelResources;
	}
	
	// chiamato da views.ClientFrame.updateDownloadList()
	public DefaultListModel getModelDownloads() {
		return modelDownloads;
	}

}
