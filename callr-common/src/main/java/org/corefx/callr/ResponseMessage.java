package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResponseMessage extends RpcMessage {
	private Object result;
	private String resultType;
	private ExceptionInfo exception;
}
