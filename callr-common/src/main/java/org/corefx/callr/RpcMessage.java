package org.corefx.callr;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class RpcMessage extends SenderMessage {
	private UUID receiver;
	private UUID requestId;
}
