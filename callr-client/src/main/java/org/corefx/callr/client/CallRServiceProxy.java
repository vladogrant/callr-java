package org.corefx.callr.client;
import lombok.SneakyThrows;
import org.corefx.callr.Parameter;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public abstract class CallRServiceProxy {

	private final CallRClient client;

	private final UUID serviceId;

	private final Map<UUID, Object> waitHandles = new HashMap<>();

	private final Map<UUID, ResponseMessage> responseRegistry = new HashMap<>();


	protected CallRServiceProxy(UUID serviceId, CallRClient client) {
		this.serviceId = serviceId;
		this.client = client;

		client.onMessage(message -> {
			ResponseMessage response = (ResponseMessage) message;
			if(!waitHandles.containsKey(response.getRequest()))
				return;
			responseRegistry.put(response.getRequest(), response);
			Object waitHandle = waitHandles.get(response.getRequest());
			synchronized(waitHandle) {
				waitHandle.notifyAll();
			}
		});
		client.connect();
	}


	@SneakyThrows
	protected Object invoke(Parameter... parameters) {
		UUID requestId = UUID.randomUUID();
		RequestMessage request = new RequestMessage();
		request.setReceiver(serviceId);
		request.setSender(client.getId());
		request.setRequest(requestId);
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String methodName = stackTraceElements[2].getMethodName();
		request.setOperation(methodName);
		request.getParameters().addAll(Arrays.stream(parameters).toList());
		client.send(request);
		Object waitHandle = new Object();
		waitHandles.put(requestId, waitHandle);
		synchronized(waitHandle) {
			waitHandle.wait();
		}
		waitHandles.remove(requestId);
		ResponseMessage response = responseRegistry.get(requestId);
		if(response == null)
			throw new TimeoutException();
		if(response.getExceptionData() != null) {
			try(ByteArrayInputStream ir = new ByteArrayInputStream(response.getExceptionData()); ObjectInputStream is = new ObjectInputStream(ir)) {
				Exception ex = (Exception) is.readObject();
				throw ex;
			}
		}
		return response.getResult();
	}

}
