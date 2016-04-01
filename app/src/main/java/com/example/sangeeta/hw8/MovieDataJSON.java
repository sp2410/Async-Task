package com.example.sangeeta.hw8;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MovieDataJSON {

    public static String PHPServer = "http://www.example.com:8089/";

    List<Map<String,?>> moviesList;

    public List<Map<String, ?>> getMoviesList() {
        return moviesList;
    }

    public int getSize(){
        return moviesList.size();
    }

    public HashMap getItem(int i){
        if (i >=0 && i < moviesList.size()){
            return (HashMap) moviesList.get(i);
        } else return null;
    }

    public MovieDataJSON() {
       /* String description;
        String length;
        String year;
        double rating;
        String director;
        String stars;
        String url;*/
        moviesList = new ArrayList<Map<String, ?>>();
    }

    public void downloadMovieDataJSON(String url){
        String jsonString = MyUtility.downloadJSONusingHTTPGetRequest(url);

        if(jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);

                    Map<String, Object> map = new HashMap<String, Object>();
                    Iterator<String> keysItr = jObject.keys();
                    while (keysItr.hasNext()) {

                        String key = keysItr.next();
                        Object value = jObject.get(key);
                        map.put(key, value);
                    }
                    moviesList.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void addMovie(HashMap<String,?> movie, int position) {

        final JSONObject json;

        if(movie != null){
            json = new JSONObject(movie);
        }
        else
            json= null;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = PHPServer+"add";
                MyUtility.sendHttPostRequest(url,json);
            }
        };
        new Thread(runnable).start();

        moviesList.add(position, movie);
    }

    public void removeMovie(int position) {

        HashMap<String,?> toRemove = (HashMap<String, ?>) moviesList.get(position);
        final JSONObject json;

        if(toRemove != null){
            json = new JSONObject(toRemove);
        }
        else
            json= null;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String url = PHPServer+"delete";
                MyUtility.sendHttPostRequest(url,json);
            }
        };
        new Thread(runnable).start();

        moviesList.remove(toRemove);
    }


    public int findFirst(String query){
        int i;
        for(i=0; i < moviesList.size();i++){
            HashMap<String,?> currentMovie =  (HashMap<String,?>) moviesList.get(i);
            String name = (String)currentMovie.get("name");
            if(name.toLowerCase().contains(query.toLowerCase()))
                break;
        }
        if(i < moviesList.size())
            return i;
        else
            return -1;
    }

    public int getLast(){
        return moviesList.size() - 1;
    }

}
