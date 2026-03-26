package client;

import chess.*;
import server.ResponseException;

import java.util.Scanner;
import java.util.function.Function;

public class ClientMain {

    public static void run(String message, Function<String, String> eval) throws ResponseException {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            System.out.print(message);
            String line = scanner.nextLine();

            try {
                result = eval.apply(line);
                if (result != "quit") {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ Welcome to 240 Chess. Type help to get started. ♕");

        String serverUrl = "http://localhost:8080";
        if (args.length == 1){
            serverUrl = args[0];
        }

        try {
            var preClient = new PreLoginClient(serverUrl);
            var server = preClient.getServer();
            while (true) {
                preClient.setState(false);
                run("\n" + "[LOGGED_OUT] >>> ", line -> {
                    try {
                        return preClient.eval(line);
                    } catch (ResponseException e) {
                        throw new RuntimeException(e);
                    }
                });
                if (preClient.isLoggedIn()) {
                    var postClient = new PostLoginClient(server);
                    run("\n" + "[LOGGED_IN] >>> ", line -> {
                        try {
                            return postClient.eval(line);
                        } catch (ResponseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    if (postClient.isDone()){
                        break;
                    }
                }
                else {
                    break;
                }
            }
        } catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }
}
