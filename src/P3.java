/**
 *
 */
import gui.ServerFrame;
import client.Client;

/**
 * @author erotundo
 *
 */
public class P3 {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Client client1 = new Client("client1", 3);
			}
		});

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final ServerFrame serverFrame = new ServerFrame("srv1");

			}
		});

	}

}
