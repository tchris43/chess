package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.util.Collection;

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
        facade.resetAuth();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        LoginResult registerResult = facade.register(registerRequest);
        assertNotNull(facade.getAuth());
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
        facade.logout();
        LoginRequest loginRequest = new LoginRequest("testUser","pass");
        facade.login(loginRequest);
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
    public void joinGame() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameRequest gameRequest = new GameRequest("game1");
        GameResult gameResult = facade.createGame(gameRequest);

        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, gameResult.gameID());
        assertDoesNotThrow(() -> {
            facade.joinGame(joinRequest);
        });
    }

    @Test
    public void joinGameAlreadyTaken() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameRequest gameRequest = new GameRequest("game1");
        GameResult gameResult = facade.createGame(gameRequest);

        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.WHITE, gameResult.gameID());
        facade.joinGame(joinRequest);
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(joinRequest);
        });
    }

    @Test
    public void createGame() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameRequest gameRequest = new GameRequest("testGame");
        facade.createGame(gameRequest);

        GameListResult gameListResult = facade.listGames();

        assertEquals(1, gameListResult.games().size());

    }

    @Test
    public void invalidCreateGame() throws ResponseException {
        GameRequest gameRequest = new GameRequest("testGame");
        assertThrows(ResponseException.class, () -> {
            facade.createGame(gameRequest);
        });
    }



    @Test
    public void listGames() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameRequest gameRequest = new GameRequest("testGame");
        GameRequest gameRequest2 = new GameRequest("testGame2");

        facade.createGame(gameRequest);
        facade.createGame(gameRequest2);

        GameListResult gameListResult = facade.listGames();

        assertEquals(2, gameListResult.games().size());

    }

    @Test
    public void invalidGameList() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameRequest gameRequest = new GameRequest("testGame");
        GameRequest gameRequest2 = new GameRequest("testGame2");

        facade.createGame(gameRequest);
        facade.createGame(gameRequest2);

        facade.logout();

        assertThrows(ResponseException.class, () -> {
            facade.listGames();
        });
    }

    @Test
    public void noGamesToList() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        GameListResult gameListResult = facade.listGames();

        assertEquals(0, gameListResult.games().size());


    }



    @Test
    public void logoutRemoveAuth() throws ResponseException {
        UserData registerRequest = new UserData("testUser", "pass", "email");
        facade.register(registerRequest);

        facade.logout();

        assertNull(facade.getAuth());
    }

    @Test
    public void invalidLogout() throws ResponseException {
        assertThrows(ResponseException.class, () -> {
            facade.logout();
        });
    }



}
