package io.blackracoon.units;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class SwitchTab extends Interpreter {

	public SwitchTab(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		if(webContext instanceof WebDriver) {
			try {
				WebDriver driver = ((WebDriver)webContext);
				boolean closePrevious = map.containsKey("closeprevious") ? map.getBoolean("closeprevious") : false;
				String toTab = map.getString("tab");
				String[] tabs = driver.getWindowHandles().toArray(new String[]{});
				String currentTab = driver.getWindowHandle();
				String nextTab = null;
				int toTabInt = -1;
				try {
					toTabInt = Integer.parseInt(toTab);
				} catch(Exception e) {}
				if(toTab.equals("last"))
					nextTab = tabs[tabs.length - 1];
				else if(toTab.equals("first"))
					nextTab = tabs[0];
				else if(toTabInt > -1 && toTabInt < tabs.length) 
					nextTab = tabs[toTabInt];
				
				if(closePrevious == true && !nextTab.equals(currentTab)) {
					driver.switchTo().window(currentTab).close();
				}
				driver.switchTo().window(nextTab);
			} catch(Exception e) {
				processException("Error switching tabs", e);
			}
			
		} else {
			throw new BRException("Error going to url: switchnewtab can only be used in the top web context");
		}
		return null;
	}

}
