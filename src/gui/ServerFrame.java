package gui;

import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JPanel;

import server.Server;

public final class ServerFrame extends AbstractBasicFrame {
	private JPanel connectedClientsPanel;
	private JPanel connectedServersPanel;
	// private JList<Client> connectedClientsList;
	private JList<Server> connectedServersList;
	public static final long serialVersionUID = 44L;

	public ServerFrame(final String s) {
		super(s);

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void setTopPanel() {
		// topPanel = new JPanel();
		// topPanel.setOpaque(true);
		//
		// setTopSubPanels();
		//
		// topPanel.add(connectedClientsPanel, BorderLayout.LINE_START);
		// topPanel.add(connectedServersPanel, BorderLayout.LINE_END);
	}

	/**
	 * <code>setTopSubPanels</code> sets in the top panel two paneled
	 * JTextAreas: "Connected client" and "Connected servers"
	 */
	@Override
	protected void setTopSubPanels() {
		// connectedClientsPanel = new JPanel();
		// connectedServersPanel = new JPanel();
		// connectedClientsList = new JList<Client>();
		// connectedServersList = new JList<Server>();
		//
		// // setting graphics
		// connectedClientsPanel.setBorder(BorderFactory
		// .createTitledBorder("Connceted clients"));
		// connectedClientsPanel.setOpaque(true);
		// connectedClientsList
		// .setPreferredSize(new Dimension(getWidth() / 2, 270));
		//
		// connectedServersPanel.setBorder(BorderFactory
		// .createTitledBorder("Connceted servers"));
		// connectedServersPanel.setOpaque(true);
		// connectedServersList
		// .setPreferredSize(new Dimension(getWidth() / 2, 270));
		//
		// final JScrollPane areaScrollPaneClients = new
		// JScrollPane(connectedClientsList);
		// areaScrollPaneClients
		// .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// areaScrollPaneClients.setPreferredSize(new Dimension(280, 250));
		// areaScrollPaneClients.setVisible(true);
		//
		// final JScrollPane areaScrollPaneServers = new JScrollPane(
		// connectedServersList);
		// areaScrollPaneServers
		// .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// areaScrollPaneServers.setPreferredSize(new Dimension(280, 250));
		// areaScrollPaneServers.setVisible(true);
		//
		// connectedServersPanel.add(areaScrollPaneClients,
		// BorderLayout.CENTER);
		// connectedClientsPanel.add(areaScrollPaneServers,
		// BorderLayout.CENTER);
	}

}
