package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

import static java.sql.Types.NULL;

//TODO add better error messages

//TODO intial testing

//TODO go through error cases after
//TODO final tests
//TODO past service tests

public class MySqlDataAccess implements DataAccess{
    //This implementation will be very similar to MemoryDataAccess but will configureDatabase() and will have
    // to call a separate function to read the JSON into a GSON when querying the data
    public MySqlDataAccess() throws DataAccessException{
        configureDataBase();
    }

    public String createUser(String username, UserData registerRequest) throws DataAccessException {
        //add the user to the database with key or primary key as username, and the rest of the data as well
        var users = getUsers();
        for (UserData user : users){
            if (user.username().equals(username)){
                throw new AlreadyTakenException("Error: Username already taken");
            }
        }
        var statement = "INSERT INTO users (username, password, email) VALUES (?,?,?)";

        String password = registerRequest.password();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        executeUpdate(statement, username, hashedPassword, registerRequest.email());
        return username;
    }

    public void createAuth(String authToken, AuthData authRequest) throws DataAccessException {
        var auths = getAuths();
        for (AuthData auth : auths){
            if (auth.authToken().equals(authToken)){
                throw new AlreadyTakenException("That auth is already created");
            }
        }
        var statement = "INSERT INTO auths (authToken, username) VALUES (?,?)";
        executeUpdate(statement, authToken, authRequest.username());
    }

    public List<UserData> getUsers() throws DataAccessException {
        var result = new UserList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT * FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while(rs.next()){
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
        var users = getUsers();
        boolean exists = false;
        for (UserData user : users){
            if (user.username().equals(username)){
                exists = true;
            }
        }
        if (!exists){
            return null;
        }

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
        var auths = getAuths();
        boolean exists = false;
        for (AuthData auth: auths){
            if (auth.authToken().equals(authToken)){
                exists = true;
            }
        }
        if (!exists){
            throw new UnauthorizedException("Error: Invalid authToken");
        }

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

    public void deleteAuth(String authToken) throws UnauthorizedException, DataAccessException {
        var auths = getAuths();
        boolean valid = false;
        for (AuthData auth : auths){
            if (auth.authToken().equals(authToken)){
                valid = true;
            }
        }
        if (!valid){
            throw new UnauthorizedException("Error: Unauthorized");
        }
        var statement = "DELETE FROM auths WHERE authToken=?";
        executeUpdate(statement, authToken);

    }

    public GameList listGames() throws DataAccessException {
        var result = new GameList();
        try(Connection conn = DatabaseManager.getConnection()) {
            var statement  = "SELECT * FROM games";
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

    public int createGame(String gameName) throws DataAccessException {
        var games = listGames();
        for (GameData g : games) {
            if (g.gameName().equals(gameName)){
                throw new AlreadyTakenException("Error: game name already taken");
            }
        }
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?,?,?,?)";
        ChessGame newGame = new ChessGame();
        String game = new Gson().toJson(newGame);
        //TODO: verify this is the correct way to get ID
        int gameID = executeUpdate(statement, null, null, gameName, game);
        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException{
        var games = listGames();
        boolean exists = false;
        for (GameData game : games){
            if (game.gameID()==gameID){
                exists = true;
            }
        }
        if (!exists){
            throw new DataAccessException("Error: This game does not exist");
        }

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
                               String blackUsername, String gameName, ChessGame game) throws AlreadyTakenException, DataAccessException {
        //TODO verify that the user should be null when updating

        updatePlayers(ChessGame.TeamColor.WHITE, username, whiteUsername, blackUsername);

        String[] usernames = updatePlayers(ChessGame.TeamColor.WHITE, username, whiteUsername, blackUsername);

        whiteUsername = usernames[0];
        blackUsername = usernames[1];

        //TODO update the state with deserializing and modifying instead of doing this
        //TODO verify that I do not need to add gameID to update
        //TODO verify this is all I need to do and I can return the GameData like this
        var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        //TODO verify that it is ok that I changed the hierarchy
        var gameJson = new Gson().toJson(game);
        executeUpdate(statement, whiteUsername, blackUsername, gameName, gameJson, gameID);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);

    }

    //TODO verify this is all I need to do for deletion
    //TODO verify that it is ok I changed all methods in hierarchy
    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    public void deleteAllAuths() throws DataAccessException {
        var statement = "TRUNCATE auths";
        executeUpdate(statement);
    }

    //TODO verify this is all I need to do
    private UserData readUser(ResultSet rs) throws DataAccessException {
        try {
            var username = rs.getString("username");
            var password = rs.getString("password");
            var email = rs.getString("email");
            UserData user = new UserData(username, password, email);
            return user;
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    private AuthData readAuth(ResultSet rs) throws DataAccessException{
        try {
            var authToken = rs.getString("authToken");
            var username = rs.getString("username");
            AuthData auth = new AuthData(authToken, username);
            return auth;
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    private GameData readGame(ResultSet rs) throws DataAccessException{
        try {
            var gameID = rs.getInt("gameID");
            var whiteUsername = rs.getString("whiteUsername");
            var blackUsername = rs.getString("blackUsername");
            var gameName = rs.getString("gameName");
            var json = rs.getString("game");
            ChessGame game = new Gson().fromJson(json, ChessGame.class);
            GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            return gameData;
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }

    }

    //TODO: verify executeUpdate works properly
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i+1, p);
                    else if (param instanceof Integer p) ps.setInt(i+1, p);
                    else if (param instanceof ChessGame p) ps.setString(i+1, p.toString());
                    else if (param == null) ps.setNull(i+1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private final String[] createUsersStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] createAuthsStatements = {
            """
            CREATE TABLE IF NOT EXISTS auths (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`)
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
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void createTable(Connection conn, String[] table) throws DataAccessException{
        for(String statement: table){
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }




    private void configureDataBase() throws DataAccessException {
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
