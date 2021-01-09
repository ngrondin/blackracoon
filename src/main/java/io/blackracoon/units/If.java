package io.blackracoon.units;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class If extends Interpreter {

	public If(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		if(map.containsKey("then")) {
			boolean comp = false;
			try {
				List<WebElement> elements = getElements("path", context);				
				if(elements.size() == 0) {
					if(map.containsKey("value")) {
						String value = map.getString("value");
						if(value != null) {
							comp = true;
						} 
					} 					
				} else if(elements.size() == 1) {
					WebElement element = elements.get(0);
					if(map.containsKey("value")) {
						String value = map.getString("value");
						if(value != null && value.equalsIgnoreCase(element.getText())) {
							comp = true;
						} else if(value == null && element.getText().equals("")) {
							comp = true;
						}
					} else {
						comp = true;
					}					
				} else {
					throw new BRException("Found more than one element with the path");
				}		
				
				if(comp) {
					Stepper stepper = new Stepper(webContext, map.getObject("then"));
					return stepper.exec(context);
				} else {
					if(map.containsKey("else")) {
						Stepper stepper = new Stepper(webContext, map.getObject("else"));
						return stepper.exec(context);
					}
				}
			} catch(Exception e) {
				processException("Error evaluating If", e);
			}
		} else {
			throw new BRException("If needs at least a Then");
		}
		return null;
	}

}
