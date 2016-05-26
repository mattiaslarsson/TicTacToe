package dao;

import models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-25.
 */
public class DatabaseConnector {

    private Connection conn = null;

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

    public void closeConnection() {
        try {
            conn.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getStackTrace());
        }

    }

    public boolean firstRun() {
        System.out.println("In first run.");
        boolean isFirst = false;
        String sqlQ = "SELECT count(*) AS number FROM sqlite_master WHERE type='table' AND name='myPlayer';";
        ArrayList results = executeSQLQuery(sqlQ);
        Map currRow = (HashMap) results.get(0);
        int number = (int) currRow.get("number");

        if (number > 0) {
            System.out.println("Tidigare spelare funnen");
        } else {
            initDB();
            System.out.println("Skapar databas, ingen tidigare spelare funnen.");
            isFirst = true;
        }
        return isFirst;
    }

    private void initDB() {
        String sqlInit[] = {
                "CREATE TABLE IF NOT EXISTS myPlayer (id INT NOT NULL PRIMARY KEY,firstName TEXT,surName TEXT,rank INT);",
                "CREATE TABLE IF NOT EXISTS players (id INT NOT NULL PRIMARY KEY,firstName TEXT,surName INT,rank INT);",
                "CREATE TABLE IF NOT EXISTS matches (id INT AUTO_INCREMENT PRIMARY KEY,opponent INT,points, INT opppoints,startTime INT,endTime INT,gridSize INT,FOREIGN KEY (opponent) REFERENCES players(id));"
        };
        for (String currSQL : sqlInit) {
            executeSQL(currSQL);
        }
    }

    public void createOwnPlayer(String firstName, String surName) {
        long id = (System.currentTimeMillis() << 20) | (System.nanoTime() & ~9223372036854251520L);
        insertPlayer("myPlayer", firstName, surName, 0, id);
    }

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

    public void updatePlayer(Player sentPlayer) {
        Player currPlayer = null;
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

    public void addMatch (Player remotePlayer, int points, int opppoints, long startTime, long endTime, int gridSize) {
        // id INT opponent INT,points, INT opppoints,startTime INT,endTime INT,gridSize INT"
        String sql = "INSERT INTO matches (opponent, points, opppoints, startTime, endTime, gridSize) VALUES ("+
                remotePlayer.getId() + "," + points + "," + opppoints+ "," + startTime + "," +endTime+ "," +gridSize+ ");";
        executeSQL(sql);
    }


    private void insertPlayer(String table, String name, String surName, int rank, long id) {
        String sql = "INSERT INTO " + table + " (id,firstName,surName,rank)VALUES(" + id + ",\"" + name + "\",\"" + surName + "\"," + rank + ");";
        executeSQL(sql);
    }

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
}
