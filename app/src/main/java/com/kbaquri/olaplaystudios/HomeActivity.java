package com.kbaquri.olaplaystudios;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements SongsAdapter.SongsAdapterListener {

    private static final int RC_WRITE_EXTERNAL = 101;
    private String TAG = HomeActivity.class.getSimpleName();
    private static final String apiUrl = "http://starlord.hackerearth.com/studio";

    private RecyclerView mRecyclerView;
    private SongsAdapter mAdapter;

    private SearchView searchView;

    private SimpleExoPlayerView exoPlayerView;
    private PlayerManager player;

    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_logo_wide);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView = findViewById(R.id.recycler_view);

        exoPlayerView = findViewById(R.id.exo_player_view);
        player = new PlayerManager();


        mAdapter = new SongsAdapter(this, songList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        new GetSongs().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        player.init(this, exoPlayerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.reset();
    }

    @Override
    public void onDestroy() {
        player.release();
        super.onDestroy();
    }

    private class GetSongs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(HomeActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(apiUrl);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray songs = new JSONArray(jsonStr);
                    Song song;

                    // looping through All Contacts
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject s = songs.getJSONObject(i);

                        song = new Song();
                        song.setSong(s.getString("song"));
                        song.setArtists(s.getString("artists"));
                        song.setUrl(s.getString("url"));
                        song.setCoverImage(s.getString("cover_image"));

                        // adding song to songs list
                        songList.add(song);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSongSelected(Song song, int position) {
        Toast.makeText(getApplicationContext(), song.getSong().toUpperCase() + ": " + song.getArtists(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSongPlay(Song song) {
        player.setSong(song.getUrl());
    }

    @Override
    public void onSongDownload(Song song) {

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            new DownloadFile().execute(song.getUrl(), song.getSong());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.write_permission),
                    RC_WRITE_EXTERNAL, perms);
        }

    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        String songName;
        URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),
                    "Downloading song",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... urlParams) {
            songName = urlParams[1];
            int count;
            try {
                url = new URL(urlParams[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                //set the path where we want to save the file
                File SDCardRoot = Environment.getExternalStorageDirectory();
                //create a new file, to save the downloaded file
                File file = new File(SDCardRoot, songName + ".mp3");

                FileOutputStream fileOutput = new FileOutputStream(file);

                //Stream used for reading the data from the internet
                InputStream inputStream = connection.getInputStream();

                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = connection.getContentLength();

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / lenghtOfFile));
                    fileOutput.write(data, 0, count);
                }

                fileOutput.flush();
                fileOutput.close();
                inputStream.close();
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return songName;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("Download progress: ", String.valueOf(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),
                    "Download complete:" + s,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
