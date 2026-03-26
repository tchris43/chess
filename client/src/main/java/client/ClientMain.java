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
            var preClient = new PreLoginClient(serverUrl);
            preClient.run();
            var server = preClient.getServer();
            new PostLoginClient(server).run();
        } catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }
}
