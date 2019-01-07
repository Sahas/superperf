package com.turvo.perf.grafana.domain;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Series {
	String name;
	List<String> columns;
	@JsonProperty("tags")
	Map<String, String> tagNames;
	List<Object[]> values;
}
