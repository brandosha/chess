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
    
                                        // { WHITE, BLACK }
    private final Boolean[] canLeftCastle = { true, true };
    private final Boolean[] canRightCastle = { true, true };
    private ChessPosition enPassantPos = null;

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
        var movingPiece = pieceMoves.startPiece;
        var teamColor = movingPiece.getTeamColor();
        var king = new ChessPiece(teamColor, PieceType.KING);

        for (ChessMove move : pieceMoves) {
            ChessBoard boardWithMove = board.withMove(move);
            var kingPos = boardWithMove.piecePos(king);
            var otherTeam = teamColor == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
            if (!squareDefended(boardWithMove, kingPos, otherTeam)) {
                safeMoves.add(move);
            }
        }

        if (movingPiece.getPieceType() == PieceType.KING) {
            addCastlingMoves(startPosition, safeMoves);
        } else if (movingPiece.getPieceType() == PieceType.PAWN) {
            addEnPassantMoves(startPosition, safeMoves);
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

        if (makeCastlingMove(move)) {}
        else if (makeEnPassantMove(move)) {}
        else { board.makeMove(move); }

        int castlingIndex = 1;
        if (this.turn == TeamColor.WHITE) {
            castlingIndex = 0;
            this.turn = TeamColor.BLACK;
        } else {
            this.turn = TeamColor.WHITE;
        }

        if (movingPiece.getPieceType() == PieceType.KING) {
            canLeftCastle[castlingIndex] = false;
            canRightCastle[castlingIndex] = false;
        }

        ChessPosition endPos = move.getEndPosition();
        ChessPosition[] rooks = {
            new ChessPosition(1, 1),
            new ChessPosition(8, 1),
            new ChessPosition(1, 8),
            new ChessPosition(8, 8)
        };
        if (rooks[0].equals(startPos) || rooks[0].equals(endPos)) {
            canLeftCastle[0] = false;
        } else if (rooks[1].equals(startPos) || rooks[1].equals(endPos)) {
            canLeftCastle[1] = false;
        } else if (rooks[2].equals(startPos) || rooks[2].equals(endPos)) {
            canRightCastle[0] = false;
        } else if (rooks[3].equals(startPos) || rooks[3].equals(endPos)) {
            canRightCastle[1] = false;
        }

        // Keep track of the en passant square
        enPassantPos = null;
        if (movingPiece.getPieceType() == PieceType.PAWN) {
            int dist = endPos.getRow() - startPos.getRow();
            if (dist == 2) {
                enPassantPos = startPos.plus(1, 0);
            } else if (dist == -2) {
                enPassantPos = startPos.plus(-1, 0);
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = board.piecePos(new ChessPiece(teamColor, PieceType.KING));
        TeamColor otherTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        return squareDefended(kingPos, otherTeam);
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

    // Special moves (casting + en passant)

    private void addCastlingMoves(ChessPosition startPos, Collection<ChessMove> moves) {
        ChessPiece movingPiece = board.getPiece(startPos);
        TeamColor team = movingPiece.getTeamColor();
        TeamColor otherTeam = team == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK;
        if (movingPiece.getPieceType() != PieceType.KING) { return; }
        if (squareDefended(startPos, otherTeam)) { return; }

        int castlingIndex = 0;
        ChessPosition[] leftCastle = { new ChessPosition(1, 3), new ChessPosition(1, 4) };
        ChessPosition[] rightCastle = { new ChessPosition(1, 7), new ChessPosition(1, 6) };

        if (movingPiece.getTeamColor() == TeamColor.BLACK) {
            castlingIndex = 1;
            leftCastle[0] = new ChessPosition(8, 3);
            leftCastle[1] = new ChessPosition(8, 4);
            rightCastle[0] = new ChessPosition(8, 7);
            rightCastle[1] = new ChessPosition(8, 6);
        }

        if (!canLeftCastle[castlingIndex] && !canRightCastle[castlingIndex]) { return; }
        
        if (
            canLeftCastle[castlingIndex] &&
            board.getPiece(leftCastle[0]) == null &&
            board.getPiece(leftCastle[1]) == null &&
            !squareDefended(leftCastle[0], otherTeam) && 
            !squareDefended(leftCastle[1], otherTeam)
        ) {
            moves.add(new ChessMove(startPos, leftCastle[0], null));
        }

        if (
            canRightCastle[castlingIndex] &&
            board.getPiece(rightCastle[0]) == null &&
            board.getPiece(rightCastle[1]) == null &&
            !squareDefended(rightCastle[0], otherTeam) && 
            !squareDefended(rightCastle[1], otherTeam)
        ) {
            moves.add(new ChessMove(startPos, rightCastle[0], null));
        }
    }

    private Boolean makeCastlingMove(ChessMove move) {
        int backRank = 1;

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece movingPiece = board.getPiece(startPos);
        if (movingPiece.getPieceType() != PieceType.KING) {
            return false;
        }

        if (movingPiece.getTeamColor() == TeamColor.BLACK) {
            backRank = 8;
        }
        
        if (startPos.getRow() != backRank || endPos.getRow() != backRank) {
            return false;
        }
        if (startPos.getColumn() != 5) {
            return false;
        }
        int dist = endPos.getColumn() - startPos.getColumn();
        if (dist == 2) {
            board.makeMove(move);
            board.makeMove(
                new ChessMove(new ChessPosition(backRank, 8), startPos.plus(0, 1), null)
            );
        } else if (dist == -2) {
            board.makeMove(move);
            board.makeMove(
                new ChessMove(new ChessPosition(backRank, 1), startPos.plus(0, -1), null)
            );
        } else {
            return false;
        }

        return true;
    }

    private void addEnPassantMoves(ChessPosition startPos, Collection<ChessMove> moves) {
        if (enPassantPos == null) { return; }

        ChessPiece movingPiece = board.getPiece(startPos);
        if (movingPiece.getPieceType() != PieceType.PAWN) { return; }

        int direction = movingPiece.getTeamColor() == TeamColor.BLACK ? -1 : 1;
        int rowDifference = enPassantPos.getRow() - startPos.getRow();
        if (rowDifference != direction) { return; }

        int colDifference = enPassantPos.getColumn() - startPos.getColumn();
        if (colDifference == 1 || colDifference == -1) {
            moves.add(new ChessMove(startPos, enPassantPos, null));
        }
    }

    private Boolean makeEnPassantMove(ChessMove move) {
        ChessPosition startPos = move.getStartPosition();
        ChessPiece movingPiece = board.getPiece(startPos);
        if (movingPiece.getPieceType() != PieceType.PAWN) {
            return false;
        }

        if (!move.getEndPosition().equals(enPassantPos)) {
            return false;
        }

        int direction = movingPiece.getTeamColor() == TeamColor.BLACK ? 1 : -1;
        board.makeMove(move);
        board.addPiece(enPassantPos.plus(direction, 0), null);
        enPassantPos = null;
        return true;
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
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ChessGame other = (ChessGame) obj;
        if (board == null) {
            if (other.board != null) { return false; }
        } else if (!board.equals(other.board)) { return false; }
        if (turn != other.turn) { return false; }
        return true;
    }
}
