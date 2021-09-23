package UtilityClasses;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;

public class JSONReader {
	private JsonObject jsonObject;
	
	public JSONReader(String fileName) throws Exception {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(new FileReader(fileName));
		jsonObject = element.getAsJsonObject();
	}
	
	public String getString(String n) {
		return jsonObject.get(n).getAsString();
	}
	
	public int getInt(String n) {
		return jsonObject.get(n).getAsInt();
	}
	
	public double getDouble(String n) {
		return jsonObject.get(n).getAsDouble();
	}
	
	public long getLong(String n) {
		return jsonObject.get(n).getAsLong();
	}
	
	public boolean getBoolean(String n) {
		return jsonObject.get(n).getAsBoolean();
	}
	
	public Location getLocation(String n) {
		String locationString = jsonObject.get(n).getAsString();
		locationString = locationString.substring(1, locationString.length() - 1);
		String[] locationData = locationString.split(", ");
		double[] locationVals = new double[locationData.length];
		for (int i = 0; i < locationData.length; i++) {
			locationVals[i] = Double.parseDouble(locationData[i]);
		}
		if (locationData.length == 2) {
			return new Location(locationVals[0], locationVals[1]);
		}
		else if (locationData.length == 3) {
			return new Location(locationVals[0], locationVals[1], locationVals[2]);
		}
		else {
			throw new JsonSyntaxException("Location invalid");
		}
	}
}
