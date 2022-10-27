package org.corefx.callr;

import lombok.Data;

@Data
public class Parameter {
	private String name;
	private String formalType;
	private String actualType;
	private Object value;
}
