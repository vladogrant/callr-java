package org.corefx.callr.client;
import lombok.SneakyThrows;
import org.corefx.callr.Parameter;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public abstract class CallRServiceProxy {

	private final CallRClient callRClient;

	private final UUID serviceId;

	private final Map<UUID, Object> waitHandles = new HashMap<>();

	private final Map<UUID, ResponseMessage> responseRegistry = new HashMap<>();


	protected CallRServiceProxy(UUID serviceId, CallRClient CallRClient) {
		this.serviceId = serviceId;
		this.callRClient = CallRClient;

		CallRClient.onMessage(message -> {
			ResponseMessage response = (ResponseMessage) message;
			if(!waitHandles.containsKey(response.getRequestId()))
				return;
			responseRegistry.put(response.getRequestId(), response);
			Object waitHandle = waitHandles.get(response.getRequestId());
			synchronized(waitHandle) {
				waitHandle.notifyAll();
			}
		});
		CallRClient.connect();
	}


	@SneakyThrows
	protected Object invoke(Parameter... parameters) {
		UUID requestId = UUID.randomUUID();
		RequestMessage request = new RequestMessage();
		request.setReceiver(serviceId);
		request.setSender(callRClient.getId());
		request.setRequestId(requestId);
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String methodName = stackTraceElements[2].getMethodName();
		request.setOperation(methodName);
		request.getParameters().addAll(Arrays.stream(parameters).toList());
		callRClient.send(request);
		Object waitHandle = new Object();
		waitHandles.put(requestId, waitHandle);
		synchronized(waitHandle) {
			waitHandle.wait();//git );
		}
		waitHandles.remove(requestId);
		ResponseMessage response = responseRegistry.get(requestId);
		if(response == null)
			throw new TimeoutException();
		if(response.getException() != null) {
			Exception ex;
/*
			String exceptionTypeName = response.getException().getType();
			Class<Exception> exceptionType = (Class<Exception>)Class.forName(exceptionTypeName);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ex = objectMapper.readValue(response.getException().getData(), exceptionType);
*/
			ex = response.getException();
			throw ex;
		}
		return response.getResult();
	}

}
