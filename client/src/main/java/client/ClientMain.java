package client;

import chess.*;
import jakarta.websocket.DeploymentException;
import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;
import server.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
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
            runLoop(preClient, server);
        } catch(Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

    }

    private static void runLoop(PreLoginClient preClient, ServerFacade server) throws ResponseException, URISyntaxException, DeploymentException, IOException {
        var postClient = new PostLoginClient(server);
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
            if (postClient.isInGame()){
                var gameClient = new GameClient(server, postClient.getGameID(), postClient.getJoin());
                run("\n" + "[IN_GAME] >>> ", line -> {
                    try {
                        return gameClient.eval(line);
                    } catch (ResponseException e){
                        throw new RuntimeException(e);
                    }
                });
            }
            else {
                break;
            }
        }
    }
}
