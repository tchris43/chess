package server;

import model.*

import java.net.http.HttpClient;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public LoginResult register(LoginRequest registerRequest){
        LoginResult registerResult = new LoginResult("","");
        return registerResult;
    }

    public LoginResult login(LoginRequest loginRequest){
        LoginResult loginResult = new LoginResult("","");
        return loginResult;
    }

    public void logout(LogoutRequest logoutRequest){
    }

    public GameResult createGame(GameRequest gameRequest){
        GameResult gameResult = new GameResult(0);
        return gameResult;
    }

    public GameListResult listGames(GameList gameListRequest){
        GameListResult gameListResult = new GameListResult(null);
        return gameListResult;
    }

    public UpdatedGameResult joinGame(JoinRequest joinRequest){
        UpdatedGameResult  updatedGameResult = new UpdatedGameResult("");
        return updatedGameResult;
    }




}
