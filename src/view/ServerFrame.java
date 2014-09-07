package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.server.ConnectedClients;
import model.server.ConnectedServers;

public class ServerFrame extends AbstractBasicFrame implements Observer {

	private static final long serialVersionUID = -7319784823079373838L;
	private final JPanel connectedClientsPanel = new JPanel();
	private final JPanel connectedServersPanel = new JPanel();
	private final JList<String> connectedClientsList;
	private final JList<String> connectedServersList;
	private final JScrollPane areaScrollPaneClients;
	private final JScrollPane areaScrollPaneServers;

	
	private ConnectedClients connectedClients; // MODEL
	private ConnectedServers connectedServers; // MODEL
	
	public ServerFrame(final String paramServerName, final ConnectedClients connectedClients, final ConnectedServers connectedServers) {
		super(paramServerName);
		this.connectedClients = connectedClients; // assegno il MODEL
		this.connectedServers = connectedServers; // assegno il MODEL
		
		appendLogEntry("Buliding server " + paramServerName);

		topPanel = new JPanel();
		topPanel.setOpaque(true);
		
		// init JLists
		connectedClientsList = new JList<String>();
		updateConnectedClients();
		connectedServersList = new JList<String>();
		updateConnectedServers();

		// init Panes
		areaScrollPaneClients = new JScrollPane(connectedClientsList);
		areaScrollPaneServers = new JScrollPane(connectedServersList);

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
	public final void updateConnectedClients() {	
		// chiamare model.ConnectedClients.getConnectedClientsModel();	
		try {
			
			connectedServersList.setModel(connectedClients.getConnectedClientsDefaultListModel());			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	// chiamato da model.server.ConnectedServers al quale chiede i dati da visualizzare
	public final void updateConnectedServers() {
		// chiamare model.ConnectedServers.getConnectedServersModel();
		try {
			
			connectedServersList.setModel(connectedClients.getConnectedClientsDefaultListModel());			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	// invocato quando il MODEL viene modificato -> aggiorna la VIEW
	@Override
	public void update(Observable o, Object arg) {
		updateConnectedClients();
		updateConnectedServers();
	}
}
