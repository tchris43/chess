package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1){
            serverUrl = args[0];
        }

        try {
            new PreLoginClient(serverUrl).run();
            new PostLoginClient(serverUrl).run();
        } catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }
}
