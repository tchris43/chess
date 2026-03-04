package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;

import io.javalin.http.UnauthorizedResponse;
import model.*;
import service.UserService;



public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryDataAccess());

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser)
            .post("/session", this::loginUser)
            .delete("/session", this::logoutUser)
            .get("/game", this::listGames)
            .post("/game", this::createGame)
            .put("/game", this::joinGame)
            .delete("/db", this::clearApplication)
            .exception(ServerException.class, this::ServerExceptionHandler);
    }

    private void ServerExceptionHandler(ServerException exception, Context ctx){
        ctx.status(500);
        ErrorResponse errorResponse = new ErrorResponse("Error: (description of error)");
        ctx.result(new Gson().toJson(errorResponse));
    }

    private void registerUser(Context ctx) throws DataAccessException {
        UserData registerRequest = new Gson().fromJson(ctx.body(), UserData.class);
        try {
            LoginResult registerResult = userService.register(registerRequest);
            ctx.result(new Gson().toJson(registerResult));
        }
        catch(DataAccessException e) {
            ctx.status(403).json(e.getMessage());
        }
    }

    private void loginUser(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);
        ctx.result(new Gson().toJson(loginResult));
    }

    private void logoutUser(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.body(), String.class);
        userService.logout(authToken);
        ctx.status(200);
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.body(), String.class);
        try {
            GameList gameList = userService.listGames(authToken);
            ctx.result(new Gson().toJson(gameList));
        }
        catch(DataAccessException e) {
            ctx.status(500).json(e.getMessage());
        }

    }

    private void createGame(Context ctx) throws DataAccessException {
        GameRequest gameRequest = new Gson().fromJson(ctx.body(), GameRequest.class);
        GameResult gameResult = userService.createGame(gameRequest.authToken(), gameRequest.gameName());
        ctx.result(new Gson().toJson(gameResult));
    }

    private void joinGame(Context ctx) throws DataAccessException {
        try {
            JoinRequest joinRequest = new Gson().fromJson(ctx.body(), JoinRequest.class);
            String resultingGameName = userService.joinGame(joinRequest.authToken(), joinRequest.playerColor(), joinRequest.gameID());
            ctx.result(new Gson().toJson(resultingGameName));
        }
        catch(DataAccessException e) {
            ctx.status(500).json(e.getMessage());
        }
    }



    private void clearApplication(Context ctx) throws ServerException{
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
