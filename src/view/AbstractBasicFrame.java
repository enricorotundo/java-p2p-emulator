package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Timestamp;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public abstract class AbstractBasicFrame extends JFrame {
	
	/*
	 * http://stackoverflow.com/questions/4448523/how-can-i-catch-event-dispatch-thread-edt-exceptions
	 */
	public static class ExceptionHandler
	implements Thread.UncaughtExceptionHandler {
		
		public void handle(Throwable thrown) {
			// for EDT exceptions
			handleException(Thread.currentThread().getName(), thrown);
		}
		
		public void uncaughtException(Thread thread, Throwable thrown) {
			// for other uncaught exceptions
			handleException(thread.getName(), thrown);
		}
		
		protected void handleException(String tname, Throwable thrown) {
			System.err.println("Exception on " + tname);
			thrown.printStackTrace();
		}
	}
	
	

	private static final long serialVersionUID = 4673538213978317742L;
	protected JPanel mainPanel;
	protected JPanel topPanel;
	protected JPanel bottomPanel;
	protected JTextArea log;
	private Object consoleMonitor = new Object();
	
	public AbstractBasicFrame(final String paramFrameNameString) {
		super(paramFrameNameString);
		
		/*
		 * http://stackoverflow.com/questions/4448523/how-can-i-catch-event-dispatch-thread-edt-exceptions
		 */
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		
		setSize(new Dimension(400, 500));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setting main panel
		mainPanel = new JPanel();
		mainPanel.setOpaque(true);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		mainPanel.setLayout(new BorderLayout(2, 2));

		// setting bottom
		bottomPanel = new JPanel();
		bottomPanel.setOpaque(true);
		bottomPanel.setBorder(BorderFactory.createTitledBorder("Log"));
		bottomPanel.setLayout(new BorderLayout());
		log = new JTextArea();
		log.setCaretPosition(0);
		log.setLineWrap(true);
		log.setRows(10);
		log.setEditable(false);
		final JScrollPane areaScrollPane = new JScrollPane(log);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(250, 250));
		areaScrollPane.setVisible(true);
		bottomPanel.add(areaScrollPane, BorderLayout.CENTER);
		mainPanel.add(bottomPanel,BorderLayout.PAGE_END);
	}

	/**
	 * Insert a new entry in the log area with a carriage return at the end.
	 * NOTE: insert is synchronized!
	 * @param logEntry is the String to be inserted into the log.
	 */
	public void appendLogEntry(final String logEntry)  {
		synchronized (consoleMonitor) {
			final java.util.Date date = new java.util.Date();
			log.insert(new Timestamp(date.getTime()).toString().substring(11) + ": " + logEntry + "\n", 0);
		}
	}

}
