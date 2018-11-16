package it.makeit.pentaho.steps.alfresco.upload.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class AlfrescoUploadStepJsonHelper {
	private static Gson gson = new GsonBuilder().create();

	public static Map<String, Object> jsonProperties(String json) {
		if (json == null || json.trim().length() == 0) {
			return new HashMap<>();
		}

		JsonElement element = gson.fromJson(json, JsonElement.class);

		Map<String, Object> properties = new HashMap<>();
		if (!element.isJsonObject()) {
			throw new IllegalArgumentException("Invalid json");

		}
		for (Entry<String, JsonElement> property : element.getAsJsonObject().entrySet()) {
			String propertyName = property.getKey();
			JsonElement value = property.getValue();

			if (value.isJsonNull()) {
				properties.put(propertyName, null);
			}

			if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				Object convert = JsonTypes.convert(value.getAsJsonPrimitive().getAsString());
				if (convert != null) {
					properties.put(propertyName, convert);
				} else {
					properties.put(propertyName, value.getAsJsonPrimitive().getAsString()); // simple string
				}
			}
			if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
				properties.put(propertyName, value.getAsJsonPrimitive().getAsBoolean());
			}
			if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) {
				String numericValue = value.getAsJsonPrimitive().getAsString();
				if (numericValue.contains(".")) {
					properties.put(propertyName, value.getAsJsonPrimitive().getAsDouble());
				} else {
					properties.put(propertyName, value.getAsJsonPrimitive().getAsInt());
				}
			}

		}

		return properties;
	}

	private enum JsonTypes {
		S, L, D, I, F, B, DT, TS;

		public static Object convert(String s) {

			for (JsonTypes type : JsonTypes.values()) {
				if (s.startsWith(type.name() + "#") || s.startsWith(type.name().toLowerCase() + "#")) {
					switch (type) {
					case S:
						return s.substring(2);
					case L:
						return Long.valueOf(s.substring(2));
					case I:
						return Integer.valueOf(s.substring(2));
					case F:
						return Float.valueOf(s.substring(2));
					case D:
						return Double.valueOf(s.substring(2));
					case B:
						return Boolean.valueOf(s.substring(2));
					case DT:
						return convertDate(s.substring(3), "dd/MM/yyyy");
					case TS:
						return convertDate(s.substring(3), "dd/MM/yyyy HH:mm:ss");
					}
				}
			}

			return null;
		}

		private static Date convertDate(String s, String format) {
			try {
				return new SimpleDateFormat(format).parse(s);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
