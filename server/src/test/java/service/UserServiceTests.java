package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    UserService userService = new UserService(new MemoryDataAccess());

    @Test
    void registerUser() throws DataAccessException {
        UserData newUser = new UserData("taylor", "password", "tchris.gmail.com");
        userService.register(newUser);

        Collection<UserData> users = userService.getUsers();
        Collection<AuthData> auths = userService.getAuths();

        assertEquals(1, users.size());
        assertEquals(1, auths.size());
        assertTrue(users.contains(newUser));
    }

    @Test
    void newUsernameOnly() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        userService.register(user);

        UserData newUser = new UserData("taylor", "password", "tchris.gmail.com");

        assertThrows(DataAccessException.class, () ->
                userService.register(newUser));
    }

    @Test
    void login() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.logout(authToken);

        Collection<UserData> users = userService.getUsers();
        Collection<AuthData> auths = userService.getAuths();

        assertEquals(1, users.size());
        assertEquals(0, auths.size());

        LoginRequest loginRequest = new LoginRequest("taylor", "password");
        LoginResult loginResult = userService.login(loginRequest);

        Collection<UserData> newUsers = userService.getUsers();
        Collection<AuthData> newAuths = userService.getAuths();

        assertEquals(1, newUsers.size());
        assertEquals(1, newAuths.size());

    }

    @Test
    void invalidLoginAttempt() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        userService.register(user);

        LoginRequest loginRequest = new LoginRequest("tay", "pass");
        assertThrows(DataAccessException.class, () ->
                userService.login(loginRequest));
    }

    @Test
    void logoutSuccessful() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.logout(authToken);
        Collection<UserData> users = userService.getUsers();
        Collection<AuthData> auths = userService.getAuths();

        assertEquals(1, users.size());
        assertEquals(0, auths.size());
    }

    @Test
    void logoutInvalid() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.logout(authToken);

        assertThrows(DataAccessException.class, () ->
                userService.logout(authToken));
    }

//    @Test
//    void clear() throws DataAccessException {
//        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
//        LoginResult registerResult = userService.register(user);
//
//        userService.clear();
//
//        Collection<UserData> users = userService.getUsers();
//        Collection<AuthData> auths = userService.getAuths();
//
//        assertEquals(0, users.size());
//        assertEquals(0, auths.size());
//    }

    @Test
    void listGames() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.createGame(authToken,"game");

        GameList games = userService.listGames(authToken);

        assertEquals(1, games.size());

    }

    @Test
    void listGamesUnauthorized() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();
        userService.logout(authToken);


        assertThrows(DataAccessException.class, () ->
                userService.listGames(authToken));

    }





    @Test
    void createGame() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        GameList games = userService.listGames(authToken);
        assertEquals(0, games.size());

        userService.createGame(authToken,"game");

        GameList newGames = userService.listGames(authToken);
        assertEquals(1, newGames.size());



    }

    @Test
    void createGamesUnauthorized() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();
        userService.logout(authToken);


        assertThrows(DataAccessException.class, () ->
                userService.createGame(authToken,"game"));

    }

    @Test
    void joinGame() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.createGame(authToken, "newGame");

        userService.joinGame(authToken, ChessGame.TeamColor.WHITE, 1);

        GameList games = userService.listGames(authToken);

        assertEquals("taylor", games.getFirst().whiteUsername());

    }

    @Test
    void joinGameAlreadyTaken() throws DataAccessException {
        UserData user = new UserData("taylor", "password", "tchris.gmail.com");
        LoginResult registerResult = userService.register(user);
        String authToken = registerResult.authToken();

        userService.createGame(authToken, "newGame");

        userService.joinGame(authToken, ChessGame.TeamColor.WHITE, 1);

        UserData newUser = new UserData("tay", "password", "tchris.gmail.com");
        LoginResult newRegisterResult = userService.register(newUser);
        String newAuthToken = newRegisterResult.authToken();

        assertThrows(DataAccessException.class, () ->
                userService.joinGame(authToken, ChessGame.TeamColor.WHITE, 1));

    }

}
