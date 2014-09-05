package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.MaskFormatter;

import model.client.ClientResources;
import controller.client.Client;

public class ClientFrame extends AbstractBasicFrame implements ActionListener {

	private static final long serialVersionUID = 5543418955298322422L;
	private JPanel searchFilePanel;
	protected JPanel resourcesPanel;
	protected JPanel downloadQueuePanel;
	private JFormattedTextField fileSearchTextField;
	private JButton connectionButton;
	private JButton fileSearchButton;
	private ClientResources resources; // MODEL

	private JList resourcesList;
	private JList downloadsList;

	public ClientFrame(final String s, final ClientResources resources) {
		super(s);
		this.resources = resources;
		setSize(new Dimension(600, 500));

		// setting top panel
		topPanel = new JPanel();
		topPanel.setOpaque(true);
		connectionButton = new JButton("Connect");
		connectionButton.setPreferredSize(new Dimension(150, 50));
		connectionButton.setActionCommand("connection");
		connectionButton.setToolTipText("Connect/disconnect from the network.");
		fileSearchButton = new JButton("Search");
		fileSearchButton.setActionCommand("search");
		fileSearchButton.setToolTipText("Start searching the file in the network.");
		final MaskFormatter formatter = new MaskFormatter();
		try {
			formatter.setMask("U #");
		} catch (final ParseException exc) {
			System.out.println("formatter is bad: " + exc.getMessage());
		}
		fileSearchTextField = new JFormattedTextField(formatter);
		fileSearchTextField.setEditable(true);
		fileSearchTextField.setColumns(2);
		fileSearchTextField.setToolTipText("File name is like \"A 3\"");
		searchFilePanel = new JPanel();
		searchFilePanel.setOpaque(true);
		searchFilePanel.setBorder(BorderFactory.createTitledBorder("File search"));
		searchFilePanel.setLayout(new BorderLayout());
		searchFilePanel.add(fileSearchTextField, BorderLayout.LINE_START);
		searchFilePanel.add(fileSearchButton, BorderLayout.LINE_END);
		topPanel.add(searchFilePanel, BorderLayout.LINE_START);
		topPanel.add(connectionButton, BorderLayout.LINE_END);
		mainPanel.add(topPanel, BorderLayout.PAGE_START);

		// setting west panel
		resourcesPanel = new JPanel();
		resourcesPanel.setOpaque(true);
		resourcesPanel.setBorder(BorderFactory.createTitledBorder("Entire Resources"));
		resourcesPanel.setLayout(new BorderLayout());
		resourcesList = new JList();
		resourcesList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));
		final JScrollPane areaScrollPane = new JScrollPane(resourcesList);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(280, 250));
		areaScrollPane.setVisible(true);
		resourcesPanel.add(areaScrollPane, BorderLayout.CENTER);
		mainPanel.add(resourcesPanel, BorderLayout.LINE_START);

		// setting east panel
		downloadQueuePanel = new JPanel();
		downloadQueuePanel.setOpaque(true);
		downloadQueuePanel.setBorder(BorderFactory.createTitledBorder("Download queue"));
		downloadQueuePanel.setLayout(new BorderLayout());
		downloadsList = new JList();
		downloadsList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));
		final JScrollPane areaScrollPane1 = new JScrollPane(downloadsList);
		areaScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane1.setPreferredSize(new Dimension(280, 250));
		areaScrollPane1.setVisible(true);
		downloadQueuePanel.add(areaScrollPane1, BorderLayout.CENTER);
		mainPanel.add(downloadQueuePanel, BorderLayout.LINE_END);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
	}
	
	public void updateDownloadList() {
		// chiamare model.ClientResources.getDonwloadsModel();
	}
	
	public void updateResourceList() {
		// chiamare model.ClientResources.getResourcesModel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("search")) {
			//cerca
		}
		if (e.getActionCommand().equals("connection".toString())) {
			//connettiti
		}
		
		// notify dal MODEL?
	}
}
