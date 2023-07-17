package org.corefx.callr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
		@JsonSubTypes.Type(value = RequestMessage.class, name = "RequestMessage"),
		@JsonSubTypes.Type(value = ResponseMessage.class, name = "ResponseMessage")}
)
public class CallRMessage {
	private UUID sender;
}
