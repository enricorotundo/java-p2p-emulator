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
import client.Client;
import resource.Resource;
import resource.ResourceInterface.ResourceName;
import resource.part.ResourcePart;

/**
 * @author erotundo
 *
 */
public final class ClientFrame extends AbstractBasicFrame {
    private JPanel searchFilePanel;
    protected JPanel westPanel;
    protected JPanel eastPanel;
    private JList<Resource> completeFilesList;
    private JList<ResourcePart> downloadQueueList;
    private JFormattedTextField fileSearchTextField;
    private JButton connectionButton;
    private JButton fileSearchButton;
    private Client myClient;
    public static final long serialVersionUID = 43L;

    
	/**
	 * @param s
	 */
	public ClientFrame(String s, Client paramClient) {
		super(s);    
		myClient = paramClient;
		setSize(new Dimension(600, 500));
		setWestPanel();
		setEastPanel();

        contentPane.add(westPanel, BorderLayout.LINE_START);
        contentPane.add(eastPanel, BorderLayout.LINE_END);
        setContentPane(contentPane);
	}
	
	private void setConnectionButtonText(String paramText) {
		connectionButton.setText(paramText);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		 if ("search".equals(e.getActionCommand())) 
			 performSearch();
		  else
			  if ("connection".equals(e.getActionCommand()))
				  performConnection();
	}
	
	
	private void performSearch() {
		fileSearchTextField.requestFocus();
		 if (fileSearchTextField.getValue() == null) {
			 JOptionPane.showMessageDialog(this, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
		} else {
			if(myClient.getConnectionStatus()) {
				appendLogEntry("Searching for: " + fileSearchTextField.getValue());
				
				Resource searchedResource = new Resource(fileSearchTextField.getValue().toString());
				
				Resource returnedResource = myClient.requestResource(searchedResource);
				
				
				//TODO check if != null
				//TODO
				//TODO avviare la ricerca e l eventuale download
			}
			else {
				JOptionPane.showMessageDialog(this, "Connect first.", "File name empty", JOptionPane.WARNING_MESSAGE);
			}			
		}
	}
	
	private void performConnection() {
		fileSearchTextField.requestFocus();
		appendLogEntry("Connection button pressed.");
		Integer connectionResultInteger = myClient.connect();
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
	
	
    /**
     * <code>setTopSubPanels</code> sets in the top panel a paneled JTextArea and JButton, and an unpaneled JButton to "disconnect" the client.
     * @throws ParseException 
     */
    protected void setTopSubPanels() throws ParseException {
    	fileSearchButton = new JButton("Search");
    	fileSearchButton.setActionCommand("search");
    	fileSearchButton.setToolTipText("Start searching the file in the network.");
    	fileSearchButton.addActionListener(this);	
    	
    	fileSearchTextField = new JFormattedTextField(ResourceName.getMask());
    	fileSearchTextField.setEditable(true);
    	fileSearchTextField.setColumns(2);
//    	fileSearchTextField.addPropertyChangeListener("search", this);
    	
    	searchFilePanel = new JPanel();
    	searchFilePanel.setOpaque(true);
    	searchFilePanel.setBorder(BorderFactory.createTitledBorder("File search"));
    	searchFilePanel.setLayout(new BorderLayout());
    	
    	searchFilePanel.add(fileSearchTextField, BorderLayout.LINE_START);
    	searchFilePanel.add(fileSearchButton, BorderLayout.LINE_END);
    }
    
    protected void setTopPanel() {
        topPanel = new JPanel();
        topPanel.setOpaque(true);
        
        connectionButton = new JButton("Connect");
        connectionButton.setPreferredSize(new Dimension(150,50));
        connectionButton.setActionCommand("connection");
        connectionButton.setToolTipText("Connect/disconnect from the network.");
        connectionButton.addActionListener(this);	

        try {
        	setTopSubPanels();
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        topPanel.add(searchFilePanel, BorderLayout.LINE_START);
        topPanel.add(connectionButton, BorderLayout.LINE_END);        
    }
    
    protected void setWestPanel() {
    	westPanel = new JPanel();
    	westPanel.setOpaque(true);
    	westPanel.setBorder(BorderFactory.createTitledBorder("Entire Resources"));
    	westPanel.setLayout(new BorderLayout());

        completeFilesList = new JList<Resource>(myClient.getMyResources());
        completeFilesList.setPreferredSize(new Dimension(getWidth()/2 - 80, 270));
        
        JScrollPane areaScrollPane = new JScrollPane(completeFilesList);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(280, 250));
        areaScrollPane.setVisible(true);
        
    	westPanel.add(areaScrollPane, BorderLayout.CENTER);
    }
    
    protected void setEastPanel() {
    	eastPanel = new JPanel();
    	eastPanel.setOpaque(true);
    	eastPanel.setBorder(BorderFactory.createTitledBorder("Download queue"));
    	eastPanel.setLayout(new BorderLayout());
    	    	
    	downloadQueueList = new JList<ResourcePart>(myClient.getMyDownloadingParts());    	
    	downloadQueueList.setPreferredSize(new Dimension(getWidth()/2 - 80, 270));
    	
    	JScrollPane areaScrollPane = new JScrollPane(downloadQueueList);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(280, 250));
        areaScrollPane.setVisible(true);
    	
    	eastPanel.add(areaScrollPane, BorderLayout.CENTER);
    }

	/**
	 * @return the completeFilesList
	 */
	public JList<Resource> getCompleteFilesList() {
		return completeFilesList;
	}

	/**
	 * @param completeFilesList the completeFilesList to set
	 */
	public void setCompleteFilesList(JList<Resource> completeFilesList) {
		this.completeFilesList = completeFilesList;
	}

	/**
	 * @return the downloadQueueList
	 */
	public JList<ResourcePart> getDownloadQueueList() {
		return downloadQueueList;
	}

	/**
	 * @param downloadQueueList the downloadQueueList to set
	 */
	public void setDownloadQueueList(JList<ResourcePart> downloadQueueList) {
		this.downloadQueueList = downloadQueueList;
	}
}
    
//	/* (non-Javadoc)
//	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
//	 */
//	@Override
//	public void propertyChange(PropertyChangeEvent evt) { /** Called when a field's "value" property changes. */
//		
//	}
