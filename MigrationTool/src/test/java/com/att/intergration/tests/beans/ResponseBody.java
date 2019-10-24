package com.att.intergration.tests.beans;

import java.util.List;

public class ResponseBody {
	
	private List<JsonAssert> asserts;

	public List<JsonAssert> getAsserts() {
		return asserts;
	}

	public void setAsserts(List<JsonAssert> asserts) {
		this.asserts = asserts;
	}

}
