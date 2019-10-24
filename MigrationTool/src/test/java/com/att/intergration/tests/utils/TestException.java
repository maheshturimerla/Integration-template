package com.att.intergration.tests.utils;

public class TestException extends RuntimeException {
	public TestException(String error) {
		super(error);
	}
	
	public TestException(String error, Throwable cause) {
		super(error, cause);
	}
}
