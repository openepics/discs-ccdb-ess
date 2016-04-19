package org.openepics.discs.conf.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class DbExporter {

	public static void main(String[] args) throws ClassNotFoundException,
			SQLException, FileNotFoundException, IOException, DatabaseUnitException {
		// Load configuration from property file
		Config config = new Config();

		// Load JDBC driver
		Class.forName(config.getDriverClass());

		// Get password
		System.out.print("Please enter the database password: ");
		final String password = new String(System.console().readPassword());


		Connection jdbcConnection = DriverManager.getConnection(
				config.getUrl(), config.getUsername(), password);
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		(new File("output")).mkdir();

		for (String table : config.getTableNames()) {
			QueryDataSet partialDataSet = new QueryDataSet(connection);

			partialDataSet.addTable(table);

			// XML file into which data needs to be extracted
			FlatXmlDataSet.write(partialDataSet, new FileOutputStream(
					"output/"+table+".xml"));
		}
		System.out.println("Datasets written");

		connection.close();
	}
}