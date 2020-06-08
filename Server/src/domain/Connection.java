package domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import application.ServerController;

public class Connection {
	
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String address;

	/**
	 * Creates a new Connection with the inserted {@code Socket} and starts a new {@code Thread} to listen to the {@code Socket}
	 * @param socket
	 * @throws IOException If there is a problem creating the input and output Streams of the {@code Socket}.
	 * @throws IllegalArgumentException If the {@code Socket} inserted is null.
	 */
	public Connection(Socket socket) throws IOException {
		if (socket == null) throw new IllegalArgumentException("Can't create connection with empty socket.");
		this.socket = socket;
		setConnectionAddress(socket);
		//address = socket.getInetAddress().getHostAddress();
		System.out.println(address);
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		listen();
	}
	
	/**
	 * This method is used to avoid getting local IP adresses if the Client is being executed in the same machine than the Server.
	 * @throws IOException If there was a problem obtaining the address
	 */
	private void setConnectionAddress (Socket socket) throws IOException {
		if (Settings.localTest) address = socket.getInetAddress().getHostAddress();	
		else if (Settings.clientRunningInSameMachineAsServer) address = (socket.getInetAddress().getHostAddress().contains("192.168") ? InetUtils.getPublicAddress() : socket.getInetAddress().getHostAddress());
		else address = socket.getInetAddress().getHostAddress();
	}

	/**
	 * <p>Sends the inserted {@code Message} to the {@code OutputStream} attached to the {@code Socket} of this {@code Connection}. 
	 * If the {@code Message} has no destination address, this method ads the remote address connected to the {@code Socket}</p>
	 * @param message
	 * @throws IOException If a problem occurs while sending the message.
	 * @throws IllegalArgumentException If the argument is null.
	 */
	public void send(Message message) throws IOException {
		if (message == null) throw new IllegalArgumentException("Argument can't be null.");
		if (message.getDestinationAddress() == null) message.setDestinationAddress(address);
		out.writeObject(message);
		ServerController.getInstance().log("Message sent: \n"+message.toString());
		out.flush();
	}
	
	/**
	 * @return {@code String} with the remote IP address attached to the {@code Socket} of this {@code Connection}.
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * @return true if the {@code Socket} of this {@code Connection} is open, false if it has been closed.
	 */
	public boolean isActive() {
		return !socket.isClosed();
	}
	
	/**
	 *<p>Two connections are equal if they have the same remote address.</p>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Connection) {
			Connection connection = (Connection) obj;
			return address.equals(connection.getAddress());
		}
		return false;
	}
	
	/**
	 * <p>Creates and executes a new thread that receives messages from the attached socket and sends replies.</p>
	 */
	private void listen() {
		Thread listener = new Thread() {
			public void run() {
				Message reply;
				while (isActive()) {
					try {
						reply = ServerController.getInstance().messageReceived((Message) in.readObject());
						if (reply != null) {
							send(reply);
						}
					} catch (ClassNotFoundException e) {
						//Object received not Message TODO
					} catch (IOException e) {
						close();
					}
				}
			}
			
			private void close() {
				try {
					socket.close();
					ServerController.getInstance().disconnected(socket.toString());
				} catch (IOException e) {
					ServerController.getInstance().error(e.getMessage());
				}
			}
		};
		listener.start();
	}
}
