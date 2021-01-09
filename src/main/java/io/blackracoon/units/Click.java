package io.blackracoon.units;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class Click extends Interpreter {

	public Click(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		try {
			WebElement element = getExactlyOneElement("path", context);
			element.click();
		} catch(Exception e) {
			processException("Error clicking", e);
		}
		return null;
	}

}
