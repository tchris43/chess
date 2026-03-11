package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//TODO will I run into issues if I just do basic functionality before doing negative cases?

public class MySqlDataAccess implements DataAccess{
    //This implementation will be very similar to MemoryDataAccess but will configureDatabase() and will have
    // to call a separate function to read the JSON into a GSON when querying the data
    public MySqlDataAccess() throws DataAccessException, SQLException{
        configureDataBase();
    }

    public String createUser(String username, UserData registerRequest){
        //add the user to the database with key or primary key as username, and the rest of the data as well
        var statement = "INSERT INTO users (username, password, email, json) VALUES (?,?,?,?)";
        var json = new Gson().toJson(registerRequest);
        //TODO: write executeUpdate helper
        executeUpdate(statement, username, registerRequest.password(), registerRequest.email(), json);
        //TODO I need to add a json field to users? So just use JSON for things that aren't strings
        //TODO I need to write my own executeUpdate method?

        //TODO: Ask for walkthrough of executeUpdate() helper method
        //ask about the ...
        return username;
    }

    public void createAuth(String authToken, AuthData authRequest){
        var statement = "INSERT INTO auths (authToken, username, json) VALUES (?,?,?)";
        var json = new Gson().toJson(authRequest);
        executeUpdate(statement, authToken, authRequest.username(), json);
    }

    public List<UserData> getUsers() throws DataAccessException {
        var result = new UserList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT username, password, email FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        //TODO: Write readUser
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (SQLException e) {
            //TODO Understand what errors I am supposed to create and when to throw. Runtime wil crash. Follow Petshop.
            throw new RuntimeException(e);
        }

        return result;

    }

    public List<AuthData> getAuths() throws DataAccessException{
        var result = new AuthList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT authToken, username FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        //TODO: Write readAuth
                        //TODO: DO I need readAuth for these things because they are just strings, or do I only need readAuth for json?
                        //TODO: If I do it this way, what is the point of json?
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return result;
    }

    public UserData getUser(String username) throws DataAccessException {
        //TODO understand this from petshop: ps.setInt(1, id);
        try(Connection conn = DatabaseManager.getConnection()){
            //TODO figure out if I am getting the json and converting before returning it
            var statement = "SELECT * FROM users WHERE username=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                //TODO figure out if this the correct way to search for the user
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {
        //TODO same as getUser
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT json FROM auths WHERE authToken=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auths WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public GameList listGames(){
        //TODO same as getAuths and getUsers
        var result = new GameList();
        try(Connection conn = DatabaseManager.getConnection()) {
            //TODO do I want to select json or the individual params?
            var statement  = "SELECT json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return result;
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

    public UserData readUser(ResultSet rs){

        return null;
    }

    public AuthData readAuth(ResultSet rs){

        return null;
    }

    public GameData readGame(){

        return null;
    }

    //TODO: Figure out if adding the json field is correct and if the general schema is good
    private final String[] createUsersStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createAuthsStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL
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
            `game` TEXT DEFAULT NULL,
            `json` TEXT DEFAULT NULL
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
