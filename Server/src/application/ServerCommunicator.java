package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import domain.Connection;
import domain.Message;
import domain.Settings;

public class ServerCommunicator {
	
	private Map<String, Connection> identifiedConnections;
	private Collection<Connection> unidentifiedConnections;
	private Collection<Message> messageQueue;

	/**
	 * When the instance is created, a new listeting Thread is automatically created.
	 */
	public ServerCommunicator() {
		identifiedConnections = new HashMap<String, Connection>();
		unidentifiedConnections = new HashSet<Connection>();
		messageQueue = new ArrayList<Message>();
		listen();
	}

	/**
	 * Sends the inserted {@code Message} to the {@code Connection} with the name or address specified in the {@code Message}.
	 * @param message
	 * @throws IOException If there is a problem seding the {@code Message}.
	 * @throws IllegalArgumentException If the argument is null.
	 */
	public void send(Message message) throws IOException {
		checkNull(message);
		if (identifiedConnections.containsKey(message.getDestinationName())) identifiedConnections.get(message.getDestinationName()).send(message);
		else messageQueue.add(message);
	}
	
	/**
	 * If there is an identified {@code Connection} with the user name inserted, sends all the Messages from the Message Queue destinated to the inserted user name.
	 * @param name
	 * @throws IOException If there is a problem sending the Messages.
	 * @throws IllegalArgumentException If the argument is null.
	 */
	public void sendQueue(String name) throws IOException {
		checkNull(name);
		Connection connection = identifiedConnections.get(name);
		for (Iterator<Message> iterator = messageQueue.iterator(); iterator.hasNext();) {
			Message message = iterator.next();
			if (message.getDestinationName().equals(name)) {
				connection.send(message);
				iterator.remove();
			}
		}
	}
	
	public void broadcast(Message message) throws IOException {
		for (Connection connection : identifiedConnections.values()) {
			connection.send(message);
		}
	}
	
	/**
	 * Maps an unidentified {@code Connection} containing the introduced address with the introduced user name.
	 * @param name
	 * @param address
	 * @throws IllegalArgumentException If one of the parameters are null.
	 */
	public void identify(String name, String address) {
		checkNull(name);
		checkNull(address);
		Connection connection = getUnidentifiedConnection(address);
		identifiedConnections.put(name, connection);
		unidentifiedConnections.remove(connection);
	}
	
	/**
	 * Removes all the inactive Connections.
	 */
	public void purge() {
		for (Iterator<Connection> iterator = unidentifiedConnections.iterator(); iterator.hasNext();) {
			if (!iterator.next().isActive()) iterator.remove();
		}
		for (Iterator<Map.Entry<String, Connection>> iterator = identifiedConnections.entrySet().iterator(); iterator.hasNext();) {
			if (!iterator.next().getValue().isActive()) iterator.remove();
		}
	}
	
	/**
	 * Moves the connection mapped with insterted User name to the unidentified connections list.
	 * @param name
	 */
	public void unidentify(String name) {
		checkNull(name);
		Connection connection = identifiedConnections.get(name);
		identifiedConnections.remove(name);
		unidentifiedConnections.add(connection);
	}
	
	/**
	 * @return Collection containing all the user names of the identified connections.
	 */
	public Collection<String> getIdentifications() {
		return identifiedConnections.keySet();
	}
	
	/**
	 * @param address
	 * @return A {@code Connection} with the specified address, null if no such {@code Connection} could be found.
	 */
	private Connection getUnidentifiedConnection(String address) {
		for (Connection connection : unidentifiedConnections) {
			if (connection.getAddress().equals(address)) return connection;
		}
		return null;
	}
	
	/**
	 * @param o
	 * @throws IllegalArgumentException If the inserted object is null.
	 */
	private void checkNull(Object o) {
		if (o == null) throw new IllegalArgumentException("Argument can't be null.");
	}
	
	/**
	 * Starts a new Thread that receives Client Sockets and registers the Connections.
	 */
	private void listen() {
		Thread listener = new Thread() {
			public void run() {
				ServerSocket serverSocket;
				Socket receivedSocket;
				try {
					serverSocket = new ServerSocket(Settings.serverPort);
					while (true) {
						try {
							receivedSocket = serverSocket.accept();
							ServerController.getInstance().connected(receivedSocket.toString());
							unidentifiedConnections.add(new Connection(receivedSocket));
						} catch (IOException e) {
							ServerController.getInstance().exception(e);
						}
					}
				} catch (IOException e1) {
					ServerController.getInstance().exception(e1);
				}
			}
		};
		listener.start();
	}
}
