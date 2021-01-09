package io.blackracoon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.blackracoon.units.Stepper;
import io.firebus.utils.DataEntity;
import io.firebus.utils.DataList;
import io.firebus.utils.DataMap;

public class Executor extends Thread {
	
	protected static Executor singleton;
	protected static boolean headless = true;
	
	List<Entry> entries;
	protected boolean started;

	public static Executor getSingleton() {
		if(singleton == null) {
			singleton = new Executor();
		}
		return singleton;
	}
	
	public Executor() {
		entries = new ArrayList<Entry>();
		started = false;
	}
	
	public String schedule(DataMap map) {
		String id = UUID.randomUUID().toString();
		Entry entry = new Entry(id, map);
		entries.add(entry);
		if(!started) {
			started = true;
			start();
		}
		return id;
	}
	
	protected void execute(String id, DataMap map) {
		WebDriver driver = null;
		if(map != null && map.containsKey("steps")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
			if(headless) 
				options.addArguments("--headless");
			driver = new ChromeDriver(options);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			System.out.println("Running " + id);
			System.out.println(map);

			DataMap context = map.getObject("context");
			if(context == null)
				context = new DataMap();
			DataMap steps = map.getObject("steps");
			DataEntity result = null;
			String error = null;
			Stepper stepper = new Stepper(driver, steps);
			try {
				result = stepper.exec(context);
			} catch(BRException e) {
				try {
					TakesScreenshot scrShot =((TakesScreenshot)driver);
					File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
				    Path srcPath = srcFile.toPath();
				    Path copied = Paths.get("error_" + id + ".png");
				    try {
						Files.copy(srcPath, copied, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {}
				} catch(Exception e2) {}
				
			    error = e.rollupExceptions();
				System.err.println(error);
			}
			
			if(map.containsKey("callback")) {
				try {
					DataMap callBackMap = map.getObject("callback");
					URL url = new URL(callBackMap.getString("url"));
					DataMap wrapper = callBackMap.getObject("wrapper");
					if(wrapper != null) {
						Iterator<String> it = wrapper.keySet().iterator();
						while(it.hasNext()) {
							String key = it.next();
							String value = wrapper.getString(key);
							if(value.equals("$result")) {
								if(result != null) {
									wrapper.put(key, result);
								} else {
									wrapper.remove(key);
								}
							} else if(value.equals("$error")) {
								if(error != null) {
									wrapper.put(key, error);
								} else {
									wrapper.remove(key);
								}
							}
						}
					}
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json");
					if(callBackMap.containsKey("cookie")) {
						con.setRequestProperty("Cookie", callBackMap.getString("cookie"));
					}
					if(callBackMap.containsKey("auth")) {
						con.setRequestProperty("Authorization", callBackMap.getString("auth"));
					}					
					con.setDoOutput(true);
					con.setDoInput(true);
					OutputStream os = con.getOutputStream();
					if(wrapper != null) 
						wrapper.write(os);
					else if(result instanceof DataMap) 
						((DataMap)result).write(os);
					else if(result instanceof DataList) 
						(new DataMap("result", result)).write(os);
					InputStream is = con.getInputStream();
					byte[] bytes = is.readAllBytes();
					System.out.println(new String(bytes));
					con.disconnect();
				} catch(Exception e) {
					System.err.println("Error calling back: " + e.getMessage());
				}
			} 
			System.out.println("Result of " + id);
			System.out.println(result);
			
			driver.close();
			driver.quit();
		}
	}
	
	public void run() {
		while(entries.size() > 0) {
			Entry entry = entries.get(0);
			execute(entry.id, entry.map);
			entries.remove(0);
		}
		singleton = null;
	}
	


}
