package client;

import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String url = "http://localhost:"+port;
        facade = new ServerFacade(url);
    }

//    @BeforeAll
//    public static void clear() {
//
//    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        LoginResult registerResult = facade.register(registerRequest);
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void badRegisterRequest() throws ResponseException {
        UserData registerRequest = new UserData("testUser", null, "email");
        assertThrows(ResponseException.class, () -> {facade.register(registerRequest);});
    }

}
