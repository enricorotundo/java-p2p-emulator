package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import resource.Resource;
import resource.Resource.ResourceName;
import resource.part.ResourcePart;

public final class ClientFrame extends AbstractBasicFrame {
	private JPanel searchFilePanel;
	protected JPanel resourcesPanel;
	protected JPanel downloadQueuePanel;
	private JList<Resource> completeResourcesList;
	private JList<ResourcePart> downloadQueueList;
	private JFormattedTextField fileSearchTextField;
	private JButton connectionButton;
	private JButton fileSearchButton;
	public static final long serialVersionUID = 43L;

	public ClientFrame(final String s) {
		super(s);
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
		try {
			fileSearchTextField = new JFormattedTextField(ResourceName.getMask());
		} catch (final ParseException e) {
			e.printStackTrace();
		}
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
		completeResourcesList = new JList<Resource>();
		completeResourcesList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));
		final JScrollPane areaScrollPane = new JScrollPane(completeResourcesList);
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
		downloadQueueList = new JList<ResourcePart>();
		downloadQueueList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));
		final JScrollPane areaScrollPane1 = new JScrollPane(downloadQueueList);
		areaScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane1.setPreferredSize(new Dimension(280, 250));
		areaScrollPane1.setVisible(true);
		downloadQueuePanel.add(areaScrollPane1, BorderLayout.CENTER);
		mainPanel.add(downloadQueuePanel, BorderLayout.LINE_END);

		setContentPane(mainPanel);
		pack();
		setVisible(true);
	}

	public JButton getConnectionButton() {
		return connectionButton;
	}

	public JButton getFileSearchButton() {
		return fileSearchButton;
	}

	public JFormattedTextField getFileSearchTextField() {
		return fileSearchTextField;
	}

	public void setDownloadQueueList(final Vector<ResourcePart> paramResourcePart) {
		final DefaultListModel<ResourcePart> model = new DefaultListModel<ResourcePart>();
		for (int i = 0; i < paramResourcePart.size(); i++) {
			model.addElement(paramResourcePart.elementAt(i));
		}
		downloadQueueList.setModel(model);
	}

	public void setResourceList(final Vector<Resource> paramResources) {
		final DefaultListModel<Resource> model = new DefaultListModel<Resource>();
		for (int i = 0; i < paramResources.size(); i++) {
			model.addElement(paramResources.elementAt(i));
		}
		completeResourcesList.setModel(model);
	}
}
