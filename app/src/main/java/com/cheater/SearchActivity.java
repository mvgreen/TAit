package com.cheater;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.cheater.search.SearchResult;
import com.cheater.search.SearchResultFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 120);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
        }
    }

    private void prepareTessData() {
        // Создаем новую папку
        App.getInstance().tessDir = getExternalFilesDir("");
        if (!App.getInstance().tessDir.exists()) {
            if (!App.getInstance().tessDir.mkdir()) {
                Toast.makeText(getApplicationContext(), "The folder " + App.getInstance().tessDir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
            }
        }
        File dir = new File(App.getInstance().tessDir, "tessdata");
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
            }
        }

        String fileList[] = {"eng.traineddata", "rus.traineddata"};


        for (String fileName : fileList) {
            try {
                // Создаем файлы с теми же именами что и ассеты
                String pathToDataFile = dir + "/" + fileName;
                if (!(new File(pathToDataFile)).exists()) {
                    // Копируем туда файлы
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = in.read(buff)) > 0) {
                        out.write(buff, 0, len);
                    }
                    in.close();
                    out.close();
                }

            } catch (Exception e) {
                Log.e("APP", e.getMessage());
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.searchTabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, PhotoActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction()))
            onNewIntent(getIntent());
        //Toast.makeText(this, "Первый запуск может быть длительным...", Toast.LENGTH_SHORT).show();
        checkPermission();
        prepareTessData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        }
    }

    private void doSearch(CharSequence query) {
        String q = (String) query;
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            if (mSectionsPagerAdapter.pages.get(i).queryEquals(q)) {
                mViewPager.setCurrentItem(i, true);
                return;
            }
        }
        try {
            mSectionsPagerAdapter.addPage(SearchResultFactory.search(q));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "При осуществлении поиска произошла ошибка!", Toast.LENGTH_LONG).show();
        }
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getCount() - 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Не удалось загрузить запросы", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<CharSequence> arr;

        try {
            arr = Objects.requireNonNull(intent.getExtras()).getCharSequenceArrayList("queries");
        } catch (NullPointerException e) {
            Toast.makeText(this, "Данных нет", Toast.LENGTH_SHORT).show();
            return;
        }

        if (arr == null) {
            Toast.makeText(this, "Данных нет", Toast.LENGTH_SHORT).show();
            return;
        }

        for (CharSequence s :
                arr) {
            doSearch(s);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_closetab) {
            if (mSectionsPagerAdapter.pages.size() != 0)
                mSectionsPagerAdapter.removePage(mViewPager.getCurrentItem());
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"StatementWithEmptyBody", "Duplicates"})
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<SearchResult> pages;
        private int count = 5;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ArrayList<SearchResult>();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a TabFragment (defined as a static inner class below).
            return TabFragment.newInstance(pages.get(position));
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position).getQuery();
        }

        public void removePage(int pos) {
            for (SearchResult res : pages)
                res.saveScroll();
            pages.remove(pos);
            notifyDataSetChanged();
            updateListeners();
        }

        public void addPage(SearchResult searchResult) {
            pages.add(searchResult);
            notifyDataSetChanged();
            updateListeners();
        }

        private void updateListeners() {
            LinearLayout tabs = (LinearLayout) mTabLayout.getChildAt(0);
            for (int i = 0; i < tabs.getChildCount(); i++) {
                final int fin = i;
                tabs.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(SearchActivity.this, "" + fin, Toast.LENGTH_SHORT).show();
                        removePage(fin);
                        return true;
                    }
                });
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TabFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        private SearchResult result;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_CONTENT = "search results";

        public TabFragment() {

        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static TabFragment newInstance(SearchResult result) {
            TabFragment fragment = new TabFragment();
            fragment.result = result;
            result.setTab(fragment);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabs, container, false);

            mRecyclerView = rootView.findViewById(R.id.cardList);
            mRecyclerView.setHasFixedSize(false);
            mLayoutManager = new LinearLayoutManager(container.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new SearchAdapter(getActivity(), result);
            mRecyclerView.setAdapter(mAdapter);

            // TODO Прокрутка к следющему элемменту
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_CONTENT)));
            return rootView;
        }

        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
            result.restoreScroll();
        }

        public RecyclerView getRecyclerView() {
            return mRecyclerView;
        }
    }
}
