package client;

import chess.ChessBoard;
import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import jakarta.websocket.DeploymentException;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import ui.DrawBoard;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostLoginClient implements NotificationHandler {
    private final ServerFacade server;
    private boolean loggedIn = true;
    private boolean done = false;
    private Map<String, Integer> gameIDs = new HashMap<>();
    private int numGames = 0;
    private WebSocketFacade ws;
    private boolean inGame = false;
    private int gameID;
    private ChessBoard board;
    private ChessGame game;

    public PostLoginClient(ServerFacade serverFacade) throws ResponseException, URISyntaxException {
        server = serverFacade;
    }

    public ChessGame getGame(){
        return game;
    }

    public ChessBoard getBoard(){
        return board;
    }

    public int getGameID(){
        return gameID;
    }

    public WebSocketFacade getWS() {
        return ws;
    }


    public boolean isDone() {
        return done;
    }

    public boolean isInGame() {
        return inGame;
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
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
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

    public String join(String... params) throws ResponseException, DeploymentException, IOException, URISyntaxException {
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
            this.gameID = gameID;


            JoinRequest joinRequest = new JoinRequest(teamColor, gameID);
            server.joinGame(joinRequest);
            ws = new WebSocketFacade(server.getUrl(), this);
            ws.connect(server.getAuth(), gameID);
            String result = String.format("Successfully joined game %s", gameNumber);
            inGame = true;

            System.out.println();
            return "quit";
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
//            receiveBoard(board);
            System.out.print("DEBUG: now it should draw board");
            System.out.println();
            return result;
        }
        else {
            throw new ResponseException("Please enter valid parameters: observe <gameID>");
        }
    }

    private void loadBoard(ChessBoard board){
        this.board = board;
    }

    private void receiveBoard(ChessBoard chessBoard, ChessGame chessGame){
        loadBoard(chessBoard);
        this.game = chessGame;
        var drawBoard = new DrawBoard();
        drawBoard.printBoard(board, game.getTeamTurn(), null);
    }

    @Override
    public void notify(LoadGameMessage message) {
        ChessBoard chessBoard = message.getGame().getBoard();
        receiveBoard(chessBoard, message.getGame());
    }

}
