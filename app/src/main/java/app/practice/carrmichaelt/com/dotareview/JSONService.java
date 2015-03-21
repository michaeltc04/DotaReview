package app.practice.carrmichaelt.com.dotareview;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Michael on 3/16/2015.
 */
public class JSONService extends IntentService {

    public static final String ACTION_HERO_COMPLETE = "com.practice.carrmichaelt.action.HERO_COMPLETE";
    public static final String ACTION_ITEM_COMPLETE = "com.practice.carrmichaelt.action.ITEM_COMPLETE";
    public static final String ACTION_MATCH_COMPLETE = "com.practice.carrmichaelt.action.MATCH_COMPLETE";

    String mFeedURL, mPreference;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public JSONService() {
        super("JSONService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mFeedURL = intent.getStringExtra("url");
        mPreference = intent.getStringExtra("api");

        if (mFeedURL == null) return;

        try {
            URL url = new URL(mFeedURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Gets the data
            InputStream input = connection.getInputStream();
            //Reads the data in
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            //Convert to String
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String jsonData = builder.toString();

            Intent i;
            switch (mPreference) {
                case ("hero"):
                    sp = getSharedPreferences("hero", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("hero", jsonData);
                    editor.commit();
                    i = new Intent(ACTION_HERO_COMPLETE);
                    JSONService.this.sendBroadcast(i);
                    break;
                case ("item"):
                    sp = getSharedPreferences("item", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("item", jsonData);
                    editor.commit();
                    i = new Intent(ACTION_ITEM_COMPLETE);
                    JSONService.this.sendBroadcast(i);
                    break;
                case ("match"):
                    sp = getSharedPreferences("match", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("match", jsonData);
                    editor.commit();
                    sp = getSharedPreferences("matchid", Context.MODE_PRIVATE);
                    editor = sp.edit();
                    editor.putString("matchid", intent.getStringExtra("matchid"));
                    editor.commit();
                    i = new Intent(ACTION_MATCH_COMPLETE);
                    JSONService.this.sendBroadcast(i);
                    break;
            }
            //Release resources
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
