package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    public Boolean isValid() {
        return (row > 0 && row <= ChessBoard.HEIGHT && col > 0 && col <= ChessBoard.WIDTH);
    }

    public ChessPosition plus(int dRow, int dCol) {
        return new ChessPosition(this.row + dRow, this.col + dCol);
    }

    public ChessMove movingBy(int dRow, int dCol) {
        return new ChessMove(this, this.plus(dRow, dCol), null);
    }

    /**
     * @return array row index
     */
    public int r() {
        return this.row - 1;
    }
    /**
     * @return array column index
     */
    public int c() {
        return this.col - 1;
    }

    @Override
    public String toString() {
        return "ChessPosition [row=" + row + ", col=" + col + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + row;
        result = prime * result + col;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChessPosition other = (ChessPosition) obj;
        if (row != other.row)
            return false;
        if (col != other.col)
            return false;
        return true;
    }
}
