package UtilityClasses;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.io.FileReader;
import java.io.InputStream;

public class JSONReader {
	private JsonObject jsonObject;
	
	public JSONReader(HardwareMap hardwareMap, String fileName) {
		JsonParser parser = new JsonParser();
		JsonElement element = null;
		try {
			InputStream in = hardwareMap.appContext.getAssets().open(fileName);
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			in.close();
			
			element = parser.parse(new String(buffer));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		assert element != null;
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
