package com.thakurprojects.waterresourcemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowDevicesActivity extends AppCompatActivity {

    ListView list;

    TextView id;
    TextView name;
    TextView devicetags;

    // Channel Details
    private String channelID="";
    private String channelKey="";
    private String deviceDetails="";

    //Filter option
    private String displayDeviceOnly="BOTH";

    private boolean filterDeviceDetails=false;

    // List for all channels details
    static ArrayList<String> allDeives=new ArrayList<String>();
    static String allDeivesLocation="";

    ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();

    // Loger
    Logger logger;

    // Default URL
    private String url = "https://api.thingspeak.com/channels/";

    //JSON Node Names
    private static final String TAG_feed = "feeds";
    private static final String TAG_ID = "field1";
    private static final String TAG_NAME = "field2";
    private static final String TAG_Tags = "field3";
    private static final String TAG_Write_Key = "field4";
    private static final String TAG_Read_Key = "field5";
    private static final String TAG_lat = "field6";
    private static final String TAG_lng = "field7";
    private static final String TAG_userID = "USER_ID";

    // Intent Key
    public final static String DEVICE_ID="com.thakurprojects.waterresourcemanager.DEVICE_ID";

    JSONArray feed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_devices);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Devices");

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
        logger.addRecordToLog(" >> Show Device details ");
        Intent intent = getIntent();
        deviceDetails = intent.getStringExtra(MainActivity.DEVICE_DETAILS);
        // Check device status
        if(deviceDetails.equals("ALL")){
            getSupportActionBar().setTitle("ALL Devices");
            channelID=getString(R.string.main_channel_id);
            channelKey=getString(R.string.main_channel_read_api);
            logger.addRecordToLog("************* Display All DEVICES Details ******************* ");
            logger.addRecordToLog(" - "+deviceDetails);
        } else if(deviceDetails.equals("USER")) {
            getSupportActionBar().setTitle("My Devices");
            channelID=MainActivity.USER_channel_id;
            channelKey=MainActivity.USER_readkey;
            logger.addRecordToLog("************* Display MY DEVICES Details ******************* ");
            logger.addRecordToLog(" - "+deviceDetails);
        } else if(deviceDetails.equals("FILTER")) {
            getSupportActionBar().setTitle("Configure Devices");
            filterDeviceDetails=true;
            channelID=getString(R.string.main_channel_id);
            channelKey=getString(R.string.main_channel_read_api);
            logger.addRecordToLog("************* Display Configure DEVICES Details ******************* ");
            logger.addRecordToLog(" - "+deviceDetails);
        } else {
            getSupportActionBar().setTitle("USER Devices");
            channelID=deviceDetails.split("#")[0];
            channelKey=deviceDetails.split("#")[1];
            logger.addRecordToLog("************* Display USER DEVICES Details ******************* ");
            logger.addRecordToLog(" - "+deviceDetails);
        }

        oslist = new ArrayList<HashMap<String, String>>();
        allDeives=new ArrayList<String>();

        // Get all devices details
        new ShowDevicesActivity.ShowDevices().execute();
    }

    @Override
    public void onBackPressed() {
        // Check if backgound task is running
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_show_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Display all devices on MAP
        if (id == R.id.all_map_view) {

            if(allDeivesLocation.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No Details Present !!", Toast.LENGTH_LONG).show();
            } else {
                // Start Map Activity
                logger.addRecordToLog(" STARTING MAP ACTIVITY  \n -- "+allDeivesLocation.toString());
                Intent addIntent = new Intent(this, DisplayDevicesOnMapActivity.class);
                addIntent.putExtra(MainActivity.DEVICE_MAP_ID, allDeivesLocation);
                startActivity(addIntent);
            }

            return true;
        }

        // Apply some filter
        if (id == R.id.both_filter) {
            displayDeviceOnly="BOTH";
            allDeives.clear();
            allDeivesLocation="";

            // Get all devices details
            new ShowDevicesActivity.ShowDevices().execute();

            return true;
        }

        if (id == R.id.supplier_filter) {
            displayDeviceOnly="Supplier";
            allDeives.clear();
            allDeivesLocation="";

            // Get all devices details
            new ShowDevicesActivity.ShowDevices().execute();

            return true;
        }

        if (id == R.id.reservoir_filter) {
            displayDeviceOnly="Reservoir";
            allDeives.clear();
            allDeivesLocation="";

            // Get all devices details
            new ShowDevicesActivity.ShowDevices().execute();

            //Toast.makeText(getApplicationContext(), "Filter Device", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Read all devices details from MAIN channel
    private class ShowDevices extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //id = (TextView)findViewById(R.id.devicecloudIDTV);
            name = (TextView)findViewById(R.id.deviceNameTV);
            devicetags = (TextView)findViewById(R.id.devicetagsTV);
            allDeivesLocation="";
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(null);
            oslist.clear();

            pDialog = new ProgressDialog(ShowDevicesActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Getting All Devices Details ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Update url
            url=url+channelID+"/feeds.json?api_key="+channelKey;

            logger.addRecordToLog(" URL - "+url);

            JSONObject json = jParser.getJSONFromUrl(url);
            return json;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            allDeives.clear();

            if(filterDeviceDetails){
                /*
                    FOr Filter details
                 */

                logger.addRecordToLog("**********  FILTER DETAILS *************");

                try {
                    // Getting JSON Array from URL
                    feed = json.getJSONArray(TAG_feed);
                    for (int i = 0; i < feed.length(); i++) {
                        JSONObject c = feed.getJSONObject(i);

                        // Storing  JSON item in a Variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String tags = c.getString(TAG_Tags);
                        String write_key = c.getString(TAG_Write_Key);
                        String read_key = c.getString(TAG_Read_Key);
                        String lat = c.getString(TAG_lat);
                        String lng = c.getString(TAG_lng);


                        // Check for FIlter details
                        logger.addRecordToLog(MainActivity.USER_name+" : "+tags.split(";")[4].toUpperCase()+" - "+tags.split(";")[5].toUpperCase());
                        if ((MainActivity.USER_name.equals(tags.split(";")[4].toUpperCase())) || (MainActivity.USER_name.equals(tags.split(";")[5].toUpperCase()))) {

                            logger.addRecordToLog("DEVICE FOUND !!");
                            logger.addRecordToLog("FILTER INFORMATION WITH : "+displayDeviceOnly);
                            logger.addRecordToLog("DEVICE STATUS : "+tags.split(";")[2]);
                            boolean considerStatus=false;

                            if(displayDeviceOnly.equals("BOTH")){
                                considerStatus=true;
                            } else considerStatus = displayDeviceOnly.equals(tags.split(";")[2]);

                            logger.addRecordToLog("STATUS : "+considerStatus);

                            if(considerStatus) {

                                // Updating arrayList
                                allDeives.add(id + "#" + name + "#" + tags + "#" + write_key + "#" + read_key + "#" + lat + "#" + lng);
                                // Adding value HashMap key => value

                                HashMap<String, String> map = new HashMap<String, String>();

                                //String editTags="#"+tags.replace(";"," #").substring(0,tags.length()-1);

                                String editTags = "#" + tags.replace(";na", "").replace(";", " #");
                                String mapDisplayLine = tags.split(";")[2] + "-" + tags.split(";")[1] + "," + tags.split(";")[3];

                                allDeivesLocation = allDeivesLocation + lat + "#" + lng + "#" + name + "#" + mapDisplayLine + "%";

                                map.put(TAG_ID, id);
                                map.put(TAG_NAME, name);
                                map.put(TAG_Tags, editTags);
                                map.put(TAG_Write_Key, write_key);
                                map.put(TAG_Read_Key, read_key);
                                map.put(TAG_userID, tags.split(";")[1].trim());


                                oslist.add(map);


                                ListAdapter adapter = new SimpleAdapter(ShowDevicesActivity.this, oslist,
                                        R.layout.list_template,
                                        new String[]{TAG_NAME, TAG_Tags}, new int[]{
                                        R.id.deviceNameTV, R.id.devicetagsTV});

                                list.setAdapter(adapter);
                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {
                                        Toast.makeText(ShowDevicesActivity.this, "DEVICE : " + oslist.get(+position).get(TAG_NAME), Toast.LENGTH_SHORT).show();

                                        // Pass Channel ID and read Key details to Intent
                                        String intentKey = oslist.get(+position).get(TAG_ID) + "#" + oslist.get(+position).get(TAG_Read_Key);


                                        Intent eachIntent = new Intent(getBaseContext(), EachDeviceActivity.class);
                                        eachIntent.putExtra(DEVICE_ID, intentKey);
                                        startActivity(eachIntent);

                                    }
                                });
                            }//eni consider

                        }//Check if end
                    } // Check for end

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {

                logger.addRecordToLog("**********  NO FILTER DETAILS *************");

                try {
                    // Getting JSON Array from URL
                    feed = json.getJSONArray(TAG_feed);
                    for (int i = 0; i < feed.length(); i++) {
                        JSONObject c = feed.getJSONObject(i);

                        // Storing  JSON item in a Variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_NAME);
                        String tags = c.getString(TAG_Tags);
                        String write_key = c.getString(TAG_Write_Key);
                        String read_key = c.getString(TAG_Read_Key);
                        String lat = c.getString(TAG_lat);
                        String lng = c.getString(TAG_lng);

                        logger.addRecordToLog("FILTER INFORMATION WITH : "+displayDeviceOnly);
                        logger.addRecordToLog("DEVICE STATUS : "+tags.split(";")[2]);
                        boolean considerStatus=false;

                        if(displayDeviceOnly.equals("BOTH")){
                            considerStatus=true;
                        } else considerStatus = displayDeviceOnly.equals(tags.split(";")[2]);

                        logger.addRecordToLog("STATUS : "+considerStatus);

                        if(considerStatus) {

                            // Updating arrayList
                            allDeives.add(id + "#" + name + "#" + tags + "#" + write_key + "#" + read_key + "#" + lat + "#" + lng);
                            // Adding value HashMap key => value

                            HashMap<String, String> map = new HashMap<String, String>();

                            //String editTags="#"+tags.replace(";"," #").substring(0,tags.length()-1);

                            String editTags = "#" + tags.replace(";na", "").replace(";", " #");
                            String mapDisplayLine = tags.split(";")[2] + "-" + tags.split(";")[1] + "," + tags.split(";")[3];

                            allDeivesLocation = allDeivesLocation + lat + "#" + lng + "#" + name + "#" + mapDisplayLine + "%";

                            map.put(TAG_ID, id);
                            map.put(TAG_NAME, name);
                            map.put(TAG_Tags, editTags);
                            map.put(TAG_Write_Key, write_key);
                            map.put(TAG_Read_Key, read_key);
                            map.put(TAG_userID, tags.split(";")[1].trim());


                            oslist.add(map);
                            list = (ListView) findViewById(R.id.list);

                            ListAdapter adapter = new SimpleAdapter(ShowDevicesActivity.this, oslist,
                                    R.layout.list_template,
                                    new String[]{TAG_NAME, TAG_Tags}, new int[]{
                                    R.id.deviceNameTV, R.id.devicetagsTV});

                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Toast.makeText(ShowDevicesActivity.this, "DEVICE : " + oslist.get(+position).get(TAG_NAME), Toast.LENGTH_SHORT).show();

                                    // Pass Channel ID and read Key details to Intent
                                    String intentKey = oslist.get(+position).get(TAG_ID) + "#" + oslist.get(+position).get(TAG_Read_Key);


                                    Intent eachIntent = new Intent(getBaseContext(), EachDeviceActivity.class);
                                    eachIntent.putExtra(DEVICE_ID, intentKey);
                                    startActivity(eachIntent);

                                }
                            });
                        }//eni consider

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }//else


        }

    }

}
