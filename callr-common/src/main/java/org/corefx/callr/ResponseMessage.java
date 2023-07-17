package org.corefx.callr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResponseMessage extends RpcMessage {
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible = true)
	private Object result;
	/*
		private ExceptionInfo exception;
	*/
	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, visible = true)
	@JsonIgnoreProperties(ignoreUnknown = true)
	private Exception exception;


	public ResponseMessage(UUID sender, UUID receiver, UUID requestId) {
		super(sender, receiver, requestId);
	}

}