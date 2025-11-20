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
}
