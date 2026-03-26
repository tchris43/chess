package server;

import com.google.gson.Gson;
import com.sun.jdi.StringReference;
import model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String url){
        serverUrl = url;
    }

    public String getAuth(){
        return authToken;
    }

    public void resetAuth(){
        authToken = null;
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    public LoginResult register(UserData registerRequest) throws ResponseException{
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request);
        LoginResult loginResult = handleResponse(response, LoginResult.class);
        authToken = loginResult.authToken();
        return loginResult;
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException{
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(){
        //must check for authtoken
    }

    public GameResult createGame(GameRequest gameRequest){

        return null;
    }

    public GameListResult listGames(){
        //must check for authToken
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

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException{
        var status = response.statusCode();
        if (!isSuccessful(status)){
            var body = response.body();
            if (body != null){
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException("Other failure: " + status);
        }

        if (responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status){
        return status / 100 == 2;
    }




}
