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

    @BeforeEach
    public void clear() throws ResponseException{
        facade.clear();
    }


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
        assertThrows(ResponseException.class, () -> {
            facade.register(registerRequest);
        });
    }

    @Test
    public void duplicateRegisterRequest() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);
        assertThrows(ResponseException.class, () -> {
            facade.register(registerRequest);
        });
    }

    @Test
    public void loginValidAuth() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser","pass");
        LoginResult loginResult = facade.login(loginRequest);
        assertNotNull(facade.getAuth());
    }

    @Test
    public void invalidLogin() throws ResponseException {
        LoginRequest loginRequest = new LoginRequest("newUser", "pass");
        assertThrows(ResponseException.class, () -> {
            facade.login(loginRequest);
        });
    }


    @Test
    public void createGame() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser","pass");
        LoginResult loginResult = facade.login(loginRequest);

        GameRequest gameRequest = new GameRequest("testGame");

        assertDoesNotThrow(() -> {
            facade.createGame(gameRequest);
        });

    }

    @Test
    public void invalidCreateGame() throws ResponseException {
        GameRequest gameRequest = new GameRequest("testGame");

        assertThrows(ResponseException.class, () -> {
            facade.createGame(gameRequest);
        });
    }


    @Test
    public void logoutRemoveAuth() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser","pass");
        LoginResult loginResult = facade.login(loginRequest);

        String authToken = loginResult.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        facade.logout(logoutRequest);

        GameRequest gameRequest = new GameRequest("testGame");

        assertThrows(ResponseException.class, () -> {
            facade.createGame(gameRequest);
        });

        assertNull(loginResult.authToken());
    }

    @Test
    public void invalidLogout() throws ResponseException {
        LoginRequest loginRequest = new LoginRequest("newUser", "pass");
        assertThrows(ResponseException.class, () -> {
            facade.login(loginRequest);
        });
    }

}
