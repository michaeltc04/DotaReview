package app.practice.carrmichaelt.com.dotareview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by Michael on 3/19/2015.
 */
public class InitialActivity extends Activity {
    String mItemURL = "https://api.steampowered.com/IEconDOTA2_205790/GetGameItems/V001/?key=DF73E7763AF78B24350928B6B5F9FBE0&language=en"; //items
    String mHeroURL = "https://api.steampowered.com/IEconDOTA2_205790/GetHeroes/v0001/?key=DF73E7763AF78B24350928B6B5F9FBE0&language=en_us"; //heroes
    Context mContext;
    ArrayList<String> mLocalizedNameList, mNameList, mGameItemList;
    ArrayList<Integer> mGameItemCostList;
    String mItemJSONString, mHeroJSONString;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Button mFindButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_activity);

        mContext = this;
        mLocalizedNameList = new ArrayList<String>();
        mNameList = new ArrayList<String>();
        mGameItemList = new ArrayList<String>();
        mGameItemCostList = new ArrayList<Integer>();
        TextView textView = (TextView) findViewById(R.id.text_matchid);
        mFindButton = (Button) findViewById(R.id.button_matchid);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMatch();
            }
        });
    }

    public void findMatch() {
        EditText editText = (EditText) findViewById(R.id.edit_text_matchid);
        String matchIDText = editText.getText().toString();
        sp = getSharedPreferences("match", Context.MODE_PRIVATE);
        String previousMatchID = sp.getString("match", "");
        boolean test = previousMatchID.equals(matchIDText);

        if (matchIDText.length() == 10) {
            Intent intent = new Intent(this, matchActivity.class);
            intent.putExtra("matchid", matchIDText);
            intent.putExtra("previousMatch", test);
            startActivity(intent);
        }
    }

}
