package io.blackracoon.units;

import org.openqa.selenium.SearchContext;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataList;
import io.firebus.utils.DataMap;

public class Stepper extends Interpreter {
	
	public Stepper(SearchContext c, DataMap s) {
		super(c, s);
	}
	
	public DataEntity exec(DataMap context) throws BRException {
		DataEntity result = null;
		int step = 0;
		while(map.containsKey(Integer.toString(step))) {
			DataMap stepMap = map.getObject(Integer.toString(step));
			Interpreter inter = null;
			if(stepMap.containsKey("goto")) {
				inter = new Goto(webContext, stepMap.getObject("goto")); 
			} else if(stepMap.containsKey("sendkeys")) {
				inter = new SendKeys(webContext, stepMap.getObject("sendkeys")); 
			} else if(stepMap.containsKey("click")) {
				inter = new Click(webContext, stepMap.getObject("click")); 
			} else if(stepMap.containsKey("list")) {
				inter = new ExtractList(webContext, stepMap.getObject("list")); 
			} else if(stepMap.containsKey("data")) {
				inter = new ExtractData(webContext, stepMap.getObject("data")); 
			} else if(stepMap.containsKey("firstoflist")) {
				inter = new ExtractFirstOfList(webContext, stepMap.getObject("firstoflist")); 
			} else if(stepMap.containsKey("foreach")) {
				inter = new ForEach(webContext, stepMap.getObject("foreach")); 
			} else if(stepMap.containsKey("scrolldown")) {
				inter = new ScrollDown(webContext, stepMap.getObject("scrolldown")); 
			} else if(stepMap.containsKey("put")) {
				inter = new Put(webContext, stepMap.getObject("put")); 
			} else if(stepMap.containsKey("switchtab")) {
				inter = new SwitchTab(webContext, stepMap.getObject("switchtab")); 
			} else if(stepMap.containsKey("assert")) {
				inter = new Assert(webContext, stepMap.getObject("assert")); 
			} 
			if(inter != null) {
				try {
					DataEntity de = inter.exec(context);
					if(de != null) {
						if(result == null) {
							result = de;
						} else if(result instanceof DataMap && de instanceof DataMap) {
							((DataMap)result).merge((DataMap)de);
						} else if(result instanceof DataList && de instanceof DataList) {
							DataList resList = (DataList)result;
							DataList list = (DataList)de;
							for(int i = 0; i < list.size(); i++) {
								resList.add(list.get(i));
							}
						}
					}
				} catch(Exception e) {
					processException("Error executing step " + step, e);
				}
			} else {
				throw new BRException("Error going through steps: no interpreter for step " + step);
			}
			step++;
		}				
		return result;

	}
}
