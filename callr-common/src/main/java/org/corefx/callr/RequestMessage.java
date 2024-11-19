package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestMessage extends RpcMessage {
	private String operation;
	private List<Parameter> parameters = new ArrayList<>();
	private List<String> authorities = new ArrayList<>();
}
