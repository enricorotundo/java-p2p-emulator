/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author erotundo
 *
 */
public abstract class AbstractBasicFrame extends JFrame implements ActionListener {
    protected JPanel contentPane;
    protected JPanel topPanel;
    protected JPanel bottomPanel;
    protected JTextArea log;
    public static final long serialVersionUID = 42L;
    protected abstract void setTopSubPanels() throws ParseException;
    protected abstract void setTopPanel();
    
    /**
	 * 
	 * @param s
	 */
	public AbstractBasicFrame(final String paramFrameNameString) {
		super(paramFrameNameString);
		setSize(new Dimension(400, 500));
		setResizable(false);
		setMainPanel();
		setTopPanel();
		setBottomPanel();
		
        contentPane.add(topPanel,BorderLayout.PAGE_START);
        contentPane.add(bottomPanel,BorderLayout.PAGE_END);
        setContentPane(contentPane);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
	}
	
	/**
	 * Insert a new entry in the log area with a carriage return at the end.
	 * @param logEntry is the String to be inserted into the log.
	 */
	public void appendLogEntry(String logEntry) {
		logEntry += "\n";
		log.insert(logEntry, log.getSelectionEnd());
	}
    
    protected final void setMainPanel() {
        contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        contentPane.setLayout(new BorderLayout(2, 2));
    }
    
    protected final void setBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setOpaque(true);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Log"));
        bottomPanel.setLayout(new BorderLayout());
        
        log = new JTextArea();
        log.setLineWrap(true);
        log.setRows(10);
        log.setEditable(false);
        
        final JScrollPane areaScrollPane = new JScrollPane(log);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        areaScrollPane.setVisible(true);
        
        
        bottomPanel.add(areaScrollPane,BorderLayout.CENTER);
    }
}
