package io.blackracoon.units;

import org.openqa.selenium.By;
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
			WebElement element = webContext.findElement(By.xpath(resolveConfig("path", context)));
			element.click();
		} catch(Exception e) {
			if(!ignoreException) {
				throw new BRException("Error clicking", e);
			}
		}
		return null;
	}

}
