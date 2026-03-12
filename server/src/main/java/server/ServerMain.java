package server;

import dataaccess.DataAccessException;

import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
