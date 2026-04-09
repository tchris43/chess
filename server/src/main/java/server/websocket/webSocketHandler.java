package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.websocket.*;



import model.AuthData;
import model.GameData;
import model.GameList;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
                case MAKE_MOVE -> makeMove(userName, ctx.session, command.getMove(), command.getGameID());
//                case LEAVE -> leave(userName, ctx.session);
//                case RESIGN -> resign(userName, ctx.session);
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

    private void makeMove(String userName, Session session, ChessMove move, int gameID){
        //verify move validity
        //get the piece by the position on board
        //check if the move is in pieceMoves
        boolean valid = false;
        ChessGame game = connections.get(gameID).game;
        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        for (ChessMove m : validMoves){
            if (m.equals(move)){
                valid = true;
            }
        }
        if (valid){

        }


        //update the game

        //Load game to all clients

        //notify all other clients of move

        //notify all clients if in check, checkmate or stalemate
    }


}
