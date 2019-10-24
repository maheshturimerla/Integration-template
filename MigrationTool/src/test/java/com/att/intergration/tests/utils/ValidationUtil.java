/**
 * 
 */
package com.att.intergration.tests.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.ValidationException;
import org.json.JSONException;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import com.att.intergration.tests.beans.ITValidationResults;
import com.att.intergration.tests.beans.ServiceDetails;
import com.att.intergration.tests.core.AbstractIntergrationTest;
import com.att.intergration.tests.core.IntergrationTestExecutor;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.restassured.path.json.JsonPath;


import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author sk4117
 *
 */
public class ValidationUtil {

	/**
	 * SUCCESS.
	 */
	public static final String SUCCESS = "Validation Success";
	public static final String FAILED = "Validation Failed";

	/** The log. */
	// protected static EELFLogger log =
	// AjscEelfManager.getInstance().getLogger(ValidationUtil.class);
	public static Logger log = Logger.getLogger("ValidationUtil");

	public static final ObjectMapper Mapper;

	static {
		Mapper = new ObjectMapper();
		Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// Mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		Mapper.setSerializationInclusion(Include.NON_NULL);
	}

	/**
	 * @author sk4117
	 *
	 */
	public enum ValidationType {
		schema, actual,partial,expression
	}

	public enum OrderIdType{
		single,multiple
	}

	/**
	 * @param actualResponse
	 * @param testDetails
	 * @return
	 * @throws ITValidationResults
	 */
	public ITValidationResults validate(ServiceDetails testDetails, String actualResponse, Response response) {
		ITValidationResults ex = new ITValidationResults();

		try {
			ValidationType type = testDetails.getValidationType();
			// JSONObject data = new JSONObject(new JSONTokener(jsonPath));
			String filePath = "testCases/" + AbstractIntergrationTest.getCurrentProfile() + "/"
					+ testDetails.getReferenceData();
			//log.info("*****Test Case : " + testDetails.getTestCaseName());
			Map<String, JsonPath> mockedJsonObject = loadMockData(filePath);
			if (null == mockedJsonObject || mockedJsonObject.size() < 0) {
				log.info("response json file is null");
				return ex;
			}
			if (type == null) {
				log.info("Validation Type : " + testDetails.getReferenceData());
				type = ValidationType.actual;
			}

			switch (type) {
			case schema:
				validateSchema(mockedJsonObject.get(testDetails.getTestCaseName()), actualResponse);
				break;
			case actual:
				validateActual(mockedJsonObject.get(testDetails.getTestCaseName()), actualResponse,testDetails);
				break;
			case partial:
				validatePartial(mockedJsonObject.get(testDetails.getTestCaseName()), actualResponse,testDetails);
				break;
			case expression:
				validateExpression(mockedJsonObject.get(testDetails.getTestCaseName()), actualResponse,testDetails,response);
				break;
			}

		} catch (ValidationException e) {
			e.printStackTrace();
			StringBuffer buf = new StringBuffer();
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(buf::append);
			ex.setActualData(actualResponse);
			ex.setReferanceData(testDetails.getReferenceData());
			ex.setValidationType(testDetails.getValidationType().name());
			ex.setErrorMessage(buf.toString());
			ex.setStatus("FAILURE");
			log.warning(buf.toString());
			return ex;
		} catch (JSONException e) {
			e.printStackTrace();
			ex.setStackTrace(e.getStackTrace());
			ex.setActualData(actualResponse);
			ex.setReferanceData(testDetails.getReferenceData());
			ex.setValidationType(testDetails.getValidationType().name());
			ex.setErrorMessage(e.getMessage());
			ex.setStatus("FAILURE");
			log.warning(ex.toString());
			return ex;
		} catch (Exception e) {
			e.printStackTrace();
			ex.setStackTrace(e.getStackTrace());
			ex.setActualData(actualResponse);
			ex.setReferanceData(testDetails.getReferenceData());
			ex.setValidationType(testDetails.getValidationType().name());
			ex.setErrorMessage(e.getMessage());
			ex.setStatus("FAILURE");
			log.warning(ex.toString());
			return ex;
		}
		ex.setStatus(SUCCESS);
		return ex;
	}
	/** Method to validate Json Path level values
	 * @param testDetails
	 * @param actualResponse
	 * @param jsonPath
	 * @param response
	 * @return
	 */
	public String validateExpression(JsonPath jsonPath, String actualResponse, ServiceDetails testDetails, Response response) {
		try {
			List<Map<String, String>> asserts = testDetails.getAsserts();
			Map<String, String> testResults = new HashMap<String,String>();
			if (null != asserts && asserts.size() > 0) {
			//	log.info("***** API's Actual  Response Received :  " + actualResponse );

				JsonPath actualResult = JsonPath.from(actualResponse);
				for (Map<String, String> attributes : asserts) {
					for (Entry<String, String> entry : attributes.entrySet()) {
							Object value = response.body().jsonPath().get(entry.getKey());
							if(value instanceof Map) {
								bodyJsonAssert(entry, response,testResults);
							} else if (value instanceof List) {
								if(((List)value).get(0) instanceof Map) {
									bodyJsonAssert(entry, response,testResults);
								} else {
									bodyCollectionValueAssert(response, entry,testResults);
								}

							} else {
								bodyAtomicValueAssert(response, entry,testResults);
							}

					}
				}
				log.info("##Test Result: "+ SUCCESS);
			} else {
				log.warning("***** please configure partial asserts");
			}
		} catch (Exception e) {

			log.warning(stackTraceToString(e));
			return FAILED;
		}

		return SUCCESS;
	}


	/** Method to validate attribute level values
	 * @param testDetails 
	 * @param actualResponse
	 * @param jsonPath
	 * @return
	 */
	public String validatePartial(JsonPath jsonPath, String actualResponse, ServiceDetails testDetails) {
		try {
			List<Map<String, String>> asserts = testDetails.getAsserts();
			if (null != asserts && asserts.size() > 0) {
				log.info("***** API's Actual  Response Received :  " + actualResponse );

				JsonPath actualResult = JsonPath.from(actualResponse);
				for (Map<String, String> attributes : asserts) {
					for (Entry<String, String> entry : attributes.entrySet()) {
						log.info("@AttributeName:  " + entry.getKey() + ",   [actualValue]: " + actualResult.getString(entry.getKey()) + ", [ExpectedValue]: " + entry.getValue());
						String actualValue = actualResult.getString(entry.getKey());
						if( actualValue == null ) {
							IntergrationTestExecutor.prevTestFailed = true;
							log.info("##Test Result: " + FAILED);
							throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is null.");
						}
						else {
							boolean success = true;
							success = (actualResult.getString(entry.getKey()).trim().equalsIgnoreCase(entry.getValue().trim()))? true : false;
							if(!success){
								IntergrationTestExecutor.prevTestFailed = true;
								log.info("##Test Result: "+ FAILED);
							}

							Assert.assertEquals(true, actualResult.getString(entry.getKey()).trim().equalsIgnoreCase(entry.getValue().trim()));
						}

					}
				}
				log.info("##Test Result: "+ SUCCESS);
			} else {
				log.warning("***** please configure partial asserts");
			}
		} catch (Exception e) {

			log.warning(stackTraceToString(e));
		}

		return SUCCESS;
	}
	
	public String stackTraceToString(Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    for (StackTraceElement element : e.getStackTrace()) {
	        sb.append(element.toString());
	        sb.append("\n");
	    }
	    return sb.toString();
	}
	/**
	 * @param actualResponse
	 * @param jsonPath
	 * @return
	 */
	public String validateSchema(JsonPath jsonPath, String actualResponse) {
		try {
			if(null != jsonPath && null != actualResponse){
				// JsonSchemaValidator.matchesJsonSchema(Mapper.writeValueAsString(jsonPath.get("response")));
				JSONAssert.assertEquals(Mapper.writeValueAsString(jsonPath.get("response")), actualResponse, true);
			}else{
				throw new  Exception("validateSchema :: either actual and expected values are NULL");
			}
		} catch (JSONException | IOException e) {
			log.warning(stackTraceToString(e)); 
		} catch (Exception e) {
			log.warning(stackTraceToString(e)); 
		}
		log.info(SUCCESS);
		return SUCCESS;
	}

	/**
	 * @param jsonPath
	 * @param actualResponse
	 * @return
	 */
	public String validateActual(JsonPath jsonPath, String actualResponse,ServiceDetails testDetails) {
		try {
			if(null != jsonPath && null != actualResponse){
				log.info("***** API's Actual  Response Received :  " + actualResponse + "\n");
				log.info("***** API's Expected  Response :  " + jsonPath.prettify().replaceAll("\\s", "") );
				JSONCompareResult result = JSONCompare.compareJSON(jsonPath.prettify(), actualResponse, JSONCompareMode.STRICT);
				if(result.failed()) {
					log.info("mismatch : " + result.getMessage());
					log.info("##Test Result: " + FAILED);
					IntergrationTestExecutor.prevTestFailed = true;
					log.info("#########TEST-CASE-END#  Flow : " + testDetails.getServiceGroup() +  "  TestCase Name : " + testDetails.getTestCaseName() + " Validation Type : " + testDetails.getValidationType().name() );
					throw new AssertionError(" Result mismatch : " + result.getMessage());
				}
				else {
					JSONAssert.assertEquals(jsonPath.prettify(), actualResponse, true);
					log.info("##Test Result: " + SUCCESS);
				}
			}else{
				log.info("***** API's Actual  Response Received :  " + actualResponse + "\n");
				//throw new  Exception("validateActual :: either actual and expected values are NULL");
			}
		} catch (Exception e) {
			log.warning(stackTraceToString(e)); 
		}
		return SUCCESS;
	}

	public Map<String, JsonPath> loadMockData(String filePath) {
		Map<String, JsonPath> jsonPathMap = new HashMap<String, JsonPath>();
		try {
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, LinkedHashMap<String, String>> mockDataMap = Mapper.readValue(loadFile(filePath),
					LinkedHashMap.class);
			mockDataMap.forEach((k, v) -> {
				try {
					jsonPathMap.put(k, new JsonPath(Mapper.writerWithDefaultPrettyPrinter().writeValueAsString(v)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (JsonParseException e) {
			log.warning(stackTraceToString(e)); 
		} catch (JsonMappingException e) {
			log.warning(stackTraceToString(e)); 
		} catch (IOException e) {
			log.warning(stackTraceToString(e)); 
		}
		return jsonPathMap;
	}

	public static String loadFile(String filePath) {
		if (StringUtils.isNotEmpty(filePath)) {
			return readAsStringFromInputSteam(ValidationUtil.class.getClassLoader().getResourceAsStream(filePath));
		}
		return null;
	}

	public static String readAsStringFromInputSteam(InputStream inputStream) {
		if (inputStream != null) {
			return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
		}
		return null;
	}

	private void bodyCollectionValueAssert(Response response, Entry<String, String> entry,Map<String, String> testResults) {
		List<?> expected = Arrays.asList(entry.getValue().split("[\\s,]+"));
		List<?> actual = response.body().jsonPath().getList(entry.getKey());
		log.info("@AttributeName:  " + entry.getKey() + ",   [actualValue]: " + actual.toString() + ", [ExpectedValue]: " + expected.toString());
		Assert.assertThat(actual, equalTo(expected));
	}

	private void bodyAtomicValueAssert(Response response, Entry<String, String> entry, Map<String, String> testResults) {
		String expected = entry.getValue();
		String actual = response.body().jsonPath().getString(entry.getKey());
		log.info("@AttributeName:  " + entry.getKey() + ",   [actualValue]: " + actual + ", [ExpectedValue]: " + expected);
		boolean success = true;
				if( actual == null ) {
					IntergrationTestExecutor.prevTestFailed = true;
				}
				else if (expected.equalsIgnoreCase("isNotNull")) {
					IsNotNull(actual, expected, entry );
				}
				else {
					IntergrationTestExecutor.prevTestFailed = false;
					success = (actual.trim().equalsIgnoreCase(expected.trim())) ? true : false;
					if (!success) {
						IntergrationTestExecutor.prevTestFailed = true;
						log.info("##Test Result: " + FAILED);
					}
					Assert.assertThat(actual, equalTo(expected));
				}
		if(IntergrationTestExecutor.prevTestFailed == true){
			log.info("##Test Result: " + FAILED);
			throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is not match with expected.. Null ");
		}

		/*if( actual == null ) {
			IntergrationTestExecutor.prevTestFailed = true;
			log.info("##Test Result: " + FAILED);
			throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is null.");
		}
		else {
			success = (actual.trim().equalsIgnoreCase(expected.trim())) ? true : false;
			if (!success) {
				IntergrationTestExecutor.prevTestFailed = true;
				log.info("##Test Result: " + FAILED);
			}
			Assert.assertThat(actual, equalTo(expected));
		}*/
	}

	private void IsNotNull(String actual, String expected, Entry<String, String> entry ){

		if (expected.equalsIgnoreCase("isNotNull")) {
			log.info("@AttributeName:  " + entry.getKey());
			log.info("[Criteria]: " + expected + "\n");
			log.info("[actual Value for Not Null]: " + actual + "\n");
			if(actual == null){
				IntergrationTestExecutor.prevTestFailed = true;
				log.info("##Test Result: " + FAILED);
				throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is null.");
			}
			Assert.assertNotNull(actual);
		}


	}

	private void containsKey(String actual, String expected, Entry<String, String> entry ,Response response){

		log.info("@AttributeName:  " + entry.getKey());
		log.info("[Criteria]: " + expected + "\n");
		log.info("[parentObject value]: " + actual + "\n");
		if(response.body().jsonPath().get(entry.getKey()) != null){
			String tempVal = expected.replaceAll("containsKey:","");
			tempVal = tempVal.replaceAll("\"","");
			if(null == response.body().jsonPath().get(entry.getKey() + "." + tempVal)){
				IntergrationTestExecutor.prevTestFailed = true;
				log.info("##Test Result: " + FAILED);
				throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is null.");
			}
			else{

				String value = response.body().jsonPath().get(entry.getKey() + "." + tempVal);
				log.info("[actual value]: " + value + "\n");
				Assert.assertEquals((value != null), true);
			}
		}
		else{
			IntergrationTestExecutor.prevTestFailed = true;
			log.info("[actual value]: " + null + "\n");
			log.info("##Test Result: " + FAILED);
			throw new AssertionError("Actual Value for the Attribute " + entry.getKey()   + " is null.");
		}
	}

	private void bodyJsonAssert(Entry<String, String> entry, Response response,Map<String, String> testResults) throws JSONException {
		String expected = entry.getValue();
		String actual = JsonMapper.toJson(response.body().jsonPath().get(entry.getKey()));

		if (expected.equalsIgnoreCase("isNotNull")) {
			IsNotNull(actual, expected, entry );
		}
		else if (expected.contains("containsKey")) {
			containsKey(actual,expected, entry , response);
		}else {
			log.info("@AttributeName:  " + entry.getKey());
			log.info("[actualValue]: " + actual + "\n");
			log.info("[ExpectedValue]: " + expected + "\n");

			JSONCompareResult result = JSONCompare.compareJSON(expected, actual, JSONCompareMode.STRICT);
			if (result.failed()) {
				IntergrationTestExecutor.prevTestFailed = true;
				log.info("##Test Result: " + FAILED);
			}
			JSONAssert.assertEquals(expected, actual, true);
	   }
	}


	public String  getOrderId( Response response, ServiceDetails testDetails){
		if(testDetails != null && testDetails.getTestCaseName() != null && testDetails.getTestCaseName().contains("validateOrderSubmit")){
			return response.getBody().jsonPath().getString("payload.customer_order_id");
		}
		return  null;
	}


}
