package com.example.victorhom.flickster;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.victorhom.flickster.Adapter.MoviesArrayAdapter;
import com.example.victorhom.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    ArrayList<Movie> movies;
    MoviesArrayAdapter movieAdapter;
    ListView lvItems;
    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // look up the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // make sure you call swipeContainer.setRefreshing(false);
                // once the network request has completed successfully
                movieAdapter.clear();
                populateMoviesOnScreen();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        movies = new ArrayList<>();
        lvItems = (ListView) findViewById(R.id.lvMovies);
        // instantiate movie adapter
        movieAdapter = new MoviesArrayAdapter(this, movies);
        lvItems.setAdapter(movieAdapter);
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        client = new AsyncHttpClient();
        populateMoviesOnScreen();

    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP

//        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
//            public void onSuccess(JSONArray json) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                movieAdapter.clear();
//                // ...the data has come back, add new items to your adapter...
//                //movieAdapter.addAll(...);
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
//            }
//        });
    }

    private void populateMoviesOnScreen() {
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray moviesJSONResults = null;
                try {
                    moviesJSONResults = response.getJSONArray("results");
                    movies.addAll(Movie.fromJSONArray(moviesJSONResults));
                    movieAdapter.notifyDataSetChanged();
                    //Log.d("movies", movies.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }



}
