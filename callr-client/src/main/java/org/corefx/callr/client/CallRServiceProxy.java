package org.corefx.callr.client;

import lombok.SneakyThrows;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.Parameter;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public abstract class CallRServiceProxy implements AutoCloseable {

	private final CallRClient client;

	private final UUID serviceId;

	private final Map<UUID, Object> waitHandles = new HashMap<>();

	private final Map<UUID, CompletableFuture<Object>> futures = new HashMap<>();

	private final Map<UUID, ResponseMessage> responseRegistry = new HashMap<>();


	protected CallRServiceProxy(UUID serviceId, CallRClient client) {
		this.serviceId = serviceId;
		this.client = client;
		client.onMessage(this::handleMessage);
		client.connect();
	}


	public void disconnect() {
		client.disconnect();
	}


	@Override
	public void close() {
		disconnect();
	}


	private void handleMessage(CallRMessage message) {
		ResponseMessage response = (ResponseMessage) message;
		if(waitHandles.containsKey(response.getRequest())) {
			responseRegistry.put(response.getRequest(), response);
			Object waitHandle = waitHandles.get(response.getRequest());
			synchronized(waitHandle) {
				waitHandle.notifyAll();
			}
		}
		else if(futures.containsKey(response.getRequest())) {
			complete(response);
		}
	}


	@SneakyThrows
	private void complete(ResponseMessage response) {
		CompletableFuture<Object> future = futures.get(response.getRequest());
		if(response.getExceptionData() != null) {
			try(ByteArrayInputStream ir = new ByteArrayInputStream(response.getExceptionData()); ObjectInputStream is = new ObjectInputStream(ir)) {
				//noinspection UnnecessaryLocalVariable
				Exception ex = (Exception) is.readObject();
				future.completeExceptionally(ex);
			}
		}
		future.complete(response.getResult());
		futures.remove(response.getRequest());
	}


	@SneakyThrows
	protected Object invoke(Parameter... parameters) {
		RequestMessage requestMessage = createRequestMessage(parameters);

		client.send(requestMessage);

		Object waitHandle = new Object();
		waitHandles.put(requestMessage.getRequest(), waitHandle);
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized(waitHandle) {
			waitHandle.wait();
			// waitHandle.wait(timeout);
		}
		waitHandles.remove(requestMessage.getRequest());

		ResponseMessage response = responseRegistry.get(requestMessage.getRequest());
		if(response == null)
			throw new TimeoutException();
		if(response.getExceptionData() != null) {
			try(ByteArrayInputStream ir = new ByteArrayInputStream(response.getExceptionData()); ObjectInputStream is = new ObjectInputStream(ir)) {
				//noinspection UnnecessaryLocalVariable
				Exception ex = (Exception) is.readObject();
				throw ex;
			}
		}
		return response.getResult();

	}


	@SneakyThrows
	protected CompletableFuture<Object> invokeAsync(Parameter... parameters) {
		RequestMessage requestMessage = createRequestMessage(parameters);

		CompletableFuture<Object> future = new CompletableFuture<>();
/*
		long timeout = 5000;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		future.orTimeout(timeout, timeUnit);
*/
		futures.put(requestMessage.getRequest(), future);

		client.send(requestMessage);

		return future;

	}


	private RequestMessage createRequestMessage(Parameter[] parameters) {
		UUID requestId = UUID.randomUUID();
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setReceiver(serviceId);
		requestMessage.setSender(client.getId());
		requestMessage.setRequest(requestId);
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String methodName = stackTraceElements[3].getMethodName();
		requestMessage.setOperation(methodName);
		requestMessage.getParameters().addAll(Arrays.stream(parameters).toList());
		return requestMessage;
	}
}
