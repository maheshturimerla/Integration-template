/**
 * 
 */
package com.att.intergration.tests.beans;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.att.intergration.tests.utils.ValidationUtil;

/**
 * @author sk4117
 *
 */
public class ServiceDetails {

	private String testCaseName;

	private String url;

	private RequestMethod type;

	private Map<String, String> params;

	private Map<String, String> headers;

	private Map<String, String> cookies;
	
	private List<Map<String,String>> asserts; 
	
	private String body;
	private String requsstBodyReference;

	private String referenceData;

	private HttpStatus statusCode;

	private ValidationUtil.ValidationType validationType;

	private ServiceDetails serviceDetails;

	public String getServiceGroup() {
		return serviceGroup;
	}

	public void setServiceGroup(String serviceGroup) {
		this.serviceGroup = serviceGroup;
	}

	private String serviceGroup;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	private String hostName;

	public ValidationUtil.OrderIdType getOrderIdType() {
		return orderIdType;
	}

	public void setOrderIdType(ValidationUtil.OrderIdType orderIdType) {
		this.orderIdType = orderIdType;
	}

	private ValidationUtil.OrderIdType orderIdType;

	public String getRequsstBodyReference() {
		return requsstBodyReference;
	}

	public void setRequsstBodyReference(String requsstBodyReference) {
		this.requsstBodyReference = requsstBodyReference;
	}


	/**
	 * @return the param
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param param
	 *            the param to set
	 */
	public void setParams(Map<String, String> param) {
		this.params = param;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the statusCode
	 */
	public HttpStatus getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
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
	 * @return the serviceDetails
	 */
	public ServiceDetails getServiceDetails() {
		return serviceDetails;
	}

	/**
	 * @param serviceDetails
	 *            the serviceDetails to set
	 */
	public void setServiceDetails(ServiceDetails serviceDetails) {
		this.serviceDetails = serviceDetails;
	}

	/**
	 * @return the validationType
	 */
	public ValidationUtil.ValidationType getValidationType() {
		return validationType;
	}

	/**
	 * @param validationType
	 *            the validationType to set
	 */
	public void setValidationType(ValidationUtil.ValidationType validationType) {
		this.validationType = validationType;
	}

	/**
	 * @return the referenceData
	 */
	public String getReferenceData() {
		return referenceData;
	}

	/**
	 * @param referenceData
	 *            the referenceData to set
	 */
	public void setReferenceData(String referenceData) {
		this.referenceData = referenceData;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public List<Map<String, String>> getAsserts() {
		return asserts;
	}

	public void setAsserts(List<Map<String, String>> asserts) {
		this.asserts = asserts;
	}

}
