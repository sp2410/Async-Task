package com.example.sangeeta.hw8;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.LruCache;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;

public class Fragment_RecyclerView extends Fragment
{

    private RecyclerView mRecycler;
    private Adapter_RecyclerView mRecyclerViewAdapter;
    private MovieDataJSON movieData;

    private OnItemSelected mListnerFragment;
    private static LruCache<String,Bitmap>  mImgCache;
    private ArrayList<Map<String,?>> saveMovieList;

    public Fragment_RecyclerView(){    }


    public static Fragment_RecyclerView newInstance(LruCache<String,Bitmap> cache){
        mImgCache= cache;
        Fragment_RecyclerView myfragment = new Fragment_RecyclerView();
        return myfragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState != null){
            movieData.moviesList = (ArrayList<Map<String,?>>) savedInstanceState.getSerializable("MovieList");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        saveMovieList = (ArrayList<Map<String,?>>)movieData.moviesList;
        outState.putSerializable("MovieList", saveMovieList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = null;

        try{
            mListnerFragment = (OnItemSelected) getContext();
        }catch (ClassCastException e){
            throw new ClassCastException("Forgot to implement OnButtonSelected");
        }

        rootView = inflater.inflate(R.layout.activity_recyclerview, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.insiderecview1);

        LinearLayoutManager mLayout = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayout);

        mRecyclerViewAdapter = new Adapter_RecyclerView(getActivity(), movieData, mImgCache);
        mRecycler.setAdapter(mRecyclerViewAdapter);

        movieData = new MovieDataJSON();
        MyDownloadJSONAsyncTask downloadJSON = new MyDownloadJSONAsyncTask(mRecyclerViewAdapter);
        String url = MovieDataJSON.PHPServer + "movies";
        downloadJSON.execute(url);

        mRecyclerViewAdapter.registerListener(new Adapter_RecyclerView.OnItemViewSelected() {

            @Override
            public void onItemClick(View x, int position) {
                if (mListnerFragment != null) {
                    HashMap<String,?> movie = movieData.getItem(position);
                    String id = movie.get("id").toString();
                    String url = MovieDataJSON.PHPServer+"movies/id/"+id;
                    MyDownloadOneJSONAsyncTask downloadOneJSON = new MyDownloadOneJSONAsyncTask(mListnerFragment);
                    downloadOneJSON.execute(url);

                }
            }

            @Override
            public void onItemLongClick(View x, int position) {
                getActivity().startActionMode( new ActionBarCallBack(position));

            }

            @Override
            public void onOverflowItemClicked(View x, final int position) {

                PopupMenu popup = new PopupMenu(getActivity(),x);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id){
                            case R.id.del:
                                movieData.removeMovie(position);
                                mRecyclerViewAdapter.notifyItemRemoved(position);
                                break;

                            case R.id.copy:
                                HashMap<String,?> currentMovie = (HashMap < String, ?>) movieData.getItem(position);
                                HashMap<String,Object> newMovie = new HashMap<String, Object>();
                                for (Map.Entry m : currentMovie.entrySet() ){

                                    if(m.getKey().toString().equals("id")){
                                        String newID = m.getValue() + "_new";
                                        newMovie.put("id",newID);
                                    }else{
                                        newMovie.put(m.getKey().toString(),m.getValue());
                                    }
                                }
                                newMovie.put("director","SASHA");
                                newMovie.put("year",2016);
                                newMovie.put("image","T");
                                newMovie.put("length","10");
                                newMovie.put("stars","10");

                                movieData.addMovie(newMovie, position + 1);
                                mRecyclerViewAdapter.notifyItemInserted(position+1);
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menufrag2,popup.getMenu());
                popup.show();

            }
        });

        itemAnimation();
        adapterAnimation();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if(menu.findItem(R.id.search) == null){
            inflater.inflate(R.menu.menufrag1, menu);
        }

        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();

        if(search != null){
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

                @Override
                public boolean onQueryTextSubmit(String query) {
                    String url = "http://www.example.com:8089/movies/rating/"+query;
                    MyDownloadJSONAsyncTask downloadJSON = new MyDownloadJSONAsyncTask(mRecyclerViewAdapter);
                    downloadJSON.execute(url);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.search:
                //mysearch():
                break;
            case R.id.setting:
                Toast.makeText(getActivity(),"Clicked settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.help:
                Toast.makeText(getActivity(),"Clicked help", Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(getActivity(),"Pika Pika !!!", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void itemAnimation(){

        ScaleInAnimator animator = new ScaleInAnimator();
        animator.setInterpolator(new OvershootInterpolator());

        animator.setAddDuration(300);
        animator.setRemoveDuration(200);

        mRecycler.setItemAnimator(animator);

    }

    private void adapterAnimation(){

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mRecyclerViewAdapter);
        ScaleInAnimationAdapter scaleAdaptor = new ScaleInAnimationAdapter(alphaAdapter);
        mRecycler.setAdapter(scaleAdaptor);

    }

    public interface OnItemSelected {
        public void onClickItemSelected(HashMap<String,?> movie);
    }

    class ActionBarCallBack implements android.view.ActionMode.Callback{

        int position;

        public ActionBarCallBack(int position){
            this.position= position;
        }


        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
           // mode.getMenuInflater().inflate(R.menu.contextuallongclick_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            HashMap<String, ?> hm = movieData.getItem(position);
            mode.setTitle((String)hm.get("name"));
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {

            int id = item.getItemId();

            switch (id){
                case R.id.del:
                    movieData.removeMovie(position);
                    mRecyclerViewAdapter.notifyItemRemoved(position);
                    mode.finish();
                    break;


                case R.id.copy:
                    movieData.addMovie((HashMap<String,?>) movieData.getItem(position), position+1);
                    mRecyclerViewAdapter.notifyItemInserted(position+1);
                    mode.finish();
                    break;

                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {

        }
    }

    private class MyDownloadJSONAsyncTask extends AsyncTask<String, Void, MovieDataJSON> {

        private final WeakReference<Adapter_RecyclerView> adapterReference;

        public MyDownloadJSONAsyncTask(Adapter_RecyclerView mRecyclerAdapter){
            adapterReference = new WeakReference<Adapter_RecyclerView>(mRecyclerAdapter);
        }

        @Override
        protected MovieDataJSON doInBackground(String... url) {
            MovieDataJSON threadMovieData = new MovieDataJSON();

            threadMovieData.downloadMovieDataJSON(url[0]);
            return threadMovieData;
        }

        @Override
        protected void onPostExecute(MovieDataJSON threadMovieData) {
            movieData.moviesList.clear();

            for(int i=0; i< threadMovieData.getSize();i++){
                movieData.moviesList.add(threadMovieData.moviesList.get(i));
            }
            if(adapterReference != null){
                final Adapter_RecyclerView adapter = adapterReference.get();
                if(adapter != null){
                    adapter.setMovieData(movieData);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class MyDownloadOneJSONAsyncTask extends AsyncTask<String, Void, MovieDataJSON> {

        private final WeakReference<OnItemSelected> listenerReference;

        public MyDownloadOneJSONAsyncTask(OnItemSelected listenerFragment){
            listenerReference = new WeakReference<OnItemSelected>(listenerFragment);
        }

        @Override
        protected MovieDataJSON doInBackground(String... url) {
            MovieDataJSON threadMovieData = new MovieDataJSON();

            threadMovieData.downloadMovieDataJSON(url[0]);
            return threadMovieData;
        }

        @Override
        protected void onPostExecute(MovieDataJSON threadMovieData) {
            movieData.moviesList.clear();

            for(int i=0; i< threadMovieData.getSize();i++){
                movieData.moviesList.add(threadMovieData.moviesList.get(i));
            }
            if(listenerReference != null){
                mListnerFragment = listenerReference.get();
                if(mListnerFragment != null){
                    mListnerFragment.onClickItemSelected((HashMap<String, ?>) movieData.moviesList.get(0));
                }
            }
        }
    }
}
