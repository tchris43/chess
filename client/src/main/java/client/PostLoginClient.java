package client;

import chess.ChessGame;
import model.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;
    private boolean loggedIn = true;
    private boolean done = false;
    private Map<String, Integer> gameIDs = new HashMap<>();

    public PostLoginClient(ServerFacade serverFacade) throws ResponseException {
        server = serverFacade;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public boolean isDone() {
        return done;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
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

    public void printPrompt(){
        System.out.print("\n" + "[LOGGED_IN] >>> ");
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
            String gameString = String.format("%d %s %s %s \n", i, game.gameName(), game.whiteUsername(), game.blackUsername());
            games.append(gameString);
            gameIDs.put(String.valueOf(i), game.gameID());
            i ++;
        }

        return games.toString();
    }

    public String join(String... params) throws ResponseException{
        ChessGame.TeamColor teamColor;
        if (params[1].equals("white")){
            teamColor = ChessGame.TeamColor.WHITE;
        }
        else if(params[1].equals("black")){
            teamColor = ChessGame.TeamColor.BLACK;
        }
        else {
            throw new ResponseException("Invalid player color");
        }

        String gameNumber = params[0];
        int gameID = gameIDs.get(gameNumber);

        JoinRequest joinRequest = new JoinRequest(teamColor, gameID);
        server.joinGame(joinRequest);
         String result = String.format("Successfully joined game %s", gameNumber);
         return result;
    }
}
