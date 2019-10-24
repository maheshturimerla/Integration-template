package com.att.ajsc.ccc.config.model;

import java.util.List;

public class AppConfigItem {
	private String id;
	private String key;
	private String displayName;
	private String valueKey;
	private String value;
	private List<String> customerType;
	private List<String> includedFlows;
	private List<String> includedSubFlows;
	private List<String> excludedSubFlows;
	private List<String> includedCommitmentTerms;
	private String startDate;
	private String endDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getValueKey() {
		return valueKey;
	}
	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public List<String> getCustomerType() {
		return customerType;
	}
	public void setCustomerType(List<String> customerType) {
		this.customerType = customerType;
	}
	public List<String> getIncludedFlows() {
		return includedFlows;
	}
	public void setIncludedFlows(List<String> includedFlows) {
		this.includedFlows = includedFlows;
	}
	public List<String> getIncludedSubFlows() {
		return includedSubFlows;
	}
	public void setIncludedSubFlows(List<String> includedSubFlows) {
		this.includedSubFlows = includedSubFlows;
	}
	public List<String> getExcludedSubFlows() {
		return excludedSubFlows;
	}
	public void setExcludedSubFlows(List<String> excludedSubFlows) {
		this.excludedSubFlows = excludedSubFlows;
	}
	public List<String> getIncludedCommitmentTerms() {
		return includedCommitmentTerms;
	}
	public void setIncludedCommitmentTerms(List<String> includedCommitmentTerms) {
		this.includedCommitmentTerms = includedCommitmentTerms;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
