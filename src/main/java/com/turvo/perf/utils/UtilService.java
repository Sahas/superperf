package com.turvo.perf.utils;

import java.util.Map;

public class UtilService {
	
	public static StringBuilder getParameters(Map<String, Object> paramMap) {
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
			params.append(entry.getKey()).append(entry.getValue());

		}
		return params;
	}
}
