package com.att.intergration.tests.beans;

import java.util.List;
import java.util.Map;

public class TestResults {
	private String testCaseName;
	private Map<String, List<String>> response;

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public Map<String, List<String>> getResponse() {
		return response;
	}

	public void setResponse(Map<String, List<String>> response) {
		this.response = response;
	}

}
