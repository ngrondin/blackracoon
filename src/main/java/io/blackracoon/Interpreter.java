package io.blackracoon;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public abstract class Interpreter {
	protected DataMap map;
	protected SearchContext webContext;
	protected boolean ignoreException;

	
	public Interpreter(SearchContext c, DataMap m) {
		map = m;
		webContext = c;
		ignoreException = map != null && map.containsKey("ignoreexception") ? map.getBoolean("ignoreexception") : false;
	}
	
	protected String resolveConfig(String attribute, DataMap context) {
		String str = map.getString(attribute);
		if(str != null) {
			int pos1 = -1;
			while((pos1 = str.indexOf("${")) > -1) {
				int pos2 = str.indexOf("}", pos1);
				String key = str.substring(pos1 + 2, pos2);
				str = str.substring(0, pos1) + context.getString(key) + str.substring(pos2 + 1);
			}
		}
		return str;
	}
	
	protected List<WebElement> getElements(String pathkey, DataMap context) throws BRException {
		List<WebElement> elements = null;
		if(map.containsKey(pathkey)) {
			try {
				elements = webContext.findElements(By.xpath(resolveConfig(pathkey, context)));
			} catch(NoSuchElementException e) {
				elements = new ArrayList<WebElement>();
			} catch(Exception e) {
				throw new BRException("Error getting elements", e);
			}
		}
		return elements;
	}
	
	protected WebElement getExactlyOneElement(String pathkey, DataMap context) throws BRException {
		List<WebElement> elements = getElements(pathkey, context);
		if(elements.size() == 0) {
			throw new BRException("No element found");
		} else if(elements.size() == 1) {
			return elements.get(0);
		} else {
			throw new BRException("More than one element found");
		}
	}
	
	protected void processException(String msg, Exception e) throws BRException {
		BRException bre = new BRException(msg, e);
		if(ignoreException) {
			System.out.println("Ignored exception: " + bre.rollupExceptions());
		} else {
			throw bre;
		}
	}
		
	public abstract DataEntity exec(DataMap context) throws BRException;
}
