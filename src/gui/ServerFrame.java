package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import server.Server;
import server.ServerInterface;
import client.ClientInterface;

public final class ServerFrame extends AbstractBasicFrame {
	private JPanel connectedClientsPanel;
	private JPanel connectedServersPanel;
	private JList<ClientInterface> connectedClientsList;
	private JList<ServerInterface> connectedServersList;
	public static final long serialVersionUID = 44L;
	private final Server server;

	public ServerFrame(final String s, final Server paramServer) {
		super(s);
		server = paramServer;
		setLists();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		// TODO Auto-generated method stub
	}

	private void setLists() {
		// connectedClientsList = new JList<ClientInterface>();
		// connectedServersList = new JList<ServerInterface>();
		try {
			connectedClientsList = new JList<ClientInterface>(server.getClients());
			connectedServersList = new JList<ServerInterface>(server.getAllServers());
		} catch (final RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void setTopPanel() {
		topPanel = new JPanel();
		topPanel.setOpaque(true);

		setTopSubPanels();

		topPanel.add(connectedClientsPanel, BorderLayout.LINE_START);
		topPanel.add(connectedServersPanel, BorderLayout.LINE_END);
	}

	/**
	 * <code>setTopSubPanels</code> sets in the top panel two paneled
	 * JTextAreas: "Connected client" and "Connected servers"
	 */
	@Override
	protected void setTopSubPanels() {
		connectedClientsPanel = new JPanel();
		connectedServersPanel = new JPanel();

		connectedClientsList = new JList<ClientInterface>();
		connectedServersList = new JList<ServerInterface>();

		// setting graphics
		connectedClientsPanel.setBorder(BorderFactory.createTitledBorder("Connceted clients"));
		connectedClientsPanel.setOpaque(true);
		connectedClientsList.setPreferredSize(new Dimension(getWidth() / 2, 270));

		connectedServersPanel.setBorder(BorderFactory.createTitledBorder("Connceted servers"));
		connectedServersPanel.setOpaque(true);
		connectedServersList.setPreferredSize(new Dimension(getWidth() / 2, 270));

		// connectedClientsList = new JList<Resource>();
		// // connectedClientsList.setPreferredSize(new Dimension(getWidth() / 2
		// -
		// // 80, 270));
		//
		// connectedServersList = new JList<Resource>(client.getResources());
		// // connectedServersList.setPreferredSize(new Dimension(getWidth() / 2
		// -
		// // 80, 270));

		final JScrollPane areaScrollPaneClients = new JScrollPane(connectedClientsList);
		areaScrollPaneClients.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneClients.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneClients.setVisible(true);

		final JScrollPane areaScrollPaneServers = new JScrollPane(connectedServersList);
		areaScrollPaneServers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneServers.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneServers.setVisible(true);

		connectedServersPanel.add(areaScrollPaneClients, BorderLayout.CENTER);
		connectedClientsPanel.add(areaScrollPaneServers, BorderLayout.CENTER);
	}

}
