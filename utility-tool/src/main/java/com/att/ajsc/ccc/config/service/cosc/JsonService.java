/**
 * 
 */
package com.att.ajsc.ccc.config.service.cosc;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The Class JsonService.
 *
 * @version 0 An Utility class used to create and handle Json string. It
 *          facilitates the conversion from Object to Json String and From Json
 *          String to Object
 */
public final class JsonService {


	/**
	 * Field mapper.
	 */
	private static final ObjectMapper MAPPER;

	/**
	 * Instantiates a new json service.
	 */
	private JsonService() {

	}

	static {
		MAPPER = new ObjectMapper();
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		MAPPER.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * Takes an object as parameter and converts in json string format like
	 * {"dataValue":"Data Value","cloudType":"cart"}.
	 *
	 * @param object
	 *            holds the object which need to be converted in json string
	 * @return Json String
	 */
	public static String getJsonFromObject(final Object object)	{
		String jsonString = null;
		try {
			jsonString = MAPPER.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonString;
	}


	/**
	 * Takes Json String and valueType as parameter and converts the Json string
	 * as object of valueType.
	 *
	 * @param <T>
	 *            the generic type
	 * @param jsonString
	 *            holds the json string which needs to be converted in object
	 * @param valueType
	 *            describe the type in which json string needs to converted
	 * @return <T>
	 */

	public static <T> T getObjectFromJson(final Object jsonString, final Class<T> valueType)	{

		T object = null;

		if (jsonString != null) {
			try {
				object = MAPPER.readValue(jsonString.toString(), valueType);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return object;
	}

	
	/**
	 * returns ObjectMapper for reading/writing JSON.
	 *
	 * @return the mapper
	 */
	public static ObjectMapper getMapper() {
		return MAPPER;
	}
}