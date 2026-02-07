package chess;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessBoard.Square;
import chess.ChessPiece.PieceType;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor turn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var safeMoves = new ArrayList<ChessMove>();
        var pieceMoves = new ChessMoves(board, startPosition);
        var teamColor = pieceMoves.startPiece.getTeamColor();
        var king = new ChessPiece(teamColor, PieceType.KING);

        for (ChessMove move : pieceMoves) {
            ChessBoard boardWithMove = board.withMove(move);
            var kingPos = boardWithMove.piecePos(king);
            var otherTeam = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
            if (!squareDefended(boardWithMove, kingPos, otherTeam)) {
                safeMoves.add(move);
            }
        }

        return safeMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece movingPiece = board.getPiece(startPos);
        if (movingPiece == null) {
            throw new InvalidMoveException("No piece at " + startPos);
        }
        if (movingPiece.getTeamColor() != this.turn) {
            throw new InvalidMoveException("Moving out of turn");
        }

        if (!validMoves(startPos).contains(move)) {
            throw new InvalidMoveException();
        }

        board.makeMove(move);
        
        if (this.turn == TeamColor.WHITE) {
            this.turn = TeamColor.BLACK;
        } else {
            this.turn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Square kingSquare = null;
        for (Square square : board.squares()) {
            if (
                square.piece != null && 
                square.piece.getPieceType() == PieceType.KING && 
                square.piece.getTeamColor() == teamColor
            ) {
                kingSquare = square;
            }
        }

        TeamColor otherTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        return squareDefended(kingSquare.getPosition(), otherTeam);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && teamHasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && teamHasNoValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Boolean teamHasNoValidMoves(TeamColor team) {
        for (Square s : board.squares()) {
            if (s.piece == null || s.piece.getTeamColor() != team) { continue; }
            var moves = validMoves(s.getPosition());
            if (!moves.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private static Boolean squareDefended(ChessBoard board, ChessPosition pos, TeamColor byTeam) {
        for (Square s : board.squares()) {
            if (s.piece == null || s.piece.getTeamColor() != byTeam) { continue; }
            ChessMoves moves = new ChessMoves(board, s.getPosition());
            if (moves.hasMoveTo(pos)) {
                return true;
            }
        }

        return false;
    }

    private Boolean squareDefended(ChessPosition pos, TeamColor byTeam) {
        return squareDefended(board, pos, byTeam);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((turn == null) ? 0 : turn.hashCode());
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
        ChessGame other = (ChessGame) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        } else if (!board.equals(other.board))
            return false;
        if (turn != other.turn)
            return false;
        return true;
    }
}
