/**
 * 
 */
package com.att.intergration.tests.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;

import java.util.*;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

import io.restassured.http.Header;
import io.restassured.internal.assertion.Assertion;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.att.intergration.tests.beans.ITValidationResults;
import com.att.intergration.tests.beans.ServiceDetails;
import com.att.intergration.tests.beans.TestCases;
import com.att.intergration.tests.utils.ValidationUtil;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
 

/**
 * @author sk4117
 *
 */
public class IntergrationTestExecutor extends AbstractIntergrationTest {

	/** The log. */
 	public static Logger log = Logger.getLogger("IntergrationTestExecutor");

	/** Test Name to executing currently. */
	private String testName;

	
	/** Test Data. */
	@Parameter(value = 0)
	public TestCases testData;

	/**
	 * Parameterized Data .
	 *
	 * @return the list
	 * @throws Exception
	 *             the exception
	 */
	@Parameters(name = "{index}: test - {0} ")
	public static List<TestCases> data() throws Exception {
		return getTestCases();
	}

	@Rule
	public ErrorCollector collector = new ErrorCollector();
	
	public static String previousFlow = "";
	
	public static String currentFLow;
	
	public static boolean prevTestFailed = false; 
	
	public static int count = 0;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		super.setUp();
		if (ignoreTest) {
			return;
		}
	}

	/**
	 * Validate get offers success response.
	 *
	 * @throws Exception
	 *             the exception
	 */
	
	@Test
	public void validateService() throws Exception {

		TestCases testCases = testData;
		Map<String, ServiceDetails> serviceDetails = testCases.getTestCases();
		Map<String, String> testResults = new HashMap<>();
		ValidationUtil util = new ValidationUtil();
		if (serviceDetails != null) {
			currentFLow = testCases.getTestGroup();
			if(prevTestFailed && (previousFlow.equalsIgnoreCase(currentFLow)) ){
				previousFlow = currentFLow;
				throw new AssertionError("Previous test case for the flow :'"+currentFLow+"' had failed. Cannot execute : "+testData.getTestCaseName());
			}else if(prevTestFailed && (!previousFlow.equalsIgnoreCase(currentFLow))){
				prevTestFailed = false;
			}

			// Count to increment Test group serial
			if((!previousFlow.equalsIgnoreCase(currentFLow))){
				count=count+10;
			}
		//	for (String testName : testCases.getTestCases().keySet()) {
			ServiceDetails testDetails = serviceDetails.get(testData.getTestCaseName());
			log.info("#########TEST-CASE-START#  Flow : " + testCases.getTestGroup() +"_TC_" + count + "  TestCase Name : " + testDetails.getTestCaseName() + " Validation Type : " + testDetails.getValidationType().name() );
			testDetails.setServiceGroup(testCases.getTestGroup());
			ITValidationResults itValidationResults = validateServiceDetails(testDetails, testResults, util, testName );
			if(!ValidationUtil.SUCCESS.equalsIgnoreCase(itValidationResults.getStatus())){
				prevTestFailed = true;
			}
			log.info("#########TEST-CASE-END#  Flow : " + testCases.getTestGroup() +"_TC_" + count + "  TestCase Name : " + testDetails.getTestCaseName() + " Validation Type : " + testDetails.getValidationType().name() );
			//}
			previousFlow = currentFLow;
		}
		
		
 		//log.info(testResults.toString());

	}

	private ITValidationResults validateServiceDetails(ServiceDetails testDetails, Map<String, String> testResults,
			ValidationUtil util, String testName) {

		Map<String, String> parametersMap = testDetails.getParams();
		Map<String, String> cookieMap = testDetails.getCookies();
		String body = testDetails.getBody();
		if(testDetails.getRequsstBodyReference() !=null){
			body = "";
			String requestBodyFilePath = "testCases/" + this.getCurrentProfile() + "/"
					+ testDetails.getRequsstBodyReference();
			body = loadRequestBodyFromFilePath(requestBodyFilePath);
		}

		if(testDetails.getOrderIdType() != null) {
			rspec.baseUri(this.coscBaseURI);
		}
		if(testDetails.getOrderIdType() != null &&  testDetails.getOrderIdType().equals(ValidationUtil.OrderIdType.single)){
			if(this.orderId != null && testDetails.getUrl().contains("order-id")) {
				testDetails.setUrl(testDetails.getUrl().replace("order-id", orderId));
				orderId = null;
			}
		}
		log.info("***api url : " + testDetails.getUrl());

		RequestSpecification requestSpecification =  given().header("X-CCC-PVT-MODE", "true").spec(rspec).contentType(MediaType.APPLICATION_JSON);
		if(testDetails.getHeaders() != null ) {
			log.info("*****headers : " + testDetails.getHeaders());
			requestSpecification.headers(testDetails.getHeaders());
		}

		Response response = null;
		if (parametersMap != null) {
			requestSpecification.queryParameters(parametersMap);
		}
		if (body != null) {
			requestSpecification.body(body);
		}
		if (cookieMap != null) {
			requestSpecification.cookies(cookieMap);
		}

		log.info("****Method : "  + testDetails.getType() +  "\n");
		switch (testDetails.getType()) {
		case GET:
			response = requestSpecification.get(testDetails.getUrl());
			break;
		case POST:
			log.info(" request - body :  " + body+ "\n");
			response = requestSpecification.post(testDetails.getUrl());
			break;
		case PUT:
			response = requestSpecification.put(testDetails.getUrl());
			break;
		default:
			response = requestSpecification.get(testDetails.getUrl());
			break;
		}

		// then:
		assertNotNull(response);
		collector.checkThat(response.statusCode(), is(200));
 		// and:
		ITValidationResults validationResult = util.validate(testDetails, response.getBody().asString(),response);
		testResults.put(testName, validationResult.getErrorMessage());
		collector.checkThat(testName + " - " + validationResult.getErrorMessage(), validationResult.getStatus(),
				is(ValidationUtil.SUCCESS));
		
		if (ValidationUtil.SUCCESS.equals(validationResult.getStatus()) && testDetails.getServiceDetails() != null) {
			validateServiceDetails(testDetails.getServiceDetails(), testResults, util, testName + "-Child");
		}else if(ValidationUtil.SUCCESS.equals(validationResult.getStatus())){
			validationResult.setStatus(ValidationUtil.SUCCESS);
			if(testDetails.getTestCaseName().contains("validateOrderSubmit")){
				orderId =  util.getOrderId(response,testDetails);
				log.info(" Order Id : " + orderId);
			}
		}else{
			validationResult.setStatus(ValidationUtil.FAILED);
		}
		return validationResult;
	}

	/**
	 * Returns the Test name.
	 */
	@Override
	public String getTestName() {
		return testName;
	}

	public String loadRequestBodyFromFilePath(String filePath){
		String bodyJsonStr = "";
		ValidationUtil validationUtil = new ValidationUtil();
		Map<String, JsonPath> requestBodyJson = validationUtil.loadMockData(filePath);
		String testCaseName = testData.getTestCaseName();
		String key = testCaseName.replace("Response","Request");
		if (null != requestBodyJson && requestBodyJson.size() > 0) {
			if(requestBodyJson.containsKey(key)){
				bodyJsonStr = requestBodyJson.get(key).prettify();
			}
		}
		return bodyJsonStr;
	}
}
