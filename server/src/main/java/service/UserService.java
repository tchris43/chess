package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
import server.ServerException;

import java.util.List;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public boolean isAuthorized(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = dataAccess.getAuth(authToken);
        return authData != null;
    }

    public static String generateToken(){
        return UUID.randomUUID().toString();
    }

    public boolean registerMalformed(UserData registerRequest){
        return registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null;
    }

    public LoginResult register(UserData registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {
        //check for proper json
        if (registerMalformed(registerRequest)){
            throw new BadRequestException("Error: bad request");
        }
        //check the name is not taken
        if (dataAccess.getUser(registerRequest.username()) != null){
            throw new AlreadyTakenException("Error: already taken");
        }
        String username = dataAccess.createUser(registerRequest.username(), registerRequest);
        String authToken = generateToken();
        dataAccess.createAuth(authToken, new AuthData(authToken, username));
        return new LoginResult(username, authToken);
    }

    public boolean loginMalformed(LoginRequest loginRequest){
        return (loginRequest.username() == null || loginRequest.password() == null);
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException, ServerException{
        //check JSON
        if (loginMalformed(loginRequest)){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = dataAccess.getUser(loginRequest.username());
        String authToken = generateToken();
        dataAccess.createAuth(authToken, new AuthData(authToken, loginRequest.username()));
        //TODO check I have successfully unhashed the password
        boolean pwMatch;
        try {
            if (dataAccess.getClass().equals(MemoryDataAccess.class)) {
                pwMatch = loginRequest.password() == user.password();
            } else {
                if (user != null) {
                    pwMatch = BCrypt.checkpw(loginRequest.password(), user.password());
                } else {
                    pwMatch = false;
                }
            }
            if (user != null && pwMatch) {
                AuthData authData = dataAccess.getAuth(authToken);
                return new LoginResult(user.username(), authData.authToken());
            }
            throw new UnauthorizedException("Error: unauthorized");
        } catch(NullPointerException e){
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException{
        if (isAuthorized(authToken)) {
            dataAccess.deleteAuth(authToken);
        }
        else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public GameList listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (isAuthorized(authToken)){
            return dataAccess.listGames();
        }
        else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public boolean gameMalformed(String authToken, String gameName){
        return (authToken == null || gameName == null);
    }

    public GameResult createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException, BadRequestException {
        if (gameMalformed(authToken, gameName)){
            throw new BadRequestException("Error: bad request");
        }

        if (isAuthorized(authToken)){
            int gameID = dataAccess.createGame(gameName);
            return new GameResult(gameID);
        }
        else {
            throw new UnauthorizedException("Error: unauthorized");
        }

    }

    public boolean joinMalformed(String authToken, ChessGame.TeamColor playerColor, int gameID) {
        return (authToken == null || playerColor == null || gameID < 1);
    }

    public String joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID)
            throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        if (joinMalformed(authToken, playerColor, gameID)){
            throw new BadRequestException("Error: bad request");
        }
        if (isAuthorized(authToken)){
            GameData game = dataAccess.getGame(gameID);
            AuthData authData = dataAccess.getAuth(authToken);
            String username = authData.username();
            if (game != null){
                GameData updatedGame = dataAccess.updateGame(username, gameID, playerColor, game.whiteUsername(), game.blackUsername(),
                        game.gameName(), game.game());
                return updatedGame.gameName();
            }
            else {
                throw new DataAccessException("Game does not exist");
            }

        }
        else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void clear() throws ServerException, DataAccessException {
        dataAccess.deleteAllGames();
        dataAccess.deleteAllUsers();
        dataAccess.deleteAllAuths();
    }

    public List<UserData> getUsers() throws DataAccessException{
        return dataAccess.getUsers();
    }

    public List<AuthData> getAuths() throws DataAccessException{
        return dataAccess.getAuths();
    }

}
