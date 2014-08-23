/**
 * 
 */
import gui.ServerFrame;
import client.Client;;


/**
 * @author erotundo
 *
 */
public class P3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	Client client1 = new Client("client1", 3);
            }
        });
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	ServerFrame serverFrame = new ServerFrame("srv1");

            }
        });
        
	}

}
