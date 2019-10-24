/*
 * <AT&T Copyright> Copyright (C) 2014 AT&T All Rights Reserved. No use, copying
 * or distribution of this work may be made except in accordance with a valid
 * license agreement from AT&T. This notice must be included on all copies,
 * modifications and derivatives of this work.
 * 
 * AT&T MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. AT&T SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. </AT&T Copyright>
 */
package com.att.ajsc.ccc.config.common;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * An Utility class used to create and handle Json string. It facilitates the
 * conversion from Object to Json String and From Json String to Object
 */
public final class CCCJsonParser {

	/**
	 * Field mapper.
	 */
	private static final ObjectMapper Mapper;

	static {
		Mapper = new ObjectMapper();
		Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		Mapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * Takes an object as parameter and converts in json string format like
	 * {"dataValue":"Data Value","cloudType":"cart"}
	 * 
	 * @param object
	 *            holds the object which need to be converted in json string
	 * 
	 * @return Json String
	 */
	public static String getJsonFromObject(final Object object) {
		String jsonString = null;
		try {
			jsonString = Mapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	/**
	 * Takes an object as parameter and converts in json string format like
	 * {"dataValue":"Data Value","cloudType":"cart"}
	 * 
	 * @param object
	 *            holds the object which need to be converted in json string
	 * 
	 * @return Json String
	 */
	public static String getJsonWithPrettyPrintFromObject(final Object object) {
		String jsonString = null;
		try {
			jsonString = Mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonString;
	}
	
	/**
	 * Takes Json String and valueType as parameter and converts the Json string
	 * as object of valueType
	 * 
	 * @param jsonString
	 *            holds the json string which needs to be converted in object
	 * @param valueType
	 *            describe the type in which json string needs to converted
	 * 
	 * @return object
	 */
	public static <T> T getObjectFromJson(Object jsonString, final Class<T> valueType) {
		T object = null;
		if(jsonString == null) {
			return null;
		}
		
		try {
			object = Mapper.readValue(jsonString.toString(), valueType);

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * Takes Json String and valueType as parameter and converts the Json string
	 * as object of valueType
	 * 
	 * @param jsonString
	 *            holds the json string which needs to be converted in object
	 * @param valueType
	 *            describe the type in which json string needs to converted
	 * 
	 * @return object
	 */
	public static <T> T getObjectFromJsonNode(JsonNode jsonNode, final Class<T> valueType) {
		T object = null;
		try {
			object = Mapper.treeToValue(jsonNode, valueType);

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public static ObjectMapper getMapper() {
		return Mapper;
	}
	/**
	 * Method to merger two JSON object into single 
	 * @param <T>
	 * @param target
	 * @param source
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public static <T> Object merge(T target, T source, final Class valueType) {
        ObjectNode objNodeFrom = Mapper.valueToTree(source);
        ObjectNode objNodeTo = Mapper.valueToTree(target);
        Iterator<String> fieldNames = objNodeFrom.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode primaryValue = objNodeFrom.get(fieldName);
            if (primaryValue != null && !primaryValue.isNull()) {
                objNodeTo.put(fieldName, primaryValue.asText());
            }
        }
        return getObjectFromJson(objNodeTo, valueType) ;
    }
    
    public static <T> T readValue(String p, TypeReference<?> valueTypeRef){
    	T object = null;
    		try {
				object = Mapper.readValue(p, valueTypeRef);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return object;
	}
}
