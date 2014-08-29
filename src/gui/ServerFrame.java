package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import server.ServerInterface;
import client.ClientInterface;

public final class ServerFrame extends AbstractBasicFrame {
	public static final long serialVersionUID = 44L;
	private final JPanel connectedClientsPanel = new JPanel();
	private final JPanel connectedServersPanel = new JPanel();
	private final JList<ClientInterface> connectedClientsList = new JList<ClientInterface>();
	private final JList<ServerInterface> connectedServersList = new JList<ServerInterface>();
	private final JScrollPane areaScrollPaneClients = new JScrollPane(connectedClientsList);
	private final JScrollPane areaScrollPaneServers = new JScrollPane(connectedServersList);

	public ServerFrame(final String paramServerName) {
		super(paramServerName);
		appendLogEntry("Buliding server " + paramServerName);

		topPanel = new JPanel();
		topPanel.setOpaque(true);

		// setting graphics
		connectedClientsPanel.setBorder(BorderFactory.createTitledBorder("Connceted clients"));
		connectedClientsPanel.setOpaque(true);
		areaScrollPaneClients.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneClients.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneClients.setVisible(true);
		connectedClientsPanel.add(areaScrollPaneServers, BorderLayout.CENTER);

		areaScrollPaneServers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPaneServers.setPreferredSize(new Dimension(280, 250));
		areaScrollPaneServers.setVisible(true);
		connectedServersPanel.setBorder(BorderFactory.createTitledBorder("Connceted servers"));
		connectedServersPanel.setOpaque(true);
		connectedServersPanel.add(areaScrollPaneClients, BorderLayout.CENTER);

		topPanel.add(connectedClientsPanel, BorderLayout.LINE_START);
		topPanel.add(connectedServersPanel, BorderLayout.LINE_END);
		mainPanel.add(topPanel, BorderLayout.PAGE_START);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
	}

	public void setConnectedClientsList(final Vector<ClientInterface> paramClientInterface) {
		final DefaultListModel<ClientInterface> model = new DefaultListModel<ClientInterface>();
		for (int i = 0; i < paramClientInterface.size(); i++) {
			model.addElement(paramClientInterface.elementAt(i));
		}
		connectedClientsList.setModel(model);
	}

	public void setConnectedServersList(final Vector<ServerInterface> paramServerInterface) {
		final DefaultListModel<ServerInterface> model = new DefaultListModel<ServerInterface>();
		for (int i = 0; i < paramServerInterface.size(); i++) {
			model.addElement(paramServerInterface.elementAt(i));
		}
		connectedServersList.setModel(model);
	}
}
