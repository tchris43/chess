package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;


public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
            .post("/user", this::registerUser);
    }

    private void registerUser(Context ctx) {
        RegisterRequest registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        registerRequest = RegisterService(registerRequest);
        ctx.result(new Gson().toJson(registerRequest));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
