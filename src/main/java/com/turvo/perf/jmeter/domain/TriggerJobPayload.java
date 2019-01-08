package com.turvo.perf.jmeter.domain;

import java.util.Map;

import lombok.Data;

@Data
public class TriggerJobPayload {
	Map<String,String> triggerParameters;
	JTestPlan testPlan;
}
