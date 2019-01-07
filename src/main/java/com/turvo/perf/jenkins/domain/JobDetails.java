package com.turvo.perf.jenkins.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDetails {
	private String id;
	private String name;
	private String currStatus;
	private int numRuns;
}
