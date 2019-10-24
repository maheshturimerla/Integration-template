/**
 * 
 */
package com.att.intergration.tests.beans;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author sk4117
 *
 */
public class TestCases {

	private String testCaseName;

	private String url;

    private String testGroup;
	private RequestMethod type;

	Map<String, ServiceDetails> testCases;

	public String getTestGroup() {
		return testGroup;
	}

	public void setTestGroup(String testGroup) {
		this.testGroup = testGroup;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the type
	 */
	public RequestMethod getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(RequestMethod type) {
		this.type = type;
	}

	/**
	 * @return the testCaseName
	 */
	public String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * @param testCaseName
	 *            the testCaseName to set
	 */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	/**
	 * @return the testCases
	 */
	public Map<String, ServiceDetails> getTestCases() {
		return testCases;
	}

	/**
	 * @param testCases
	 *            the testCases to set
	 */
	public void setTestCases(Map<String, ServiceDetails> testCases) {
		this.testCases = testCases;
	}

	@Override
	public String toString() {
		return testCaseName;
	}
}
