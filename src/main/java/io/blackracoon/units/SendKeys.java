package io.blackracoon.units;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class SendKeys extends Interpreter {

	public SendKeys(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		try {
			WebElement element = webContext.findElement(By.xpath(resolveConfig("path", context)));
			element.sendKeys(resolveConfig("text", context));
			if(map.getBoolean("typeenter"))
				element.sendKeys(Keys.ENTER);
		} catch(Exception e) {
			processException("Error sending keys", e);
		}
		return null;
	}

}
