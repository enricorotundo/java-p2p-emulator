package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import resource.Resource;
import resource.ResourceInterface.ResourceName;
import resource.part.ResourcePart;
import client.Client;

public final class ClientFrame extends AbstractBasicFrame {
	private JPanel searchFilePanel;
	protected JPanel westPanel;
	protected JPanel eastPanel;
	private JList<Resource> completeFilesList;
	private JList<ResourcePart> downloadQueueList;
	private JFormattedTextField fileSearchTextField;
	private JButton connectionButton;
	private JButton fileSearchButton;
	private final Client client;
	public static final long serialVersionUID = 43L;

	public ClientFrame(final String s, final Client paramClient) {
		super(s);
		client = paramClient;
		setSize(new Dimension(600, 500));
		setWestPanel();
		setEastPanel();

		contentPane.add(westPanel, BorderLayout.LINE_START);
		contentPane.add(eastPanel, BorderLayout.LINE_END);
		setContentPane(contentPane);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if ("search".equals(e.getActionCommand()))
			performSearch();
		else if ("connection".equals(e.getActionCommand()))
			performConnection();
	}

	public JList<Resource> getCompleteFilesList() {
		return completeFilesList;
	}

	public JList<ResourcePart> getDownloadQueueList() {
		return downloadQueueList;
	}

	private void performConnection() {
		fileSearchTextField.requestFocus();
		appendLogEntry("Connection button pressed.");
		final Integer connectionResultInteger = client.connect();
		if (connectionResultInteger == 1) {
			appendLogEntry("Connection done.");
			setConnectionButtonText("Disconnect");
		} else {
			if (connectionResultInteger == 0) {
				appendLogEntry("Disconnection done.");
				setConnectionButtonText("Connect");
			} else {
				if (connectionResultInteger == -1) {
					appendLogEntry("Connect/Disconnect something wrong.");
					setConnectionButtonText("Connect?");
				}
			}
		}
	}

	private void performSearch() {
		fileSearchTextField.requestFocus();
		if (fileSearchTextField.getValue() == null) {
			JOptionPane.showMessageDialog(this, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
		} else {
			if (client.getConnectionUpBoolean()) {
				appendLogEntry("Searching for: " + fileSearchTextField.getValue());

				final Resource searchedResource = new Resource(fileSearchTextField.getValue().toString());

				final Resource returnedResource = client.requestResource(searchedResource);

				// TODO check if != null
				// TODO
				// TODO avviare la ricerca e l eventuale download
			} else {
				JOptionPane.showMessageDialog(this, "Connect first.", "File name empty", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/**
	 * @param completeFilesList
	 *            the completeFilesList to set
	 */
	public void setCompleteFilesList(final JList<Resource> completeFilesList) {
		this.completeFilesList = completeFilesList;
	}

	private void setConnectionButtonText(final String paramText) {
		connectionButton.setText(paramText);
	}

	public void setDownloadQueueList(final JList<ResourcePart> downloadQueueList) {
		this.downloadQueueList = downloadQueueList;
	}

	protected void setEastPanel() {
		eastPanel = new JPanel();
		eastPanel.setOpaque(true);
		eastPanel.setBorder(BorderFactory.createTitledBorder("Download queue"));
		eastPanel.setLayout(new BorderLayout());

		downloadQueueList = new JList<ResourcePart>(client.getDownloadingParts());
		downloadQueueList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));

		final JScrollPane areaScrollPane = new JScrollPane(downloadQueueList);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(280, 250));
		areaScrollPane.setVisible(true);

		eastPanel.add(areaScrollPane, BorderLayout.CENTER);
	}

	@Override
	protected void setTopPanel() {
		topPanel = new JPanel();
		topPanel.setOpaque(true);

		connectionButton = new JButton("Connect");
		connectionButton.setPreferredSize(new Dimension(150, 50));
		connectionButton.setActionCommand("connection");
		connectionButton.setToolTipText("Connect/disconnect from the network.");
		connectionButton.addActionListener(this);

		try {
			setTopSubPanels();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		topPanel.add(searchFilePanel, BorderLayout.LINE_START);
		topPanel.add(connectionButton, BorderLayout.LINE_END);
	}

	/**
	 * <code>setTopSubPanels</code> sets in the top panel a paneled JTextArea
	 * and JButton, and an unpaneled JButton to "disconnect" the client.
	 *
	 * @throws ParseException
	 */
	@Override
	protected void setTopSubPanels() throws ParseException {
		fileSearchButton = new JButton("Search");
		fileSearchButton.setActionCommand("search");
		fileSearchButton.setToolTipText("Start searching the file in the network.");
		fileSearchButton.addActionListener(this);

		fileSearchTextField = new JFormattedTextField(ResourceName.getMask());
		fileSearchTextField.setEditable(true);
		fileSearchTextField.setColumns(2);
		fileSearchTextField.setToolTipText("File name is like \"A 3\"");

		searchFilePanel = new JPanel();
		searchFilePanel.setOpaque(true);
		searchFilePanel.setBorder(BorderFactory.createTitledBorder("File search"));
		searchFilePanel.setLayout(new BorderLayout());

		searchFilePanel.add(fileSearchTextField, BorderLayout.LINE_START);
		searchFilePanel.add(fileSearchButton, BorderLayout.LINE_END);
	}

	protected void setWestPanel() {
		westPanel = new JPanel();
		westPanel.setOpaque(true);
		westPanel.setBorder(BorderFactory.createTitledBorder("Entire Resources"));
		westPanel.setLayout(new BorderLayout());

		completeFilesList = new JList<Resource>(client.getResources());
		completeFilesList.setPreferredSize(new Dimension(getWidth() / 2 - 80, 270));

		final JScrollPane areaScrollPane = new JScrollPane(completeFilesList);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(280, 250));
		areaScrollPane.setVisible(true);

		westPanel.add(areaScrollPane, BorderLayout.CENTER);
	}
}
