package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ServerFrame extends AbstractBasicFrame {

	private static final long serialVersionUID = -7319784823079373838L;
	private final JPanel connectedClientsPanel = new JPanel();
	private final JPanel connectedServersPanel = new JPanel();
	private final JList<String> connectedClientsList = new JList<String>();
	private final JList<String> connectedServersList = new JList<String>();
	private final JScrollPane areaScrollPaneClients = new JScrollPane(connectedClientsList);
	private final JScrollPane areaScrollPaneServers = new JScrollPane(connectedServersList);

	
	public ServerFrame(final String paramServerName) {
		super(paramServerName);
		appendLogEntry("Buliding server " + paramServerName);

		topPanel = new JPanel();
		topPanel.setOpaque(true);

		// setting clients panel
		connectedClientsPanel.setBorder(BorderFactory.createTitledBorder("Connected clients"));
		connectedClientsPanel.setOpaque(true);
		areaScrollPaneClients.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneClients.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneClients.setVisible(true);
		connectedClientsPanel.add(areaScrollPaneClients, BorderLayout.CENTER);

		// setting servers panel
		areaScrollPaneServers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneServers.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneServers.setVisible(true);
		connectedServersPanel.setBorder(BorderFactory.createTitledBorder("Connected servers"));
		connectedServersPanel.setOpaque(true);
		connectedServersPanel.add(areaScrollPaneServers, BorderLayout.CENTER);

		topPanel.add(connectedClientsPanel, BorderLayout.LINE_START);
		topPanel.add(connectedServersPanel, BorderLayout.LINE_END);
		mainPanel.add(topPanel, BorderLayout.PAGE_START);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
	}
	
	// chiamato da model.server.ConnectedClients  al quale chiede i dati da visualizzare
	public void updateConnectedClients() {	
//		TODO
	}
	
	// chiamato da model.server.ConnectedServers al quale chiede i dati da visualizzare
	public void updateConnectedServers() {
//		TODO
	}
}
