package com.example.victorhom.flickster.Adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.victorhom.flickster.R;
import com.example.victorhom.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by victorhom on 10/15/16.
 */
public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    // View lookup cache
    private static class ViewHolder {
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivMovieImage;
    }

    public MoviesArrayAdapter(Context context,List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    // NOTE: not good that I'm cutting it off into 2 decisions
    // doesn't make it easy for future types for heterogenous listviewss
    @Override
    public int getItemViewType(int position) {
        if (Float.parseFloat(getItem(position).getVoteAverage()) > 5) {
            return 1;
        } else {
            return 0;
        }
    }

    // 2 for a lv item that is less than 5 and those greater than 5
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get data item for this position
        Movie movie = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache sorted in tag
        // The viewHolder is used here to speed up ref lookups to the view
        int type = getItemViewType(position);
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();

            if (type == 1) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_move_popular, parent, false);
                viewHolder.ivMovieImage = (ImageView) convertView.findViewById(R.id.idMovieImage);
            } else {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_movie, parent, false);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.tvOverview = (TextView) convertView.findViewById(R.id.tvOverview);
                viewHolder.ivMovieImage = (ImageView) convertView.findViewById(R.id.idMovieImage);
                // Cache the viewHolder object inside the fresh view
            }

            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (type == 1) {
            viewHolder.ivMovieImage.setImageResource(0);
        } else {
            try {
                // Populate the data into the template view using the data object
                viewHolder.tvTitle.setText(movie.getOriginalTitle());
                viewHolder.tvOverview.setText(movie.getOverview());
                // clear out image from convertView
                viewHolder.ivMovieImage.setImageResource(0);
            } catch (Exception e){}
        }


        // there isn't a built in way of loading images, so we are using Picasso
        // loading poster in portrait and backdrop in landscape
        int orientation = getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && type == 0) {
            Picasso.with(getContext()).load(movie.getPosterPath())
                    .transform(new RoundedCornersTransformation(10, 10))
                    .placeholder(R.drawable.play_icon_poster)
                    .error(R.drawable.play_icon_poster)
                    .into(viewHolder.ivMovieImage);
        } else {
            Picasso.with(getContext()).load(movie.getBackdropPath())
                    .transform(new RoundedCornersTransformation(10, 10))
                    .placeholder(R.drawable.play_icon_backdrop)
                    .error(R.drawable.play_icon_backdrop)
                    .into(viewHolder.ivMovieImage);
        }

        //return the view
        return convertView;
    }
}
