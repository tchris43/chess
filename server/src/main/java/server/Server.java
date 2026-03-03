package server;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;

import model.UserData;
import service.UserService;
import model.LoginResult;


public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService(new MemoryDataAccess());

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser);
    }

    private void registerUser(Context ctx) throws DataAccessException {
        UserData registerRequest = new Gson().fromJson(ctx.body(), UserData.class);
        LoginResult registerResult = userService.register(registerRequest);
        ctx.result(new Gson().toJson(registerResult));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
