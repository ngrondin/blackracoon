package io.blackracoon;

import org.openqa.selenium.SearchContext;

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
		
	public abstract DataEntity exec(DataMap context) throws BRException;
}
