package server;


import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import model.UserData;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser);
    }

    private void registerUser(Context ctx) {
        UserData registerRequest = new Gson().fromJson(ctx.body(), UserData.class);
        registerResult = RegisterService(registerRequest);
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
