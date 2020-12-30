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
			while((Long)executor.executeScript("return window.scrollY;") < (Long)executor.executeScript("return document.body.offsetHeight;") - (Long)executor.executeScript("return window.innerHeight;") - 10) {
				executor.executeScript("window.scrollBy(0,500)");
			}
		} catch(Exception e) {
			if(!ignoreException) {
				throw new BRException("Error scrolling down", e);
			}
		}
		return null;
	}

}
