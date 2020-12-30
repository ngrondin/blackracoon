package io.blackracoon;

public class BRException extends Exception{
	private static final long serialVersionUID = 1L;

	public BRException(String msg) {
		super(msg);
	}
	
	public BRException(String msg, Throwable t) {
		super(msg, t);
	}
}
