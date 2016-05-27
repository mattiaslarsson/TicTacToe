package dao;

import models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Database connection and operations class.
 * <p>
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-25.
 */

public class DatabaseConnector {

	private Connection conn = null;

	/*******************************************************************************************************************
	 * INIT AND STANDARD OPERATIONS
	 ******************************************************************************************************************/

	/**
	 * Initializes database connection.
	 */
	public DatabaseConnector() {
		// db parameters
		String url = "jdbc:sqlite:game.db";
		// create a connection to the database
		try {
			conn = DriverManager.getConnection(url);
			System.out.println("Database connection established.");
		} catch (SQLException e) {
			System.out.println("Error connecting to database: " + e.getStackTrace());
		}
	}

	/**
	 * Closes database connection.
	 */
	public void closeConnection() {
		try {
			conn.close();
			System.out.println("Database connection closed.");
		} catch (SQLException e) {
			System.out.println("Error closing database connection: " + e.getStackTrace());
		}

	}

	/**
	 * Executes a SQL-query that doesn't return useful information.
	 *
	 * @param sql String
	 */
	private void executeSQL(String sql) {
		if (conn != null) {
			Statement stmnt = null;
			try {
				stmnt = conn.createStatement();
				stmnt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (stmnt != null) {
						stmnt.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("No connection to database.");
		}
	}

	/**
	 * Executes a SQL-query that returns information from the database.
	 *
	 * @param sql String
	 * @return ArrayList<HashMap>
	 */
	private ArrayList executeSQLQuery(String sql) {
		ArrayList returnList = null;
		Statement stmnt = null;
		ResultSet currRs = null;
		if (conn != null) {
			try {
				stmnt = conn.createStatement();
				currRs = stmnt.executeQuery(sql);
				returnList = resultSetToArrayList(currRs);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (stmnt != null) {
					try {
						stmnt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (currRs != null) {
					try {
						currRs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("No connection to database.");
		}
		return returnList;
	}

	/**
	 * Converts a resultset to a ArrayList of HashMaps.
	 *
	 * @param rs ResultSet
	 * @return ArrayList<HashMap>
	 */
	private ArrayList resultSetToArrayList(ResultSet rs) {
		ResultSetMetaData md = null;
		ArrayList list = null;
		try {
			md = rs.getMetaData();
			int columns = md.getColumnCount();
			list = new ArrayList(50);
			while (rs.next()) {
				HashMap row = new HashMap(columns);
				for (int i = 1; i <= columns; ++i) {
					row.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/*******************************************************************************************************************
	 * OPERATION METHODS
	 ******************************************************************************************************************/

	/**
	 * Checks if myPlayer-table contains entries and if not intializes database tables.
	 *
	 * @return boolean
	 */
	public boolean firstRun() {
		boolean isFirst = false;
		String sqlQ = "SELECT count(*) AS number FROM sqlite_master WHERE type='table' AND name='myPlayer';";
		ArrayList results = executeSQLQuery(sqlQ);
		Map currRow = (HashMap) results.get(0);
		int number = (int) currRow.get("number");

		if (number > 0) {
			// Earlier player found - no action.
		} else {
			// First run. Create database.
			initDB();
			isFirst = true;
		}
		return isFirst;
	}

	/**
	 * Creates the tables.
	 */
	private void initDB() {
		String sqlInit[] = {
				"CREATE TABLE IF NOT EXISTS myPlayer (id INTEGER NOT NULL PRIMARY KEY,firstName TEXT,surName TEXT,rank INTEGER);",
				"CREATE TABLE IF NOT EXISTS players (id INTEGER NOT NULL PRIMARY KEY,firstName TEXT,surName INTEGER,rank INTEGER);",
				"CREATE TABLE IF NOT EXISTS matches (id INTEGER PRIMARY KEY,opponent INTEGER,points INTEGER, opppoints INTEGER, startTime INTEGER,endTime INTEGER,gridSize INTEGER,numMoves INTEGER,FOREIGN KEY (opponent) REFERENCES players(id));"
		};
		for (String currSQL : sqlInit) {
			executeSQL(currSQL);
		}
	}

	/**
	 * Creates the own player entry.
	 *
	 * @param firstName String
	 * @param surName   String
	 */
	public void createOwnPlayer(String firstName, String surName) {
		// Make unique id
		long id = (System.currentTimeMillis() << 20) | (System.nanoTime() & ~9223372036854251520L);
		insertPlayer("myPlayer", firstName, surName, 0, id);
	}

	/**
	 * Gets the own player from database.
	 *
	 * @return Player
	 */
	public Player getOwnPlayer() {
		String sql = "SELECT * FROM myPlayer;";
		ArrayList result = executeSQLQuery(sql);
		HashMap currMap = (HashMap) result.get(0);
		String firstName = (String) currMap.get("firstName");
		String surName = (String) currMap.get("surName");
		int rank = (int) currMap.get("rank");
		long id = (long) currMap.get("id");
		Player currPlayer = new Player(firstName, surName, id, rank);
		return currPlayer;
	}

	/**
	 * Updates own player.
	 *
	 * @param ownPlayer Player
	 */
	public void updateOwnPlayer(Player ownPlayer) {
		String updateSql = "UPDATE players SET firstName =\"" + ownPlayer.getFirstName() + "\", surName = \"" + ownPlayer.getSurName() + "\", rank =" + ownPlayer.getRank() + " WHERE id =" + ownPlayer.getId() + ";";
		executeSQL(updateSql);
	}

	/**
	 * Updates a player in database.
	 *
	 * @param sentPlayer Player
	 */
	public void updatePlayer(Player sentPlayer) {
		String sql = "SELECT * FROM players WHERE id = " + sentPlayer.getId() + ";";
		ArrayList result = executeSQLQuery(sql);
		if (result.size() > 0) {
			HashMap currMap = (HashMap) result.get(0);
			String firstName = (String) currMap.get("firstName");
			String surName = (String) currMap.get("surName");
			int rank = (int) currMap.get("rank");
			long id = (long) currMap.get("id");
			String updateSql = "UPDATE players SET firstName =\"" + firstName + "\", surName = \"" + surName + "\", rank =" + rank + " WHERE id =" + id + ";";
			executeSQL(updateSql);
		} else {
			insertPlayer("players", sentPlayer.getFirstName(), sentPlayer.getSurName(), sentPlayer.getRank(), sentPlayer.getId());
		}
	}

	/**
	 * Adds a finished match to database.
	 *
	 * @param remotePlayer Player
	 * @param points       int
	 * @param opppoints    int
	 * @param startTime    long
	 * @param endTime      long
	 * @param gridSize     int
	 * @param numMoves     int
	 */
	public void addMatch(Player remotePlayer, int points, int opppoints, long startTime, long endTime, int gridSize, int numMoves) {
		String sql = "INSERT INTO matches (opponent, points, opppoints, startTime, endTime, gridSize, numMoves) VALUES (" +
				remotePlayer.getId() + "," + points + "," + opppoints + "," + startTime + "," + endTime + "," + gridSize + "," + numMoves + ");";
		executeSQL(sql);
	}

	/**
	 * Adds a player to database.
	 *
	 * @param table   String
	 * @param name    String
	 * @param surName String
	 * @param rank    int
	 * @param id      long
	 */
	private void insertPlayer(String table, String name, String surName, int rank, long id) {
		String sql = "INSERT INTO " + table + " (id,firstName,surName,rank)VALUES(" + id + ",\"" + name + "\",\"" + surName + "\"," + rank + ");";
		executeSQL(sql);
	}

	/*******************************************************************************************************************
	 * STATISTICS METHODS
	 ******************************************************************************************************************/

	private int getIntValue(String sql) {
		ArrayList result = executeSQLQuery(sql);
		HashMap currMap = (HashMap) result.get(0);
		int value = (int) currMap.get("TOTAL");
		return value;
	}

	private double getFloatValue(String sql) {
		ArrayList result = executeSQLQuery(sql);
		HashMap currMap = (HashMap) result.get(0);
		double value = (double) currMap.get("TOTAL");
		return value;
	}

	// TOTAL POINTS
	public int getTotalPoints() {
		String sql = "SELECT SUM(points) as TOTAL FROM matches;";
		int totPoints = getIntValue(sql);
		return totPoints;
	}

	// POINTS GIVEN
	public int getTotalGivenPoints() {
		String sql = "SELECT SUM(opppoints) as TOTAL FROM matches;";
		int totPoints = getIntValue(sql);
		return totPoints;
	}

	// NUM WINS
	public int getTotalWins() {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points > opppoints;";
		int totWins = getIntValue(sql);
		return totWins;
	}

	// NUM LOSSES
	public int getTotalDefeats() {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points < opppoints;";
		int totDefeats = getIntValue(sql);
		return totDefeats;
	}

	// NUM DRAWS
	public int getTotalDraws() {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points = opppoints;";
		int totDraws = getIntValue(sql);
		return totDraws;
	}

	// NUM MOVES
	public int getTotalMoves() {
		String sql = "SELECT SUM(numMoves) as TOTAL FROM matches;";
		int totMoves = getIntValue(sql);
		return totMoves;
	}

	// AVG TIME
	public String getAvgTime() {
		String avgTime = "";
		return avgTime;
	}

	// AVG GRIDSIZE
	public double getAvgGrid() {
		String sql = "SELECT AVG(gridSize) as TOTAL FROM matches;";
		double avgGrid = getFloatValue(sql);
		return avgGrid;
	}

	// AVG MOVES / GAME
	public double getAvgMoves() {
		String sql = "SELECT AVG(numMoves) as TOTAL FROM matches;";
		double avgMoves = getFloatValue(sql);
		return avgMoves;
	}

	// AVG POINTS
	public double getAvgPoints() {
		String sql = "SELECT AVG(points) as TOTAL FROM matches;";
		double avgPoints = getFloatValue(sql);
		return avgPoints;
	}

	// TOTAL WINS VS PLAYER
	public int getTotalWinsVS(long playerId) {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points > opppoints AND opponent = "+playerId+";";
		int totWins = getIntValue(sql);
		return totWins;
	}

	// TOTAL DEFEATS VS PLAYER
	public int getTotalDefeatsVS(long playerId) {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points < opppoints AND opponent = "+playerId+";";
		int totDefeats = getIntValue(sql);
		return totDefeats;
	}

	// TOTAL DRAWS VS PLAYER
	public int getTotalDrawsVS(long playerId) {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE points = opppoints AND opponent = "+playerId+";";
		int totDraws = getIntValue(sql);
		return totDraws;
	}

	// TOTAL GAMES VS PLAYER
	public int getTotalGamesVS(long playerId) {
		String sql = "SELECT COUNT(*) AS TOTAL FROM matches WHERE opponent = "+playerId+";";
		int totGames = getIntValue(sql);
		return totGames;
	}

}
