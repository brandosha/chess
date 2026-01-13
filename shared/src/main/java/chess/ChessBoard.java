package chess;

import java.util.Arrays;

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
}
