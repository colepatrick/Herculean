package com.example.herculean.datahandling;

import java.util.ArrayList;

public class JsonData {
    public ArrayList<UserAccount> accounts;
    public String lastLoggedInUser;

    public JsonData() {
        this.accounts = new ArrayList<>();
        this.lastLoggedInUser = null;
    }
}
