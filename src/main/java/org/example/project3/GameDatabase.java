package org.example.project3;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {
    private Connection connection;
    //look to see if db exists locally mind you this is a bad way to do it but im lazy
    public GameDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:checkers.db");
            connection.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS games (
                    game_id TEXT PRIMARY KEY,
                    player1 TEXT,
                    player2 TEXT,
                    winner TEXT,
                    date_played TEXT
                )
            """);
            connection.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS moves (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id TEXT,
                    move_index INTEGER,
                    board BLOB
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //make an entry where we only care about who is playing and who won
    public void saveGame(String gameId, String player1, String player2, String winner) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO games VALUES (?,?,?,?,datetime('now'))"
            );
            ps.setString(1, gameId);
            ps.setString(2, player1);
            ps.setString(3, player2);
            ps.setString(4, winner);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //save the game status so that we can navigate
    public void saveMove(String gameId, int moveIndex, Board board) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(board);
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO moves (game_id, move_index, board) VALUES (?,?,?)"
            );
            ps.setString(1, gameId);
            ps.setInt(2, moveIndex);
            ps.setBytes(3, bos.toByteArray());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //get the game so that we can navigate it
    public Board getMove(String gameId, int moveIndex) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT board FROM moves WHERE game_id = ? AND move_index = ?"
            );
            ps.setString(1, gameId);
            ps.setInt(2, moveIndex);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Board) new ObjectInputStream(
                        new ByteArrayInputStream(rs.getBytes("board"))
                ).readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //for ui
    public int getMoveCount(String gameId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM moves WHERE game_id = ?"
            );
            ps.setString(1, gameId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //list view of ui
    public List<GameRecord> getAllGames() {
        List<GameRecord> records = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "SELECT game_id, player1, player2, winner, date_played FROM games ORDER BY date_played DESC"
            );
            while (rs.next()) {
                records.add(new GameRecord(
                        rs.getString("game_id"),
                        rs.getString("player1"),
                        rs.getString("player2"),
                        rs.getString("winner"),
                        rs.getString("date_played")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    //practice safe sql
    public void close() {
        try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
    //store games as such
    public static class GameRecord {
        public final String gameId;
        public final String player1;
        public final String player2;
        public final String winner;
        public final String datePlayed;

        public GameRecord(String gameId, String player1, String player2, String winner, String datePlayed) {
            this.gameId = gameId;
            this.player1 = player1;
            this.player2 = player2;
            this.winner = winner;
            this.datePlayed = datePlayed;
        }

        @Override
        public String toString() {
            return player1 + " vs " + player2  + winner + "DEMOLISHED ON" + " | " + datePlayed;
        }
    }
}