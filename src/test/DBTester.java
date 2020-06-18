/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */
package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import hw5.DBCollection;
import org.junit.jupiter.api.Test;

import hw5.DB;

class DBTester {
	
	/**
	 * Things to consider testing:
	 * 
	 * Properly creates directory for new DB (done)
	 * Properly accesses existing directory for existing DB
	 * Properly accesses collection
	 * Properly drops a database
	 * Special character handling?
	 */
	
	@Test
	public void testCreateDB() {
		DB hw5 = new DB("hw5"); //call method
		assertTrue(new File("testfiles/hw5").exists()); //verify results
	}


	@Test
	public void testInvalidDatabaseName() {
		// White space
		try {
			DB db = new DB("hw 5");
			fail("Should throw an Exception");
		} catch (Exception ignored) {

		}

		// Single quote
		try {
			DB db = new DB("hw'5");
			fail("Should throw an Exception");
		} catch (Exception ignored) {

		}

		// Double quote
		try {
			DB db = new DB("hw\"5");
			fail("Should throw an Exception");
		} catch (Exception ignored) {

		}

		assertTrue(true);
	}

	@Test
	public void testAccessingCollection() {
		DB data = new DB("data");
		DBCollection c = data.getCollection("test");
		assertEquals(3, c.count());
	}

	@Test
	public void testDrop() {
		DB hw5 = new DB("hw5");
		hw5.getCollection("test1");
		hw5.getCollection("test2");
		hw5.dropDatabase();
		assertFalse(new File("testfiles/hw5").exists());
	}
}
