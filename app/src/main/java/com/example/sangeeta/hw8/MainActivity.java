package com.example.sangeeta.hw8;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FirstFragment.OnButtonSelected{

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    private int COVER_PAGE =1;


    Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState != null){
            mContent = getSupportFragmentManager().getFragment(savedInstanceState,"mContent");
        }
        else{
            mContent = FirstFragment.newInstance(COVER_PAGE);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mContent)
                .commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
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
        if(mContent.isAdded())
            getSupportFragmentManager().putFragment(outState,"mContent", mContent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id){
            case R.id.item1 :
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, FirstFragment.newInstance(2))
                        .addToBackStack(null)
                        .commit();
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
    public void onClickButtonSelected(int param) {
        Intent intent;

        if(param == 2) {
            mContent =FirstFragment.newInstance(param);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mContent)
                    .addToBackStack(null)
                    .commit();
        }
        else if(param ==3){
            intent = new Intent(this, Activity_Task1.class);
            startActivity(intent);
        }
    }
}