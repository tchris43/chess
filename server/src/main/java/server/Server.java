package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;

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
            .clear("/db", this::clearApplication);
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
        try {
            LoginResult loginResult = userService.login(loginRequest);
            ctx.result(new Gson().toJson(loginResult));
        }
        catch(DataAccessException e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    private void logoutUser(Context ctx) throws DataAccessException {
        String authToken = new Gson().fromJson(ctx.body(), String.class);
        userService.logout(authToken);
        ctx.status(200);
    }

    private void clearApplication(Context ctx) throws DataAccessException{
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
