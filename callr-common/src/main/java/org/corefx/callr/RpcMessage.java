package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class RpcMessage extends CallRMessage {
	private UUID receiver;
	private UUID requestId;


	protected RpcMessage(UUID sender, UUID receiver, UUID requestId) {
		super(sender);
		this.receiver = receiver;
		this.requestId = requestId;
	}

}
