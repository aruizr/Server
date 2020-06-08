package domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String password;
	
	private Map<String, String> chatMap;
	
	/**
	 * <p>Creates a new user with the specified name and password.</p>
	 * @param name
	 * @param password
	 * @throws IllegalArgumentException if one of the parameters are null.
	 */
	public User(String name, String password) throws IllegalArgumentException {
		if (name == null || password == null) throw new IllegalArgumentException("Parameters can't be null");
		this.name = name;
		this.password = password;
		chatMap = new LinkedHashMap<String, String>();
	}
	
	/**
	 * @param name
	 * @return true if the chat map contains a key with the inserted contact name, false if the user does not exist or the parameter is null.
	 */
	public boolean hasContact(String name) {
		if (name == null) return false;
		return chatMap.containsKey(name);
	}

	/**
	 * <p>Adds the user name introduced as parameter to the User Chat Map only if there was no previous key with the same name in the map.</p>
	 * @param name
	 */
	public void addContact(String name) {
		if (!hasContact(name)) chatMap.put(name, "");
	}
	
	/**
	 * <p>Removes the user and chat mapped to that user from the User Chat Map. Does nothing if there is no key in the map with the introduced name.</p>
	 * @param name
	 */
	public void removeContact(String name) {
		if (hasContact(name)) chatMap.remove(name);
	}
	
	/**
	 * @param name
	 * @return String with the whole conversation mapped to the introduced user, null if there is no key in the Chat Map with the specified user or the argument is null.
	 */
	public String getChat(String name) {
		if (!hasContact(name)) return null;
		return chatMap.get(name);
	}
	
	/**
	 * <p>Adds the text to the specified user in the Chat Map</p>
	 * @param name
	 * @param text
	 */
	public void addToChat(String name, String text) {
		if (hasContact(name)) chatMap.put(name, chatMap.get(name).concat(text));
	}
	
	public Collection<String> getContacts() {
		return chatMap.keySet();
	}
	
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		if (password != null) this.password = password;
	}
	
	/**
	 * Two Users are equal if their names contain the same value so that: <br> {@code this.getName().equals(other.getName())}
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User)) return false;
		User other = (User) o;
		return this.getName().equals(other.getName());
	}
	
	@Override
	public String toString() {
		return "Username: "+name+" Password: "+password+" Contacts: "+contactsToString();
	}
	
	private String contactsToString() {
		String s = "(";
		for (String name : getContacts()) {
			s += name+" ,";
		}
		s += ")";
		return s;
	}
}
