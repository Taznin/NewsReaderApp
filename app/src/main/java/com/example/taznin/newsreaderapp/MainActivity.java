package com.example.taznin.newsreaderapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.taznin.newsreaderapp.Adapter.HeadLineAdapter;
import com.example.taznin.newsreaderapp.Interfaces.HeadLineService;
import com.example.taznin.newsreaderapp.Manager.ApiClient;
import com.example.taznin.newsreaderapp.Manager.Constant;
import com.example.taznin.newsreaderapp.Manager.InternetConnectivityCheck;
import com.example.taznin.newsreaderapp.Model.Article;
import com.example.taznin.newsreaderapp.Model.News;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private HeadLineAdapter adapter;
    private List<Article> articles= new ArrayList<>();
    private RecyclerView recyclerView;
    ProgressDialog progressDoalog;
    private SwipeRefreshLayout swipeRefreshLayoutOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Loading....");
        progressDoalog.show();
        swipeRefreshLayoutOne=(SwipeRefreshLayout)findViewById(R.id.swipeOne) ;;
        recyclerView =(RecyclerView) findViewById(R.id.listView1);
        Paper.init(this);
        showHeadlines();
        swipeRefreshLayoutOne.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               showHeadlines();
            }
        });

    }
    private void showHeadlines(){
        if(InternetConnectivityCheck.isConnectedToInternet(this)){
            headLineLoad();
        }else {
            progressDoalog.dismiss();
            List<Article> articleList= Paper.book().read("articel");
            if(articleList!=null && !articleList.isEmpty()){
                headlineGenerate(articleList);
                swipeRefreshLayoutOne.setRefreshing(false);
            }
            Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
        }
    }
    private void headLineLoad() {
        swipeRefreshLayoutOne.setRefreshing(true);

        HeadLineService service = ApiClient.getRetrofitInstance().create(HeadLineService.class);
        Call<News> call = service.getAllArticales(Constant.NEWS_API_COUNTRY, Constant.NEWS_API_KEY);

        List<Article> articleList= Paper.book().read("articel");
        //if have cache
        if(articleList!=null && !articleList.isEmpty()){
            headlineGenerate(articleList);
            swipeRefreshLayoutOne.setRefreshing(false);
        }else{
            call.enqueue(new Callback<News>() {
                @Override
                public void onResponse(Call<News> call, Response<News> response) {
                    progressDoalog.dismiss();
                    if(response.isSuccessful() && response.body().getArticles()!=null){
                        if(!articles.isEmpty()){
                            articles.clear();
                        }


                        articles=response.body().getArticles();
                        Paper.book().write("articel",articles);
                        headlineGenerate(articles);
                        swipeRefreshLayoutOne.setRefreshing(false);
                    }else{
                        swipeRefreshLayoutOne.setRefreshing(false);
                        Toast.makeText(MainActivity.this, "no response", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<News> call, Throwable t) {
                    progressDoalog.dismiss();

                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private void headlineGenerate(List<Article> articleList) {

        adapter = new HeadLineAdapter(this,articleList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }
    public void exitApp(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_exit)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
            exitApp();
    }
}
