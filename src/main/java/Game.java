import java.io.Serializable;
import java.util.UUID;
//general came will save into database
public class Game implements Serializable {
    String gameId;
    Board board;
    String player1, player2;
    Piece.Color currentTurn;
    boolean gameOver;
    String winner;
    public Game(String player1, String player2) {
        this.gameId = UUID.randomUUID().toString();
        this.board = new Board();
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = Piece.Color.RED;
        this.gameOver = false;
    }
    public boolean makeMove(Move m) {
        if (gameOver) return false;
        board.applyMove(m);
        checkWin();
        if (!gameOver)
            currentTurn = (currentTurn == Piece.Color.RED) ? Piece.Color.BLACK : Piece.Color.RED;
        return true;
    }
    private void checkWin() {
        if (board.getValidMoves(Piece.Color.RED).isEmpty()) {
            gameOver = true;
            winner = player2;
        } else if (board.getValidMoves(Piece.Color.BLACK).isEmpty()) {
            gameOver = true;
            winner = player1;
        }
    }
}