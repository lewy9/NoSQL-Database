/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;

class CursorTester {
	
	/**
	 * Things to consider testing:
	 * 
	 * hasNext (done?)
	 * count (done?)
	 * next (done?)
	 */

	@Test
	public void testFindAll() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		DBCursor results = test.find();
		
		assertTrue(results.count() == 3);
		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		//verify contents?
		assertEquals("value", d1.getAsJsonPrimitive("key").getAsString());
		assertTrue(results.hasNext());//still more documents
		JsonObject d2 = results.next(); //pull second document
		//verfiy contents?
		JsonObject comp1 = new JsonObject();
		comp1.addProperty("key2", "value2");
		assertEquals(comp1, d2.getAsJsonObject("embedded"));
		assertTrue(results.hasNext()); //still one more document
		JsonObject d3 = results.next();//pull last document
		JsonArray comp2 = new JsonArray();
		comp2.add("one");
		comp2.add("two");
		comp2.add("three");
		assertEquals(comp2, d3.getAsJsonArray("array"));
		assertFalse(results.hasNext());//no more documents
	}
}
