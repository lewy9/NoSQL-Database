/*
 * Student 1 name: Weichen Zhu
 * Student 2 name: Hongchuan Shi
 * Date: 2019 12/13
 */

package hw5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.gson.*;

public class Document {
	
	/**
	 * Parses the given json string and returns a JsonObject
	 * This method should be used to convert text data from
	 * a file into an object that can be manipulated.
	 */
	public static JsonObject parse(String json) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(json);
		return element.getAsJsonObject();
	}
	
	/**
	 * Takes the given object and converts it into a
	 * properly formatted json string. This method should
	 * be used to convert JsonObjects to strings
	 * when writing data to disk.
	 */
	public static String toJsonString(JsonObject json) {
		return json.toString();
	}
}
