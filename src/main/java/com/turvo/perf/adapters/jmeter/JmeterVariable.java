package com.turvo.perf.adapters.jmeter;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class JmeterVariable {
	private String name;
	private VarType type;
	private Object value;
}
