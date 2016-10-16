package com.example.victorhom.flickster;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

        // discouraged to have icon in Material design ¯\_(ツ)_/¯
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.moviews_actionbar);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("");

        // movie data passed in from onclick in the movie activity
        Intent movieIntent = getIntent();
        id = movieIntent.getStringExtra("id");
        backdrop_path = movieIntent.getStringExtra("backdrop_path");
        votes = movieIntent.getStringExtra("votes");
        overview = movieIntent.getStringExtra("overview");
        title = movieIntent.getStringExtra("title");

        TextView tvTitle = (TextView) findViewById(R.id.movieTitle);
        TextView tvVote = (TextView) findViewById(R.id.movieVotes);
        TextView tvOverview = (TextView) findViewById(R.id.movieOverview);

        tvTitle.setText(title.toString());
        tvVote.setText("Average Vote: " + votes.toString() + "/10");
        tvOverview.setText(overview.toString());

        client = new AsyncHttpClient();

        youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);

        populateYoutubeOnScreen();
        populateMovieDataOnScreen();
    }

    private void populateYoutubeOnScreen() {

        String youtubeUrl = "https://api.themoviedb.org/3/movie/" + this.id + "/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

        String movieReviewUrl = "https://api.themoviedb.org/3/movie/"+ this.id +"/reviews?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
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
                            // auto play if the votes for the movie is > 5
                            if (Float.parseFloat(peopleVotes) > 5) {
                                youTubePlayer.loadVideo(youtubeKey);
                                // want to set fullscreen, but issue:  when leave fullscreen
                                // activity is remade, and I end up back in fullscreen
                                // don't know how to listen on the minimize screen button
                                // to send an savedInstance
                                //youTubePlayer.setFullscreen(true);
                            } else {
                                youTubePlayer.cueVideo(youtubeKey);
                            }

                            int orientation = getApplicationContext().getResources().getConfiguration().orientation;
                            if (orientation == Configuration.ORIENTATION_LANDSCAPE ) {
                                youTubePlayer.setFullscreen(true);
                            }

                            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {
                                }

                                @Override
                                public void onLoaded(String s) {

                                }

                                @Override
                                public void onAdStarted() {

                                }

                                @Override
                                public void onVideoStarted() {
                                    youTubePlayer.seekRelativeMillis(currentVideoTime);
                                }

                                @Override
                                public void onVideoEnded() {

                                }

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {

                                }
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

                    String hour = String.valueOf(runtime / 60);
                    String minute = String.valueOf(runtime % 60);
                    String movieLength = hour + " hrs " + minute + " mins";

                    TextView tvGenres = (TextView) findViewById(R.id.genres);
                    TextView tvDuration = (TextView) findViewById(R.id.duration);
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
}
