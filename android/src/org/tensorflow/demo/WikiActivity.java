package org.tensorflow.demo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;

public class WikiActivity extends AppCompatActivity implements WikiJSON.IWikiJSON {
    static public String AppName = "Waila";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar pb;
    private TextView wikiText;
    private ImageView wikiImage;
    protected VolleyFetch volleyFetch;
    private final String BASE_URL = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts%7Cpageimages&list=&meta=&continue=&titles=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wiki_main);
        Intent activityThatCalled = getIntent();
        String query = activityThatCalled.getStringExtra("item");
        pb = (ProgressBar) findViewById(R.id.progressBar);
        wikiText = (TextView) findViewById(R.id.picTextRowText);
        wikiImage = (ImageView) findViewById(R.id.picTextRowPic);

        volleyFetch = new VolleyFetch();
        newSearch(query);
        Net.init(getApplicationContext());
    }

    // https://www.reddit.com/dev/api#GET_search
    protected void newSearch(String searchTerm) {
        Log.d(AppName, "Search for "+ searchTerm);
        // XXX Check the search term, then build the URL request.
        // see https://www.reddit.com/dev/api/
        // Reddit search terms must be less than 512 characters
        // You must use /r/aww
        // include these parameters &sort=hot&limit=100
        // Note: For testing, you can request a lower limit.
        String url = BASE_URL;
        if(!searchTerm.equals("") && searchTerm.length()<512){
            url += searchTerm ;
        }
        url += "&exintro=1&explaintext=1&pithumbsize=100";
        try {
            URL u = new URL(url);
            volleyFetch.add(this,u);
            fetchStart();
        }
        catch (MalformedURLException me){
            me.printStackTrace();
        }
        // Call VolleyFetch when you are ready
    }

    // XXX RedditJSON.IRedditJSON implies responsibilities
    public void fetchStart(){
        pb.setVisibility(View.VISIBLE);
    }

    public void fetchComplete(WikiItem wikiItem){
        wikiText.setText(wikiItem.title);
        Glide.with(wikiImage.getContext()).load(wikiItem.thumbnailURL).placeholder(R.drawable.ic_cloud_download_black_50dp).dontTransform().error(new ColorDrawable(Color.RED)).into(wikiImage);
        pb.setVisibility(View.GONE);
    }

    public void fetchCancel(String url){
        pb.setVisibility(View.GONE);
    }

    @Override
    protected void onStop () {
        super.onStop();
        // Advantage to Volley, can cancel requests when Activity dies,
        // not so for AsyncTask
        Net.getInstance().cancelPendingRequests(WikiActivity.AppName);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
