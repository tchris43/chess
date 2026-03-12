package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

//TODO add better error messages
//TODO verify DataAccessException is the right exception for connection fails (Petshop or TA?)
//TODO go through error cases after
//TODO add password hashing

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
            var statement  = "SELECT * FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        //TODO: Write readUser
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return result;

    }

    public List<AuthData> getAuths() throws DataAccessException{
        var result = new AuthList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT * FROM auths";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        //TODO: Write readAuth
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return result;
    }

    public UserData getUser(String username) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM users WHERE username=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return readUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public AuthData getAuth(String authToken) throws DataAccessException, UnauthorizedException {
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM auths WHERE authToken=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, authToken);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return readAuth(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auths WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    public GameList listGames() throws DataAccessException {
        var result = new GameList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT json FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return result;
    }

    public int createGame(String gameName){
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)";
        ChessGame game = new ChessGame();
        //TODO: verify this is the correct way to get ID
        int gameID = executeUpdate(statement, null, null, gameName, game);
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games WHERE gameID=?";
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try(ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    private boolean notTaken(String username){

        return username == null;
    }

    public GameData updateGame(String username, int gameID, ChessGame.TeamColor playerColor, String whiteUsername,
                               String blackUsername, String gameName, ChessGame game) throws AlreadyTakenException {
        //TODO verify that the user should be null when updating
        String statement;
        if (playerColor == ChessGame.TeamColor.WHITE){
            if (notTaken(whiteUsername)) {
                whiteUsername = username;
            }
            else {
                throw new AlreadyTakenException("Error: already taken");
            }
        }
        else {
            if (notTaken(blackUsername)) {
                blackUsername = username;
            }
            else {
                throw new AlreadyTakenException("Error: already taken");
            }

        }

        GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);

        //TODO verify that I do not need to add gameID to update
        //TODO verify this is all I need to do and I can return the GameData like this
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        executeUpdate(statement, whiteUsername, blackUsername, gameName, game, gameID);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);

    }

    //TODO verify this is all I need to do for deletion
    public void deleteAllGames(){
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    public void deleteAllUsers() {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    public void deleteAllAuths() {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs){

        return null;
    }

    private AuthData readAuth(ResultSet rs){

        return null;
    }

    private GameData readGame(ResultSet rs){

        return null;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)){
                for(int i = 0; i < params.length, i++){
                    
                }
            }
        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
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

    private void createTable(Connection conn, String[] table) throws SQLException, DataAccessException{
        for(String statement: table){
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }




    private void configureDataBase() throws DataAccessException, SQLException {
        //This method will create the tables with all of their columns set up. The Database should be
        //created using the built-in CreateDatabase method in DatabaseManager.
        DatabaseManager.createDatabase();
        try(Connection conn = DatabaseManager.getConnection()){
            createTable(conn, createUsersStatements);
            createTable(conn, createAuthsStatements);
            createTable(conn, createGamesStatements);
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
