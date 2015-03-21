package app.practice.carrmichaelt.com.dotareview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael on 3/19/2015.
 */
public class matchActivity extends Activity {

    String mItemURL = "https://api.steampowered.com/IEconDOTA2_205790/GetGameItems/V001/?key=DF73E7763AF78B24350928B6B5F9FBE0&language=en"; //items
    String mHeroURL = "https://api.steampowered.com/IEconDOTA2_205790/GetHeroes/v0001/?key=DF73E7763AF78B24350928B6B5F9FBE0&language=en_us"; //heroes
    String mMatchURL = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=DF73E7763AF78B24350928B6B5F9FBE0&match_id="; //match info
    Context mContext;
    MyDireAdapter mDireAdapter;
    MyRadiantAdapter mRadiantAdapter;
    ArrayList<String> mLocalizedNameList, mNameList, mGameItemList, mRadiantHeroes, mDireHeroes;
    ArrayList<Integer> mGameItemCostList;
    ListView radiantListView, direListView;
    String mItemJSONString, mHeroJSONString, mMatchJSONString, mMatchID, mPrevMatchID;
    Map<String, String[]> mHeroItems, mHeroStats;
    ArrayAdapter<String> mAdapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_activity);
        mContext = this;
        direListView = (ListView) findViewById(R.id.list_view_dire);
        radiantListView = (ListView) findViewById(R.id.list_view_radiant);
        mRadiantHeroes = new ArrayList<String>();
        mDireHeroes = new ArrayList<String>();
        mLocalizedNameList = new ArrayList<String>();
        mNameList = new ArrayList<String>();
        mGameItemList= new ArrayList<String>();
        mGameItemCostList = new ArrayList<Integer>();
        mHeroItems = new HashMap<String, String[]>();
        mHeroStats = new HashMap<String, String[]>();

//        direListView.setAdapter(new ArrayAdapter<String>(this, R.layout.dire_layout, mRadiantHeroes));
//        radiantListView.setAdapter(new ArrayAdapter<String>(this, R.layout.dire_layout, mDireHeroes));
        doInitialIntentsAndRegistering();

        mDireAdapter = new MyDireAdapter(this, mDireHeroes, mHeroItems, mHeroStats);
        mRadiantAdapter = new MyRadiantAdapter(this, mRadiantHeroes, mHeroItems, mHeroStats);
        //radiantListView.setAdapter(mRadiantAdapter);
        direListView.setAdapter(mDireAdapter);
        radiantListView.setAdapter(mRadiantAdapter);
    }

    private void doInitialIntentsAndRegistering() {
        IntentFilter heroIntentFilter = new IntentFilter();
        heroIntentFilter.addAction(JSONService.ACTION_HERO_COMPLETE);
        registerReceiver(heroReceiver, heroIntentFilter);

        IntentFilter itemIntentFilter = new IntentFilter();
        itemIntentFilter.addAction(JSONService.ACTION_ITEM_COMPLETE);
        registerReceiver(itemReceiver, itemIntentFilter);

        IntentFilter matchIntentFilter = new IntentFilter();
        matchIntentFilter.addAction(JSONService.ACTION_MATCH_COMPLETE);
        registerReceiver(matchReceiver, matchIntentFilter);

        //If hero or item has already been done, do not do again
        sp = getSharedPreferences("hero", Context.MODE_PRIVATE);
        mHeroJSONString = "";
        //mHeroJSONString = sp.getString("hero", "");
        sp = getSharedPreferences("item", Context.MODE_PRIVATE);
        mItemJSONString = "";
        //mItemJSONString = sp.getString("item", "");
        sp = getSharedPreferences("matchid", Context.MODE_PRIVATE);
        mPrevMatchID = sp.getString("matchid", "");

        if (mHeroJSONString.length() == 0) {
            Intent heroIntent = new Intent(this, JSONService.class);
            heroIntent.putExtra("url", mHeroURL);
            heroIntent.putExtra("api", "hero");
            startService(heroIntent);
        } else if (mHeroJSONString.length() > 0) {
            mapHeroes();
        }

        if (mItemJSONString.length() == 0) {
            Intent itemIntent = new Intent(this, JSONService.class);
            itemIntent.putExtra("url", mItemURL);
            itemIntent.putExtra("api", "item");
            startService(itemIntent);
        } else if (mItemJSONString.length() > 0) {
            mapItems();
        }

        Intent startingIntent = getIntent();
        String mMatchID = startingIntent.getStringExtra("matchid");
        //Ensure that this isn't the same match ID as previously entered. If it IS then the
        //desired JSON information is already stored inside the key "match", so just run the setup.
        if (!mPrevMatchID.equals(mMatchID)) {
            mMatchID = mMatchURL + "" + startingIntent.getStringExtra("matchid");
            Intent matchIntent = new Intent(this, JSONService.class);
            matchIntent.putExtra("url", mMatchID);
            matchIntent.putExtra("api", "match");
            matchIntent.putExtra("matchid", startingIntent.getStringExtra("matchid"));
            startService(matchIntent);
        } else {
            setupMatchInformation();
        }



    }

    /**
     *  Populates the proper lists with their desired values (name/localized name) by the hero id.
     *  All information provided via a web API call (only once initially) inside JSONService.class.
     *  The JSON string is saved into preferences to avoid future API calls.
     */
    public void mapHeroes() {
        int i = 0;
        while (i < 114) {
            mLocalizedNameList.add("");
            mNameList.add("");
            i++;
        }
        String name;
        try {
            JSONObject json = new JSONObject(mHeroJSONString);
            JSONObject results = json.getJSONObject("result");
            JSONArray heroes = results.getJSONArray("heroes");
            for (i = 0; i < heroes.length(); i++) {
                JSONObject hero = heroes.getJSONObject(i);
                mLocalizedNameList.set(Integer.parseInt(hero.getString("id")),
                        hero.getString("localized_name"));
                name = hero.getString("name").substring(14, hero.getString("name").length());
                mNameList.set(Integer.parseInt(hero.getString("id")), name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Populates the proper lists with their desired values (name/cost) by the item id.
     *  All information provided via a web API call (only once initially) inside JSONService.class.
     *  The JSON string is saved into preferences to avoid future API calls.
     */
    public void mapItems() {
        int i = 0;
        while (i < 244) {
            mGameItemCostList.add(0);
            mGameItemList.add("");
            i++;
        }
        String name;
        try {
            JSONObject json = new JSONObject(mItemJSONString);
            JSONObject results = json.getJSONObject("result");
            JSONArray items = results.getJSONArray("items");
            for (i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                name = item.getString("name").substring(5, item.getString("name").length());
                mGameItemList.set(Integer.parseInt(item.getString("id")), name);
                mGameItemCostList.set(Integer.parseInt(item.getString("id")),
                        item.getInt("cost"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("arf");
    }

    private void setupMatchInformation() {
        sp = getSharedPreferences("match", Context.MODE_PRIVATE);
        mMatchJSONString = sp.getString("match", "");
        String[] itemIDs = new String[6];
        String[] heroStats = new String[9];
        int i = 0;
        while (i < 5) {
            mRadiantHeroes.add("");
            mDireHeroes.add("");
            i++;
        }

        try {
            JSONObject json = new JSONObject(mMatchJSONString);
            JSONObject results = json.getJSONObject("result");
            JSONArray players = results.getJSONArray("players");
            //JSONArray abilities = results.getJSONArray("ability_upgrades");
            for (i = 0; i < players.length(); i++) {
                itemIDs[0] = ""; itemIDs[1] = ""; itemIDs[2] = "";
                itemIDs[3] = ""; itemIDs[4] = ""; itemIDs[5] = "";
                String heroKey = "" + i;
                JSONObject player = players.getJSONObject(i);

                if (i < 5 && i >= 0) {
                    mRadiantHeroes.set(i, player.getString("hero_id"));
                } else if (i < 10 && i >= 5) {
                    mDireHeroes.set(i-5, player.getString("hero_id"));
                }

                itemIDs[0] = player.getString("item_0");
                itemIDs[1] = player.getString("item_1");
                itemIDs[2] = player.getString("item_2");
                itemIDs[3] = player.getString("item_3");
                itemIDs[4] = player.getString("item_4");
                itemIDs[5] = player.getString("item_5");
                mHeroItems.put(heroKey, itemIDs);

                heroStats[0] = player.getString("level");
                heroStats[1] = player.getString("kills");
                heroStats[2] = player.getString("deaths");
                heroStats[3] = player.getString("assists");
                heroStats[4] = player.getString("last_hits");
                heroStats[5] = player.getString("denies");
                heroStats[6] = player.getString("gold_per_min");
                heroStats[7] = player.getString("xp_per_min");
                heroStats[8] = player.getString("leaver_status");
                mHeroStats.put(heroKey, heroStats);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("arf");
    }

    //Broadcast Receivers
    private BroadcastReceiver matchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast arf = Toast.makeText(context, "Match Processed", Toast.LENGTH_LONG);
            setupMatchInformation();
        }
    };
    private BroadcastReceiver heroReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast arf = Toast.makeText(context, "Heros Processed", Toast.LENGTH_LONG);
            mapHeroes();
        }
    };
    private BroadcastReceiver itemReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast arf = Toast.makeText(context, "Items Processed", Toast.LENGTH_LONG);
            mapItems();
        }
    };
}
