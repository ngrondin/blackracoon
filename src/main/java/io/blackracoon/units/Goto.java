package io.blackracoon.units;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class Goto extends Interpreter {
	
	public Goto(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		if(webContext instanceof WebDriver) {
			try {
				((WebDriver)webContext).get(resolveConfig("url", context));
			} catch(Exception e) {
				processException("Error going to url", e);
			}
		} else {
			throw new BRException("Error going to url: goto can only be used in the top web context");
		}
		return null;
	}
	
	
}
