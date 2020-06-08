package domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class UserData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Collection<User> registeredUsers;

	public UserData() {
		registeredUsers = new HashSet<User>();
	}
	
	/**
	 * @param user
	 * @return true if the user has been registered, false if the user already existed or the parameter is null.
	 */
	public boolean registerUser(User user) {
		if (contains(user)) return false;
		return registeredUsers.add(user);
	}
	
	/**
	 * Removes the specified user from the registered user list and from the contact list of all the users who have it.
	 * @param name
	 * @return true if the user exists and could be removed, false if the user does not exist or the parameter is null.
	 */
	public boolean removeUser(String name) {
		for (User user : registeredUsers) {
			if (user.hasContact(name)) user.removeContact(name);
		}
		return remove(name);
	}
	
	/**
	 * Removes the specified user from the registered user list and from the contact list of all the users who have it.
	 * @param name
	 * @return true if the user exists and could be removed, false if the user does not exist or the parameter is null.
	 */
	public boolean removeUser(User user) {
		for (User u : registeredUsers) {
			if (u.hasContact(u.getName())) u.removeContact(u.getName());
		}
		return remove(user);
	}
	
	/**
	 * <p>Replaces the user with the same name of the User List with the introduced user.</p>
	 * @param user
	 * @return true if the user has been updated, false if the user does not exist or the parameter is null.
	 */
	public boolean updateUser(User user) {
		if (!contains(user)) return false;
		remove(user);
		return registeredUsers.add(user);
	}
	
	/**
	 * @param name
	 * @return User with the specified name, null if the user does not exist or the parameer is null.
	 */
	public User getUser(String name) {
		return obtain(name);
	}
	
	/**
	 * @return Collection of Users with all the registered users.
	 */
	public Collection<User> getAllUsers() {
		return registeredUsers;
	}
	
	/**
	 * @return Collection of Strings with the names of all the registered users.
	 */
	public Collection<String> getAllUserNames() {
		Collection<String> names = new HashSet<String>();
		for (User user : registeredUsers) {
			names.add(user.getName());
		}
		return names;
	}

	/**
	 * @param name
	 * @return true if the user is registered, false if the user does not exist or the parameter is null.
	 */
	public boolean isRegistered(String name) {
		return contains(name);
	}
	
	/**
	 * @param name
	 * @return true if the user is registered, false if the user does not exist or the parameter is null.
	 */
	public boolean isRegistered(User user) {
		return contains(user);
	}
	
	/**
	 * @param name
	 * @param password
	 * @return true if the user with the introduced name contains the specified password, false if the password is wrong or the user is not registered or one of the parameters are null.
	 */
	public boolean isPasswordValid(String name, String password) {
		if (password == null || name == null) return false;
		if (!contains(name)) return false;
		return obtain(name).getPassword().equals(password);
	}
	
	private User obtain(User user) {
		if (user == null) return null;
		for (User u : registeredUsers) {
			if (u.equals(user)) return u;
		}
		return null;
	}
	
	private User obtain(String name) {
		if (name == null) return null;
		for (User u : registeredUsers) {
			if (u.getName().equals(name)) return u;
		}
		return null;
	}
	
	private boolean contains(User user) {
		return obtain(user) != null;
	}
	
	private boolean contains(String name) {
		return obtain(name) != null;
	}
	
	private boolean remove(User user) {
		User u = obtain(user);
		if (u == null) return false;
		return registeredUsers.remove(u);
	}
	
	private boolean remove(String name) {
		User u = obtain(name);
		if (u == null) return false;
		return registeredUsers.remove(u);
	}
}
