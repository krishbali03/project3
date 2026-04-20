import java.io.Serializable;
public class Move implements Serializable {
    int fromRow, fromCol, toRow, toCol;
    boolean isCapture;
    public Move(int fromRow, int fromCol, int toRow, int toCol, boolean isCapture) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow   = toRow;
        this.toCol   = toCol;
        this.isCapture = isCapture;
    }
}
