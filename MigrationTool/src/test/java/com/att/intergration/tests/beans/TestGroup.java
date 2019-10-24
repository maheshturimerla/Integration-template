package com.att.intergration.tests.beans;

import java.util.List;

public class TestGroup {
	
	private String name;
	private boolean skip;
	private List<RestTest> tests;

	public List<RestTest> getTests() {
		return tests;
	}

	public void setTests(List<RestTest> tests) {
		this.tests = tests;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

}
