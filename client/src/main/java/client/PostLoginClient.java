package client;

import chess.ChessGame;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;
    private boolean loggedIn = true;
    private boolean done = false;
    private Map<String, Integer> gameIDs = new HashMap<>();
    private int numGames = 0;

    public PostLoginClient(ServerFacade serverFacade) throws ResponseException {
        server = serverFacade;
    }

    public boolean isDone() {
        return done;
    }

    public void run(String message) {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            System.out.print(message);
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (result != "quit") {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public String eval(String input) throws ResponseException{
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "logout" -> logout();
                case "quit" -> quit();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String quit() {
        done = true;
        return "quit";
    }

    public boolean goodParams(String[] params, int requiredNum){
        return params.length == requiredNum;
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                quit - playing chess
                help - with possible commands
                """;
    }

    public String logout() throws ResponseException{
        server.logout();
        loggedIn = false;
        return "quit";
    }

    public String create(String... params) throws ResponseException{
        GameRequest gameRequest = new GameRequest(params[0]);
        server.createGame(gameRequest);
        String result = String.format("Created game: '%s'", gameRequest.gameName());
        return result;
    }

    public String list() throws ResponseException{
        GameList gameList = server.listGames().games();



        StringBuilder games = new StringBuilder();
        int i = 1;
        for (GameData game : gameList){

            String whiteUser = String.format("WHITE: %s", game.whiteUsername());
            String blackUser = String.format("BLACK: %s", game.blackUsername());

            if (game.whiteUsername() == null){
                whiteUser = "WHITE: available";
            }
            if (game.blackUsername() == null){
                blackUser = "BLACK: available";
            }


            String gameString = String.format("%d %s %s %s \n", i, game.gameName(), whiteUser, blackUser);
            games.append(gameString);
            gameIDs.put(String.valueOf(i), game.gameID());
            i ++;
        }

        numGames = gameList.size();

        return games.toString();
    }

    public String join(String... params) throws ResponseException{
        if (goodParams(params, 2)) {
            ChessGame.TeamColor teamColor;
            if (params[1].equals("white")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (params[1].equals("black")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException("Invalid player color");
            }

            String gameNumber = params[0];
            try {
                if (Integer.parseInt(gameNumber) < 1 || Integer.parseInt(gameNumber) > numGames){
                    throw new ResponseException("Please enter a valid gameID");
                }
            } catch (Exception ex){
                throw new ResponseException("Please enter a valid gameID");
            }


            int gameID = gameIDs.get(gameNumber);


            JoinRequest joinRequest = new JoinRequest(teamColor, gameID);
            server.joinGame(joinRequest);
            String result = String.format("Successfully joined game %s", gameNumber);
            var board = new DrawBoard();
            board.draw(teamColor);
            System.out.println();
            return result;
        }
        else {
            throw new ResponseException("Please enter valid parameters: join <gameID> <playerColor>");
        }
    }

    public String observe(String... params) throws ResponseException{
        if (goodParams(params, 1)) {
            String gameNumber = params[0];

            try {
                if (Integer.parseInt(gameNumber) < 1 || Integer.parseInt(gameNumber) > numGames){
                    throw new ResponseException("Please enter a valid gameID");
                }
            } catch (Exception ex){
                throw new ResponseException("Please enter a valid game ID");
            }

            int gameID = gameIDs.get(gameNumber);


            String result = String.format("Observing game %s", gameNumber);
            var board = new DrawBoard();
            board.draw(ChessGame.TeamColor.WHITE);
            System.out.println();
            return result;
        }
        else {
            throw new ResponseException("Please enter valid parameters: observe <gameID>");
        }
    }
}
