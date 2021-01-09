package io.blackracoon.units;

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataList;
import io.firebus.utils.DataMap;

public class ExtractList extends Interpreter {

	public ExtractList(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		DataList resultList = new DataList();
		WebElement countElement = map.containsKey("countpath") ? getExactlyOneElement("countpath", context) : null;
		int maxPages = map.containsKey("maxpages") ? map.getNumber("maxpages").intValue() : 1;
		int page = 0;
		boolean noMorePages = false;
		int totalCount = -1;
		try {
			if(countElement != null) {
				totalCount = Integer.parseInt(countElement.getText()); 
			}
		} catch(Exception e) {}
		DataMap scroll = map.getObject("scroll");
		
		try {
			if(totalCount != 0) {
				while(!noMorePages && page < maxPages) {
					if(scroll != null) {
						ScrollDown sd = new ScrollDown(webContext, scroll);
						sd.exec(context);
					}
					List<WebElement> elements = getElements("itemspath", context);
					for(WebElement element: elements) {
						DataMap resultObject = new DataMap();
						DataList dataConfigList = map.getList("data");
						for(int i = 0; i < dataConfigList.size(); i++) {
							DataMap dataConfig = dataConfigList.getObject(i);
							ExtractData ed = new ExtractData(element, dataConfig);
							resultObject.merge((DataMap)ed.exec(context));					
						}	
						resultList.add(resultObject);
					}
					try {
						WebElement nextElement = getExactlyOneElement("nextpath", context);
						nextElement.click();
						page++;
					} catch(Exception e) {
						noMorePages = true;
					}				
				}
				
			}
		} catch(Exception e) {
			processException("Error extracting list", e);
		}
		return resultList;
	}

}
