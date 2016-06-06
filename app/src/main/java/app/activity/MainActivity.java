package app.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.githubdemo.app.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.adapter.ItemAdapter;
import app.model.Item;
import app.model.Users;
import app.service.GithubService;
import app.service.ServiceFactory;
import retrofit2.Call;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    TextView noResultView;

    RecyclerView recyclerView;

    SearchView searchview;

    private List<Item> itemList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private PublishSubject<String> publishSubject;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchview = (SearchView) findViewById(R.id.searchview);
        noResultView = (TextView) findViewById(R.id.no_result_view);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);
        createObservables();
        checkInput();
    }

    private void createObservables() {
        final GithubService service = ServiceFactory.createRetrofitService(GithubService.class, GithubService.SERVICE_ENDPOINT);
        publishSubject = PublishSubject.create();
        subscription = publishSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(s -> {
                    Call<Users> call = service.getUsers(s);
                    Users users = null;
                    try {
                        users = call.execute().body();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return users;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Users>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showEmptyResult();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ошибка соединения", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onNext(Users users) {
                        processSearchResult(users);
                    }
                });
    }

    private void processSearchResult(Users response) {
        if ((response == null) || (response.getItems().isEmpty())) {
            showEmptyResult();
        } else {
            showSearchResult(response);
        }
    }

    private void showEmptyResult() {
        noResultView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showSearchResult(Users response) {
        itemAdapter.clear();
        noResultView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        for (int i = 0; i < response.getItems().size(); i++) {
            itemList.add(response.getItems().get(i));
            itemAdapter.notifyDataSetChanged();
        }
    }

    private void checkInput() {
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchview.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                publishSubject.onNext(newText);
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}