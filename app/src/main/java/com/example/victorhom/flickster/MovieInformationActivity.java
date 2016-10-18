package com.example.victorhom.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieInformationActivity extends AppCompatActivity {

    AsyncHttpClient client;
    String id;
    String backdrop_path;
    String votes;
    String overview;
    String title;
    YouTubePlayerFragment youtubeFragment;
    YouTubePlayer player;
    int currentVideoTime;
    TextView tvTitle;
    TextView tvOverview;
    TextView tvGenres;
    TextView tvDuration;
    RatingBar rbVote;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.homeButton:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_information);

        setActionBarStyle();

        // movie data passed in from onclick in the movie activity
        Intent movieIntent = getIntent();
        id = movieIntent.getStringExtra("id");
        backdrop_path = movieIntent.getStringExtra("backdrop_path");
        votes = movieIntent.getStringExtra("votes");
        overview = movieIntent.getStringExtra("overview");
        title = movieIntent.getStringExtra("title");

        tvTitle = (TextView) findViewById(R.id.movieTitle);
        tvOverview = (TextView) findViewById(R.id.movieOverview);
        rbVote = (RatingBar) findViewById(R.id.ratingBar);
        // set data for tvGenres and tvDuration in populateMovieDataOnScreen method
        tvGenres = (TextView) findViewById(R.id.genres);
        tvDuration = (TextView) findViewById(R.id.duration);

        tvTitle.setText(title.toString());
        tvOverview.setText(overview.toString());
        rbVote.setRating(Float.parseFloat(votes));

        client = new AsyncHttpClient();

        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);

        populateYoutubeOnScreen();
        populateMovieDataOnScreen();
    }

    private void populateYoutubeOnScreen() {

        String youtubeUrl = "https://api.themoviedb.org/3/movie/" + this.id + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

        final String peopleVotes = this.votes;

        client.get(youtubeUrl, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJSONResults = null;
                try {
                    movieJSONResults = response.getJSONArray("results");
                    final String youtubeKey;
                    if (movieJSONResults.length() > 0){
                        JSONObject youtubeObject = (JSONObject) movieJSONResults.get(0);
                        youtubeKey = youtubeObject.getString("key");
                    } else {
                        // default if no key https://www.youtube.com/watch?v=BIPa_UpVrWk
                        youtubeKey = "BIPa_UpVrWk";
                    }
                    youtubeFragment.initialize("AIzaSyB5VsMiIkPGnaOxU4lGxPeJBVyJz9nTyWEs",
                        new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            final YouTubePlayer youTubePlayer, boolean b) {

                            player = youTubePlayer;
                            int orientation;
                            // auto play if the votes for the movie is > 5
                            if (Float.parseFloat(peopleVotes) > 5) {
                                youTubePlayer.loadVideo(youtubeKey);
                            } else {
                                youTubePlayer.cueVideo(youtubeKey);
                            }

//                            orientation = getApplicationContext().getResources().getConfiguration().orientation;
//                            if (orientation == Configuration.ORIENTATION_LANDSCAPE ) {
//                                youTubePlayer.setFullscreen(true);
//                            }

                            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {}

                                @Override
                                public void onLoaded(String s) {}

                                @Override
                                public void onAdStarted() {}

                                @Override
                                public void onVideoStarted() {
                                    youTubePlayer.seekRelativeMillis(currentVideoTime);
                                }

                                @Override
                                public void onVideoEnded() {}

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {}
                            });

                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    });


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

    private void populateMovieDataOnScreen(){
        String movieInformationUrl = "https://api.themoviedb.org/3/movie/" + this.id +"?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
        client.get(movieInformationUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray genres = null;
                int runtime = 0;
                String hour;
                String minute;
                String movieLength;

                try {
                    genres = response.getJSONArray("genres");
                    runtime = response.getInt("runtime");

                    StringBuilder formatGenres = new StringBuilder();
                    for (int i = 0; i < genres.length(); i++) {
                        String genre = genres.getJSONObject(i).getString("name");
                        formatGenres.append(genre);
                        if (i != genres.length() - 1){
                            formatGenres.append(" | ");
                        }
                    }

                    hour = String.valueOf(runtime / 60);
                    minute = String.valueOf(runtime % 60);
                    movieLength = hour + " hrs " + minute + " mins";

                    tvGenres.setText(formatGenres);
                    tvDuration.setText(movieLength);

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

    // String movieReviewUrl = "https://api.themoviedb.org/3/movie/"+ this.id +"/reviews?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    // add on reviews to this activity

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("videoTime", player.getCurrentTimeMillis());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentVideoTime = savedInstanceState.getInt("videoTime");
    }

    private void setActionBarStyle() {
        // discouraged to have icon in Material design ¯\_(ツ)_/¯
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.moviews_actionbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");
    }
}
