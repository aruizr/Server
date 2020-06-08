/**
 * 
 */
package application;

import java.io.IOException;

import display.ServerWindow;
import domain.Message;
import domain.MessageType;
import domain.Settings;
import domain.User;
import domain.UserData;
import persistence.FileManager;

public class ServerController {
	
	private static ServerController instance;
	
	private Console console;
	private ServerCommunicator serverCommunicator;
	private UserData userData;
	private FileManager fileManager;
	private ServerWindow serverWindow;
	
	public static void main(String[] args) {
		getInstance();
	}
	
	public static synchronized ServerController getInstance() {
		if (instance == null) instance = new ServerController();
		return instance;
	}
	
	//Initalization methods------------------------------------------------------------------------------------------------------

	/**
	 * Creates instance of Controller, creating the Application Window and Console.
	 */
	private ServerController() {
		serverWindow = new ServerWindow();
		console = new Console();
		try {
			Settings.loadProperties();
		} catch (IOException e) {
			exception(e);
		}
	}
	
	//Message handling methods------------------------------------------------------------------------------------------------------
	
	public Message messageReceived(Message message) {
		log("Message received: \n"+message.toString());
		switch (message.getMessageType()) {
		case LOGIN_REQUEST:
			return loginRequest(message);
			
		case LOGIN_CONFIRMATION:
			return loginConfirmation(message);
			
		case ADDING_REQUEST:
			return addingRequest(message);
			
		case ADDING_REPLY:
			return forwardMessage(message);
			
		case ADDING_REMOVE:
			return forwardMessage(message);
			
		case USER_MESSAGE:
			return userMessage(message);
			
		case USER_UPDATE:
			return userUpdate(message);
			
		case USER_LOGOUT:
			return userLogout(message);
			
		case USER_REMOVE:
			return userRemove(message);

		default:
			return null;
		}
	}

	private Message loginRequest(Message message) {
		String 	name = message.getSourceName(), 
				password = message.getContent();
		Message reply = replyToMessage(message, MessageType.LOGIN_REPLY);
		if (userData.isRegistered(name)) {
			if (userData.isPasswordValid(name, password)) {
				reply.setCondition("true");
				reply.setContent("Logged in successfully as "+name);
				reply.setAdditionalData(userData.getUser(name));
			}
			else {
				reply.setCondition("false");
				reply.setContent("Incorrect password.");
			}
		}
		else {
			userData.registerUser(new User(name, password));
			updateRegisteredUsers();
			reply.setCondition("true");
			reply.setContent("User "+name+" registered successfully.");
			reply.setAdditionalData(userData.getUser(name));
		}
		return reply;
	}

	private Message loginConfirmation(Message message) {
		serverCommunicator.identify(message.getSourceName(), message.getSourceAddress());
		updateOnlineUsers();
		try {
			serverCommunicator.sendQueue(message.getSourceName());
		} catch (IOException e) {
			exception(e);
		}
		return null;
	}
	
	private Message addingRequest(Message message) {
		String dstName = message.getDestinationName();
		Message reply = replyToMessage(message, MessageType.ADDING_FORWARDING);
		if (userData.isRegistered(dstName)) {
			send(message);
			reply.setCondition("true");
			reply.setContent("Adding request sent to "+dstName);
		}
		else {
			reply.setCondition("false");
			reply.setContent("User "+dstName+" does not exist.");
		}
		return reply;
	}
	
	private Message forwardMessage(Message message) {
		send(message);
		return null;
	}

	private Message userMessage(Message message) {
		Message reply = replyToMessage(message, MessageType.USER_MESSAGE_FORWARDING);
		if (userData.isRegistered(message.getDestinationName())) {
			reply.setCondition("true");
			reply.setContent(message.getContent());
			reply.setSourceName(message.getDestinationName());
			send(message);
		}
		else {
			reply.setCondition("false");
			reply.setContent("User "+message.getDestinationName()+" does not exist.");
		}
		return reply;
	}

	private Message userUpdate(Message message) {
		User user = (User) message.getAdditionalData();
		log("User "+user.getName()+" has been updated: "+userData.updateUser(user));
		return null;
	}
	
	private Message userLogout(Message message) {
		serverCommunicator.unidentify(message.getSourceName());
		updateOnlineUsers();
		return null;
	}
	
	private Message userRemove(Message message) {
		userData.removeUser(message.getSourceName());
		updateRegisteredUsers();
		return null;
	}

	//Command methods--------------------------------------------------------------------------------------------------------------
	
	public void commandReceived(String command) {
		console.command(command);
	}
	
	/**
	 * Initializes {@code FileManager}, {@code UserData} and {@code ServerCommunicator} if they haven't been initalized yet.
	 */
	public void start() {
		try {
			if (fileManager == null) {
				fileManager = new FileManager();
			}
			if (userData == null) {
				userData = fileManager.load();
				log("User data loaded: "+(userData != null));
				updateRegisteredUsers();
			}
			if (serverCommunicator == null) {
				serverCommunicator = new ServerCommunicator();
				log("Network communicator initialized: "+(serverCommunicator != null));
				log("Server is now listening for connections...");
			}
		} catch (ClassNotFoundException | IOException e) {
			exception(e);
		}
	}
	
	/**
	 * Stops and removes all the connections with the server.
	 */
	public void stop() {
		serverCommunicator = null;
		log("Network communicator stopped: "+(serverCommunicator == null));
		log("Server is no longer listening for connections.");
	}
	
	/**
	 * Saves user data and shuts the application down.
	 */
	public void exit() {
		if (fileManager == null) System.exit(0);
		try {
			fileManager.save(userData);
			System.exit(0);
		} catch (IOException e) {
			exception(e);
		}
	}
	
	public void update() {
		try {
			serverCommunicator.broadcast(new Message(MessageType.USER_UPDATE_REQUEST));
			log("Update request sent to all identified conenctions.");
		} catch (IOException e) {
			exception(e);
		}
	}
	
	//Public auxiliar methods-------------------------------------------------------------------------------------------------------
	
	public void log(String text) {
		if (serverWindow != null) serverWindow.log(text);
	}
	
	public void exception(Exception e) {
		log("An error ocurred ("+e.toString()+"): "+e.getMessage());
	}
	
	public void error(String text) {
		log("An error ocurred: "+text);
	}
	
	public void connected(String socketInfo) {
		log("Connection received: "+socketInfo);
	}
	
	public void disconnected(String socketInfo) {
		log("Connection lost: "+socketInfo);
		serverCommunicator.purge();
		updateOnlineUsers();
	}

	//Private auxiliar methods------------------------------------------------------------------------------------------------------
	
	private void send(Message message) {
		try {
			serverCommunicator.send(message);
		} catch (IOException e) {
			exception(e);
		}
	}
	
	private void updateRegisteredUsers() {
		
		serverWindow.updateRegisteredUsers(userData.getAllUserNames());
	}
	
	private void updateOnlineUsers() {
		serverWindow.updateOnlineUsers(serverCommunicator.getIdentifications());
	}
	
	private Message replyToMessage(Message message, MessageType messageType) {
		Message reply = new Message(messageType);
		reply.setDestinationName(message.getSourceName());
		reply.setDestinationAddress(message.getSourceAddress());
		return reply;
	}
}
