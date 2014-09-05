package controller.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface ClientInterface extends Remote {
	
	public Boolean download(String callerName) throws RemoteException;
	public String getClientName() throws RemoteException;
	public String getConnectedServer() throws RemoteException;
	public Boolean test() throws RemoteException;
	public Vector<String[]> getResourceList() throws RemoteException;
}
