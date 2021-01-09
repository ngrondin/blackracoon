package io.blackracoon.units;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class ScrollDown extends Interpreter {

	public ScrollDown(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		JavascriptExecutor executor = ((JavascriptExecutor) webContext);
		try {
			String to = map.getString("to");
			long delay = map.containsKey("delay") ? map.getNumber("delay").longValue() : 100;
			if(to.equalsIgnoreCase("bottom")) {
				while((Long)executor.executeScript("return window.scrollY;") < (Long)executor.executeScript("return document.body.offsetHeight;") - (Long)executor.executeScript("return window.innerHeight;") - 10) {
					executor.executeScript("window.scrollBy(0,500)");
					Thread.sleep(delay);
				}
			} else {
				int toInt = Integer.parseInt(to);
				for(int i = 0; i < (toInt / 500); i++) {
					executor.executeScript("window.scrollBy(0,500)");
					Thread.sleep(delay);
				}
			}
		} catch(Exception e) {
			processException("Error scrolling down", e);
		}
		return null;
	}

}
