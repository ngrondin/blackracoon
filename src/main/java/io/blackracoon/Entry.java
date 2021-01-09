package io.blackracoon;

import io.firebus.utils.DataMap;

public class Entry {
	public String id;
	public DataMap map;
	
	public Entry(String i, DataMap m) {
		id = i;
		map = m;
	}
}
