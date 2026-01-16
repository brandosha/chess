package chess;

import java.util.HashSet;

import chess.ChessPiece.PieceType;

public class ChessMoves extends HashSet<ChessMove> {
  private ChessBoard board;
  private ChessPosition startPosition;
  private ChessPiece startPiece;

  public ChessMoves(ChessBoard board, ChessPosition startPosition) {
    this.board = board;
    this.startPosition = startPosition;
    this.startPiece = board.getPiece(startPosition);

    addMoves(this.startPiece.getPieceType());
  }

  public ChessMoves(ChessPiece piece, ChessBoard board, ChessPosition startPosition) {
    this.board = board;
    this.startPosition = startPosition;
    this.startPiece = piece;

    addMoves(piece.getPieceType());
  }

  private void addMoves(PieceType pieceType) {
    if (pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN) {
      addHorizontalMoves();
    }
    if (pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN) {
      addDiagonalMoves();
    }
    if (pieceType == PieceType.KNIGHT) {
      addDiagonalSymmetry(1, 2);
      addDiagonalSymmetry(2, 1);
    }
    if (pieceType == PieceType.KING) {
      addDiagonalSymmetry(1, 1);
      addMoveIfValid(0, 1);
      addMoveIfValid(0, -1);
      addMoveIfValid(1, 0);
      addMoveIfValid(-1, 0);
    }
  }

  private void addDiagonalSymmetry(int dRow, int dCol) {
    addMoveIfValid( dRow,  dCol);
    addMoveIfValid( dRow, -dCol);
    addMoveIfValid(-dRow,  dCol);
    addMoveIfValid(-dRow, -dCol);
  }

  private void addMoveIfValid(int dRow, int dCol, PieceType promotionPiece) {
    this.add(newMoveIfValid(dRow, dCol, promotionPiece));
  }
  private void addMoveIfValid(int dRow, int dCol) {
    this.add(newMoveIfValid(dRow, dCol, null));
  }

  private ChessMove newMoveIfValid(int dRow, int dCol, PieceType promotionPiece) {
    ChessPosition endPosition = startPosition.plus(dRow, dCol);
    if (!endPosition.isValid()) return null;

    ChessPiece endPiece = board.getPiece(endPosition);
    if (endPiece != null && endPiece.getTeamColor() == startPiece.getTeamColor()) return null;

    return new ChessMove(startPosition, endPosition, promotionPiece);
  }

  private void addHorizontalMoves() {
    addMovesAlongPath(0, 1);
    addMovesAlongPath(0, -1);
    addMovesAlongPath(1, 0);
    addMovesAlongPath(-1, 0);
  }

  private void addDiagonalMoves() {
    addMovesAlongPath(1, 1);
    addMovesAlongPath(1, -1);
    addMovesAlongPath(-1, 1);
    addMovesAlongPath(-1, -1);
  }

  private void addMovesAlongPath(int dRow, int dCol) {

    ChessPosition endPosition = startPosition.plus(dRow, dCol);
    while (endPosition.isValid()) {
      ChessPiece endPiece = board.getPiece(endPosition);
      if (endPiece == null) {
        super.add(new ChessMove(startPosition, endPosition, null));
      } else {
        if (endPiece.getTeamColor() != startPiece.getTeamColor()) {
          super.add(new ChessMove(startPosition, endPosition, null));
        }
        break;
      }

      endPosition = endPosition.plus(dRow, dCol);
    }

  }

  @Override
  public boolean add(ChessMove move) {
    if (move == null) return false;
    return super.add(move);
  }
}
