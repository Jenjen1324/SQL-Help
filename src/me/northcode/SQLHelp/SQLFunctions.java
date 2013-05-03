package me.northcode.SQLHelp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;

public class SQLFunctions {
	private static SQLHelp plugin;
	private static Database sql;

	public SQLFunctions(SQLHelp plugin, String prefix, String host, int port,
			String database, String username, String password) {
		SQLFunctions.plugin = plugin;
		sql = new MySQL(SQLFunctions.plugin.getLogger(), prefix, host, port, database,
				username, password);
		connect();
	}

	public void connect() {
		if (!sql.isOpen())
			sql.open();
	}

	public void close() {
		if (sql.isOpen())
			sql.close();
	}

	private void close(PreparedStatement statement) {
		if (statement != null)
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	private void close(ResultSet results) {
		if (results != null)
			try {
				results.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public void createTable() {
		if (!sql.isTable("SQLHelp")) {
			String query = "CREATE  TABLE SQLHelp (id INT NOT NULL AUTO_INCREMENT,page VARCHAR(45),content TEXT,PRIMARY KEY (id))";
			PreparedStatement statement = null;
			try {
				statement = sql.prepare(query);
				sql.query(statement);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				close(statement);
			}
		}
	}

	public List<String> select(String page)
			throws SQLException {
		PreparedStatement statement = null;
		ResultSet results = null;
		try {
			String query = "SELECT * FROM SQLHelp WHERE page=?";
			statement = sql.prepare(query);
			statement.setString(1, page);

			results = sql.query(statement);

			List<String> flags = new ArrayList<String>();
			while (results.next()) {
				flags.add(results.getString("content"));
			}

			return flags;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(statement);
			close(results);
		}

		return null;
	}
}
