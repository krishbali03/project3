import java.io.Serializable;
import java.util.ArrayList;
//should i add more comments? yes. will i? NO!
public class Board implements Serializable {
    Piece[][] grid = new Piece[8][8];
    public Board() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 8; col++)
                if ((row + col) % 2 == 1)
                    grid[row][col] = new Piece(Piece.Color.BLACK);

        for (int row = 5; row < 8; row++)
            for (int col = 0; col < 8; col++)
                if ((row + col) % 2 == 1)
                    grid[row][col] = new Piece(Piece.Color.RED);
    }
    public void applyMove(Move m) {
        Piece p = grid[m.fromRow][m.fromCol];
        grid[m.toRow][m.toCol] = p;
        grid[m.fromRow][m.fromCol] = null;
        if (m.isCapture) {
            grid[(m.fromRow + m.toRow) / 2][(m.fromCol + m.toCol) / 2] = null;
        }
        if (m.toRow == 0 && p.color == Piece.Color.RED)   p.isKing = true;
        if (m.toRow == 7 && p.color == Piece.Color.BLACK) p.isKing = true;
    }
    //triple nested for loop. Yes i do NOT care about effeciency
    public ArrayList<Move> getValidMoves(Piece.Color color) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<Move> captures = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = grid[r][c];
                if (p == null || p.color != color) continue;
                int[] dirs = p.isKing ? new int[]{-1, 1}
                        : color == Piece.Color.RED ? new int[]{-1} : new int[]{1};
                for (int dr : dirs) {
                    for (int dc : new int[]{-1, 1}) {
                        int nr = r + dr, nc = c + dc;
                        if (!inBounds(nr, nc)) continue;
                        if (grid[nr][nc] == null) {
                            moves.add(new Move(r, c, nr, nc, false));
                        } else if (grid[nr][nc].color != color) {
                            int jr = r + 2*dr, jc = c + 2*dc;
                            if (inBounds(jr, jc) && grid[jr][jc] == null)
                                captures.add(new Move(r, c, jr, jc, true));
                        }
                    }
                }
            }
        }
        return captures.isEmpty() ? moves : captures;
    }
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }
}
