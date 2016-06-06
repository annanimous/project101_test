package app.service;

import app.model.Users;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GithubService {
    String SERVICE_ENDPOINT = "https://api.github.com/";

    @GET("search/users")
    Call<Users> getUsers(@Query("q") String searchstring);
}
