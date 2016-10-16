package com.example.victorhom.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

        // discouraged to have icon in Material design ¯\_(ツ)_/¯
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.moviews_actionbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");

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
        client = new AsyncHttpClient();
        populateMoviesOnScreen();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MovieActivity.this, MovieInformationActivity.class);
                Movie selectedMovie = movieAdapter.getItem(position);
                // send data to the movie information activity
                // sending the additional data for quick population
                // figure this would be faster than the network request for extra data
                intent.putExtra("id", selectedMovie.getMovieId().toString());
                intent.putExtra("backdrop_path", selectedMovie.getBackdropPath().toString());
                intent.putExtra("votes", selectedMovie.getVoteAverage().toString());
                intent.putExtra("overview", selectedMovie.getOriginalOverview().toString());
                intent.putExtra("title", selectedMovie.getOriginalTitle().toString());
                startActivity(intent);
            }
        });

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
