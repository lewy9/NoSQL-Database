/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */

package hw5;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DBCursor implements Iterator<JsonObject>{

	private Queue<JsonObject> queue;

	public DBCursor(DBCollection collection, JsonObject query, JsonObject fields) {
		queue = new LinkedList<>();
		List<JsonObject> documents = collection.getDocuments();
		if(query == null && fields == null) {
			// All the documents
			queue.addAll(documents);
		}
		else if(query != null) {
			List<JsonObject> res = query(documents, query);
			if(fields == null) {
				queue.addAll(res);
			}
			else {
				JsonPrimitive type = fields.getAsJsonPrimitive(fields.keySet().iterator().next());
				int flag = 0;
				if(type.getAsString().equals("1"))
					flag = 1;

				for(JsonObject json: res) {
					JsonObject modified = new JsonObject();
					if(flag == 1) {
						for(String key: fields.keySet()) {
							modified.add(key, json.get(key));
						}
						if(modified.get("id") != null)
							modified.add("id", json.get("id"));
					}
					else {
						modified = json.deepCopy();
						for(String key: fields.keySet()) {
							modified.remove(key);
						}
					}
					queue.offer(modified);
				}
			}
		}
	}

	private List<JsonObject> query(List<JsonObject> documents, JsonObject query) {
		List<JsonObject> res = new ArrayList<>();
		for(JsonObject json: documents) {
			String key = query.keySet().iterator().next();
			if(key.contains(".")) {
				// Querying Nested Field
				String[] keys = key.split("\\.");
				String key1 = keys[0];
				String key2 = keys[1];
				JsonElement e1 = json.get(key1);
				if(e1 == null || !e1.isJsonObject()) continue;
				JsonElement e2 = e1.getAsJsonObject().get(key2);
				if(e2 == null) continue;
				if(e2.equals(query.get(key))) {
					res.add(json);
				}
				if(query.get(key).isJsonObject()) {
					// Relational Operator
					if(operate(e2, query.getAsJsonObject(key))) {
						res.add(json);
					}
				}
			}
			else {
				JsonElement e1 = json.get(key);
				JsonElement e2 = query.get(key);
				if(e1 == null) {
					if(e2 != null && e2.isJsonObject()) {
						String operator = e2.getAsJsonObject().keySet().iterator().next();
						if(operator.equals("$ne") || operator.equals("$nin"))
							res.add(json);
					}
					continue;
				}
				if(e1.equals(e2))
					res.add(json);
				else {
					if(e2 != null && e1.isJsonArray() && e2.isJsonPrimitive()) {
						// Query an Array for an Element
						JsonArray ja = e1.getAsJsonArray();
						if(ja.contains(e2))
							res.add(json);
					}
					else if(e2 != null && e2.isJsonObject()) {
						// Relational Operator
						if(operate(e1, e2.getAsJsonObject())) {
							res.add(json);
						}
					}
				}
			}
		}
		return res;
	}

	private boolean operate(JsonElement e1, JsonObject e2) {
		String key = e2.keySet().iterator().next();
		boolean res = false;
		if(e1.isJsonPrimitive()) {
			int comp = 0;
			if(e2.get(key).isJsonPrimitive()) {
				comp = e1.getAsString().compareTo(e2.getAsJsonPrimitive(key).getAsString());
			}
			JsonPrimitive jp = e1.getAsJsonPrimitive();
			switch (key) {
				case "$eq":
					res = e1.equals(e2.get(key));
					break;
				case "$gt":
					res = comp > 0;
					break;
				case "$gte":
					res = comp >= 0;
					break;
				case "$in":
					JsonArray ja1 = e2.getAsJsonArray(key);
					res = ja1.contains(jp);
					break;
				case "$lt":
					res = comp < 0;
					break;
				case "$lte":
					res = comp <= 0;
					break;
				case "$ne":
					res = comp != 0;
					break;
				case "$nin":
					JsonArray ja2 = e2.getAsJsonArray(key);
					res = !ja2.contains(jp);
					break;
			}
		}
		else if(e1.isJsonArray()) {
			JsonArray ja = e1.getAsJsonArray();
			switch (key) {
				case "$eq":
					for(JsonElement je: ja) {
						if(je.equals(e2.get(key))) {
							res = true;
							break;
						}
					}
					break;
				case "$gt":
					for(JsonElement je: ja) {
						int compare = je.getAsString().compareTo(e2.getAsJsonPrimitive(key).getAsString());
						if(compare > 0) {
							res = true;
							break;
						}
					}
					break;
				case "$gte":
					for(JsonElement je: ja) {
						int compare = je.getAsString().compareTo(e2.getAsJsonPrimitive(key).getAsString());
						if(compare >= 0) {
							res = true;
							break;
						}
					}
					break;
				case "$in":
					JsonArray ja1 = e2.getAsJsonArray(key);
					for(JsonElement je: ja) {
						if(ja1.contains(je)) {
							res = true;
							break;
						}
					}
					break;
				case "$lt":
					for(JsonElement je: ja) {
						int compare = je.getAsString().compareTo(e2.getAsJsonPrimitive(key).getAsString());
						if(compare < 0) {
							res = true;
							break;
						}
					}
					break;
				case "$lte":
					for(JsonElement je: ja) {
						int compare = je.getAsString().compareTo(e2.getAsJsonPrimitive(key).getAsString());
						if(compare <= 0) {
							res = true;
							break;
						}
					}
					break;
				case "$ne":
					for(JsonElement je: ja) {
						if(!je.equals(e2.get(key))) {
							res = true;
							break;
						}
					}
					break;
				case "$nin":
					JsonArray ja2 = e2.getAsJsonArray(key);
					res = true;
					for(JsonElement je: ja) {
						if(ja2.contains(je)) {
							res = false;
							break;
						}
					}
					break;
			}
		}
		return res;
	}

	/**
	 * Returns true if there are more documents to be seen
	 */
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	/**
	 * Returns the next document
	 */
	public JsonObject next() {
		return queue.poll();
	}

	/**
	 * Returns the total number of documents
	 */
	public long count() {
		return queue.size();
	}

}
