/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import hw5.Document;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import hw5.DB;
import hw5.DBCollection;
import hw5.DBCursor;

import java.io.File;

class CollectionTester {
	
	/**
	 * Things to consider testing
	 * 
	 * Queries:
	 * 	Find all
	 * 	Find with relational select
	 * 		Conditional operators
	 * 		Embedded documents
	 * 		Arrays
	 * 	Find with relational project
	 * 
	 * Inserts
	 * Updates
	 * Deletes
	 * 
	 * getDocument (done?)
	 * drop
	 */
	
	@Test
	public void testGetDocument() {
		DB db = new DB("data");
		DBCollection test = db.getCollection("test");
		// Three documents
		assertEquals(3, test.count());
		JsonObject primitive = test.getDocument(0);
		assertTrue(primitive.getAsJsonPrimitive("key").getAsString().equals("value"));
		JsonObject embedded = test.getDocument(1);
		JsonObject comp1 = new JsonObject();
		comp1.addProperty("key2", "value2");
		assertEquals(embedded.getAsJsonObject("embedded"), comp1);
		JsonObject array = test.getDocument(2);
		JsonArray comp2 = new JsonArray();
		comp2.add("one");
		comp2.add("two");
		comp2.add("three");
		assertEquals(array.getAsJsonArray("array"), comp2);
	}

	@Test
	//Test Find all
	public void testFind1()
	{
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		DBCursor results = dbc.find();

		assertTrue(results.count() == 6);

		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		assertTrue(d1.get("banana").getAsString().equals("1"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d2 = results.next(); //pull second document
		assertTrue(d2.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d2.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d3 = results.next();//pull last document
		assertTrue(d3.get("fruit").toString().equals("[\"banana\",\"lemon\",\"apple\"]"));
		assertTrue(d3.get("city").toString().equals("[\"beijing\",\"chicago\",\"new york\"]"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d4 = results.next();//pull last document
		assertTrue(d4.get("lemon").getAsString().equals("5"));
		assertTrue(d4.get("beijing").getAsString().equals("large"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d5 = results.next();//pull last document
		assertTrue(d5.get("fruit").toString().equals("{\"orange\":\"8\",\"banana\":\"2\"}"));
		assertTrue(d5.get("city").toString().equals("{\"chicage\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d6 = results.next();//pull last document
		assertTrue(d6.get("fruit").toString().equals("[\"peach\",\"apple\",\"lemon\"]"));
		assertTrue(d6.get("city").toString().equals("[\"dallas\",\"chicago\",\"new york\"]"));

		assertFalse(results.hasNext());//no more documents
		//assertTrue(results.getAsJsonPrimitive("key").getAsString().equals("value"));

	}

	@Test
	//Test that finds documents that match Strings without comparator.
	public void testFind2()
	{
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"beijing\":\"large\"}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 2);

		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		assertTrue(d1.get("banana").getAsString().equals("1"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("lemon").getAsString().equals("5"));
		assertTrue(d2.get("beijing").getAsString().equals("large"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match Strings with comparator equal.
	public void testFind3() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"beijing\":{ \"$eq\":\"large\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 2);

		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		assertTrue(d1.get("banana").getAsString().equals("1"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("lemon").getAsString().equals("5"));
		assertTrue(d2.get("beijing").getAsString().equals("large"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match Strings with comparator gt.
	public void testFind4() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"lemon\":{ \"$gt\":\"4\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		assertTrue(results.hasNext()); //still one more document
		JsonObject d1 = results.next();//pull last document
		assertTrue(d1.get("lemon").getAsString().equals("5"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match an Embedded/Nested Document with comparator gte.
	public void testFind5() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"fruit.orange\":{ \"$gte\":\"7\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		assertTrue(results.hasNext()); //still one more document
		JsonObject d1 = results.next();//pull last document
		assertTrue(d1.get("fruit").toString().equals("{\"orange\":\"8\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"chicage\":\"large\",\"st.Louis\":\"small\"}"));

		assertFalse(results.hasNext());//no more documents
	}


	@Test
	//Test that finds documents that match an Embedded/Nested Document with comparator in.
	public void testFind6() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"fruit.apple\":{ \"$in\":[\"1\",\"20\"]}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertFalse(results.hasNext());//no more documents
	}


	@Test
	//Test that finds documents that match String with comparator lt.
	public void testFind7() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"banana\":{ \"$lt\":\"3\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		assertTrue(d1.get("banana").getAsString().equals("1"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match String with comparator lte.
	public void testFind8() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"beijing\":{ \"$lte\":\"large\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 2);

		//verify contents
		assertTrue(results.hasNext());
		JsonObject d1 = results.next(); //pull first document
		assertTrue(d1.get("banana").getAsString().equals("1"));
		assertTrue(d1.get("beijing").getAsString().equals("large"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("lemon").getAsString().equals("5"));
		assertTrue(d2.get("beijing").getAsString().equals("large"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match Embedded documents and array with comparator ne.
	public void testFind9() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"beijing\":{ \"$ne\":\"large\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 4);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("fruit").toString().equals("[\"banana\",\"lemon\",\"apple\"]"));
		assertTrue(d2.get("city").toString().equals("[\"beijing\",\"chicago\",\"new york\"]"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d3 = results.next();//pull last document
		assertTrue(d3.get("fruit").toString().equals("{\"orange\":\"8\",\"banana\":\"2\"}"));
		assertTrue(d3.get("city").toString().equals("{\"chicage\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d4 = results.next();//pull last document
		assertTrue(d4.get("fruit").toString().equals("[\"peach\",\"apple\",\"lemon\"]"));
		assertTrue(d4.get("city").toString().equals("[\"dallas\",\"chicago\",\"new york\"]"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match Embedded documents and array with comparator nin.
	public void testFind10() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"banana\":{ \"$nin\":[\"1\",\"20\"]}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 5);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("fruit").toString().equals("[\"banana\",\"lemon\",\"apple\"]"));
		assertTrue(d2.get("city").toString().equals("[\"beijing\",\"chicago\",\"new york\"]"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d3 = results.next();//pull last document
		assertTrue(d3.get("lemon").getAsString().equals("5"));
		assertTrue(d3.get("beijing").getAsString().equals("large"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d4 = results.next();//pull last document
		assertTrue(d4.get("fruit").toString().equals("{\"orange\":\"8\",\"banana\":\"2\"}"));
		assertTrue(d4.get("city").toString().equals("{\"chicage\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d5 = results.next();//pull last document
		assertTrue(d5.get("fruit").toString().equals("[\"peach\",\"apple\",\"lemon\"]"));
		assertTrue(d5.get("city").toString().equals("[\"dallas\",\"chicago\",\"new york\"]"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match first-level documents in Embedded documents without comparator.
	public void testFind11() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"city\":{\"beijing\":\"large\",\"st.Louis\":\"small\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match second-level documents in Embedded documents without comparator.
	public void testFind12() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"city.beijing\":\"large\"}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match second-level documents in Embedded documents without comparator.
	public void testFind13() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json = "{\"fruit.banana\":{ \"$gt\":\"1\"}}";//setup
		JsonObject jo = Document.parse(json); //call method to be tested
		DBCursor results = dbc.find(jo);

		assertTrue(results.count() == 2);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertTrue(d1.get("fruit").toString().equals("{\"apple\":\"1\",\"banana\":\"2\"}"));
		assertTrue(d1.get("city").toString().equals("{\"beijing\":\"large\",\"st.Louis\":\"small\"}"));

		assertTrue(results.hasNext()); //still one more document
		JsonObject d2 = results.next();//pull last document
		assertTrue(d2.get("fruit").toString().equals("{\"orange\":\"8\",\"banana\":\"2\"}"));
		assertTrue(d2.get("city").toString().equals("{\"chicage\":\"large\",\"st.Louis\":\"small\"}"));

		assertFalse(results.hasNext());//no more documents
	}

	@Test
	//Test that finds documents that match array
	public void testFind14() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");

		// Match an Array
		String query1 = "{\"fruit\":[\"peach\", \"apple\", \"lemon\"]}";
		JsonObject q1 = Document.parse(query1);
		DBCursor cursor = dbc.find(q1);
		assertEquals(cursor.count(), 1);
		// Should match the last document
		assertEquals(cursor.next(), dbc.getDocument(5));

		// Query an Array for an Element
		String query2 = "{\"fruit\":\"apple\"}";
		JsonObject q2 = Document.parse(query2);
		DBCursor cursor2 = dbc.find(q2);
		assertEquals(cursor2.count(), 2);
		// Should match the 2 documents, the 2th & 5th
		assertEquals(cursor2.next(), dbc.getDocument(2));
		assertEquals(cursor2.next(), dbc.getDocument(5));
		assertFalse(cursor2.hasNext());//no more documents

		// Query an Array for an Element with comparison operators
		String query3 = "{\"city\":{\"$in\":[\"beijing\",\"dallas\"]}}";
		JsonObject q3 = Document.parse(query3);
		DBCursor cursor3 = dbc.find(q3);
		assertEquals(cursor3.count(), 2);
		// should match 2th & 5th documents
		assertEquals(cursor3.next(), dbc.getDocument(2));
		assertEquals(cursor3.next(), dbc.getDocument(5));
		assertFalse(cursor3.hasNext());//no more documents
	}

	@Test
	// Test that finds documents that match String in Embedded documents with projection 1
	public void testFind15() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json1 = "{\"banana\":\"1\"}";//setup
		JsonObject jo1 = Document.parse(json1); //call method to be tested
		String json2 = "{\"beijing\":\"1\"}";//setup
		JsonObject jo2 = Document.parse(json2); //call method to be tested
		DBCursor results = dbc.find(jo1,jo2);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertNull(d1.get("banana"));
		assertEquals("large", d1.get("beijing").getAsString());
		assertFalse(results.hasNext());//no more documents
	}

	@Test
	// Test that finds documents that match String in Embedded documents with projection 0
	public void testFind16() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");
		String json1 = "{\"lemon\":\"5\"}";//setup
		JsonObject jo1 = Document.parse(json1); //call method to be tested
		String json2 = "{\"beijing\":\"0\"}";//setup
		JsonObject jo2 = Document.parse(json2); //call method to be tested
		DBCursor results = dbc.find(jo1,jo2);

		assertTrue(results.count() == 1);

		//verify contents
		assertTrue(results.hasNext());//still more documents
		JsonObject d1 = results.next(); //pull second document
		assertNull(d1.get("beijing"));
		assertEquals("5", d1.get("lemon").getAsString());
		assertFalse(results.hasNext());//no more documents
	}

	@Test
	public void testEmbeddedProject() {
		DB db = new DB("testFind");
		DBCollection dbc = db.getCollection("testFind");

		// Query embedded document
		String query1 = "{\"fruit.apple\":\"1\"}";
		JsonObject q1 = Document.parse(query1);
		String project1 = "{\"city\":\"1\"}";
		JsonObject p1 = Document.parse(project1);
		DBCursor cursor = dbc.find(q1, p1);
		assertEquals(cursor.count(), 1);
		assertEquals(cursor.next().toString(), "{\"city\":{\"beijing\":\"large\",\"st.Louis\":\"small\"}}");
	}

	@Test
	public void testInsert() {
		DB db = new DB("test");
		File file = new File("testfiles/test/test1.json");
		// Reset(Delete the json file if this test was previously executed)
		if(file.exists()) file.delete();
		DBCollection test1 = db.getCollection("test1");

		// Insert primitive type
		String json1 = "{ \"key1\":\"value1\", \"key2\":\"value2\"}";
		JsonObject j1 = Document.parse(json1);
		test1.insert(j1);
		String json2 = "{ \"key3\":\"value3\", \"key4\":\"value4\"}";
		JsonObject j2 = Document.parse(json2);
		test1.insert(j2);
		// Two documents inserted
		assertEquals(test1.count(), 2);
		// Examine the content & getDocument()
		assertEquals(test1.getDocument(0), j1);
		assertEquals(test1.getDocument(1), j2);

		// Insert Embedded document
		String embedded1 = "{\"key5\":\"value5\",\"city1\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		String embedded2 = "{\"key6\":\"value6\",\"city2\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		JsonObject e1 = Document.parse(embedded1);
		JsonObject e2 = Document.parse(embedded2);
		test1.insert(e1);
		test1.insert(e2);
		// Another Two documents inserted
		assertEquals(test1.count(), 4);
		// Examine the content & getDocument()
		assertEquals(test1.getDocument(2), e1);
		assertEquals(test1.getDocument(3), e2);

		// Insert Arrays
		String array1 = "{\"key7\":[\"apple\", \"banana\", \"pear\"], \"key8\": \"fruit\"}";
		String array2 = "{\"key9\":[\"apple\", \"banana\", \"pear\"], \"key10\": \"fruit\"}";
		JsonObject a1 = Document.parse(array1);
		JsonObject a2 = Document.parse(array2);
		test1.insert(a1);
		test1.insert(a2);
		// Another Two documents inserted
		assertEquals(test1.count(), 6);
		// Examine the content & getDocument()
		assertEquals(test1.getDocument(4), a1);
		assertEquals(test1.getDocument(5), a2);
	}

	@Test
	public void testUpdate() {
		// Pre setup
		DB db = new DB("test");
		File file = new File("testfiles/test/test1.json");
		// Reset(Delete the json file if this test was previously executed)
		if(file.exists()) file.delete();
		DBCollection test1 = db.getCollection("test1");

		// Insert primitive type
		String json1 = "{ \"key1\":\"value1\", \"key2\":\"value2\"}";
		JsonObject j1 = Document.parse(json1);
		test1.insert(j1);
		String json2 = "{ \"key1\":\"value3\", \"key2\":\"value2\"}";
		JsonObject j2 = Document.parse(json2);
		test1.insert(j2);

		// Insert Embedded document
		String embedded1 = "{\"key5\":\"value5\",\"city1\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		String embedded2 = "{\"key6\":\"value6\",\"city2\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		JsonObject e1 = Document.parse(embedded1);
		JsonObject e2 = Document.parse(embedded2);
		test1.insert(e1);
		test1.insert(e2);

		// Insert Arrays
		String array1 = "{\"key7\":[\"apple\", \"banana\", \"pear\"], \"key1\": \"fruit\"}";
		String array2 = "{\"key9\":[\"apple\", \"banana\", \"pear\"], \"key1\": \"fruit2\"}";
		JsonObject a1 = Document.parse(array1);
		JsonObject a2 = Document.parse(array2);
		test1.insert(a1);
		test1.insert(a2);

		/*
		data:
		{"key1":"value1","key2":"value2","id":"9cbfaff7-28fa-498d-9b22-bc23ade029e6"}

		{"key1":"value3","key2":"value2","id":"060ff45c-00b7-4919-b2ed-5a8219e2dcd5"}

		{"key5":"value5","city1":{"city1":"Nanjing","city2":"St. Louis"},"id":"81b373f1-4aba-42ba-9449-5bffe4d06f31"}

		{"key6":"value6","city2":{"city1":"Nanjing","city2":"St. Louis"},"id":"11b1d56b-48db-45ab-be3e-5ea0185adfee"}

		{"key7":["apple","banana","pear"],"key1":"fruit","id":"8456c669-5ad4-4572-87b6-ea8d7c499e55"}

		{"key9":["apple","banana","pear"],"key1":"fruit2","id":"a1a9473a-55b1-4f73-ad7e-0eadb26ed8b6"}
		*/

		/*
			 Update Primitive type
		 */
		String query1 = "{ \"key2\":\"value2\"}";
		JsonObject q1 = Document.parse(query1);
		String update1 = "{ \"key1\":\"PrimitiveChange\"}";
		JsonObject u1 = Document.parse(update1);

		// only update the first one (multi = false)
		test1.update(q1, u1, false);
		assertEquals(test1.getDocument(0).getAsJsonPrimitive("key1").getAsString(), "PrimitiveChange");

		// recover
		String recover = "{ \"key1\":\"value1\"}";
		JsonObject r = Document.parse(recover);
		test1.update(u1, r, false);
		assertEquals(test1.getDocument(0).getAsJsonPrimitive("key1").getAsString(), "value1");

		// Update all (multi = true)
		test1.update(q1, u1, true);
		assertEquals(test1.getDocument(0).getAsJsonPrimitive("key1").getAsString(), "PrimitiveChange");
		assertEquals(test1.getDocument(1).getAsJsonPrimitive("key1").getAsString(), "PrimitiveChange");

		/*
			 Update Embedded type
		 */
		String query2 = "{ \"key5\":\"value5\"}";
		JsonObject q2 = Document.parse(query2);
		String update2 = "{\"city1\":{\"city1\":\"EmbeddedChange\",\"city2\":\"EmbeddedChange\"}}";
		JsonObject u2 = Document.parse(update2);
		test1.update(q2, u2, false);
		assertEquals(test1.getDocument(2).getAsJsonObject("city1"), u2.getAsJsonObject("city1"));

		/*
			 Update Array type
		 */
		String query3 = "{ \"key1\":\"fruit\"}";
		JsonObject q3 = Document.parse(query3);
		String update3 = "{\"key7\":[\"ArrayChange\", \"ArrayChange\", \"ArrayChange\"]}";
		JsonObject u3 = Document.parse(update3);
		test1.update(q3, u3, false);
		assertEquals(test1.getDocument(4).getAsJsonArray("key7"), u3.getAsJsonArray("key7"));
	}

	@Test
	public void testRemove() {
		// Pre setup
		DB db = new DB("test");
		File file = new File("testfiles/test/test1.json");
		// Reset(Delete the json file if this test was previously executed)
		if(file.exists()) file.delete();
		DBCollection test1 = db.getCollection("test1");

		// Insert primitive type
		String json1 = "{ \"key1\":\"value1\", \"key2\":\"value2\"}";
		JsonObject j1 = Document.parse(json1);
		test1.insert(j1);
		String json2 = "{ \"key1\":\"value3\", \"key2\":\"value2\"}";
		JsonObject j2 = Document.parse(json2);
		test1.insert(j2);

		// Insert Embedded document
		String embedded1 = "{\"key5\":\"value5\",\"city1\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		String embedded2 = "{\"key6\":\"value6\",\"city2\":{\"city1\":\"Nanjing\",\"city2\":\"St. Louis\"}}";
		JsonObject e1 = Document.parse(embedded1);
		JsonObject e2 = Document.parse(embedded2);
		test1.insert(e1);
		test1.insert(e2);

		// Insert Arrays
		String array1 = "{\"key7\":[\"apple\", \"banana\", \"pear\"], \"key1\": \"fruit\"}";
		String array2 = "{\"key9\":[\"apple\", \"banana\", \"pear\"], \"key1\": \"fruit\"}";
		JsonObject a1 = Document.parse(array1);
		JsonObject a2 = Document.parse(array2);
		test1.insert(a1);
		test1.insert(a2);

		// remove first one
		String query1 = "{ \"key1\":\"value1\"}";
		JsonObject q1 = Document.parse(query1);
		test1.remove(q1, false);
		// 5 documents remaining
		assertEquals(test1.count(), 5);

		// remove multiple
		String query2 = "{ \"key1\":\"fruit\"}";
		JsonObject q2 = Document.parse(query2);
		test1.remove(q2, true);
		// 5 documents remaining
		assertEquals(test1.count(), 3);
	}

	@Test
	public void testDrop() {
		DB db = new DB("test");
		DBCollection test1 = db.getCollection("test1");
		assertTrue(new File("testfiles/test/test1.json").exists());
		test1.drop();
		assertFalse(new File("testfiles/test/test1.json").exists());
	}

}
