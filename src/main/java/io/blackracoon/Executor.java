package io.blackracoon;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.NoSuchElementException;
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
	protected DataMap map;
	protected WebDriver driver;
	public String id;

	
	public Executor(DataMap m) {
		map = m;
		id = UUID.randomUUID().toString();
	}
	
	public void run() {
		if(map != null && map.containsKey("steps")) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments(/*"--headless", */"--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
			driver = new ChromeDriver(options);
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

			System.out.println("Running");
			DataMap context = map.getObject("context");
			if(context == null)
				context = new DataMap();
			DataMap steps = map.getObject("steps");
			DataEntity result = null;
			String error = null;
			Stepper stepper = new Stepper(driver, steps);
			try {
				result = stepper.exec(context);
			} catch(Exception e) {
				TakesScreenshot scrShot =((TakesScreenshot)driver);
				File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
			    Path srcPath = srcFile.toPath();
			    Path copied = Paths.get("error_" + id + ".png");
			    try {
					Files.copy(srcPath, copied, StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {}
			    error = rollupExceptions(e);
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
					OutputStream os = con.getOutputStream();
					if(wrapper != null) 
						wrapper.write(os);
					else if(result instanceof DataMap) 
						((DataMap)result).write(os);
					else if(result instanceof DataList) 
						(new DataMap("result", result)).write(os);
					DataMap resp = new DataMap(con.getInputStream());
					System.out.println(resp);
				} catch(Exception e) {
					System.err.println("Error calling back: " + e.getMessage());
				}
			} else {
				System.out.println(result);
			}
			driver.close();
		}
	}
	
	protected String rollupExceptions(Throwable t) {
		StringBuilder sb = new StringBuilder();
		Throwable c = t;
		while(c != null) {
			if(sb.length() > 0)
				sb.append(": ");
			String msg = null;
			if(c instanceof NoSuchElementException) {
				msg = c.getMessage();
				if(msg.indexOf("\n") > -1)
					msg = msg.substring(0, msg.indexOf("\n")).trim();
			} else if(c instanceof NullPointerException) {
				msg = c.getMessage() + " [" + c.getStackTrace()[0].getFileName() + " at " + c.getStackTrace()[0].getLineNumber() + "]";
			} else {
				msg = c.getMessage();
			}
			sb.append(msg);
			c = c.getCause();
		}
		return sb.toString();
	}

}