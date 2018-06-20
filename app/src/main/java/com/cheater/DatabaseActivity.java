package com.cheater;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.cheater.search.SearchResultFactory;

/** Активити навигации по базе данных */
public class DatabaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private boolean showAll;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseAdapter adapter;
    private String lastQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DatabaseActivity.this, EditActivity.class);
                DatabaseActivity.this.startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.database_recycler);
        recyclerView.setHasFixedSize(false);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        try {
            adapter = new DatabaseAdapter(this, SearchResultFactory.getAll());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "При инициализации списка произошла ошибка!", Toast.LENGTH_LONG).show();
        }
        recyclerView.setAdapter(adapter);
        showAll = true;
        onNewIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    protected void refresh() {
        if (lastQuery != null && !lastQuery.equals("")) {
            try {
                adapter.setRecords(SearchResultFactory.search(lastQuery));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "При совершении поиска произошла ошибка!", Toast.LENGTH_LONG).show();
            }
            showAll = false;
        }
        else {
            try {
                adapter.setRecords(SearchResultFactory.getAll());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "При инициализации списка произошла ошибка!", Toast.LENGTH_LONG).show();
            }
            showAll = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            lastQuery = intent.getStringExtra(SearchManager.QUERY);
            try {
                adapter.setRecords(SearchResultFactory.search(lastQuery));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "При совершении поиска произошла ошибка!", Toast.LENGTH_LONG).show();
            }
            showAll = false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!showAll) {
                try {
                    adapter.setRecords(SearchResultFactory.getAll());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "При инициализации списка произошла ошибка!", Toast.LENGTH_LONG).show();
                }
                showAll = true;
            }
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.database, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.to_database) {
            Intent intent = new Intent(this, DatabaseActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.to_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
