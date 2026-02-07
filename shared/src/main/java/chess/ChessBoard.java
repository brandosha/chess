package chess;

import java.util.Arrays;
import java.util.Iterator;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        // this.resetBoard();
    }

    public ChessBoard(ChessBoard other) {
        for (int i = 0; i < HEIGHT; i++) {
            this.board[i] = other.board[i].clone();
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.r()][position.c()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        ChessPiece piece = this.board[position.r()][position.c()];
        return piece;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[WIDTH][HEIGHT];

        ChessPiece.PieceType[] pieceRow = {
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, null, null, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        };

        for (int i = 0; i < WIDTH; i++) {
            this.board[0][i] = new ChessPiece(TeamColor.WHITE, pieceRow[i]);
            this.board[1][i] = new ChessPiece(TeamColor.WHITE, PieceType.PAWN);

            this.board[6][i] = new ChessPiece(TeamColor.BLACK, PieceType.PAWN);
            this.board[7][i] = new ChessPiece(TeamColor.BLACK, pieceRow[i]);
        }

        this.board[0][3] = new ChessPiece(TeamColor.WHITE, PieceType.QUEEN);
        this.board[0][4] = new ChessPiece(TeamColor.WHITE, PieceType.KING);

        this.board[7][3] = new ChessPiece(TeamColor.BLACK, PieceType.QUEEN);
        this.board[7][4] = new ChessPiece(TeamColor.BLACK, PieceType.KING);
    }

    @Override
    public String toString() {
        String str = "";
        int lastIndex = board.length - 1;
        for (int i = lastIndex; i >= 0; i--) {
            ChessPiece[] row = board[i];
            if (i != lastIndex) str += "|\n";
            for (int j = 0; j < row.length; j++) {
                str += "|";
                str += ChessPiece.boardString(row[j]);
            }
        }

        return str + "|";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(board);
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
        ChessBoard other = (ChessBoard) obj;
        if (!Arrays.deepEquals(board, other.board))
            return false;
        return true;
    }

    public static class Square {
        public final int row;
        public final int col;
        public final ChessPiece piece;

        public Square(int row, int col, ChessPiece piece) {
            this.row = row;
            this.col = col;
            this.piece = piece;
        }

        public ChessPosition getPosition() {
            return new ChessPosition(row, col);
        }
    }

    SquareIterator squares() {
        return new SquareIterator();
    }

    class SquareIterator implements Iterator<Square>, Iterable<Square> {
        int r = 0;
        int c = 0;

        public SquareIterator() {
        }

        @Override
        public boolean hasNext() {
            return r < HEIGHT;
        }

        @Override
        public Square next() {
            ChessPiece piece = board[r][c];
            int row = r + 1;
            int col = c + 1;

            c += 1;
            if (c >= WIDTH) {
                r += 1;
                c = 0;
            }

            return new Square(row, col, piece);
        }

        @Override
        public Iterator<Square> iterator() {
            return this;
        }
    }

    public ChessPosition piecePos(ChessPiece findPiece) {
        for (int r = 0; r < board.length; r++) {
            ChessPiece[] row = board[r];
            for (int c = 0; c < row.length; c++) {
                ChessPiece piece = row[c];
                if (findPiece.equals(piece)) {
                    return new ChessPosition(r + 1, c + 1);
                }
            }
        }

        return null;
    }

    public ChessBoard clone() {
        return new ChessBoard(this);
    }

    public void makeMove(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece movingPiece = getPiece(startPos);

        addPiece(move.getStartPosition(), null);
        PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), promotionPiece));
        } else {
            addPiece(move.getEndPosition(), movingPiece);
        }
    }

    public ChessBoard withMove(ChessMove move) {
        var newBoard = clone();
        newBoard.makeMove(move);
        return newBoard;
    }
}
