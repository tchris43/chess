package client;

import model.UserData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;

    public PostLoginClient(ServerFacade serverFacade) throws ResponseException {
        server = serverFacade;
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
        System.out.print("\n" + "[LOGGED_IN] >>> ");
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
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
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
