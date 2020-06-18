/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */

package hw5;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonObject;

public class DBCollection {

	private DB db;
	private String name;
	private File file;
	private List<JsonObject> documents;

	/**
	 * Constructs a collection for the given database
	 * with the given name. If that collection doesn't exist
	 * it will be created.
	 */
	public DBCollection(DB database, String name) {
		this.db = database;
		name = name.trim();
		this.name = name;
		this.documents = new ArrayList<>();
		String jsonPath = "testfiles/" + db.getName() + "/" + name + ".json";
		File json = new File(jsonPath);
		this.file = json;
		if(!json.exists()) {
			// Create a new one
			try {
				json.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			// read the file, store the documents into list
			readFile(json);
		}
	}

	private void readFile(File json) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(json));
			StringBuilder sb = new StringBuilder();
			for(String line = br.readLine(); line != null; line = br.readLine()) {
				if(line.equals("\t")) {
					// one complete document is read
					documents.add(Document.parse(sb.toString()));
					// Clear the StringBuilder
					sb.setLength(0);
				}
				else {
					sb.append(line);
				}
			}
			br.close();
			// The last document
			if(sb.length() > 0) {
				documents.add(Document.parse(sb.toString()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a cursor for all of the documents in
	 * this collection.
	 */
	public DBCursor find() {
		return new DBCursor(this, null, null);
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @return
	 */
	public DBCursor find(JsonObject query) {
		return new DBCursor(this, query, null);
	}
	
	/**
	 * Finds documents that match the given query parameters.
	 * 
	 * @param query relational select
	 * @param projection relational project
	 * @return
	 */
	public DBCursor find(JsonObject query, JsonObject projection) {
		return new DBCursor(this, query, projection);
	}
	
	/**
	 * Inserts documents into the collection
	 * Must create and set a proper id before insertion
	 * When this method is completed, the documents
	 * should be permanently stored on disk.
	 * @param documents
	 */
	public void insert(JsonObject... documents) {
		if(documents == null) return;
		for(JsonObject json: documents) {
			// Create a unique id using UUID
			UUID uuid = UUID.randomUUID();
			String id = uuid.toString();
			json.addProperty("id", id);
			this.documents.add(json);
			writeDocument(json);
		}
	}

	private void writeDocument(JsonObject json) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(this.file, true));
			bw.write(Document.toJsonString(json) + "\n\t\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void clearFile() {
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write("");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Locates one or more documents and replaces them
	 * with the update document.
	 * @param query relational select for documents to be updated
	 * @param update the document to be used for the update
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void update(JsonObject query, JsonObject update, boolean multi) {
		DBCursor dbc = find(query);
		while(dbc.hasNext()) {
			JsonObject json = dbc.next();
			for(String key: update.keySet()) {
				if(json.get(key) != null) {
					json.remove(key);
					json.add(key, update.get(key));
				}
			}
			if(!multi) break;
		}
		clearFile();
		for(JsonObject json: documents) {
			writeDocument(json);
		}
	}
	
	/**
	 * Removes one or more documents that match the given
	 * query parameters
	 * @param query relational select for documents to be removed
	 * @param multi true if all matching documents should be updated
	 * 				false if only the first matching document should be updated
	 */
	public void remove(JsonObject query, boolean multi) {
		DBCursor dbc = find(query);
		while(dbc.hasNext()) {
			JsonObject toRemove = dbc.next();
			documents.remove(toRemove);
			if(!multi) break;
		}
		clearFile();
		for(JsonObject json: documents) {
			writeDocument(json);
		}
	}
	
	/**
	 * Returns the number of documents in this collection
	 */
	public long count() {
		return documents.size();
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the ith document in the collection.
	 * Documents are separated by a line that contains only a single tab (\t)
	 * Use the parse function from the document class to create the document object
	 */
	public JsonObject getDocument(int i) {
		if(i < 0 || i >= documents.size())
			throw new IndexOutOfBoundsException("Invalid index");
		return documents.get(i);
	}
	
	/**
	 * Drops this collection, removing all of the documents it contains from the DB
	 */
	public void drop() {
		this.name = null;
		this.file.delete();
		this.documents.clear();
	}

	public List<JsonObject> getDocuments() {
		return this.documents;
	}
}
