package io.blackracoon.units;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class Assert extends Interpreter {

	public Assert(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		try {
			WebElement element = null;
			if(map.containsKey("path")) {
				element = webContext.findElement(By.xpath(map.getString("path")));
				if(map.containsKey("value")) {
					String value = map.getString("value");
					if(!element.getText().equals(value)) {
						throw new BRException("Value is not equal");
					}
				}
			} else {
				throw new BRException("No path provided");
			}			
		} catch(Exception e) {
			processException("Assertion Error", e);
		}
		return null;
	}

}
