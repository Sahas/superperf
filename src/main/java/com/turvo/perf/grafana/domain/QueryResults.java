package com.turvo.perf.grafana.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QueryResults {
	@JsonProperty("results")
	List<StatementResult> statementResults;
}
