package com.example.herculean.database;

import com.example.herculean.datahandling.UserAccount;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface AccountService {
    @GET("accounts")
    Call<List<UserAccount>> listAccounts();

    @GET("accounts/{username}")
    Call<UserAccount> getAccount(@Path("username") String username);

    @GET("accounts/email/{email}")
    Call<Void> getAccountByEmail(@Path("email") String email);

    @POST("accounts")
    Call<Void> createAccount(@Body UserAccount account);

    @PUT("accounts/{username}")
    Call<Void> updateAccount(@Path("username") String username, @Body UserAccount account);

    @DELETE("accounts/{username}")
    Call<Void> deleteAccount(@Path("username") String username);
}