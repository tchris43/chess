package server;

import com.google.gson.Gson;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null){
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request){
        if (request != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException{
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex){
            throw new ResponseException(ex.getMessage());
        }
    }




}
