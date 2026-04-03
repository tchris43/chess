package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import io.javalin.http.Context;

import io.javalin.http.UnauthorizedResponse;
import model.*;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

import java.sql.SQLException;

import server.websocket.webSocketHandler;


public class Server {

    private final Javalin javalin;
    private final UserService userService;

    public Server() {
        try {
            userService = new UserService(new MySqlDataAccess());
        } catch(DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }

        webSocketHandler = new WebSocketHandler();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser)
            .post("/session", this::loginUser)
            .delete("/session", this::logoutUser)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .delete("/db", this::clearApplication)
            .exception(ServerException.class, this::serverExceptionHandler)
            .exception(BadRequestException.class, this::badRequestExceptionHandler)
            .exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler)
            .exception(UnauthorizedException.class, this::unauthorizedExceptionHandler)
            .exception(DataAccessException.class, this::dataAccessExceptionHandler)
            .ws("/ws", ws -> {
                ws.onConnect(webSocketHandler);
                ws.onMessage(webSocketHandler);
                ws.onClose(webSocketHandler);
            });
    }

    private void dataAccessExceptionHandler(DataAccessException exception, Context ctx){
        ctx.status(500);
        ErrorResponse errorResponse = new ErrorResponse("Error: Server failure");
        ctx.result(new Gson().toJson(errorResponse));
    }

    private void serverExceptionHandler(ServerException exception, Context ctx){
        ctx.status(500);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        ctx.result(new Gson().toJson(errorResponse));
    }

    private void badRequestExceptionHandler(BadRequestException exception, Context ctx){
        ctx.status(400);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        ctx.result(new Gson().toJson(errorResponse));
    }

    private void alreadyTakenExceptionHandler(AlreadyTakenException exception, Context ctx){
        ctx.status(403);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        ctx.result(new Gson().toJson(errorResponse));
    }

    private void unauthorizedExceptionHandler(UnauthorizedException exception, Context ctx){
        ctx.status(401);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        ctx.result(new Gson().toJson(errorResponse));
    }



    private void registerUser(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException, ServerException {
        UserData registerRequest = new Gson().fromJson(ctx.body(), UserData.class);
        LoginResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    private void loginUser(Context ctx) throws BadRequestException, UnauthorizedException, DataAccessException, ServerException{
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    private void logoutUser(Context ctx) throws UnauthorizedException, ServerException, DataAccessException {
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws ServerException, UnauthorizedException, DataAccessException {
        String authToken = ctx.header("authorization");
        GameList gameList = userService.listGames(authToken);
        GameListResult gameListResult = new GameListResult(gameList);
        ctx.result(new Gson().toJson(gameListResult));

    }

    private void createGame(Context ctx) throws BadRequestException, UnauthorizedException, ServerException, DataAccessException {
        String authToken = ctx.header("authorization");
        GameRequest gameRequest = new Gson().fromJson(ctx.body(), GameRequest.class);
        GameResult gameResult = userService.createGame(authToken, gameRequest.gameName());
        ctx.result(new Gson().toJson(gameResult));
    }

    private void joinGame(Context ctx) throws DataAccessException, BadRequestException,
            UnauthorizedException, AlreadyTakenException, ServerException {
        try {
            String authToken = ctx.header("authorization");
            JoinRequest joinRequest = new Gson().fromJson(ctx.body(), JoinRequest.class);
            String resultingGameName = userService.joinGame(authToken, joinRequest.playerColor(), joinRequest.gameID());
            UpdatedGameResult updatedGameResult = new UpdatedGameResult(resultingGameName);
            ctx.result(new Gson().toJson(updatedGameResult));
        }
        catch(DataAccessException exception){
            ctx.status(500);
            ErrorResponse errorResponse = new ErrorResponse("Error: DataAccess Error");
            ctx.result(new Gson().toJson(errorResponse));
        }

    }



    private void clearApplication(Context ctx) throws ServerException, DataAccessException{
        userService.clear();
        ctx.status(200);
    }


    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
