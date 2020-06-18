/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */

package test;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.Document;

class DocumentTester {
	
	/*
	 * Things to consider testing:
	 * 
	 * Invalid JSON
	 * 
	 * Properly parses embedded documents
	 * Properly parses arrays
	 * Properly parses primitives (done!)
	 * 
	 * Object to embedded document
	 * Object to array
	 * Object to primitive
	 */

	@Test
	public void testInvalidJson() {
		String json = "{\"key\": \"value}";
		try {
			JsonObject res = Document.parse(json);
			fail("Should throw a JsonSyntaxException");
		} catch (JsonSyntaxException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testParsePrimitive() {
		String json = "{ \"key\":\"value\" }";//setup
		JsonObject results = Document.parse(json); //call method to be tested
		assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value")); //verify results
	}

	@Test
	public void testParseArrays() {
		String json = "{\"key\":[\"apple\", \"banana\", \"pear\"], \"key1\": \"fruit\"}";
		JsonObject res = Document.parse(json);
		JsonObject compare = new JsonObject();
		JsonArray array = new JsonArray();
		array.add("apple");
		array.add("banana");
		array.add("pear");
		compare.add("key", array);
		compare.addProperty("key1", "fruit");

		// Test parse
		assertEquals(res, compare);
		assertEquals(res.getAsJsonArray("key").get(0).getAsString(), "apple");
		assertEquals(res.getAsJsonArray("key").get(1).getAsString(), "banana");
		assertEquals(res.getAsJsonArray("key").get(2).getAsString(), "pear");

		// Test toString
		assertEquals(Document.toJsonString(res), Document.toJsonString(compare));
	}

	@Test
	public void testParseEmbedded() {
		String json = "{\"key\":\"value\",\"city\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		JsonObject res = Document.parse(json);

		// Test parse
		assertEquals(res.getAsJsonObject("city").getAsJsonPrimitive("city1").getAsString(), "Nanjing");
		assertEquals(res.getAsJsonObject("city").getAsJsonPrimitive("city2").getAsString(), "St. Louis");

		// Test toString
		assertEquals(json, Document.toJsonString(res));
	}
}
