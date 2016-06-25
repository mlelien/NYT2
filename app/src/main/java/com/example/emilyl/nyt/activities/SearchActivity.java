package com.example.emilyl.nyt.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.emilyl.nyt.Article;
import com.example.emilyl.nyt.ArticleArrayAdapter;
import com.example.emilyl.nyt.EndlessRecyclerViewScrollListener;
import com.example.emilyl.nyt.FiltersDialogFragment;
import com.example.emilyl.nyt.R;
import com.example.emilyl.nyt.RecyclerItemClickListener;
import com.example.emilyl.nyt.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    String query = "";
    SearchFilters searchFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        setUpViews();
        showTopArticles();
    }

    public void setUpViews() {
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);

        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                onArticleSearch(recyclerView, false, query, page);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(SearchActivity.this, ArticleActivity.class);
                        intent.putExtra("article", Parcels.wrap(articles.get(position)));
                        startActivity(intent);
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recyclerView.clearOnScrollListeners();
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                final String q = query;
                recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemCount) {
                        onArticleSearch(recyclerView, false, q, page);
                    }
                });
                onArticleSearch(recyclerView, true, query, 0);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FiltersDialogFragment fragment = FiltersDialogFragment.newInstance("Settings");
//            fragment.show(fragmentManager, "fragment_dialogue");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showTopArticles() {
    String url = "http://api.nytimes.com/svc/topstories/v2/home.json";

    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    params.put("api-key", "a35dc3ff1884421d84fecb8043b57008");

    client.get(url, params, new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            JSONArray articleJsonResults = null;
            try {
                articleJsonResults = response.getJSONArray("results");
                articles.addAll(Article.fromJSONArray(articleJsonResults));
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.d("fail", "fail");
        }
    });
}

    public void onArticleSearch(View view, final boolean newSearch, String q, int page) {
        query = q;
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api-key", "a35dc3ff1884421d84fecb8043b57008");
        params.put("page", page);
        if (searchFilters != null) {
            if (!searchFilters.getDate().equals(""))
                params.put("begin_date", searchFilters.getDate());
            if (!searchFilters.getSort().equals(""))
                params.put("sort", searchFilters.getSort());
            if (searchFilters.getNewsCategory().size() > 0) {
                for (String category : searchFilters.getNewsCategory())
                    params.put("fq", String.format("news_desk:(\"%s\")", category));
            }
        }
        params.put("q", query);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    if (newSearch)
                        articles.clear();
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("fail", "fail");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            searchFilters = Parcels.unwrap(data.getParcelableExtra("filters"));
            onArticleSearch(recyclerView, true, query, 0);
        }
    }
}
