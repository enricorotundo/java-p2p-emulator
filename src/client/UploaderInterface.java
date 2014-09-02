package client;

import java.rmi.RemoteException;

import resource.ResourceInterface;
import resource.part.ResourcePartInterface;

public interface UploaderInterface {
	/**
	 * consente al client di scaricare la parte indicata, simula il tempo di
	 * trasferimento (lato server)!
	 */
	/**
	 * @param paramClient
	 * @param paramResource
	 * @param paramPartNumber
	 * @return the paramPartNumber resource of paramResource, if paramClient
	 *         isn't already downloading parts from this client
	 * @throws RemoteException
	 */
	public ResourcePartInterface downloadPart(final ClientInterface paramClient, final ResourceInterface paramResource, final Integer paramPartNumber) throws RemoteException;

}
