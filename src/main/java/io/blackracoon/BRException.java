package io.blackracoon;

import org.openqa.selenium.WebDriverException;

public class BRException extends Exception{
	private static final long serialVersionUID = 1L;

	public BRException(String msg) {
		super(msg);
	}
	
	public BRException(String msg, Throwable t) {
		super(msg, t);
	}
	
	
	protected String rollupExceptions() {
		StringBuilder sb = new StringBuilder();
		Throwable c = this;
		while(c != null) {
			if(sb.length() > 0)
				sb.append(": ");
			String msg = null;
			if(c instanceof WebDriverException) {
				msg = c.getMessage();
				if(msg.indexOf("\n") > -1)
					msg = msg.substring(0, msg.indexOf("\n")).trim();
			} else if(c instanceof NullPointerException) {
				msg = c.getMessage() + " [" + c.getStackTrace()[0].getFileName() + " at " + c.getStackTrace()[0].getLineNumber() + "]";
			} else {
				msg = c.getMessage();
			}
			sb.append(msg);
			c = c.getCause();
		}
		return sb.toString();
	}
}
