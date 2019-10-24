/**
 * 
 */
package com.att.intergration.tests.beans;

/**
 * @author sk4117
 *
 */
public class ITValidationResults {

	private String actualData;

	private String referanceData;

	private String validationType;

	private String errorMessage;
	
	private String status;

	private StackTraceElement[] stackTrace;

	/**
	 * @return the actualData
	 */
	public String getActualData() {
		return actualData;
	}

	/**
	 * @param actualData
	 *            the actualData to set
	 */
	public void setActualData(String actualData) {
		this.actualData = actualData;
	}

	/**
	 * @return the referanceData
	 */
	public String getReferanceData() {
		return referanceData;
	}

	/**
	 * @param referanceData
	 *            the referanceData to set
	 */
	public void setReferanceData(String referanceData) {
		this.referanceData = referanceData;
	}

	/**
	 * @return the validationType
	 */
	public String getValidationType() {
		return validationType;
	}

	/**
	 * @param validationType
	 *            the validationType to set
	 */
	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *            the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the stackTrace
	 */
	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	/**
	 * @param stackTrace
	 *            the stackTrace to set
	 */
	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[actualData=");
		builder.append(actualData);
		builder.append(",\n referanceData=");
		builder.append(referanceData);
		builder.append(",\n validationType=");
		builder.append(validationType);
		builder.append(",\n errorMessage=");
		builder.append(errorMessage);
		builder.append("]");
		return builder.toString();
	}

}
