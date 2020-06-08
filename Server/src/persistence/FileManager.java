package persistence;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import domain.UserData;

public class FileManager {
	
	public static final String DEFAULT_DATA_FILE = "user_data.txt";
	
	private File file;

	/**
	 * Creates an instance of {@code FileManager}. This instance automatically tries to open the default file {@value FileManager#DEFAULT_DATA_FILE}, if it doesn't find any, it creates a new one.
	 * @throws IOException If there is a problem opening the file.
	 */
	public FileManager() throws IOException {
		file = new File(DEFAULT_DATA_FILE);
		file.createNewFile();
	}

	/**
	 * @return {@code UserData} object read from the default file, if the file is empty returns a new empty instance of {@code UserData}.
	 * @throws IOException If there is a problem reading the file.
	 * @throws ClassNotFoundException If the read object is not an instance of {@code UserData}.
	 */
	public UserData load() throws IOException, ClassNotFoundException {
		UserData userData;
		try {
			ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(file));
			userData = (UserData) objectReader.readObject();
			objectReader.close();
		} catch (EOFException e) {
			userData = new UserData();
		}
		return userData;
	}
	
	/**
	 * Writtes the {@code UserData} object in the default file.
	 * @param userData
	 * @throws IOException If there is a problem writting the object.
	 */
	public void save(UserData userData) throws IOException {
		FileOutputStream fileWriter = new FileOutputStream(file);
		ObjectOutputStream objectWriter = new ObjectOutputStream(fileWriter);
		objectWriter.writeObject(userData);
		objectWriter.close();
		fileWriter.close();
	}
}
