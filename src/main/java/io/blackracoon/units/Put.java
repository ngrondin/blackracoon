package io.blackracoon.units;

import org.openqa.selenium.SearchContext;

import io.blackracoon.BRException;
import io.blackracoon.Interpreter;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataMap;

public class Put extends Interpreter {

	public Put(SearchContext c, DataMap m) {
		super(c, m);
	}

	public DataEntity exec(DataMap context) throws BRException {
		String name = map.getString("name");
		Interpreter inter = null;
		if(map.containsKey("list")) {
			inter = new ExtractList(webContext, map.getObject("list"));
		}
		
		if(inter != null) {
			try {
				DataEntity de = inter.exec(context);
				if(de != null) {
					context.put(name, de);
				}
			} catch(Exception e) {
				processException("Error putting data", e);
			}
		} else {
			throw new BRException("Error putting data: no data to put");
		}
		return null;
	}

}
