package org.corefx.callr.client;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public abstract class CallRServiceBase {

	CallRClient CallRClient;


	protected CallRServiceBase(CallRClient CallRClient) {
		this.CallRClient = CallRClient;
	}


	public void start() {
		CallRClient.onMessage(message -> {
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
			response.setSender(CallRClient.getId());
			response.setReceiver(request.getSender());
			response.setRequestId(request.getRequestId());
			try {
				Object result = method.invoke(this, parameterValues);
				response.setResult(result);
			}
			catch(Exception ex) {
				if(ex instanceof InvocationTargetException)
					ex = (Exception) ((InvocationTargetException)ex).getTargetException();
/*
				ExceptionInfo exi = new ExceptionInfo();
				String exceptionData;
				try {
					exceptionData = new ObjectMapper().writeValueAsString(ex);
				}
				catch(JsonProcessingException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				exi.setData(exceptionData);
				exi.setType(ex.getClass().getName());
				response.setException(exi);
*/
				response.setException(ex);
			}
			CallRClient.send(response);
		});
		CallRClient.connect();
	}


}
