package org.openepics.discs.conf.export;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Preconditions;

public class Config {
	private final String driverClass;
	private final String url;
	private final String username;
	private final List<String> tableNames = new ArrayList<String>();


	public Config() {
		try {
			Properties prop = new Properties();
			prop.load(new FileInputStream("config.properties"));

			driverClass = prop.getProperty("driver_class");
			Preconditions.checkNotNull(driverClass);

			url = prop.getProperty("url");
			Preconditions.checkNotNull(url);

			username = prop.getProperty("username");
			Preconditions.checkNotNull(username);

			int counter = 1;
			String tableName;
			while(true) {
				tableName = prop.getProperty("table"+Integer.toString(counter++));
				if (tableName == null) break;
				tableNames.add(tableName);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load properties.", e);
		}
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getTableNames() {
		return tableNames;
	}
}
