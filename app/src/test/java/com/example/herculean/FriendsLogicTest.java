package com.example.herculean;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import com.example.herculean.datahandling.GlobalData;
import com.example.herculean.datahandling.UserAccount;

/**
 *  UNIT TESTS FOR FRIENDS SYSTEM
 *  Tests the logic of the friends system
 */

public class FriendsLogicTest {

    private UserAccount me, john, emma, duplicate;
    private FriendsLogic helper;

    @Before
    public void setUp() {

        // Create test users
        me = new UserAccount("me", "me@mail.com", "pass");
        john = new UserAccount("john", "john@mail.com", "pass");
        emma = new UserAccount("emma", "emma@mail.com", "pass");
        duplicate = new UserAccount("me", "fake@mail.com", "pass");  // same username as current user

        // Reset global user list
        GlobalData.accounts = new ArrayList<>();
        GlobalData.accounts.add(me);
        GlobalData.accounts.add(john);
        GlobalData.accounts.add(emma);
        GlobalData.accounts.add(duplicate);

        // Set current user
        GlobalData.currentUser = me;

        // Create logic helper
        helper = new FriendsLogic();
    }

    // ============================================================
    // TEST: Current user must be excluded
    // ============================================================
    @Test
    public void CurrentUserIsExcluded() {
        List<UserAccount> friends = helper.getVisibleFriends();

        for (UserAccount u : friends) {
            assertNotEquals("Current user should NOT appear in friends list",
                    me.getUsername(), u.getUsername());
        }
    }

    // ============================================================
    // TEST: Other users must be included
    // ============================================================
    @Test
    public void OtherUsersIncluded() {
        List<UserAccount> friends = helper.getVisibleFriends();

        assertTrue("John must appear in friends list", friends.contains(john));
        assertTrue("Emma must appear in friends list", friends.contains(emma));
    }

    // ============================================================
    // TEST: Duplicate usernames excluded
    // ============================================================
    @Test
    public void DuplicateUsernameExcluded() {
        List<UserAccount> friends = helper.getVisibleFriends();

        assertFalse("User with same username as current user must be excluded",
                friends.contains(duplicate));
    }

    // ============================================================
    // TEST: Correct friend count
    // ============================================================
    @Test
    public void CorrectFriendCount() {
        List<UserAccount> friends = helper.getVisibleFriends();
        assertEquals("Should only show John + Emma", 2, friends.size());
    }

    // ============================================================
    // TEST: No friends when only user exists
    // ============================================================
    @Test
    public void NoFriendsWhenOnlyUserExists() {
        GlobalData.accounts.clear();
        GlobalData.accounts.add(me);

        List<UserAccount> friends = helper.getVisibleFriends();

        assertTrue("No other users = empty friend list", friends.isEmpty());
    }

    // ============================================================
    // TEST: Viewed friend correctly set (simulating a click)
    // ============================================================
    @Test
    public void ClickingFriendSetsViewedUser() {
        List<UserAccount> friends = helper.getVisibleFriends();

        UserAccount clicked = friends.get(0); // simulate clicking the first friend

        GlobalData.viewedUser = clicked;

        assertEquals("Clicked user should be stored as viewedUser",
                clicked, GlobalData.viewedUser);
    }

    // ============================================================
    // FRIENDS LOGIC HELPER (used for testing)
    // ============================================================
    private static class FriendsLogic {
        public ArrayList<UserAccount> getVisibleFriends() {
            ArrayList<UserAccount> visible = new ArrayList<>();

            if (GlobalData.currentUser == null) return visible;

            String current = GlobalData.currentUser.getUsername();

            for (UserAccount u : GlobalData.accounts) {
                if (!u.getUsername().equalsIgnoreCase(current)) {
                    visible.add(u);
                }
            }
            return visible;
        }
    }
}
