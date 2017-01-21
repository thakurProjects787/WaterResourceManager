package com.thakurprojects.waterresourcemanager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int backButtonCount=0;
    private String userDetails="";

    TextView userInfodas;
    TextView deviceInfidas;


    // Intent Key
    public final static String USER_ID="com.thakurprojects.waterresourcemanager.USER_ID";
    public final static String DEVICE_DETAILS="com.thakurprojects.waterresourcemanager.DEVICE_DETAILS";
    public final static String DEVICE_MAP_ID="com.thakurprojects.waterresourcemanager.DEVICE_MAP_ID";
    public final static String DASHBOARD_ID="com.thakurprojects.waterresourcemanager.DASHBOARD_ID";
    public final static String STATISTICS_ID="com.thakurprojects.waterresourcemanager.STATISTICS_ID";
    public final static String DEVICE_STATISTICS_ID="com.thakurprojects.waterresourcemanager.DEVICE_STATISTICS_ID";

    // logger object
    Logger logger;

    // USER DETAILS
    public static String USER_channel_id = "";
    public static String USER_scope = "";
    public static String USER_password = "";
    public static String USER_name = "";
    public static String USER_email = "";
    public static String USER_writekey ="";
    public static String USER_readkey = "";
    public static String USER_mobile = "";
    public static String USER_createdby = "";

    //Analysis details
    public static String allStatesAnalysis="";
    public static String allDistrictAnalysis="";
    public static String allDeviceAnalysis="";

    public static ArrayList<String> allUsersDetails=new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get user email ID.
        Intent intent = getIntent();
        userDetails = intent.getStringExtra(LoginActivity.USER_DETAILS);

        //userDetails="AAA#aaa#sss#ddd#dddd#cdd#dddd#ddd#fff#rrr";
        USER_scope = userDetails.split("#")[0];
        USER_channel_id = userDetails.split("#")[1];
        USER_password = userDetails.split("#")[2];
        USER_name = userDetails.split("#")[3];
        USER_email = userDetails.split("#")[4];
        USER_writekey =userDetails.split("#")[5];
        USER_readkey = userDetails.split("#")[6];
        USER_mobile = userDetails.split("#")[8];
        USER_createdby = userDetails.split("#")[9];



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WaterM");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View navHeaderView = navigationView.getHeaderView(0);
        TextView userName = (TextView) navHeaderView.findViewById(R.id.userName_textview);
        TextView userEmail = (TextView) navHeaderView.findViewById(R.id.userEmail_textview);

        userName.setText(USER_name);
        userEmail.setText(USER_email);

        userInfodas=(TextView) findViewById(R.id.mainhomeuserinfoTV);
        deviceInfidas=(TextView) findViewById(R.id.mainhomedeviceinfoTV);


        /*
           Read all Analysis channels details
         */
        new MainActivity.ReadAllAnalysisChannels().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if(backButtonCount >= 1)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_dashboard_refresh) {
            new MainActivity.ReadAllAnalysisChannels().execute();
            return true;

        } else if(id == R.id.main_dashboard_user_details){

                String intentKey = USER_channel_id + "#" + USER_readkey;
                Intent eachIntent = new Intent(getBaseContext(), EachUserDetailsActivity.class);

                eachIntent.putExtra(MainActivity.USER_ID, intentKey);
                startActivity(eachIntent);

                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*
           All States DASHBOARD
         */
        if (id == R.id.country_dashboard) {

            // Sub String
            String centerTitle="INDIA";
            String tagsName="ALL STATES";
            String dashboardDetails=centerTitle+"%"+tagsName;


            // Start dashboard activity activity
            Intent addIntent = new Intent(this, BaseDashboardActivity.class);
            addIntent.putExtra(DASHBOARD_ID,dashboardDetails);
            startActivity(addIntent);


        }

        /*
           All Districts DASHBOARD
         */
        else if (id == R.id.district_dashboard) {

            // Sub String
            String centerTitle="DISTRICTS";
            String tagsName="ALL DISTRICTS";
            String dashboardDetails=centerTitle+"%"+tagsName;


            // Start dashboard activity activity
            Intent addIntent = new Intent(this, BaseDashboardActivity.class);
            addIntent.putExtra(DASHBOARD_ID,dashboardDetails);
            startActivity(addIntent);


        }

         /*
           All DEVICES DASHBOARD
         */
        else if (id == R.id.devices_dashboard) {

            // Sub String
            String centerTitle="DEVICES";
            String tagsName="ALL DEVICES";
            String dashboardDetails=centerTitle+"%"+tagsName;


            // Start dashboard activity activity
            Intent addIntent = new Intent(this, BaseDashboardActivity.class);
            addIntent.putExtra(DASHBOARD_ID,dashboardDetails);
            startActivity(addIntent);


        }

        /*
           All DEVICES DETAILS
         */
        else if (id == R.id.devices_details_dashboard) {
            // Start show all device activity
            Intent addIntent = new Intent(this, ShowDevicesActivity.class);
            addIntent.putExtra("com.thakurprojects.waterresourcemanager.DEVICE_DETAILS", "ALL");
            startActivity(addIntent);
            return true;
        }


        /*
           User Specific DASHBOARD
         */
        else if (id == R.id.my_dashboard) {
            /*
               Display user DEVICES
             */
            //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            //drawer.closeDrawer(GravityCompat.START);

            if(isConn()) {

                /*
                     Create Dashboard according to the scope
                 */
                String dashboardDetails="NON";

                if(MainActivity.USER_scope.equals("COUNTRY")) {

                    // Sub String
                    String centerTitle="INDIA";
                    String tagsName="ALL STATES";
                    dashboardDetails=centerTitle+"%"+tagsName;

                } else if(MainActivity.USER_scope.equals("STATE")){

                    // Sub String
                    String centerTitle=MainActivity.USER_name;
                    String tagsName="USER DISTRICT";
                    dashboardDetails=centerTitle+"%"+tagsName;


                } else if(MainActivity.USER_scope.equals("DISTRICT")){

                    // Sub String
                    String centerTitle=MainActivity.USER_name;
                    String tagsName="USER DEVICES";
                    dashboardDetails=centerTitle+"%"+tagsName;


                }

                // Start dashboard activity activity
                Intent addIntent = new Intent(this, BaseDashboardActivity.class);
                addIntent.putExtra(DASHBOARD_ID,dashboardDetails);
                startActivity(addIntent);


            } else {
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }

        } else if (id == R.id.show_my_devices) {

            if(MainActivity.USER_scope.equals("DISTRICT")) {
            /*
               Display all DEVICES
             */
                if (isConn()) {
                    // Start show all device activity
                    Intent addIntent = new Intent(this, ShowDevicesActivity.class);
                    addIntent.putExtra(DEVICE_DETAILS, "USER");
                    startActivity(addIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else {

                if (isConn()) {
                    // Start show all device activity
                    Intent addIntent = new Intent(this, ShowDevicesActivity.class);
                    addIntent.putExtra(DEVICE_DETAILS, "FILTER");
                    startActivity(addIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }

        } else if (id == R.id.add_new_device) {
            if(MainActivity.USER_scope.equals("DISTRICT")) {

            /*
               ADD NEW DEVICE
             */
                if (isConn()) {
                    // Start show all device activity
                    Intent addIntent = new Intent(this, SignupActivity.class);
                    startActivity(addIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
            }


        } else if (id == R.id.add_user) {

            if((MainActivity.USER_scope.equals("STATE"))||(MainActivity.USER_scope.equals("COUNTRY"))) {

           /*
               ADD NEW USER
             */
                if(isConn()) {
                    Intent addIntent = new Intent(this, AddUserActivity.class);
                    startActivity(addIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
            }

        }  else if (id == R.id.show_users) {

            if((MainActivity.USER_scope.equals("STATE"))||(MainActivity.USER_scope.equals("COUNTRY"))) {
            /*
              SHOW ALL USER
             */
                if(isConn()) {
                    Intent addIntent = new Intent(this, ShowAllUsersActivity.class);
                    addIntent.putExtra("com.thakurprojects.waterresourcemanager.USER_DETAILS", "USER");
                    startActivity(addIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
     Check internet is enabled or not.
    */
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    /*
       Read All Analysis channels Details
     */
    private class ReadAllAnalysisChannels extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONArray feed = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Please Wait ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            logger.addRecordToLog("-- Getting All Analysis channesl Details --  ");
            logger.addRecordToLog(": STATE Analysis ");
            allStatesAnalysis=getAnalysis(getString(R.string.STATE_ANALYSIS_channel_id), getString(R.string.STATE_ANALYSIS_channel_read_api));
            logger.addRecordToLog(": STATE Analysis : "+allStatesAnalysis.toString());
            if(allStatesAnalysis.isEmpty()){
                logger.addRecordToLog("ERROR : No STATE Analysis present ..  ");
            }

            logger.addRecordToLog(": DISTRICT Analysis ");
            allDistrictAnalysis=getAnalysis(getString(R.string.DISTRICT_ANALYSIS_channel_id), getString(R.string.DISTRICT_ANALYSIS_channel_read_api));
            logger.addRecordToLog(": DISTRICT Analysis : "+allDistrictAnalysis.toString());
            if(allDistrictAnalysis.isEmpty()){
                logger.addRecordToLog("ERROR : No DISTRICT Analysis present ..  ");
            }

            logger.addRecordToLog(": DEVICE Analysis ");
            allDeviceAnalysis=getAnalysis(getString(R.string.DEVICE_ANALYSIS_channel_id), getString(R.string.DEVICE_ANALYSIS_channel_read_api));
            logger.addRecordToLog(": DEVICE Analysis : "+allDeviceAnalysis.toString());
            if(allDeviceAnalysis.isEmpty()){
                logger.addRecordToLog("ERROR : No DEVICE Analysis present ..  ");
            }

            // Get Users Details
            allUsersDetails=getUserDetails(USER_channel_id,USER_readkey);
            logger.addRecordToLog("All Users : "+allUsersDetails.toString());

            return "YES";

        }


        @Override
        protected void onPostExecute(String json) {
            pDialog.dismiss();

            /*
               Update home dashboard infromation
             */

            String userDasboardInfo="Name : "+USER_name+"\nScope : "+USER_scope+"\nCreated By : "+USER_createdby;


            String deviceDasboardInfo="Connected States : "+allStatesAnalysis.split("%")[0].split("#").length+"\n" +
                    "Connected Districts : "+allDistrictAnalysis.split("%")[0].split("#").length+"\n" +
                    "Total Connected Devices : "+allDeviceAnalysis.split("%")[0].split("#").length;

            userInfodas.setText(userDasboardInfo);
            deviceInfidas.setText(deviceDasboardInfo);


        }

        /*
         Read MAIN DEVICE Channel
        */
        protected String getAnalysis(String channelID, String readKey){

            String allDetails="";

            logger.addRecordToLog("-- Reading ANALYSIS Channels Details ----------");

            JSONParser jParser = new JSONParser();



            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey+"&results=1";
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray("feeds");
                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");

                    // Updating arrayList
                    allDetails=field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8;

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allDetails;
        }

        /*
          Get Users details
         */
        protected ArrayList<String> getUserDetails(String channelID,String readKey){

            ArrayList<String> allUser=new ArrayList<String>();

            JSONParser jParser = new JSONParser();

            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey;
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray("feeds");
                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    /* Storing  JSON item in a Variable
                    String scope = c.getString("field1");

                    String user_channel_id = c.getString("field3");
                    String password = c.getString("field4").split("@")[0];
                    String imei = c.getString("field4").split("@")[1];
                    String writekey = c.getString("field5");
                    String readkey = c.getString("field6");
                    String email = c.getString("field7").split("%")[0];
                    String mobile = c.getString("field7").split("%")[1];
                    String createdby = c.getString("field8");
                    */

                    String user_name = c.getString("field2");

                    // Updating arrayList
                    allUser.add(user_name.toUpperCase());

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allUser;
        }
    }


}
