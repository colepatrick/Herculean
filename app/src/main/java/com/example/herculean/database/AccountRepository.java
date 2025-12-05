package com.example.herculean.database;

import com.example.herculean.datahandling.UserAccount;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private final AccountService service;

    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onError(Throwable t);
    }

    public AccountRepository(String baseUrl) {
        service = ApiClient.getClient(baseUrl).create(AccountService.class);
    }

    public void getAccount(String username, ResultCallback<UserAccount> cb) {
        service.getAccount(username).enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onError(new RuntimeException("HTTP " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {
                cb.onError(t);
            }
        });
    }

    public void createAccount(UserAccount account, ResultCallback<Void> cb) {
        service.createAccount(account).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cb.onSuccess(null);
                } else {
                    cb.onError(new RuntimeException("HTTP " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                cb.onError(t);
            }
        });
    }

    public void updateAccount(String username, UserAccount account, ResultCallback<Void> cb) {
        service.updateAccount(username, account).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cb.onSuccess(null);
                } else {
                    cb.onError(new RuntimeException("HTTP " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                cb.onError(t);
            }
        });
    }
}