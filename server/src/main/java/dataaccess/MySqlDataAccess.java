package dataaccess;

import chess.ChessGame;
import model.*;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MySqlDataAccess implements DataAccess{
    //This implementation will be very similar to MemoryDataAccess but will configureDatabase() and will have
    // to call a separate function to read the JSON into a GSON when querying the data
    public MySqlDataAccess() throws DataAccessException{
        configureDataBase();
    }

    public String createUser(String username, UserData registerRequest){

        return username;
    }

    public void createAuth(String authToken, AuthData authRequest){

    }

    public List<UserData> getUsers() {

        return List.of();
    }

    public List<AuthData> getAuths() {

        return List.of();
    }

    public UserData getUser(String username) {

        return null;
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {

        return null;
    }

    public void deleteAuth(String authToken) {

    }

    public GameList listGames(){

        return null;
    }

    public int createGame(String gameName){

        return 0;
    }

    public GameData getGame(int gameID){

        return null;
    }

    public boolean notTaken(String username){

        return false;
    }

    public GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                               String blackUsername, String gameName, ChessGame game) throws AlreadyTakenException {

        return null;
    }

    public void deleteAllGames(){

    }

    public void deleteAllUsers() {
    }

    public void deleteAllAuths() {

    }

    public UserData readUser(){

        return null;
    }

    public AuthData readAuth(){

        return null;
    }

    public GameData readGame(){

        return null;
    }

    private final String[] createUsersStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createAuthsStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createGamesStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL, 
            `gameName` varchar(256) NOT NULL, 
            `game` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public void createTable(Connection conn, String[] table) throws SQLException{
        for(String statement: table){
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException(e.getMessage());
            }
        }
    }




    public void configureDataBase() throws DataAccessException, SQLException {
        //This method will create the tables with all of their columns set up. The Database should be
        //created using the built-in CreateDatabase method in DatabaseManager.
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()){
            createTable(conn, createUsersStatements);
            createTable(conn, createAuthsStatements);
            createTable(conn, createGamesStatements);
        } catch (SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
}
