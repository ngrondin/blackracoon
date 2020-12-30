package io.blackracoon.units;

import org.openqa.selenium.SearchContext;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataList;
import io.firebus.utils.DataLiteral;
import io.firebus.utils.DataMap;

public class ForEach extends Interpreter {

	public ForEach(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		DataList resultList = new DataList();
		String correlation = map.getString("correlation");
		DataEntity data = map.get("data");
		DataList list = null;
		if(data instanceof DataList) {
			list = (DataList)data;
		} else if(data instanceof DataLiteral) {
			list = context.getList(((DataLiteral)data).getString());
		}
		
		if(list != null) {
			for(int i = 0; i < list.size(); i++) {
				try {
					DataMap newContext = (DataMap)context.getCopy();
					newContext.merge(list.getObject(i));
					Stepper stepper = new Stepper(webContext, map.getObject("steps"));
					DataEntity out = stepper.exec(newContext);
					if(out != null) {
						if(out instanceof DataMap && correlation != null) {
							((DataMap)out).put(correlation, list.getObject(i).getString(correlation));
						}
						resultList.add(out);
					}
				} catch(Exception e) {
					if(!ignoreException) {
						throw new BRException("Error looping through list at item " + list.getObject(i).toString(0, true), e);
					}
				}
			}
		} else {
			throw new BRException("Error looping through list: no list found");
		}
		return resultList;
	}
	
}
