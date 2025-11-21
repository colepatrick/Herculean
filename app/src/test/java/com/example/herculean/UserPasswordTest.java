package com.example.herculean;

import com.example.herculean.datahandling.UserAccount;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserPasswordTest {

    @Test
    public void passwordHashing() {
        UserAccount user = new UserAccount("testUser", "pass123", "test@gmail.com");
        assertNotEquals("pass123", user.getPassword());
        assertNotNull(user.getSalt());
        assertFalse(user.getSalt().isEmpty());
        assertTrue(user.checkPassword("pass123"));
    }

    @Test
    public void incorrectPassword() {
        UserAccount user = new UserAccount("testUser", "pass123", "test@gmail.com");
        assertFalse(user.checkPassword("wrongPassword"));
    }

    @Test
    public void samePassword() {
        UserAccount user1 = new UserAccount("user1", "commonPass", "test1@gmail.com");
        UserAccount user2 = new UserAccount("user2", "commonPass", "test2@gmail.com");
        assertNotEquals(user1.getSalt(), user2.getSalt());
        assertNotEquals(user1.getPassword(), user2.getPassword());
        assertTrue(user1.checkPassword("commonPass"));
        assertTrue(user2.checkPassword("commonPass"));
    }

    @Test
    public void passwordChange() {
        UserAccount user = new UserAccount("testUser", "pass123", "test@gmail.com");
        String initialHash = user.getPassword();
        String initialSalt = user.getSalt();
        user.setPassword("newPass456");
        assertNotEquals(initialHash, user.getPassword());
        assertNotEquals(initialSalt, user.getSalt());
        assertTrue(user.checkPassword("newPass456"));
        assertFalse(user.checkPassword("pass123"));
    }
}