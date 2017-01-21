package com.thakurprojects.waterresourcemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ShowAllUsersActivity extends AppCompatActivity {

    // View elements
    ListView list;

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    // Loger
    Logger logger;

    // Channel Details
    private String channelID="";
    private String channelKey="";
    private String userDetails="";

    private static final String TAG_SCOPE = "field1";
    private static final String TAG_NAME = "field2";
    private static final String TAG_CHANNEL_ID = "field3";
    private static final String TAG_PASSWORD_IMEI = "field4";
    private static final String TAG_WRITE_KEY = "field5";
    private static final String TAG_READ_KEY = "field6";
    private static final String EMAIL_CONTACT = "field7";
    private static final String TAG_CREATEDBY = "field8";

    // Default URL
    private String url = "https://api.thingspeak.com/channels/";

    //JSON Node Names
    private static final String TAG_feed = "feeds";

    JSONArray feed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All User");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        // Get DEVICE details.
        Intent intent = getIntent();
        userDetails = intent.getStringExtra("com.thakurprojects.waterresourcemanager.USER_DETAILS");
        if(userDetails.equals("ALL")){
            getSupportActionBar().setTitle("ALL Users");
            channelID=getString(R.string.user_channel_id);
            channelKey=getString(R.string.user_channel_read_api);
            logger.addRecordToLog("************* Display All users Details ******************* ");
        } else {
            getSupportActionBar().setTitle("My Users");
            channelID=MainActivity.USER_channel_id;
            channelKey=MainActivity.USER_readkey;
            logger.addRecordToLog("************* Display My users Details ******************* ");
        }

        // Display all users on view
        new ShowAllUsersActivity.ShowUser().execute();
    }





    // Read all user details from MAIN user channel
    private class ShowUser extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowAllUsersActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Getting User Details ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Update url
            url=url+channelID+"/feeds.json?api_key="+channelKey;
            logger.addRecordToLog("URL : "+url);

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;


        }


        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);

                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String scope = c.getString("field1");
                    String user_name = c.getString("field2");
                    String user_channel_id = c.getString("field3");
                    String password = c.getString("field4").split("@")[0];
                    String imei = c.getString("field4").split("@")[1];
                    String writekey = c.getString("field5");
                    String readkey = c.getString("field6");
                    String email = c.getString("field7").split("%")[0];
                    String mobile = c.getString("field7").split("%")[1];
                    String createdby = c.getString("field8");


                    HashMap<String, String> map = new HashMap<String, String>();

                    logger.addRecordToLog("USER : "+user_name);

                    map.put(TAG_SCOPE, scope);
                    map.put(TAG_NAME, user_name);
                    map.put(TAG_CHANNEL_ID, user_channel_id);
                    map.put(TAG_PASSWORD_IMEI, password);
                    map.put(TAG_WRITE_KEY, writekey);
                    map.put(TAG_READ_KEY, readkey);
                    map.put(EMAIL_CONTACT, mobile);
                    map.put(TAG_CREATEDBY, createdby);

                    oslist.add(map);
                    list=(ListView)findViewById(R.id.list);

                    ListAdapter adapter = new SimpleAdapter(ShowAllUsersActivity.this, oslist,
                            R.layout.user_list_template,
                            new String[] { TAG_NAME,TAG_SCOPE,EMAIL_CONTACT,TAG_CREATEDBY}, new int[] {
                            R.id.userNameTV,R.id.scopeTV,R.id.mobileTV,R.id.adminTV});

                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            Toast.makeText(ShowAllUsersActivity.this, "DEVICE : "+oslist.get(+position).get(TAG_NAME), Toast.LENGTH_SHORT).show();


                            // Pass Channel ID and read Key details to Intent
                            String intentKey=oslist.get(+position).get(TAG_CHANNEL_ID)+"#"+oslist.get(+position).get(TAG_READ_KEY);
                            logger.addRecordToLog("View Clicked : "+intentKey);

                            Intent eachIntent = new Intent(getBaseContext(), EachUserDetailsActivity.class);
                            eachIntent.putExtra(MainActivity.USER_ID,intentKey);
                            startActivity(eachIntent);

                        }
                    });




                }

                logger.addRecordToLog("View Created ..");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
