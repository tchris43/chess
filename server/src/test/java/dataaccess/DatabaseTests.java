package dataaccess;

import chess.ChessGame;
import model.*
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatabaseTests {
    private DataAccess getDataAccess(Class<? extends DataAccess> databaseClass) throws SQLException, DataAccessException{
        DataAccess db;
        if (databaseClass.equals(MySqlDataAccess.class)){
            db = new MySqlDataAccess();
        }
        else {
            db = new MemoryDataAccess();
        }
        return db;
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void createUser(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException {
        DataAccess db = getDataAccess(dbClass);

        String username = "taylor";
        var user = new UserData(username, "password", "taylor@gmail.com");
        assertDoesNotThrow(() -> db.createUser(username, user));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MemoryDataAccess.class})
    void listGames(Class<? extends DataAccess> dbClass) throws SQLException, DataAccessException {
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

}
