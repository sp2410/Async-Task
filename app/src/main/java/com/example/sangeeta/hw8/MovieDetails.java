package com.example.sangeeta.hw8;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.HashMap;

public class MovieDetails extends Fragment {

    private HashMap<String,?> currentMovie;
    private ShareActionProvider mShareActionProvider;
    public MovieDetails(){    }

    private static LruCache<String,Bitmap> mImgCache;

    public static MovieDetails newInstance(HashMap<String ,?> movie, LruCache<String,Bitmap> cache){

        mImgCache = cache;
        MovieDetails myfragment = new MovieDetails();
        Bundle args = new Bundle();
        args.putSerializable("Movie", movie);
        myfragment.setArguments(args);
        return myfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;
        currentMovie = (HashMap<String,?>)getArguments().getSerializable("Movie");

        rootView = inflater.inflate(R.layout.fragment_movieinfo, container, false);


        ImageView movieImage = (ImageView) rootView.findViewById(R.id.image2);
        TextView movieDesc = (TextView) rootView.findViewById(R.id.des2);
        TextView movieTitle = (TextView) rootView.findViewById(R.id.title2);
        TextView movieYear = (TextView) rootView.findViewById(R.id.year2);
        RatingBar movieRating = (RatingBar) rootView.findViewById(R.id.rate2);
        TextView movieStar = (TextView) rootView.findViewById(R.id.cast);
        TextView movieLength = (TextView) rootView.findViewById(R.id.mlen);
        TextView movieDirector = (TextView) rootView.findViewById(R.id.dir);


        //movieImage.setImageResource((Integer) currentMovie.get("image"));
        movieTitle.setText((String) currentMovie.get("name"));
        movieDesc.setText((String) currentMovie.get("description"));
        movieYear.setText(currentMovie.get("year").toString());
        movieStar.setText((String) currentMovie.get("stars"));
        movieLength.setText((String) currentMovie.get("length"));
        movieDirector.setText((String) currentMovie.get("director"));
        if(movieImage != null){
            String url = currentMovie.get("url").toString();
            final Bitmap bitmap = mImgCache.get(url);

            if(bitmap != null){
                movieImage.setImageBitmap(bitmap);
            }else{
                MyDownloadImageAsyncTask downloadImg = new MyDownloadImageAsyncTask(movieImage, mImgCache);
                downloadImg.execute(url);
            }
        }

        double currentRating = Double.parseDouble(currentMovie.get("rating").toString());
        movieRating.setStepSize((float) 0.05);
        currentRating = currentRating/2;
        movieRating.setRating((float) currentRating);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if(menu.findItem(R.id.share) == null){
            inflater.inflate(R.menu.menufrag3, menu);
        }

        MenuItem shareItem = menu.findItem(R.id.share);
        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");

        intentShare.putExtra(Intent.EXTRA_TEXT, (String) currentMovie.get("name"));
        mShareActionProvider.setShareIntent(intentShare);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
