/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */

package hw5;

import java.io.File;

public class DB {

	private File db;
	private String name;

	/**
	 * Creates a database object with the given name.
	 * The name of the database will be used to locate
	 * where the collections for that database are stored.
	 * For example if my database is called "library",
	 * I would expect all collections for that database to
	 * be in a directory called "library".
	 * 
	 * If the given database does not exist, it should be
	 * created.
	 */
	public DB(String name) {
		name = name.trim();
		this.name = name;
		File database = new File("testfiles/" + name);
		if(!database.exists()) {
			// Create a new one
			if (!isValidate(name)) {
				// Validate input(null, empty, contains whitespace)
				throw new SecurityException("Database name not supported");
			}
			database.mkdir();
		}
		db = database;
	}

	private boolean isValidate(String name) {
		for(char c: name.toCharArray()) {
			if(c == ' ' || c == '\'' || c == '\"')
				return false;
		}
		return true;
	}
	
	/**
	 * Retrieves the collection with the given name
	 * from this database. The collection should be in
	 * a single file in the directory for this database.
	 * 
	 * Note that it is not necessary to read any data from
	 * disk at this time. Those methods are in DBCollection.
	 */
	public DBCollection getCollection(String name) {
		return new DBCollection(this, name);
	}
	
	/**
	 * Drops this database and all collections that it contains
	 */
	public void dropDatabase() {
		if(db == null || !db.exists())
			throw new SecurityException("Database not existed");

		for(File file: db.listFiles()) {
			file.delete();
		}
		db.delete();
	}
	
	public String getName() {
		return name;
	}
}
