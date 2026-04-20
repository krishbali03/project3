import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDatabase {
    private Connection connection;
    public GameDatabase() {
        try {
            //this may or not be a horrendus execution but we make a table for games if they dont exist hopefully?
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

    public Board getMove(String gameId, int moveIndex) {
        try {
            // no sql injection on my watch LOSER
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
    public void close() {
        try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
    // keep a summary of each game
    public static class GameRecord {
        public final String gameId;
        public final String player1;
        public final String player2;
        public final String winner;
        public final String datePlayed;
        public GameRecord(String gameId, String player1, String player2, String winner, String datePlayed) {
            this.gameId     = gameId;
            this.player1    = player1;
            this.player2    = player2;
            this.winner     = winner;
            this.datePlayed = datePlayed;
        }
        @Override
        public String toString() {
            return player1 + " vs " + player2 + " | Winner: " + winner + " | " + datePlayed;
        }
    }
}