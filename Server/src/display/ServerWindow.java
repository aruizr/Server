package display;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import application.ServerController;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerWindow {

	private JFrame frame;
	private JTextField commandField;
	private JTextPane textLog;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_1_1;
	private JList<String> listRegisteredUsers;
	private DefaultListModel<String> registeredUsers;
	private JList<String> listOnlineUsers;
	private DefaultListModel<String> onlineUsers;

	/**
	 * Create the application.
	 */
	public ServerWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				initialize();
				frame.setVisible(true);
				commandField.grabFocus();
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to shut the server down?\nAll the queued messages will be deleted and online users will not be updated.", "Confirm closing operation", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
					ServerController.getInstance().exit();
				}
			}
		});
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		commandField = new JTextField();
		commandField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (commandField.getText().length() > 0) {
					ServerController.getInstance().commandReceived(commandField.getText());
					commandField.setText("");
				}
			}
		});
		commandField.setBounds(10, 530, 799, 20);
		frame.getContentPane().add(commandField);
		commandField.setColumns(10);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 799, 508);
		frame.getContentPane().add(scrollPane);
		
		textLog = new JTextPane();
		textLog.setEditable(false);
		scrollPane.setViewportView(textLog);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(819, 36, 155, 245);
		frame.getContentPane().add(scrollPane_1);
		
		registeredUsers = new DefaultListModel<String>();
		
		listRegisteredUsers = new JList<String>(registeredUsers);
		listRegisteredUsers.setEnabled(false);
		scrollPane_1.setViewportView(listRegisteredUsers);
		
		JLabel lblRegisteredUsers = new JLabel("Registered Users");
		lblRegisteredUsers.setBounds(819, 11, 155, 14);
		frame.getContentPane().add(lblRegisteredUsers);
		
		scrollPane_1_1 = new JScrollPane();
		scrollPane_1_1.setBounds(819, 317, 155, 233);
		frame.getContentPane().add(scrollPane_1_1);
		
		onlineUsers = new DefaultListModel<String>();
		
		listOnlineUsers = new JList<String>(onlineUsers);
		listOnlineUsers.setEnabled(false);
		scrollPane_1_1.setViewportView(listOnlineUsers);
		
		JLabel lblOnlineUsers = new JLabel("Online Users");
		lblOnlineUsers.setBounds(819, 292, 155, 14);
		frame.getContentPane().add(lblOnlineUsers);
		
		frame.setLocationRelativeTo(null);
	}
	
	/**
	 * Displays the Collection inserted in the Registered Users field.
	 * @param names
	 * @throws IllegalArgumentException If the inserted Collection is null.
	 */
	public void updateRegisteredUsers(Collection<String> names) {
		if (names == null) throw new IllegalArgumentException("Argument can't be null.");
		registeredUsers.clear();
		for (String name : names) {
			registeredUsers.addElement(name);
		}
	}
	
	/**
	 * Displays the collection inserted in the Online Users field.
	 * @param names
	 * @throws IllegalArgumentException If the inserted Collection is null.
	 */
	public void updateOnlineUsers(Collection<String> names) {
		if (names == null) throw new IllegalArgumentException("Argument can't be null.");
		onlineUsers.clear();
		for (String name : names) {
			onlineUsers.addElement(name);
		}
	}
	
	/**
	 * Adds to the TextPane the insterted text.
	 * @param text
	 */
	public void log(String text) {
		if (text != null) {
			textLog.setText(textLog.getText().concat(text+"\n"));
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		}
	}
}
