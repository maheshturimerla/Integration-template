package com.att.intergration.tests.core;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.att.intergration.tests.beans.*;
import com.att.intergration.tests.utils.ValidationUtil;
import io.restassured.path.json.JsonPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


/**
 * The Class AbstractITest.
 * 
 * @author sk4117
 */
@SuppressWarnings("unused")
@RunWith(value = Parameterized.class)
@Ignore
public abstract class AbstractIntergrationTest {

	/** The log. */
	public static Logger log = Logger.getLogger("AbstractIntergrationTest");

	/** The yaml mapper. */
	private static ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

	/** The runtime env variable for iTestEnv. */
	public static final String ITEST_ENV = "ITEST_ENV";

	/** Test Environment profiles. */
	public static final String[] PROFILES = { "dev", "ci", "test", "stageff", "stagenp" };

	/** Runtime Environment (Dev,Test,QA,Prod). */
	public static String activeProfile;

	/** Load application configuration. */
	public static Config conf;

	/** The timeout. */
	public int timeout;

	/** The port. */
	public int port;

	/** The base uri. */
	public String baseURI;

	/** The base uri. */
	public static String services;

	/** The flos. */
	public static String flows;

	/**
	 * baseConfigPath.
	 */
	public static String baseConfigPath;
	/** The rspec. */
	public RequestSpecification rspec;

	/** skip the test*. */
	public boolean ignoreTest;

	protected TestCases details;

	public String coscBaseURI;

	public static String orderId;

	public static String uuid;

	public static String orderIds;

	/** skips test execution if data is not created in test data yaml file. **/
	@Rule
	public IgnoreRule ignoreRule = new IgnoreRule(getIgnore());

	static {
		orderId = null;
		uuid = null;
		AbstractIntergrationTest.activeProfile = getCurrentProfile();

		AbstractIntergrationTest.conf = ConfigFactory.load("application-" + AbstractIntergrationTest.activeProfile);
		AbstractIntergrationTest.services = conf.getString("pl.services");
		AbstractIntergrationTest.flows = conf.getString("pl.flows");
		AbstractIntergrationTest.baseConfigPath = conf.getString("baseConfigPath");
		AbstractIntergrationTest.orderIds = conf.getString("pl.orderIds");
		log.info(AbstractIntergrationTest.orderIds);
		log.info("Loaded Profile=" + AbstractIntergrationTest.activeProfile + ",ActiveServices="
				+ AbstractIntergrationTest.services + ",Config Base Path=" + AbstractIntergrationTest.baseConfigPath);

	}

	@Before
	public void setUp() {

		// if (ignoreTest) {
		// return;
		// }

		this.baseURI = conf.getString("pl.server.baseURI");
		this.coscBaseURI = conf.getString("pl.cosc.baseURI");
		this.timeout = conf.getInt("api.timeout");
	
		final RequestSpecBuilder build = new RequestSpecBuilder().setBaseUri(this.baseURI);
		rspec = build.build();
		RestAssured.defaultParser = Parser.JSON;
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
				ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {

					@SuppressWarnings("rawtypes")
					@Override
					public com.fasterxml.jackson.databind.ObjectMapper create(Class cls, String charset) {
						com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
						objectMapper.registerModule(new JodaModule());
						objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
						return objectMapper;
					}
				}));

		RestAssured.useRelaxedHTTPSValidation();

	}

	/**
	 * Gets the test data.
	 *
	 * @return
	 * @return
	 * @return the test data
	 * @throws Exception
	 *             the exception
	 */
	public static List<TestCases> getTestCases() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		List<TestCases> testCase = new ArrayList<>();
		Map<String,String> flowURIs = new HashMap<String,String>();
		log.info("###Flows         :  to be tested on this execution : " + AbstractIntergrationTest.flows + "\n\n");
		log.info("###Services/APIs :  to be tested in each flow : " + AbstractIntergrationTest.services + "\n\n");
		IntergrationTestCases iTestCases =null;
		try {
			iTestCases = mapper.readValue(new File(AbstractIntergrationTest.baseConfigPath
					+ getCurrentProfile() + "/test-data-" + getCurrentProfile() + ".yaml"),
					IntergrationTestCases.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
			try {
				if (null != iTestCases && null != iTestCases.getIntergrationTestCases()
						&& iTestCases.getIntergrationTestCases().size() > 0) {


       					for (String flowsKey : AbstractIntergrationTest.flows.split(",")) {

                                //############## Migration Start Code
							List<TestGroup> groups = new ArrayList<TestGroup>();
								TestGroup testGroup = new TestGroup();
									testGroup.setName(flowsKey);
									testGroup.setSkip(false);
								List<RestTest> restTests = new ArrayList<RestTest>();
                                //############## Migration End Code

								List<String> integrationTestCases = iTestCases.getIntergrationTestCases().get(flowsKey);
								for (String integrationTestCase : integrationTestCases) {
									   String serviceKey = getServiceKey(integrationTestCase);
									   File testDataFile = new File(AbstractIntergrationTest.baseConfigPath
											   + getCurrentProfile() + "/" + serviceKey + "/test-data-" + serviceKey + ".yaml");
									   log.info("############--------Service key : " + serviceKey);
									   if(testDataFile.exists()) {
										   TestCases tCase = mapper.readValue(testDataFile, TestCases.class);
										   if (null != tCase && null != tCase.getTestCases() && tCase.getTestCases().size() > 0) {
											   tCase.setTestCaseName(integrationTestCase);
											   tCase.setTestGroup(flowsKey);
											   if(tCase.getTestCases() != null && tCase.getTestCases().containsKey(tCase.getTestCaseName())
													   && tCase.getTestCases().get(tCase.getTestCaseName()) != null
													   && tCase.getTestCases().get(tCase.getTestCaseName()).getOrderIdType() != null
													   && tCase.getTestCases().get(tCase.getTestCaseName()).getOrderIdType().equals(ValidationUtil.OrderIdType.multiple)){
											   	   loadMultipleTestCases(testCase,tCase,"order-id");
											   }
											   else {
												   testCase.add(tCase);
											   }


											   //############## Migration Start Code
											   RestTest restTest = new RestTest();
											   		restTest.setName(tCase.getTestCaseName());
											   		restTest.setSkip(false);
											   		ServiceDetails serviceDetails = tCase.getTestCases().get(tCase.getTestCaseName());
											   		Request request = new Request();
												   		request.setUri(serviceDetails.getUrl());
													    Map<String,String>  requestHeaders = new HashMap<String,String>();
													          requestHeaders = serviceDetails.getHeaders();
													          if(requestHeaders == null){
																  requestHeaders = new HashMap<String,String>();
															  }
											                  requestHeaders.put("content-type","${content-type}");
													          requestHeaders.put("X-CCC-PVT-MODE","true");
											            request.setHeaders(requestHeaders);
													    Map<String,String>  requestCookies = new HashMap<String,String>();
													   if(requestCookies == null){
														   requestCookies = new HashMap<String,String>();
													   }
											            requestCookies.put("cookie1","cookie1");
													    request.setMethod(serviceDetails.getType().name().toLowerCase());
													    request.setCookies(requestCookies);

													    request.setParameters(serviceDetails.getParams());
													    if(serviceDetails.getBody() == null){
													    	// Read data from Request Body Json for Post Call
													    	if(serviceDetails.getRequsstBodyReference() != null && serviceDetails.getType().equals(RequestMethod.POST)){
																String requestBodyFilePath = "testCases/ccc" +  "/"
																		+ serviceDetails.getRequsstBodyReference();
																String bodyJsonStr = "";
																ValidationUtil validationUtil = new ValidationUtil();
																Map<String, JsonPath> requestBodyJson = validationUtil.loadMockData(requestBodyFilePath);
																String testCaseName = serviceDetails.getTestCaseName();
																String key = testCaseName.replace("Response","Request");
																if (null != requestBodyJson && requestBodyJson.size() > 0) {
																	if(requestBodyJson.containsKey(key)){
																		bodyJsonStr = requestBodyJson.get(key).prettify();
																	}
																}
																request.setBody(bodyJsonStr);
																request.setParameters(null);
															}
														}
														else{
													    	if(serviceDetails.getBody().isEmpty())
													    		request.setBody("");
															else
																request.setBody(serviceDetails.getBody());
														}

											   		Response response = new Response();
											   			response.setStatus(200);
											   			Map<String,String>  responseHeaders = new HashMap<String,String>();
											   			responseHeaders.put("content-type","application/json");

											   			response.setHeaders(responseHeaders);
													    Map<String,String>  responseVariables = new HashMap<String,String>();
													    responseVariables.put("contentType","header.content-type");
													    response.setVariables(responseVariables);
													    response.setCookies(requestCookies);
											            ResponseBody responseBody = new ResponseBody();
											            List<JsonAssert> jsonAsserts = new ArrayList<JsonAssert>();

											   			if (serviceDetails.getValidationType().equals(ValidationUtil.ValidationType.actual)) {
															JsonAssert jsonAssert = new JsonAssert();
															jsonAssert.setJsonPath("response.status");
															jsonAssert.setValue("success");
															jsonAsserts.add(jsonAssert);

															JsonAssert jsonAssert1 = new JsonAssert();
															jsonAssert1.setJsonPath("response.redirect");
															jsonAssert1.setValue("false");
															jsonAsserts.add(jsonAssert1);
											   			}
											            else{

											   				List<Map<String,String>> asserts = serviceDetails.getAsserts();
											   				if(asserts != null && asserts.size() > 0){
															   for(Map<String,String> assertMap : asserts){
															   	    for(Entry<String,String> assertMapEntry : assertMap.entrySet()){

																		JsonAssert jsonAssert = new JsonAssert();
																		jsonAssert.setJsonPath(assertMapEntry.getKey());
																		jsonAssert.setValue(assertMapEntry.getValue());
																		jsonAsserts.add(jsonAssert);
																	}
															   }
															}
														}
													    responseBody.setAsserts(jsonAsserts);
											   			response.setBody(responseBody);

											   			restTest.setRequest(request);
											   			restTest.setResponse(response);

											   restTests.add(restTest);
											   //############## Migration End Code

										   }
									   }
								}

							//############## Migration Start Code
							    testGroup.setTests(restTests);
							    groups.add(testGroup);
							//############## Migration end Code

							//############## Migration Start Code
							DumperOptions options = new DumperOptions();
							options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
							options.setExplicitStart(true);
							options.setExplicitEnd(true);
							options.setAllowUnicode(false);
							options.setSplitLines(true);
							options.setPrettyFlow(true);
							options.setIndent(4);
							options.setIndicatorIndent(2);
							Yaml yaml = new Yaml(options);
							String output = yaml.dump(groups);  // List

							try {
								FileOutputStream fos = new FileOutputStream("d:/yaml/test-data-" +flowsKey + ".yaml");
								DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
								outStream.writeUTF(output);
								outStream.close();
							}catch(FileNotFoundException fx){
								fx.printStackTrace();
							}catch(IOException io){
								io.printStackTrace();
							}

							System.out.println(output);  // Here your targeted YAML File is ready
							//############## Migration end Code

						}




				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		log.info("Total test cases to executed :" + testCase.size());
		return null;
	}





	public static String getServiceKey(String integrationTestCase){

		String resultServiceKey = null;
        String tempServiceKey = null;
		List<String> services = Arrays.asList(AbstractIntergrationTest.services.split(","));
		for (String serviceKey : services) {
			if (integrationTestCase.contains(serviceKey)) {
                tempServiceKey = serviceKey;
				break;
			}
		}
		if(tempServiceKey == null) {
			if (integrationTestCase.contains("flowDataSetup"))
                tempServiceKey = "validateCart";
			else if (integrationTestCase.contains("clearCheckoutData"))
                tempServiceKey = "validateCart";
			else if (integrationTestCase.contains("validatePreCheckout"))
                tempServiceKey = "validateCheckout";
			else if (integrationTestCase.contains("validateBusinessInfo"))
                tempServiceKey = "validatePersonalInfo";
			else if (integrationTestCase.contains("validateOrderSubmit"))
                tempServiceKey = "validateReview";
			else if (integrationTestCase.contains("validateThankYou"))
                tempServiceKey = "validateReview";
			else if (integrationTestCase.contains("validateBillingPreference"))
                tempServiceKey = "validateTerms";
			else if (integrationTestCase.contains("validateCloudData"))
				tempServiceKey = "validateCart";
		}
		return resultServiceKey = (services.contains(tempServiceKey))? tempServiceKey : null;
	}

	/**
	 * Gets the current profile.
	 *
	 * @return the current profile
	 */
	public static String getCurrentProfile() {
		final String currentEnv = System.getProperty(AbstractIntergrationTest.ITEST_ENV);
		if (currentEnv != null && Arrays.asList(AbstractIntergrationTest.PROFILES).contains(currentEnv)) {
			return currentEnv;
		} else {
			return "ccc";
		}
	}

	/**
	 * Gets the ignore.
	 *
	 * @return the ignore
	 */
	private boolean getIgnore() {
		try {
			// List<JsonPath> jsonPathList = getTestData(getTestName());
			// ignoreTest = jsonPathList.get(0).get(ignoreTestJsonKey);
		} catch (Exception e) {
			log.info("Test case exists and ignoreTest = false");
		}
		return ignoreTest;
	}

	public TestCases getDetails() {
		return details;
	}

	public void setDetails(TestCases details) {
		this.details = details;
	}

	/**
	 * Gets the test name.
	 *
	 * @return the test name
	 */
	public abstract String getTestName();

	public static void loadMultipleTestCases(List<TestCases> testCase, TestCases tCase, String replaceId){

		for(String orderId : orderIds.split(",")){
			TestCases temp = tCase;
			if(orderId != null && temp.getTestCases().get(temp.getTestCaseName()).getUrl().contains(replaceId)) {
				temp.getTestCases().get(temp.getTestCaseName()).setUrl(temp.getTestCases().get(temp.getTestCaseName()).getUrl().replace(replaceId, orderId));
				testCase.add(temp);
			}
			replaceId = orderId;

		}
	}

}
