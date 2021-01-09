package io.blackracoon.units;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataList;
import io.firebus.utils.DataMap;

public class ExtractFirstOfList extends Interpreter {

	public ExtractFirstOfList(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		WebElement countElement = map.containsKey("countpath") ? getExactlyOneElement("countpath", context) : null;
		int totalCount = -1;
		try {
			if(countElement != null) {
				totalCount = Integer.parseInt(countElement.getText()); 
			}
		} catch(Exception e) {}
		boolean scrollDown = map.containsKey("scrolldown") ? map.getBoolean("scrolldown") : false;
		
		try {
			if(totalCount != 0) {
				if(scrollDown) {
					ScrollDown sd = new ScrollDown(webContext, null);
					sd.exec(context);
				}
				List<WebElement> elements = getElements("itemspath", context);
				if(elements != null && elements.size() > 0) {
					WebElement element = elements.get(0);
					DataMap resultObject = new DataMap();
					DataList dataConfigList = map.getList("data");
					for(int i = 0; i < dataConfigList.size(); i++) {
						DataMap dataItemConfig = dataConfigList.getObject(i);
						ExtractData ed = new ExtractData(element, dataItemConfig);
						resultObject.merge((DataMap)ed.exec(context));					
					}	
					return resultObject;
				}
				
			}
		} catch(Exception e) {
			processException("Error extracting first of list", e);
		}
		return null;
	}

}
