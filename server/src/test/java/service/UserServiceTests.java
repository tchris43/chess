package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTests {

    UserService userService = new UserService(new MemoryDataAccess());

    @Test
    void registerUser() throws DataAccessException {
        UserData newUser = new UserData("taylor", "password", "tchris.gmail.com");
        userService.register(newUser);

        Collection<UserData> users = userService.getUsers();
        Collection<AuthData> auths = userService.getAuths();

        assertEquals(1, users.size());
        assertTrue(users.contains(newUser));
    }


}
