package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestMessage extends RpcMessage {
	private String operation;
	private ArrayList<Parameter> parameters = new ArrayList<>();


	public RequestMessage(UUID sender, UUID receiver, UUID requestId) {
		super(sender, receiver, requestId);
	}
}
