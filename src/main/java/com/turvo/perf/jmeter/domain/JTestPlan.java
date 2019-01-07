package com.turvo.perf.jmeter.domain;

import java.util.List;

import lombok.Data;

@Data
public class JTestPlan {
	private String name;
	private List<JSampler> traffic;
}
