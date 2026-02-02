package chess;

import java.util.HashSet;

import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;

public class ChessMoves extends HashSet<ChessMove> {
  private ChessBoard board;
  private ChessPosition startPosition;
  private ChessPiece startPiece;
  private static final PieceType[] ALL_PROMOTIONS = {
    PieceType.QUEEN, PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP
  };

  public ChessMoves(ChessBoard board, ChessPosition startPosition) {
    this.board = board;
    this.startPosition = startPosition;
    this.startPiece = board.getPiece(startPosition);

    addMoves(this.startPiece.getPieceType(), this.startPiece.getTeamColor());
  }

  public ChessMoves(ChessPiece piece, ChessBoard board, ChessPosition startPosition) {
    this.board = board;
    this.startPosition = startPosition;
    this.startPiece = piece;

    addMoves(piece.getPieceType(), piece.getTeamColor());
  }

  private void addMoves(PieceType pieceType, TeamColor pieceColor) {
    if (pieceType == PieceType.ROOK || pieceType == PieceType.QUEEN) {
      addRookMoves();
    }
    if (pieceType == PieceType.BISHOP || pieceType == PieceType.QUEEN) {
      addBishopMoves();
    }
    if (pieceType == PieceType.KNIGHT) {
      add4x(1, 2);
      add4x(2, 1);
    }
    if (pieceType == PieceType.KING) {
      add4x(1, 1);
      add4x(0, 1);
    }
    if (pieceType == PieceType.PAWN) {
      int direction = pieceColor == TeamColor.WHITE ? 1 : -1;
      addPawnForwardMoves(direction);
      addPawnCaptures(direction, pieceColor);
    } 
  }

  private void addPawnForwardMoves(int direction) {
    int curRow = startPosition.getRow();
    ChessPosition oneForward = startPosition.plus(direction, 0);
    // The pawn will be moving to the last row so it needs to promote
    if (curRow + direction == 1 || curRow + direction == 8) {
      for (PieceType promotionType : ALL_PROMOTIONS) {
        this.add(new ChessMove(startPosition, oneForward, promotionType));
      }
    } else if (this.board.getPiece(oneForward) == null) {
      this.add(new ChessMove(startPosition, oneForward, null));
      // The pawn is on it's starting row so it may be able to move forward two spaces
      if (curRow - direction == 1 || curRow - direction == 8) {
        ChessPosition twoForward = oneForward.plus(direction, 0);
        if (this.board.getPiece(twoForward) == null) {
          this.add(new ChessMove(startPosition, twoForward, null));
        }
      }
    }
  }

  private void addPawnCaptures(int direction, TeamColor color) {
    int curRow = startPosition.getRow();
    Boolean willPromote = curRow + direction == 8 || curRow + direction == 1;
    ChessPosition upLeft = startPosition.plus(direction, -1);
    ChessPiece leftPiece = upLeft.isValid() ? board.getPiece(upLeft) : null;
    ChessPosition upRight = startPosition.plus(direction, 1);
    ChessPiece rightPiece = upRight.isValid() ? board.getPiece(upRight) : null;

    if (leftPiece != null && leftPiece.getTeamColor() != color) {
      if (willPromote) {
        for (PieceType promotionType : ALL_PROMOTIONS) {
          this.add(new ChessMove(startPosition, upLeft, promotionType));
        }
      } else {
        this.add(new ChessMove(startPosition, upLeft, null));
      }
    }

    if (rightPiece != null && rightPiece.getTeamColor() != color) {
      if (willPromote) {
        for (PieceType promotionType : ALL_PROMOTIONS) {
          this.add(new ChessMove(startPosition, upRight, promotionType));
        }
      } else {
        this.add(new ChessMove(startPosition, upRight, null));
      }
    }
  }

  private void add4x(int dRow, int dCol) {
    addMoveIfValid(dRow, dCol);
    addMoveIfValid(-dCol, dRow);
    addMoveIfValid(-dRow, -dCol);
    addMoveIfValid(dCol, -dRow);
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

  private void addRookMoves() {
    addMovesAlongPath(0, 1);
    addMovesAlongPath(0, -1);
    addMovesAlongPath(1, 0);
    addMovesAlongPath(-1, 0);
  }

  private void addBishopMoves() {
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

  public Boolean hasMoveTo(ChessPosition pos) {
    for (ChessMove move : this) {
      if (move.getEndPosition().equals(pos)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean add(ChessMove move) {
    if (move == null) return false;
    return super.add(move);
  }
}
