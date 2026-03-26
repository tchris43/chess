package client;

import model.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginClient {
    private final ServerFacade server;

    public PreLoginClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public ServerFacade getServer(){
        return server;
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (result != "quit") {
                    System.out.print(result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void printPrompt(){
        System.out.print("\n" + "[LOGGED_OUT] >>> ");
    }

    public String eval(String input) throws ResponseException{
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd){
                case "register" -> register(params);
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }

    public String register(String... params) throws ResponseException{
        UserData registerRequest = new UserData(params[0], params[1], params[2]);
        server.register(registerRequest);
        System.out.printf("Logged in as %s", params[0]);
        return "quit";
    }

}
