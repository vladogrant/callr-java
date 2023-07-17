package org.corefx.callr;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Parameter {
	private String name;
	private String type;
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible = true)
	private Object value;
}
