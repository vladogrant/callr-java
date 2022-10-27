package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
public class RequestMessage extends RpcMessage {
	private String operation;
	private ArrayList<Parameter> parameters = new ArrayList<>();
}
