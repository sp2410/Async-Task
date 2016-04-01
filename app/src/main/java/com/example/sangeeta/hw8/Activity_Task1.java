package com.example.sangeeta.hw8;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

public class Activity_Task1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        Fragment_RecyclerView.OnItemSelected{

    private LruCache<String,Bitmap> mImgMemoryCache;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    Fragment mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mImgMemoryCache == null){
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            final int cacheSize = maxMemory/8;

            mImgMemoryCache = new LruCache<String, Bitmap>(cacheSize){

                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        if(savedInstanceState != null){
            mContext = getSupportFragmentManager().getFragment(savedInstanceState,"mContext");
        }
        else{
            mContext = Fragment_RecyclerView.newInstance(mImgMemoryCache);
        }


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mContext)
                .commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.fmain);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(mContext.isAdded())
            getSupportFragmentManager().putFragment(outState,"mContext", mContext);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id){
            case R.id.item1 :
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.item2 :
                intent = new Intent(this, Activity_Task1.class);
                startActivity(intent);
                break;

            default:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return  true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.toolbar1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id== R.id.mypic){
            Toast.makeText(getApplicationContext(), "PIKA PIKA !!!", Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickItemSelected(HashMap<String, ?> movie) {

        mContext = MovieDetails.newInstance(movie, mImgMemoryCache);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mContext)
                .addToBackStack(null)
                .commit();

    }
}
