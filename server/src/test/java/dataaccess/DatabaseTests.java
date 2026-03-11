package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.ServerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTests {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db;
        if (databaseClass.equals(MySqlDataAccess.class)){
            db = new MySqlDataAccess();
        }
        else {
            db = new MemoryDataAccess();
        }
        db.deleteAllGames();
        db.deleteAllAuths();
        db.deleteAllUsers();
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";
        var user = new UserData(username, "password", "taylor@gmail.com");
        assertDoesNotThrow(() -> db.createUser(username, user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUserFails(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        //create the same user twice fails
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";
        UserData userData = new UserData(username, "password", "email");
        db.createUser("taylor", userData);

        assertThrows(DataAccessException.class, () -> {db.createUser(username, userData);});
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createAuth(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        String authToken = "auth1";
        AuthData authData = new AuthData(authToken, "taylor");
        assertDoesNotThrow(() -> db.createAuth(authToken, authData));
    }

    

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getUsers(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        UserData user1 = new UserData("user1", "pass", "email");
        UserData user2 = new UserData("user2", "pass", "email");
        UserData user3 = new UserData("user3", "pass", "email");

        List<UserData> expected = new ArrayList<>();
        expected.add(user1);
        expected.add(user2);
        expected.add(user3);

        db.createUser("user1", user1);
        db.createUser("user2", user2);
        db.createUser("user3", user3);


        Collection<UserData> actual = db.getUsers();
        assertUserCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getAuths(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        AuthData auth1 = new AuthData("auth1", "user1");
        AuthData auth2 = new AuthData("auth2", "user2");

        List<AuthData> expected = new ArrayList<>();
        expected.add(auth1);
        expected.add(auth2);

        db.createAuth("auth1", auth1);
        db.createAuth("auth2", auth2);

        List<AuthData> actual = db.getAuths();
        assertAuthCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getUser(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";

        UserData expected = new UserData(username, "pass", "email");

        db.createUser(username, expected);

        UserData actual = db.getUser(username);
        assertUserEqual(actual, expected);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getAuth(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        String authToken = "authToken";

        AuthData expected = new AuthData(authToken, "username");

        db.createAuth(authToken, expected);

        AuthData actual = db.getAuth(authToken);
        assertAuthEqual(actual, expected);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listGames(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        ChessGame game = new ChessGame();

        List<GameData> expected = new ArrayList<>();
        expected.add(new GameData(1, null, null, "game1", game));
        expected.add(new GameData(2, null, null, "game2", game));
        expected.add(new GameData(3, null, null, "game3", game));



        db.createGame("game1");
        db.createGame("game2");
        db.createGame("game3");


        Collection<GameData> actual = db.listGames();
        assertGameCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createGame(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        String gameName = "game";

        assertDoesNotThrow(() -> db.createGame(gameName));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void getGame(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        int gameID = 1;
        String gameName = "game";
        ChessGame game = new ChessGame();

        GameData expected = new GameData(gameID, null, null, gameName, game);

        db.createGame(gameName);

        GameData actual = db.getGame(gameID);
        assertGameEqual(actual, expected);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void updateGame(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);
        ChessGame game = new ChessGame();

        GameData expected = new GameData(1, "newUser", null, "game", game);

        db.createGame("game");

        GameData actual = db.updateGame("newUser", 1, ChessGame.TeamColor.WHITE, "newUser", null, "game", game);
        assertEquals(actual, expected);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deleteAuth(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        List<AuthData> expected = new ArrayList<>();
        expected.add(new AuthData("auth1", "taylor"));
        expected.add(new AuthData("auth3", "tim"));

        db.createAuth("auth1", new AuthData("auth1", "taylor"));
        db.createAuth("auth2", new AuthData("auth2", "fred"));
        db.createAuth("auth3", new AuthData("auth3", "tim"));
        db.deleteAuth("auth2");

        List<AuthData> actual = db.getAuths();
        assertAuthCollectionEqual(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deleteAllGames(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        db.createGame("game1");
        db.createGame("game2");

        db.deleteAllGames();

        GameList actual = db.listGames();
        assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deleteAllUsers(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        UserData user1 = new UserData("user1", "pass", "email");
        UserData user2 = new UserData("user2", "pass", "email");


        db.createUser("user1", user1);
        db.createUser("user2", user2);

        db.deleteAllUsers();

        List<UserData> actual = db.getUsers();
        assertEquals(0, actual.size());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void deleteAllAuths(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException, ServerException {
        DataAccess db = getDataAccess(dbClass);

        AuthData auth1 = new AuthData("auth1", "user1");
        AuthData auth2 = new AuthData("auth2", "user2");

        db.deleteAllAuths();

        List<AuthData> actual = db.getAuths();
        assertEquals(0, actual.size());
    }

    public static void assertAuthEqual(AuthData expected, AuthData actual){
        assertEquals(expected.authToken(), actual.authToken());
        assertEquals(expected.username(), actual.username());
    }

    public static void assertAuthCollectionEqual(List<AuthData> actual, List<AuthData> expected){
        AuthData[] actualList = actual.toArray(new AuthData[]{});
        AuthData[] expectedList = expected.toArray(new AuthData[]{});
        assertEquals(expectedList.length, actualList.length);
        for (int i = 0; i < actualList.length; i++){
            assertAuthEqual(expectedList[i], actualList[i]);
        }
    }

    public static void assertGameEqual(GameData expected, GameData actual) {
        assertEquals(expected.gameID(), actual.gameID());
        assertEquals(expected.whiteUsername(), actual.whiteUsername());
        assertEquals(expected.blackUsername(), actual.blackUsername());
        assertEquals(expected.gameName(), actual.gameName());
        assertEquals(expected.game(), actual.game()); //IF this is failing then the game comparison may not work. Try without this line
    }


    public static void assertGameCollectionEqual(Collection<GameData> expected, Collection<GameData> actual){
        GameData[] actualList = actual.toArray(new GameData[]{});
        GameData[] expectedList = expected.toArray(new GameData[]{});
        assertEquals(expectedList.length, actualList.length);
        for (int i = 0; i < actualList.length; i++){
            assertGameEqual(expectedList[i], actualList[i]);
        }
    }

    public static void assertUserEqual(UserData expected, UserData actual) {
        assertEquals(expected.username(), actual.username());
        assertEquals(expected.password(), actual.password());
        assertEquals(expected.email(), actual.email());
    }

    public static void assertUserCollectionEqual(Collection<UserData> expected, Collection<UserData> actual){
        UserData[] actualList = actual.toArray(new UserData[]{});
        UserData[] expectedList = expected.toArray(new UserData[]{});
        assertEquals(expectedList.length, actualList.length);
        for (int i = 0; i < actualList.length; i++){
            assertUserEqual(expectedList[i], actualList[i]);
        }
    }

}
