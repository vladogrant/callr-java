package org.corefx.callr.client;

public @interface Authorized {
	String[] users() default {};
	String[] roles() default {};
}
