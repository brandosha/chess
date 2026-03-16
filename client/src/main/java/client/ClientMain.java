package client;

import chess.ChessGame;
import chess.ChessPiece;
import client.repl.ReplController;
import client.views.PregameView;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var repl = new ReplController();
        repl.start(new PregameView());
    }
}
