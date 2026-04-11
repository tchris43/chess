package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.websocket.*;



import model.AuthData;
import model.GameData;
import model.GameList;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UnauthorizedException;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.NotificationFilter;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static websocket.commands.UserGameCommand.CommandType.*;

public class webSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;

    public webSocketHandler(UserService userService){
        this.userService = userService;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws DataAccessException {
        try {
            MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            UserGameCommand command = null;
            if (moveCommand.getCommandType() != MAKE_MOVE){
                command = new UserGameCommand(moveCommand.getCommandType(), moveCommand.getAuthToken(), moveCommand.getGameID());
            }
            else {
                command = moveCommand;
            }
            String userName = null;
            List<AuthData> auths = userService.getAuths();
            for (AuthData auth : auths){
                if (Objects.equals(auth.authToken(), command.getAuthToken())){
                    userName = auth.username();
                }
            }



            switch (command.getCommandType()){
                case CONNECT -> connect(command.getGameID(), userName, ctx.session, command.getAuthToken());
                case MAKE_MOVE -> makeMove(userName, ctx.session, command.getMove(), command.getGameID(), command.getAuthToken());
                case LEAVE -> leave(userName, ctx.session, command.getGameID(), command.getAuthToken());
                case RESIGN -> resign(userName, ctx.session, command.getGameID());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    private String getTeam(String authToken, int gameID, String userName) throws DataAccessException {
        // ** I have assumed that this team will exist
        //------------ approved (connection) 7:38 wed
        GameList games = userService.listGames(authToken);
        GameData game = null;
        for (GameData g : games){
            if (g.gameID() == gameID){
                game = g;
            }
        }
        if (game.whiteUsername().equals(userName)){
            return "white";
        }
        else if (game.blackUsername().equals(userName)){
            return "black";
        }
        else {
            return "observer";
        }
    }

    private ChessGame.TeamColor getPlayerColor(String authToken, int gameID, String userName) throws DataAccessException{
        String color = getTeam(authToken, gameID, userName);
        if (color.equals("white")){
            return ChessGame.TeamColor.WHITE;
        }
        else {
            return ChessGame.TeamColor.BLACK;
        }
    }

    private NotificationMessage getNotification(String teamColor, String userName){
        // ** assumed it will either be player or observer
        // ------------- approved (connect) 7:40 wed
        String message = null;
        if (teamColor.equals("white") || teamColor.equals("black")){
            message = String.format("%s connected to the game as the %s team", userName, teamColor);
        }
        else {
            message = String.format("%s connected to the game as an observer", userName);
        }
        return new NotificationMessage(message);
    }

    private void connect(int gameID, String userName, Session session, String authToken) throws IOException, DataAccessException {
        //--------  approved for both connect tests 8:23 wed
        try {
            if (userName == null){
                throw new DataAccessException("Invalid auth");
            }
            connections.add(gameID, session, userService, userName, authToken);
            String teamColor = getTeam(authToken, gameID, userName);
            NotificationMessage notification = getNotification(teamColor, userName);
            connections.broadcast(gameID, session, notification);
            //TODO verify that I am supposed to pass a new chessGame here
            LoadGameMessage loadGame = new LoadGameMessage(new ChessGame());
            connections.broadcast(gameID, session, loadGame);
        } catch(DataAccessException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: cannot connect to websocket");
            connections.broadcast(gameID, session, errorMessage);
        }
    }

    private ChessGame.TeamColor getOpposing(ChessGame.TeamColor playerColor){
        if (playerColor == ChessGame.TeamColor.WHITE){
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void makeMove(String userName, Session session, ChessMove move, int gameID, String authToken) throws DataAccessException, IOException {
        //verify move validity
        //get the piece by the position on board
        //check if the move is in pieceMoves
        try {
            boolean valid = false;
            ChessGame.TeamColor playerColor = getPlayerColor(authToken, gameID, userName);
            ChessGame game = connections.get(gameID).game;
            ChessBoard board = game.getBoard();
            Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
            for (ChessMove m : validMoves) {
                if (m.equals(move) && board.getPiece(m.getStartPosition()).getTeamColor() == playerColor) {
                    valid = true;
                }
            }
            //update the game
            if (valid) {
                DataAccess dataAccess = userService.getDataAccess();
                game.makeMove(move);
                connections.updateGame(dataAccess, userName, gameID, playerColor, game);
                //----- verified up to here 12:17 for normal
                //Load game to all clients
                LoadGameMessage loadGame = new LoadGameMessage(game);
                connections.broadcastMove(gameID, session, loadGame);
                //notify all other clients of move
                NotificationMessage notification = new NotificationMessage(String.format("%s moved from %s to %s", userName, move.getStartPosition(), move.getEndPosition()));
                connections.broadcastMove(gameID, session, notification);
                //notify all clients if in check, checkmate or stalemate
                ChessGame.TeamColor opposingTeam = getOpposing(playerColor);
                NotificationMessage stateChange = null;
                if (game.isInCheck(opposingTeam)){
                    stateChange = new NotificationMessage(String.format("%s is now in check", opposingTeam));
                }
                else if (game.isInCheckmate(opposingTeam)){
                    stateChange = new NotificationMessage(String.format("%s is now in checkmate", opposingTeam));
                }
                else if (game.isInStalemate(opposingTeam)){
                    stateChange = new NotificationMessage(String.format("%s is now in stalemate", opposingTeam));
                }

                if (stateChange != null){
                    connections.broadcastAll(gameID, session, stateChange);
                }
            }
            else {
                throw new InvalidMoveException("This move is not valid");
            }

        } catch (DataAccessException | InvalidMoveException | UnauthorizedException | NullPointerException ex){
            ErrorMessage errorMessage = new ErrorMessage("Error: cannot make this move");
            connections.broadcast(gameID, session, errorMessage);
        }


    }

    private void resign(String userName, Session session, int gameID) throws IOException, DataAccessException, ServerException {
        //marks the game as over (no more moves can be made)
        try {
            GameManager gameManager = connections.get(gameID);
            if (!(userName.equals(gameManager.blackUserName) || userName.equals(gameManager.whiteUserName))) {
                throw new ServerException("Observers can't resign");
            }
            if (gameManager.game == null){
                throw new ServerException("Opponent already resigned");
            }
            //TODO how do I mark as no more moves?
            gameManager.setGame(null);
            //game is updated in database
            DataAccess dataAccess = userService.getDataAccess();
            connections.updateGame(dataAccess, userName, gameID, ChessGame.TeamColor.WHITE, null);
            //notification to all clients that the root client has resigned (sent to players and observers)
            NotificationMessage notification = new NotificationMessage(String.format("%s resigned", userName));
            connections.broadcastAll(gameID, session, notification);
        } catch(ServerException ex) {
            ErrorMessage errorMessage = new ErrorMessage("Error: unable to resign");
            connections.broadcast(gameID, session, errorMessage);
        }
    }

    private void leave(String userName, Session session, int gameID, String authToken) throws DataAccessException, IOException {
        //game is updated to remove the client
        GameManager gameManager = connections.get(gameID);
        ChessGame game = gameManager.game;
        //game is updated in the database
        DataAccess dataAccess = userService.getDataAccess();
        if (userName.equals(gameManager.whiteUserName) || userName.equals(gameManager.blackUserName)) {
            ChessGame.TeamColor playerColor = getPlayerColor(authToken, gameID, userName);
            connections.updatePlayers(dataAccess, null, gameID, playerColor, gameManager.whiteUserName, gameManager.blackUserName, game);

            //notification to all other clients informing that the root player left
        }
        NotificationMessage notification = new NotificationMessage(String.format("%s left", userName));
        connections.broadcast(gameID, session, notification);
        connections.remove(session, gameID, userName);

    }



}
