package com.basestructure.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.basestructure.R;
import com.basestructure.base.BaseActivity;
import com.basestructure.fragment.FragmentFive;
import com.basestructure.fragment.FragmentFour;
import com.basestructure.fragment.FragmentOne;
import com.basestructure.fragment.FragmentThree;
import com.basestructure.fragment.FragmentTwo;
import com.basestructure.syncadapter.NetworkUtilities;
import com.basestructure.util.AppAndroidUtils;
import com.basestructure.util.AppMessage;
import com.basestructure.util.AppWaitDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends BaseActivity {

    AppWaitDialog mWaitDialog=null;
    Button buttonSubmit;
    String url = "http://192.168.1.223/chaitanya/angularProjects/angular_pro2/cakebackend/items/add";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWaitDialog = new AppWaitDialog(this, false, "please wait...");

        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataInBackGround();
            }
        });

    }

    AsyncTask<Void, Void, AppMessage> saveRequest;
    public void saveDataInBackGround () {
        final AppWaitDialog hxWaitDialog = new AppWaitDialog(TestActivity.this);
        hxWaitDialog.setMessage("save data on server");

        saveRequest = new AsyncTask<Void, Void, AppMessage>() {
            @Override
            protected void onPreExecute() {
                AppAndroidUtils.hideKeyboard(TestActivity.this);
                if(mWaitDialog !=null) {
                    mWaitDialog.setTitle("please wait...");
                    mWaitDialog.show();
                }
            }

            @Override
            protected AppMessage doInBackground(Void... args) {
                JSONObject params = new JSONObject();
                try {
                    params.put("title", "Sambhaji Karad");
                    params.put("description", "Android Developer");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return NetworkUtilities.executePostResponse(url, params);
            }

            @Override
            protected void onPostExecute(AppMessage result) {

                if(mWaitDialog !=null) {
                    mWaitDialog.dismiss();
                }

                if (result.getMessageType() == AppMessage.MessageTypeEnum.SUCCESS) {
                    JSONObject json = result.getResponse();
                    result.setMessage("data save sucessfully");
                } else
                    result.setMessage("failed to save data on server");
            }
        };
        saveRequest.execute(null, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        AppAndroidUtils.startBackAnimation(this);
    }
}
