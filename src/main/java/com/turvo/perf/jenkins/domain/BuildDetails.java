package com.turvo.perf.jenkins.domain;

import java.util.Map;

import lombok.Data;

@Data
public class BuildDetails {
	private String buildId;
	private String jenkinsBuildId;
	private String runName;
	private String status;
	private Map<String,String> parameters;
}
