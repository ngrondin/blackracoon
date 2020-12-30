package io.blackracoon.units;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class SwitchNewTab extends Interpreter {

	public SwitchNewTab(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		if(webContext instanceof WebDriver) {
			try {
				boolean closeFirst = map.containsKey("closefirst") ? map.getBoolean("closefirst") : false;
				String[] tabs = ((WebDriver)webContext).getWindowHandles().toArray(new String[]{});
				if(closeFirst == true && tabs.length > 1) {
					((WebDriver)webContext).switchTo().window(tabs[0]).close();
				}
				((WebDriver)webContext).switchTo().window(tabs[tabs.length - 1]);
			} catch(Exception e) {
				if(!ignoreException) {
					throw new BRException("Error switching tabs", e);
				}
			}
			
		} else {
			throw new BRException("Error going to url: switchnewtab can only be used in the top web context");
		}
		return null;
	}

}
