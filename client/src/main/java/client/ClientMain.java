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

    private static void inGameLoop(PostLoginClient postClient, ServerFacade server) throws ResponseException, DeploymentException, URISyntaxException, IOException {
        if (postClient.observing){
            var gameClient = new GameClient(server, postClient.getGameID(), postClient.authToken);
            run("\n" + "[IN_GAME] >>> ", line -> {
                try {
                    return gameClient.eval(line);
                } catch (ResponseException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else{
            var gameClient = new GameClient(server, postClient.getGameID(), postClient.getJoin());
            gameClient.setAuthToken(postClient.authToken);
            run("\n" + "[IN_GAME] >>> ", line -> {
                try {
                    return gameClient.eval(line);
                } catch (ResponseException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void loggedInLoop(PostLoginClient postClient, PreLoginClient preClient, ServerFacade server) throws ResponseException, DeploymentException, URISyntaxException, IOException {
        postClient.setAuthToken(preClient.authToken);
        while(true) {
            run("\n" + "[LOGGED_IN] >>> ", line -> {
                try {
                    return postClient.eval(line);
                } catch (ResponseException e) {
                    throw new RuntimeException(e);
                }
            });

            if (postClient.isDone()) {
                break;
            }

            if (postClient.isInGame()) {
                inGameLoop(postClient, server);
            }


        }
    }


    private static void runLoop(PreLoginClient preClient, ServerFacade server)
            throws ResponseException, URISyntaxException, DeploymentException, IOException {
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
                loggedInLoop(postClient, preClient, server);
            }
            else {
                break;
            }
        }
    }
}
