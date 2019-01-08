package com.turvo.perf.jmeter.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class JSampler {
	private String label;
	private String url;
	private String method;
	
	@JsonProperty("request_type")
	private String requestType;
	
	@JsonProperty("request_subtype")
	private String requestSubtype;
	
	private Long timestamp;
	private List<JSamplerHeader> headers;
	
	// Problem with jackson JsonNode
	//@JsonProperty(access = Access.READ_ONLY)
	private JsonNode body;
	
}
