import java.util.List;
//when we wanna go through the past games we will display this
public class GameHistory {
    private final GameDatabase db;
    private String currentGameId;
    private int currentMoveIndex;
    private int totalMoves;
    private GameDatabase.GameRecord currentRecord;
    public GameHistory(GameDatabase db) {
        this.db = db;
    }
    //lists games from most recent to least recent
    public List<GameDatabase.GameRecord> getAllGames() {
        return db.getAllGames();
    }
    //call this with handler
    public boolean loadGame(String gameId) {
        int count = db.getMoveCount(gameId);
        if (count == 0) {
            return false;
        }
        currentGameId    = gameId;
        totalMoves       = count;
        currentMoveIndex = 0;
        //look through database until we find game
        for (GameDatabase.GameRecord r : db.getAllGames()) {
            if (r.gameId.equals(gameId)) {
                currentRecord = r;
                break;
            }
        }
        return true;
    }
    //set game to game user wants
    public Board getCurrentBoard() {
        if (currentGameId == null) return null;
        return db.getMove(currentGameId, currentMoveIndex);
    }
    //go to next move should be tied to arrow
    public boolean nextMove() {
        if (currentGameId == null || currentMoveIndex >= totalMoves - 1) return false;
        currentMoveIndex++;
        return true;
    }
    //same thing but for last move
    public boolean prevMove() {
        if (currentGameId == null || currentMoveIndex <= 0) return false;
        currentMoveIndex--;
        return true;
    }
    //we show the list of moves but wanna make sure that they can click any move
    public boolean goToMove(int index) {
        if (currentGameId == null || index < 0 || index >= totalMoves) return false;
        currentMoveIndex = index;
        return true;
    }
    //the double arrow
    public void goToStart() {
        if (currentGameId != null) currentMoveIndex = 0;
    }
    //same thing another double arrow
    public void goToEnd() {
        if (currentGameId != null) currentMoveIndex = totalMoves - 1;
    }
    public int getCurrentMoveIndex() { return currentMoveIndex; }
    public int getTotalMoves() { return totalMoves; }
    public boolean isAtStart() { return currentMoveIndex == 0; }
    public boolean isAtEnd() { return currentMoveIndex == totalMoves - 1; }
    public String getCurrentGameId() { return currentGameId; }
    //easier way to name moves
    public String getMoveLabel() {
        return "move " + (currentMoveIndex + 1) + ":" + totalMoves;
    }
    //show summary of game when we pick a game so they can have a quick summary
    public GameDatabase.GameRecord getCurrentRecord() { return currentRecord; }
}