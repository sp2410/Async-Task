package com.example.sangeeta.hw8;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class Adapter_RecyclerView extends RecyclerView.Adapter<Adapter_RecyclerView.ViewHolder> {

    private MovieDataJSON movieData;
    private Context mContext;
    private LruCache<String,Bitmap>  mImgCache;

    private OnItemViewSelected mListenerAdapter = null;

    public Adapter_RecyclerView(Context context, MovieDataJSON movieData, LruCache<String,Bitmap> mImgCache){
        this.mContext = context;
        this.movieData = movieData;
        this.mImgCache = mImgCache;
    }


    public void setMovieData(MovieDataJSON movieData){
        this.movieData= movieData;
    }

    @Override
    public int getItemCount() {

        if(movieData == null)
           return 0;
        return movieData.getSize();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.insiderecview, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(movieData != null) {
            HashMap<String, ?> currentMovie = movieData.getItem(position);
            holder.bindMovieData(currentMovie);
        }
    }

    public void registerListener(OnItemViewSelected mListenerAdapter){
        this.mListenerAdapter = mListenerAdapter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView vImage;
        private TextView vTitle,vDescription,vRating;
        private ImageView vMenuImage;
        private RatingBar vMovieRating;

        public ViewHolder(View itemView) {


            super(itemView);
            vImage = (ImageView) itemView.findViewById(R.id.image1);
            vTitle = (TextView) itemView.findViewById(R.id.title);
            vDescription = (TextView) itemView.findViewById(R.id.des);
            vMenuImage = (ImageView) itemView.findViewById(R.id.popupbutton);
            vMovieRating = (RatingBar) itemView.findViewById(R.id.rate);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mListenerAdapter != null) {
                        mListenerAdapter.onItemClick(v, getLayoutPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (mListenerAdapter != null) {
                        mListenerAdapter.onItemLongClick(v, getLayoutPosition());
                    }
                    return true;
                }
            });

            if (vMenuImage != null) {
                vMenuImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListenerAdapter.onOverflowItemClicked(v, getLayoutPosition());
                    }
                });
            }
        }

        public void bindMovieData(HashMap<String,?> movie){

            vTitle.setText((String) movie.get("name"));

            String checkLength = (String) movie.get("description");
            String description =null;

            if(checkLength.length() > 90){
                description = checkLength.substring(0,90);
                description = description.concat("....");
            }
            else
                description = checkLength;
            vDescription.setText(description);
            vRating.setText((movie.get("rating").toString()));

            double currentRating = Double.parseDouble(movie.get("rating").toString());
            vMovieRating.setStepSize((float) 0.05);
            currentRating = currentRating/2;
            vMovieRating.setRating((float) currentRating);

            if(vImage != null){
                String url = movie.get("url").toString();
                final Bitmap bitmap = mImgCache.get(url);

                if(bitmap != null){
                    vImage.setImageBitmap(bitmap);
                }else{
                    MyDownloadImageAsyncTask downloadImg = new MyDownloadImageAsyncTask(vImage, mImgCache);
                    downloadImg.execute(url);
                }
            }
        }
    }

    public interface OnItemViewSelected {
        public void onItemClick(View x, int position);

        public void onItemLongClick(View x, int position);

        public void onOverflowItemClicked(View x, int position);
    }
}

class MyDownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

    private final WeakReference<ImageView> imgViewReference;
    private LruCache<String,Bitmap>  mImgCache ;
    public MyDownloadImageAsyncTask(ImageView v, LruCache<String,Bitmap>  mImgCache){
        imgViewReference = new WeakReference<ImageView>(v);
        this.mImgCache= mImgCache;
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        Bitmap bitmap = null;
        bitmap = MyUtility.downloadImageusingHTTPGetRequest(url[0]);
        if(bitmap != null){
            mImgCache.put(url[0],bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null){
            final ImageView imgV = imgViewReference.get();
            if(imgV != null){
                imgV.setImageBitmap(bitmap);
            }
        }
    }
}
