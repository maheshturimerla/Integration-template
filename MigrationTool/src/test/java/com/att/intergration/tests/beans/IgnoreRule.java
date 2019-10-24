package com.att.intergration.tests.beans;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The Class will decide whether given test case can be ignored in a specific
 * environment.
 */
public class IgnoreRule implements TestRule {

	/** The ignore. */
	private boolean ignore;

	/**
	 * Instantiates a new ignore rule.
	 *
	 * @param ignore
	 *            the ignore
	 */
	public IgnoreRule(boolean ignore) {
		this.ignore = ignore;
	}

	/**
	 * This method checks whether test case need to be executed or not as per ignore
	 * value.
	 * 
	 * @param statement
	 *            the statement
	 * @param description
	 *            the description
	 */
	@Override
	public Statement apply(Statement statement, Description description) {
		if (ignore) {
			return new Statement() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.junit.runners.model.Statement#evaluate()
				 */
				@Override
				public void evaluate() throws Throwable {
					// empty
				}
			};
		} else {
			return statement;
		}
	}

}
