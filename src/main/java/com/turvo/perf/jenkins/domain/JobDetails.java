package com.turvo.perf.jenkins.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.turvo.perf.jmeter.domain.JTestPlan;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection="jobs")
public class JobDetails {
	@Id 
	private String jobId;
	private String scriptName;
	private String scriptLocation;
	private String name;
	private String numRuns;
	private JTestPlan testPlan;
	private List<BuildDetails> builds;
	
	public JobDetails jobId(String jobId) {
		this.jobId = jobId;
		return this;
	}
	
	public JobDetails scriptName(String scriptName) {
		this.scriptName = scriptName;
		return this;
	}
	
	public JobDetails scriptLocation(String scriptLocation) {
		this.scriptLocation = scriptLocation;
		return this;
	}
	
	public JobDetails name(String name) {
		this.name = name;
		return this;
	}
	
	public JobDetails testPlan(JTestPlan testPlan) {
		this.testPlan = testPlan;
		return this;
	}
	
	public JobDetails() {}
	
//	@PersistenceConstructor
//	public JobDetails(String jobId, String scriptName,String name, String ) {
//		
//	}
}
