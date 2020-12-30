package io.blackracoon.units;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataLiteral;
import io.firebus.utils.DataMap;

public class ExtractData extends Interpreter {

	public ExtractData(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context)  throws BRException {
		try {
			WebElement element = null;
			if(map.containsKey("path"))
				element = webContext.findElement(By.xpath(resolveConfig("path", context)));
			String name = resolveConfig("name", context);
			String valueType = resolveConfig("value", context);
			if(valueType == null)
				valueType = "text";
			DataLiteral value = null;
			
			if(valueType.equals("text") || valueType.equals("link")) {
				if(element != null) {
					if(valueType.equalsIgnoreCase("text"))
						value = new DataLiteral(element.getText());
					else if(valueType.equalsIgnoreCase("link"))
						value = new DataLiteral(element.getAttribute("href"));
				} else {
					throw new BRException("No path provided for value type " + valueType);
				}
			} else if(valueType.equals("clipboard")) {
				value = new DataLiteral(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
			}
			
			return new DataMap(name, value);
		} catch(Exception e) {
			if(!ignoreException) {
				throw new BRException("Error extracting data", e);
			} else {
				return null;
			}
		}
	}

}