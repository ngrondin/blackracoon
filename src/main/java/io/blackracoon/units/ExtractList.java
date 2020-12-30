package io.blackracoon.units;

import java.util.List;

import org.openqa.selenium.By;
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
		WebElement countElement = map.containsKey("countpath") ? webContext.findElement(By.xpath(resolveConfig("countpath", context))) : null;
		int maxPages = map.containsKey("maxpages") ? map.getNumber("maxpages").intValue() : 1;
		int page = 0;
		boolean noMorePages = false;
		int totalCount = -1;
		try {
			if(countElement != null) {
				totalCount = Integer.parseInt(countElement.getText()); 
			}
		} catch(Exception e) {}
		boolean scrollDown = map.containsKey("scrolldown") ? map.getBoolean("scrolldown") : false;
		
		try {
			if(totalCount != 0) {
				while(!noMorePages && page < maxPages) {
					if(scrollDown) {
						ScrollDown sd = new ScrollDown(webContext, null);
						sd.exec(context);
					}
					List<WebElement> elements = webContext.findElements(By.xpath(resolveConfig("itemspath", context)));
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
						WebElement nextElement = map.containsKey("nextpath") ? webContext.findElement(By.xpath(resolveConfig("nextpath", context))) : null;
						nextElement.click();
						page++;
					} catch(Exception e) {
						noMorePages = true;
					}				
				}
				
			}
		} catch(Exception e) {
			if(!ignoreException) {
				throw new BRException("Error extracting list", e);
			}
		}
		return resultList;
	}

}
