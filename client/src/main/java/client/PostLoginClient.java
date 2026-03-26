package client;

import model.UserData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginClient {
    private final ServerFacade server;
    private boolean loggedIn = true;
    private boolean done = false;

    public PostLoginClient(ServerFacade serverFacade) throws ResponseException {
        server = serverFacade;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public boolean isDone() {
        return done;
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
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        }
        catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String quit() {
        done = true;
        return "quit";
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

    public String logout() throws ResponseException{
        server.logout();
        loggedIn = false;
        return "quit";
    }
}
