package chess;

import java.util.Collection;

import chess.ChessGame.TeamColor;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new ChessMoves(board, myPosition);
    }

    public static String boardString(ChessPiece piece) {
        if (piece == null) return " ";

        String str;
        switch (piece.type) {
            case PieceType.KING:
                str = "k";
                break;
            case PieceType.QUEEN:
                str = "q";
                break;
            case PieceType.BISHOP:
                str = "b";
                break;
            case PieceType.KNIGHT:
                str = "n";
                break;
            case PieceType.ROOK:
                str = "r";
                break;
            case PieceType.PAWN:
                str = "p";
                break;
        
            default:
                str = " ";
        }

        if (piece.color == TeamColor.WHITE) {
            return str.toUpperCase();
        }

        return str;
    }

    @Override
    public String toString() {
        return "ChessPiece [type=" + type + ", color=" + color + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
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
        ChessPiece other = (ChessPiece) obj;
        if (type != other.type)
            return false;
        if (color != other.color)
            return false;
        return true;
    }
}
