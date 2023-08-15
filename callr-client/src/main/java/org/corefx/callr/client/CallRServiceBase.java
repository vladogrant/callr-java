package org.corefx.callr.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public abstract class CallRServiceBase {

	CallRClient client;


	protected CallRServiceBase(CallRClient client) {
		this.client = client;
	}


	public void start() {
		client.onMessage(message -> {
			RequestMessage request = (RequestMessage) message;
			Class<?>[] parameterTypes = new Class<?>[request.getParameters().size()];
			for(int i = 0; i < request.getParameters().size(); i++) {
				try {
					parameterTypes[i] = Class.forName(request.getParameters().get(i).getType());
				}
				catch(ClassNotFoundException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}
			Method method;
			try {
				method = this.getClass().getMethod(request.getOperation(), parameterTypes);
			}
			catch(NoSuchMethodException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			Object[] parameterValues = request.getParameters().stream().map(p -> p.getValue()).toArray();
			ResponseMessage response = new ResponseMessage();
			response.setSender(client.getId());
			response.setReceiver(request.getSender());
			response.setRequest(request.getRequest());
			try {
				Object result = method.invoke(this, parameterValues);
				response.setResult(result);
			}
			catch(Exception ex) {
				if(ex instanceof InvocationTargetException)
					ex = (Exception) ((InvocationTargetException)ex).getTargetException();
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
		});
		client.connect();
	}


}
