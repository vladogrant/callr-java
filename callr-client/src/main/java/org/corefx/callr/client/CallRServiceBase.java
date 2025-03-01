package org.corefx.callr.client;

import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Slf4j
public abstract class CallRServiceBase implements AutoCloseable {

	CallRClient client;


	protected CallRServiceBase(CallRClient client) {
		this.client = client;
	}


	public void start() {
		client.onMessage(this::handleMessage);
		client.connect();
	}


	public void stop() {
	}


	@Override
	public void close() {
		client.disconnect();
		stop();
	}


	private void handleMessage(CallRMessage message) {
		if(RequestMessage.class.equals(message.getClass())) {
			handleRequestMessage((RequestMessage) message);
		}
	}


	private void handleRequestMessage(RequestMessage request) {
		ResponseMessage response = new ResponseMessage();
		response.setSender(client.getId());
		response.setReceiver(request.getSender());
		response.setRequest(request.getRequest());

		Class<?>[] parameterTypes = new Class<?>[request.getParameters().size()];
		try {
			for(int i = 0; i < request.getParameters().size(); i++) {
				parameterTypes[i] = Class.forName(request.getParameters().get(i).getType());
			}
			Method method = this.getClass().getMethod(request.getOperation(), parameterTypes);

			Authorized authorized = method.getAnnotation(Authorized.class);
			if(authorized != null && authorized.roles() != null) {
				List<String> roles = Stream.of(authorized.roles()).map(String::toUpperCase).toList();
				List<String> authorities = request.getAuthorities();
				if(authorities == null || authorities.isEmpty()) {
					throw new NotAuthorizedException();
				}
				authorities = authorities.stream().map(a -> a.toUpperCase().replaceFirst("^ROLE_", "")).toList();
				if(authorities.stream().noneMatch(roles::contains)) {
					throw new NotAuthorizedException();
				}
			}

			Object[] parameterValues = request.getParameters().stream().map(Parameter::getValue).toArray();
			Object result = method.invoke(this, parameterValues);

			if(Future.class.isAssignableFrom(method.getReturnType())) {
				@SuppressWarnings("unchecked")
				Future<Object> future = (Future<Object>) result;
				result = future.get();
			}
			response.setResult(result);
		}
		catch(Exception thrown) {
			Exception ex = thrown;
			if(ex instanceof InvocationTargetException)
				ex = (Exception) ((InvocationTargetException) ex).getTargetException();
			else if(ex instanceof ExecutionException)
				ex = (Exception) ex.getCause();
			log.error(ex.getMessage(), ex);
			response.setException(ex);
			try(ByteArrayOutputStream os = new ByteArrayOutputStream(); ObjectOutputStream ow = new ObjectOutputStream(os)) {
				ow.writeObject(ex);
				response.setExceptionData(os.toByteArray());
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		client.send(response);
	}
}
