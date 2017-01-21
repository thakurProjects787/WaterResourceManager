package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainServerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // request Details
    TextView _addRequests;
    TextView _deleteRequests;

    private  String allAddResuestDetails="";
    private  String allDeleteResuestDetails="";

    private boolean MAINUPDATED=false;

    // Loger
    Logger logger;

    // Default URL
    private static final String TAG_feed = "feeds";
    JSONArray feed = null;

    // List for all channels details
    private static ArrayList<String> allADDDevicesReq=new ArrayList<String>();
    private static ArrayList<String> allDELETEDevicesReq=new ArrayList<String>();

    private ArrayList<String> allMAINUSERS=new ArrayList<String>();
    private ArrayList<String> allMAINDEVICES=new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_server);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Server");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.server_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_server_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        _addRequests=(TextView) findViewById(R.id.alladdrequestsdetailsTV);
        _deleteRequests=(TextView) findViewById(R.id.alldeleterequestsdetailsTV);

        new MainServerActivity.GetAllRequest().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_server_refresh) {
            //Toast.makeText(getApplicationContext(), "Refresh View", Toast.LENGTH_LONG).show();
            // Get each devices details
            new MainServerActivity.GetAllRequest().execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
         /*
           PROCESS ADD REQUEST
         */
        if (id == R.id.main_server_process_add_request) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("PROCESS REQUESTS");
            alertDialogBuilder.setMessage("Are you sure,You want to Process All ADD Requests?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.ProcessADDRequest().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;

        }

        /*
           PROCESS ADD CLEAR REQUEST
         */
        if (id == R.id.main_server_clear_add_request) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("CLEAR REQUEST");
            alertDialogBuilder.setMessage("Are you sure,You want to Clear All ADD Requests?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.ClearAllADDRequest().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;

        }

         /*
           PROCESS DELETE REQUEST
         */
        if (id == R.id.main_server_process_delete_request) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("PROCESS REQUESTS");
            alertDialogBuilder.setMessage("Are you sure,You want to Process All DELETE Requests?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.ProcessDELETERequest().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;

        }

        /*
           PROCESS CLEAR DELETE REQUEST
         */
        if (id == R.id.main_server_clear_delete_request) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("CLEAR REQUEST");
            alertDialogBuilder.setMessage("Are you sure,You want to Clear All DELETE Requests?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.ClearAllDELETERequest().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;


        }

        /*
           PROCESS UPDATE MAIN CHANNEL
         */
        if (id == R.id.main_server_update_main_channel) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("UPDATE MAIN");
            alertDialogBuilder.setMessage("Are you sure,You want to Update MAIN Channel?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.UpdateMainDeviceChannel().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;

        }

        /*
           PROCESS RESET
         */
        if (id == R.id.main_server_clear_cloud) {

            if(MAINUPDATED) {

             /* Alert Dialog Code Start*/
                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
                alert.setTitle("RESET SYSTEM"); //Set Alert dialog title here
                alert.setMessage("RESET Complete System.\n Before Start RESET Process, Please first Update MAIN Channels.\n\nPlease provide RESET Password :"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTextColor(R.color.black);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String password = input.getEditableText().toString();
                        //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                        if (password.equals("ClearWater")) {
                            new MainServerActivity.ResetCompleteSystem().execute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }


                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                /* Alert Dialog Code End*/
            } else {
                Toast.makeText(getApplicationContext(), "Please Update all MAIN Channels!", Toast.LENGTH_LONG).show();
            }

            return true;

        }

        /*
           SHOW ALL DEVICE DETAILS
         */
        if (id == R.id.main_server_all_devices) {

            // Start show all device activity
            Intent addIntent = new Intent(this, ShowDevicesActivity.class);
            addIntent.putExtra("com.thakurprojects.waterresourcemanager.DEVICE_DETAILS", "ALL");
            startActivity(addIntent);
            return true;
        }

        /*
           SHOW ALL USERS DETAILS
         */
        if (id == R.id.main_server_all_users) {
            Intent addIntent = new Intent(this, ShowAllUsersActivity.class);
            addIntent.putExtra("com.thakurprojects.waterresourcemanager.USER_DETAILS", "ALL");
            startActivity(addIntent);
            return true;
        }

         /*
           PROCESS CLEAR ALl DEVICES DATA
         */
        if (id == R.id.main_server_clear_device_data) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("CLEAR DEVICE");
            alertDialogBuilder.setMessage("Are you sure,You want to Clear All DEVICE Data?");

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // Delete Device Operation
                    new MainServerActivity.ClearAllDEVICEChannels().execute();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }



    /*
      ==================================================================
      ------------- SERVER ACTIVITY ------------------------------------
      ==================================================================
     */

    /*
       Read all requests
      */
    private class GetAllRequest extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Get All Devices Requests...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
                if(getAllADDRequests().equals("YES")){
                    String status=getAllDELETERequests();
                    return status;
                } else {
                    return "NO";
                }
        }

        @Override
        protected void onPostExecute(String status) {
            pDialog.dismiss();

            // Update delete details on view
            logger.addRecordToLog(" >> "+allDeleteResuestDetails);
            if(allDeleteResuestDetails.isEmpty()){
                _deleteRequests.setText("No Request !!");
            } else {
                _deleteRequests.setText(allDeleteResuestDetails);
            }

            // Update add details on view
            logger.addRecordToLog(" >> "+allAddResuestDetails);
            if(allAddResuestDetails.isEmpty()){
                _addRequests.setText("No Request !!");
            } else {
                _addRequests.setText(allAddResuestDetails);
            }

            if(status.equals("YES")){
                logger.addRecordToLog("- Updating request PASSED !!");
                Toast.makeText(getApplicationContext(), "Requests Updated !!", Toast.LENGTH_LONG).show();
            } else {
                logger.addRecordToLog("- Updating request FAILED !!");
                Toast.makeText(getApplicationContext(), "Requests Failed !!", Toast.LENGTH_LONG).show();
            }

        }

        // Get  ADD REQUEST Channel
        protected String getAllADDRequests(){

            logger.addRecordToLog(" ******** READING ALL ADD DEVICES REQUESTS **********************");
            allAddResuestDetails="";

            String status="NO";
            JSONParser jParser = new JSONParser();
            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+getString(R.string.add_req_channel_id)+"/feeds.json?api_key="+getString(R.string.add_req_channel_read_api);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);

            // Process return json
            allADDDevicesReq.clear();

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);

                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");
                    // Updating arrayList
                    allADDDevicesReq.add(field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);

                    allAddResuestDetails=allAddResuestDetails+"\n"+field1.split("#")[0]+" - "+field2;
                    logger.addRecordToLog(" >> "+field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);
                }


                status="YES";

            } catch (JSONException e) {
                e.printStackTrace();
                logger.addRecordToLog(" ADD REQUEST LIST FAILED : "+e.toString());
                status="NO";
            }

            logger.addRecordToLog(" ADD REQUEST LIST UPDATED !!!");

            return status;

        }//end of fcn

        // Get  DELETE REQUEST Channel
        protected String getAllDELETERequests(){

            logger.addRecordToLog(" ******** READING ALL DELETE DEVICES REQUESTS **********************");

            allDeleteResuestDetails="";
            String status="NO";
            JSONParser jParser = new JSONParser();
            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+getString(R.string.delete_req_channel_id)+"/feeds.json?api_key="+getString(R.string.delete_req_channel_read_api);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);

            // Process return json
            allDELETEDevicesReq.clear();

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);

                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");
                    // Updating arrayList
                    allDELETEDevicesReq.add(field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);

                    allDeleteResuestDetails=allDeleteResuestDetails+"\n"+field2+" - "+field1;
                    logger.addRecordToLog(" >> "+field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);
                }

                status="YES";

            } catch (JSONException e) {
                e.printStackTrace();
                logger.addRecordToLog(" DELETE REQUEST LIST FAILED : "+e.toString());
                status="NO";
            }
            logger.addRecordToLog(" DELETE REQUEST LIST UPDATED !!!");
            return status;

        }//end of fcn


    }// end of class

    /*
      PROCESS ADD REQUEST
     */
    private class ProcessADDRequest extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("ADD REQUESTS Processing ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... data) {
            if(processAllRequests().equals("YES")){
                return clearADDRequestChannel();
            } else{
                return "NO";
            }

        }

        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();
            // Check Result Status
            if(result.equals("NO")){
                logger.addRecordToLog("ADD Channel Request FAILED");
                Toast.makeText(getApplicationContext(), "ADD REQUEST FAILED !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "ADD REQUEST PASSED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ADD Channel Request PASSED");
                logger.addRecordToLog("**********************************************************");
            }

            logger.addRecordToLog(" *** PROCESS ADD REQUEST END +++");
        }

        /*
           Process all ADD Devices requests
         */
        protected String processAllRequests(){
            String status="YES";

            logger.addRecordToLog(" *** PROCESS ADD REQUEST START +++");

            ArrayList<String> allREQ=getAllADDRequests();
            if(allREQ.isEmpty()){
                logger.addRecordToLog("NO ADD REQUEST");
                status="YES";
            } else {
                Iterator allRequestDetails = allREQ.iterator();
                while (allRequestDetails.hasNext()) {

                    String eachRequestDetails = allRequestDetails.next().toString();

                    logger.addRecordToLog(" >> Handle Request For : "+eachRequestDetails);

                    // Create new Channel
                    String addResponse = createNewDEVICEChannel(eachRequestDetails);
                    if (addResponse.equals("NO")) {
                        return "NO";
                    } else {
                        // Update information into MAIN Channel
                        String addingStatus= parseJSONandUpdate(addResponse, eachRequestDetails);

                        if(addingStatus.equals("NO")){
                            return "NO";
                        }

                    }

                }//end of while
            }

            return status;
        }

        /*
            Get  ADD REQUEST Channel
          */
        protected ArrayList<String> getAllADDRequests(){

            logger.addRecordToLog(" ******** READING ALL ADD DEVICES REQUESTS **********************");

            ArrayList<String> allReq=new ArrayList<String>();

            JSONParser jParser = new JSONParser();
            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+getString(R.string.add_req_channel_id)+"/feeds.json?api_key="+getString(R.string.add_req_channel_read_api);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);

                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");
                    // Updating arrayList
                    allReq.add(field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);
                 }

                logger.addRecordToLog("GETTING ALL DEVICE DETAILS PASSED");

                return allReq;

            } catch (JSONException e) {
                e.printStackTrace();
                logger.addRecordToLog("GETTING ALL DEVICE DETAILS FAILED : "+e.toString());

                return allReq;

            }

        }//end of fcn

        /*
            Create new channel and return json response.
         */
        protected String createNewDEVICEChannel(String entry){
            String status = "NO";

            logger.addRecordToLog(" -----  CREATING NEW DEVICE ---- ");

            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(create_channel_api);

            // Parse user details
            String userdetails=entry.split("%")[0];
            String name=entry.split("%")[1];
            String desc_a=entry.split("%")[2];
            String Latitude=entry.split("%")[3];
            String Longitude=entry.split("%")[4];
            String user_write_key=entry.split("%")[5];
            String areacover=entry.split("%")[6];
            String timming=entry.split("%")[7];


            logger.addRecordToLog(" -- "+name+"  START");

            // Create complete description
            String desc="TYPE : "+desc_a.split("#")[0]+"\nADDRESS : "+desc_a.split("#")[1]+"\n DIST : "+desc_a.split("#")[2]+"\n PIN CODE : "+desc_a.split("#")[3]+"\n STATE : "+desc_a.split("#")[4]+"\n COUNTRY : "+desc_a.split("#")[5]+"\n CONTACT NO : "+desc_a.split("#")[6]+"\n AREA COVER : "+areacover+
                    "\n TIMMING : "+timming+"\n USERID : "+userdetails.split("#")[0]+"\n USEREMAIL : "+userdetails.split("#")[1];
            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

            String channelKey=getString(R.string.my_api);
            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("name", name));
            nameValuePair.add(new BasicNameValuePair("description", desc));
            nameValuePair.add(new BasicNameValuePair("tags", "DEVICE,"+userdetails.split("#")[0]+","+desc_a.split("#")[0]+","+desc_a.split("#")[3]+","+desc_a.split("#")[4]+","+desc_a.split("#")[5]+","+areacover));
            nameValuePair.add(new BasicNameValuePair("latitude", Latitude));
            nameValuePair.add(new BasicNameValuePair("longitude", Longitude));

            //nameValuePair.add(new BasicNameValuePair("public_flag", "true"));
            nameValuePair.add(new BasicNameValuePair("field1", "TC_CL"));
            nameValuePair.add(new BasicNameValuePair("field2", "TInlet"));
            nameValuePair.add(new BasicNameValuePair("field3", "TOutlet"));
            nameValuePair.add(new BasicNameValuePair("field4", "OutletToOther"));
            nameValuePair.add(new BasicNameValuePair("field5", "InletStatus"));
            nameValuePair.add(new BasicNameValuePair("field6", "OutletStatus"));
            nameValuePair.add(new BasicNameValuePair("field7", "WATER_QUALITY"));
            nameValuePair.add(new BasicNameValuePair("field8", "ACTION"));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : httpPost.setEntity\n"+e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("ADD Channel HTTP Response : "+jsonresponse);
                Log.d("Http Post Response:", jsonresponse);
                status=jsonresponse;
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : Http Post Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("Adding device done..");
            return status;
        }

        /*
        Parse json response format
         */
        protected String parseJSONandUpdate(String response,String entry){

            String data = "NO";

            logger.addRecordToLog(" --- Parse JSON Format ------ ");

            // Parse user details
            String user_write_key=entry.split("%")[5];
            String timming=entry.split("%")[7];
            String name=entry.split("%")[1];


            try {
                JSONObject  jsonRootObject = new JSONObject(response);

                // Read all root objects details
                String channelID=jsonRootObject.getString("id");
                String channelname=jsonRootObject.getString("name");
                String writeKey="";
                String readKey="";
                String tags="";

                String latitude=jsonRootObject.getString("latitude");
                String longitude=jsonRootObject.getString("longitude");

                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("tags");

                //Iterate the jsonArray and print the info of JSONObjects
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if(tags.isEmpty()){
                        tags=jsonObject.getString("name");
                    } else {
                        tags = tags+";"+jsonObject.getString("name");
                    }
                }

                // Read Write key
                JSONArray jsonArray2 = jsonRootObject.optJSONArray("api_keys");
                for(int i=0; i < jsonArray2.length(); i++){
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);

                    // Check for write key
                    if(jsonObject.getBoolean("write_flag")){
                        writeKey=jsonObject.getString("api_key");
                    } else {
                        readKey=jsonObject.getString("api_key");
                    }
                }

                // Combine all Data and update MAIN Channel
                logger.addRecordToLog("Channel Filter Details : \n"+user_write_key+","+channelID+","+channelname+","+tags+","+writeKey+","+readKey+","+latitude+","+longitude);

                // If ZERO return again we have to perfrom UPDATE
                String updateStatus = "NO";

                do {
                    updateStatus = updateUserChannel(user_write_key,channelID,channelname,tags,writeKey,readKey,latitude,longitude);
                } while (updateStatus.equals("ZERO"));

                if (updateStatus.equals("NO")) {
                    logger.addRecordToLog("Updation FAILED!!");
                    data="NO";
                } else {
                    logger.addRecordToLog("Updation PASSED!!");
                }

                // Update details into MAIN DEVICE Channel
                String deviceLine=channelID+"#"+channelname+"#"+tags+"#"+writeKey+"#"+readKey+"#"+latitude+"#"+longitude+"#NA";
                do {
                    updateStatus = updateMainDeviceChannel(deviceLine);
                } while (updateStatus.equals("ZERO"));

                if (updateStatus.equals("NO")) {
                    logger.addRecordToLog("Updation FAILED!!");
                    data="NO";
                } else {
                    logger.addRecordToLog("Updation PASSED!!");
                }


                //data=channelID+"\n"+channelname+"\n"+tags+"\n"+writeKey;

                // Update new channel with dummy data
                String[] dummy = { "0", "0", "0","0", "0", "0","0", "0" };
                data=updateDummyDataChannel(writeKey,dummy);

                logger.addRecordToLog(" -- "+name+"  PARSING END");

                return data;
                //output.setText(data);
            } catch (JSONException e) {e.printStackTrace();}

            return data;

        }

        /*
         Update USER channel field
         */
        protected String updateUserChannel(String user_write_key,String id,String name,String tags,String write_key,String read_key,String lat,String lng){

            String status="NO";

            logger.addRecordToLog(" --- UPDATE User Channel ------ ");

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channelKey=user_write_key;
            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("field1", id));
            nameValuePair.add(new BasicNameValuePair("field2", name));
            nameValuePair.add(new BasicNameValuePair("field3", tags));
            nameValuePair.add(new BasicNameValuePair("field4", write_key));
            nameValuePair.add(new BasicNameValuePair("field5", read_key));
            nameValuePair.add(new BasicNameValuePair("field6", lat));
            nameValuePair.add(new BasicNameValuePair("field7", lng));
            nameValuePair.add(new BasicNameValuePair("field8", "NA"));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                logger.addRecordToLog("Update user channel with new channel : " + jsonresponse);
                if(jsonresponse.equals("0")){
                    status = "ZERO";
                } else {
                    status = "YES";
                }
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            return status;
        }


        /*
         Update new channel with dummy data
         */
        protected String updateDummyDataChannel(String key,String[] fieldDetails){

            String status="NO";

            logger.addRecordToLog(" --- UPDATE Dummy DATA ------ ");

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);
            nameValuePair.add(new BasicNameValuePair("api_key", key));
            nameValuePair.add(new BasicNameValuePair("field1", fieldDetails[0]));
            nameValuePair.add(new BasicNameValuePair("field2", fieldDetails[1]));
            nameValuePair.add(new BasicNameValuePair("field3", fieldDetails[2]));
            nameValuePair.add(new BasicNameValuePair("field4", fieldDetails[3]));
            nameValuePair.add(new BasicNameValuePair("field5", fieldDetails[4]));
            nameValuePair.add(new BasicNameValuePair("field6", fieldDetails[5]));
            nameValuePair.add(new BasicNameValuePair("field7", fieldDetails[6]));
            nameValuePair.add(new BasicNameValuePair("field8", fieldDetails[7]));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);
                logger.addRecordToLog("Updating new Channel with dummy data : "+response);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            return status;
        }

        /*
        Update MAIN DEVICE channel field
        */
        protected String updateMainDeviceChannel(String deviceLine){

            String status="NO";
            logger.addRecordToLog("**** START UPDATING MAIN CHANNEL ***********");

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channel_write_key=getString(R.string.main_channel_write_api);

            nameValuePair.add(new BasicNameValuePair("api_key",channel_write_key ));
            nameValuePair.add(new BasicNameValuePair("field1", deviceLine.split("#")[0]));
            nameValuePair.add(new BasicNameValuePair("field2", deviceLine.split("#")[1]));
            nameValuePair.add(new BasicNameValuePair("field3", deviceLine.split("#")[2]));
            nameValuePair.add(new BasicNameValuePair("field4", deviceLine.split("#")[3]));
            nameValuePair.add(new BasicNameValuePair("field5", deviceLine.split("#")[4]));
            nameValuePair.add(new BasicNameValuePair("field6", deviceLine.split("#")[5]));
            nameValuePair.add(new BasicNameValuePair("field7", deviceLine.split("#")[6]));
            nameValuePair.add(new BasicNameValuePair("field8", deviceLine.split("#")[7]));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                logger.addRecordToLog("Update main channel with new channel : " + jsonresponse);
                if(jsonresponse.equals("0")){
                    status = "ZERO";
                } else {
                    status = "YES";
                }
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("STATUS : "+status);
            return status;
        }


        /*
         clear add device request
         */
        protected String clearADDRequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING ADD REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.add_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }



    } // end of class

    /*
       PROCESS ALL DELETE REQUEST
     */
    private class ProcessDELETERequest extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("DELETE REQUESTS Processing ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... data) {
                if(processAllRequests().equals("YES")){
                    return clearDELETERequestChannel();
                } else{
                    return "NO";
                }

        }


        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();
            // Check Result Status
            if(result.equals("NO")){
                logger.addRecordToLog("DELETE Channel Request FAILED");
                Toast.makeText(getApplicationContext(), "DELETE REQUEST FAILED !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "DELETE REQUEST PASSED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("DELETE Channel Request PASSED");
                logger.addRecordToLog("**********************************************************");
            }

            logger.addRecordToLog(" *** PROCESS DELETE REQUEST END +++");
        }

        /*
           Process all DELETE Devices requests
         */
        protected String processAllRequests(){
            String status="YES";

            logger.addRecordToLog(" \n\n********* PROCESS DELETE REQUEST START ***********");

            ArrayList<String> allREQ=getAllDELETERequests();

            if(allREQ.isEmpty()){
                logger.addRecordToLog("NO DELETE REQUEST");
                status="YES";
            } else {

                Iterator allRequestDetails = allREQ.iterator();
                while (allRequestDetails.hasNext()) {

                    String eachRequestDetails = allRequestDetails.next().toString();

                    // DELETE CHANNEL
                    String responseStatus_d = deleteDeviceChannel(eachRequestDetails);

                    if (responseStatus_d.equals("NO")) {
                        logger.addRecordToLog(" ERROR : DELETE OPERATION Failed .... ");
                        return "NO";
                    } else {
                            //Update MAIN Channel with new details
                            // Read arraylist
                            String updateStatus = "NO";

                            ArrayList<String> allDEVREQ=getUserDevicesDetails(eachRequestDetails);

                            //Clear USER Channel
                            String responseStatus_c = clearUserChannel(eachRequestDetails);

                            if(allDEVREQ.isEmpty()){
                                logger.addRecordToLog("NO USER DEVICES");
                                updateStatus="YES";
                            } else {

                                Iterator eachDevice = allDEVREQ.iterator();
                                while (eachDevice.hasNext()) {

                                    Object element = eachDevice.next();
                                    String eachLine = element.toString();
                                    logger.addRecordToLog("> " + eachLine);

                                    /*
                                        Ignore DELETED DEVICE
                                     */
                                    logger.addRecordToLog("Compare : "+eachRequestDetails.split("%")[0]+" : "+eachLine.split("#")[0]);
                                    if(eachRequestDetails.split("%")[0].equals(eachLine.split("#")[0])){
                                        updateStatus="YES";
                                        logger.addRecordToLog("DEVICE DELETED.. INGNORE IT ");
                                    } else {

                                        // If ZERO return again we have to perfrom UPDATE
                                        do {
                                            updateStatus = updateMainUserChannel(eachLine, eachRequestDetails.split("%")[2]);
                                        } while (updateStatus.equals("ZERO"));

                                        if (updateStatus.equals("NO")) {
                                            logger.addRecordToLog("Updation FAILED!!");
                                            return "NO";
                                        } else {
                                            logger.addRecordToLog("Updation PASSED!!");
                                        }
                                    }

                                }//end of while
                            }

                            return updateStatus;

                    }

                }//end of while
            }

            return status;
        }

        /*
            Get  DELETE REQUEST Channel
          */
        protected ArrayList<String> getAllDELETERequests(){

            logger.addRecordToLog(" ******** READING ALL DELETE DEVICES REQUESTS **********************");

            ArrayList<String> allReq=new ArrayList<String>();

            JSONParser jParser = new JSONParser();
            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+getString(R.string.delete_req_channel_id)+"/feeds.json?api_key="+getString(R.string.delete_req_channel_read_api);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);



            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);

                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");
                    // Updating arrayList
                    allReq.add(field1+"%"+field2+"%"+field3+"%"+field4+"%"+field5+"%"+field6+"%"+field7+"%"+field8);
                }

                return allReq;

            } catch (JSONException e) {
                e.printStackTrace();
                logger.addRecordToLog(" DELETE REQUEST LIST FAILED : "+e.toString());

            }
            return allReq;
        }//end of fcn


        /*
         Delete channel and return json response.
          */
        protected String deleteDeviceChannel(String entry){
            String status = "NO";


            String mainKey=getString(R.string.my_api);


            String device_channel_id=entry.split("%")[0];

            logger.addRecordToLog(" ************* Deleting channel : "+device_channel_id+" ****************");
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+device_channel_id+"?api_key="+mainKey;
            logger.addRecordToLog("REST API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("httpdelete START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                //logger.addRecordToLog("DELETE Channel HTTP Response : "+jsonresponse);
                //Log.d("httpdelete Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("httpdelete END");

            return status;
        }

        /*
         CLEAR channel and return json response.
          */
        protected String clearUserChannel(String entry){
            String status = "NO";
            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING USER CHANNEL ***********");

            String main_ID=entry.split("%")[1];
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("USER CLEAR END");

            return status;
        }

        /*
         Update MAIN channel field
          */
        protected String updateMainUserChannel(String deviceLine,String user_write_key){

            String status="NO";
            logger.addRecordToLog(" >  START UPDATING USER CHANNEL : \n"+deviceLine+"\n"+"Write Key : "+user_write_key);

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);


            nameValuePair.add(new BasicNameValuePair("api_key", user_write_key));
            nameValuePair.add(new BasicNameValuePair("field1", deviceLine.split("#")[0]));
            nameValuePair.add(new BasicNameValuePair("field2", deviceLine.split("#")[1]));
            nameValuePair.add(new BasicNameValuePair("field3", deviceLine.split("#")[2]));
            nameValuePair.add(new BasicNameValuePair("field4", deviceLine.split("#")[3]));
            nameValuePair.add(new BasicNameValuePair("field5", deviceLine.split("#")[4]));
            nameValuePair.add(new BasicNameValuePair("field6", deviceLine.split("#")[5]));
            nameValuePair.add(new BasicNameValuePair("field7", deviceLine.split("#")[6]));
            nameValuePair.add(new BasicNameValuePair("field8", deviceLine.split("#")[7]));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                logger.addRecordToLog("Update user channel with new channel : " + jsonresponse);
                if(jsonresponse.equals("0")){
                    status = "ZERO";
                } else {
                    status = "YES";
                }
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("STATUS : "+status);
            return status;
        }


        /*
          Get user all devices details
         */
        protected ArrayList<String> getUserDevicesDetails(String entry){

            ArrayList<String> allUserDeives=new ArrayList<String>();

            JSONParser jParser = new JSONParser();

            String user_channelID=entry.split("%")[1];
            String USER_readkey=entry.split("%")[3];

            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+user_channelID+"/feeds.json?api_key="+USER_readkey;

            logger.addRecordToLog(" Read USER Channel Details : URL : "+url);

            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);
                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String id = c.getString("field1");
                    String name = c.getString("field2");
                    String tags = c.getString("field3");
                    String write_key = c.getString("field4");
                    String read_key = c.getString("field5");
                    String lat = c.getString("field6");
                    String lng = c.getString("field7");
                    String action = c.getString("field8");

                    // Updating arrayList
                    allUserDeives.add(id+"#"+name+"#"+tags+"#"+write_key+"#"+read_key+"#"+lat+"#"+lng+"#"+action);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allUserDeives;
        }

        /*
        Clear DELETE device request
        */
        protected String clearDELETERequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING DELETE REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.delete_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }


    }// end of class

    /*
       ***********************************************************************************
          UPDATE MAIN DEVICE CHANNEL
       -  GET ALL USERS DETAILS AND UPDATE INTO MAIN USER CHANNEL
       -  GET ALL DEVICES DETAILS AND UPDATE INTO MAIN USER DETAILS
       ***********************************************************************************
     */
    private class UpdateMainDeviceChannel extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;
        private ArrayList<String> alluserDevicesDetails=null;
        private ArrayList<String> deletedDevicesDetails=null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            alluserDevicesDetails=new ArrayList<String>();
            deletedDevicesDetails=new ArrayList<String>();

            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Updating MAIN Channel ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... data) {
            return processAll();
        }

        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();

            MAINUPDATED=true;

            // Check Result Status
            if(result.equals("NO")){
                Toast.makeText(getApplicationContext(), "MAIN CHANNEL UPDATION FAILED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("UPDATE MAIN CHANNEL FAILED");
            } else {
                Toast.makeText(getApplicationContext(), "MAIN CHANNEL UPDATION PASSED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("UPDATE MAIN CHANNEL PASSED");
                logger.addRecordToLog("**********************************************************");
            }

            logger.addRecordToLog(" *** PROCESS UPDATE MAIN CHANNEL END +++");
        }

        /*
           Process all ADD Devices requests
           - READ COUNTRY CHANNEL
           - READ EACH STATE CHANNEL
           - READ EACH DISTRICT CHANNEL

           - STORE ALL USERS DETAILS into LIST
           - STORE ALL DEVICES LIST into LIST

           - UPDATE MAIN USER AND DEVICE CHANNEL WITH ALL DETAILS
         */
        protected String processAll(){
            String status="YES";

            boolean delete_channel_check=false;

            logger.addRecordToLog(" *** UPDATE MAIN CHANNEL START +++");

            alluserDevicesDetails=getAllDeviceDetails(getString(R.string.main_channel_id),getString(R.string.main_channel_read_api));

            // GET ALL OLD DECVICE DETAILS
            if(alluserDevicesDetails.isEmpty()){
                logger.addRecordToLog("MAIN DEVICE CHANNEL LIST EMPTY!!");
            } else {
                delete_channel_check=true;
                logger.addRecordToLog("OLD DEVICE DETAILS FROM MAIN CHANNEL : \n");
                logger.addRecordToLog(alluserDevicesDetails.toString());
            }


            logger.addRecordToLog("CLEAR MAIN DEVICE CHANNEL!!");
            String clearDeviceChannel=clearMAINDeviceChannel();
            String clearUserChannel=clearMAINUserChannel();
            if((clearDeviceChannel.equals("YES"))&&(clearUserChannel.equals("YES"))) {

                // Get user details
                ArrayList<String> allSTATEUSER = getUserDetails(getString(R.string.INDIA_channel_id), getString(R.string.INDIA_channel_read_api));

                if (allSTATEUSER.isEmpty()) {
                    logger.addRecordToLog("NO STATE USER PRESENT !!");
                    status = "YES";
                } else {

                    Iterator alluserDetails = allSTATEUSER.iterator();
                    while (alluserDetails.hasNext()) {

                        String eachuserDetails = alluserDetails.next().toString();
                        allMAINUSERS.add(eachuserDetails);

                        // GET ALL DISTRICT USER DETAILS

                        ArrayList<String> allDISTUSERS = getUserDetails(eachuserDetails.split("#")[1], eachuserDetails.split("#")[6]);
                        if (allDISTUSERS.isEmpty()) {
                            logger.addRecordToLog("NO DISTRICT USER PRESENT !!");
                            status = "YES";
                        } else {

                            Iterator alldistuserDetails = allDISTUSERS.iterator();
                            while (alldistuserDetails.hasNext()) {

                                String eachdistuserDetails = alldistuserDetails.next().toString();
                                allMAINUSERS.add(eachdistuserDetails);

                                // GET DISTRICT DEVICES DETAILS
                                ArrayList<String> allDEVICES = getUserDevicesDetails(eachdistuserDetails.split("#")[1], eachdistuserDetails.split("#")[6]);

                                if (allDEVICES.isEmpty()) {
                                    logger.addRecordToLog("NO DISTRICT DEVICES PRESENT !!");
                                    status = "YES";
                                } else {
                                    allMAINDEVICES.addAll(allDEVICES);
                                }

                            }//end of while
                        }//else

                    }// end of while
                } //else
            } else {
                logger.addRecordToLog("BOTH CHANNEL NOT CLEARED : FAILED!!");
                return "NO";
            }
            /*
               READ BOTH LIST AND UPDATE BOTH CHANNEL
             */
            if(allMAINUSERS.isEmpty()){
                logger.addRecordToLog("NO USER DETAILS PRESENT IN MAIN LIST!!");
                status="YES";
            } else {

                logger.addRecordToLog("UPDATE MAIN USER CHANNEL!!");
                Iterator alluserDetails = allMAINUSERS.iterator();
                while (alluserDetails.hasNext()) {

                    String eachUserDetails = alluserDetails.next().toString();

                    // If ZERO return again we have to perfrom UPDATE
                    String updateStatus = "NO";
                    logger.addRecordToLog(" >> "+eachUserDetails);

                    do {
                        updateStatus = updateMainUserChannel(eachUserDetails);
                    } while (updateStatus.equals("ZERO"));

                    if (updateStatus.equals("NO")) {
                        logger.addRecordToLog("Updation FAILED!!");
                        return "NO";
                    } else {
                        logger.addRecordToLog("Updation PASSED!!");
                    }
                }//while
            }//else

            /*
               DEVICE MAIN CHANNEL
               - FILTER DELETED DEVICES
             */
            if(allMAINDEVICES.isEmpty()){
                logger.addRecordToLog("NO DEVICE DETAILS PRESENT IN MAIN LIST!!");
                status="YES";
            } else {

                logger.addRecordToLog("UPDATE MAIN DEVICE CHANNEL!!");
                Iterator alldeviceDetails = allMAINDEVICES.iterator();
                while (alldeviceDetails.hasNext()) {

                    String eachdeviceDetails = alldeviceDetails.next().toString();

                    // If ZERO return again we have to perfrom UPDATE
                    String updateStatus = "NO";
                    logger.addRecordToLog(" >> "+eachdeviceDetails);

                    // Check with OLD List
                    if(delete_channel_check) {
                        if (!alluserDevicesDetails.contains(eachdeviceDetails)) {
                            logger.addRecordToLog(" DELETED DEVICE : " + eachdeviceDetails);
                            deletedDevicesDetails.add(eachdeviceDetails);
                        }
                    }

                    do {
                        updateStatus = updateMainDeviceChannel(eachdeviceDetails);
                    } while (updateStatus.equals("ZERO"));

                    if (updateStatus.equals("NO")) {
                        logger.addRecordToLog("Updation FAILED!!");
                        return "NO";
                    } else {
                        logger.addRecordToLog("Updation PASSED!!");
                    }
                }//while
            }//else

            /*
              DELETE CHANNEL
             */
            if(deletedDevicesDetails.isEmpty()){
                logger.addRecordToLog("NO DELETED DEVICE PRESENT !!");
                status="YES";
            } else {
                logger.addRecordToLog("DELETING DEVICES DETAILS : \n"+deletedDevicesDetails.toString());

                Iterator deletedevicedetails = deletedDevicesDetails.iterator();
                while (deletedevicedetails.hasNext()) {
                    String eachdeviceDetails = deletedevicedetails.next().toString();
                    deleteDeviceChannel(eachdeviceDetails.split("#")[0]);
                }

            }



            return status;
        }

        /*
          Get user all devices details
         */
        protected ArrayList<String> getUserDevicesDetails(String channelID,String readKey){

            ArrayList<String> allUserDeives=new ArrayList<String>();

            JSONParser jParser = new JSONParser();

            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey;
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);
                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    String id = c.getString("field1");
                    String name = c.getString("field2");
                    String tags = c.getString("field3");
                    String write_key = c.getString("field4");
                    String read_key = c.getString("field5");
                    String lat = c.getString("field6");
                    String lng = c.getString("field7");
                    String action = c.getString("field8");

                    // Updating arrayList
                    allUserDeives.add(id+"#"+name+"#"+tags+"#"+write_key+"#"+read_key+"#"+lat+"#"+lng+"#"+action);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allUserDeives;
        }

        /*
          Get user all users details
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

                    // Updating arrayList
                    allUser.add(scope + "#" + user_channel_id + "#" + password + "#" + user_name + "#" + email + "#" + writekey + "#" + readkey + "#" + imei + "#" + mobile+"#"+createdby);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allUser;
        }

        /*
          Read MAIN DEVICE Channel
         */
        protected ArrayList<String> getAllDeviceDetails(String channelID,String readKey){

            ArrayList<String> allDevices=new ArrayList<String>();

            logger.addRecordToLog("-- Reading all device details from MAIN channel!!");

            JSONParser jParser = new JSONParser();

            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey;
            JSONObject json = jParser.getJSONFromUrl(url);

            try {
                // Getting JSON Array from URL
                feed = json.getJSONArray(TAG_feed);
                for(int i = 0; i < feed.length(); i++){
                    JSONObject c = feed.getJSONObject(i);

                    String id = c.getString("field1");
                    String name = c.getString("field2");
                    String tags = c.getString("field3");
                    String write_key = c.getString("field4");
                    String read_key = c.getString("field5");
                    String lat = c.getString("field6");
                    String lng = c.getString("field7");
                    String action = c.getString("field8");

                    // Updating arrayList
                    allDevices.add(id+"#"+name+"#"+tags+"#"+write_key+"#"+read_key+"#"+lat+"#"+lng+"#"+action);

                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allDevices;
        }

        /*
        Update MAIN DEVICE channel field
         */
        protected String updateMainDeviceChannel(String deviceLine){

            String status="NO";
            logger.addRecordToLog("**** START UPDATING MAIN CHANNEL ***********");

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channel_write_key=getString(R.string.main_channel_write_api);

            nameValuePair.add(new BasicNameValuePair("api_key",channel_write_key ));
            nameValuePair.add(new BasicNameValuePair("field1", deviceLine.split("#")[0]));
            nameValuePair.add(new BasicNameValuePair("field2", deviceLine.split("#")[1]));
            nameValuePair.add(new BasicNameValuePair("field3", deviceLine.split("#")[2]));
            nameValuePair.add(new BasicNameValuePair("field4", deviceLine.split("#")[3]));
            nameValuePair.add(new BasicNameValuePair("field5", deviceLine.split("#")[4]));
            nameValuePair.add(new BasicNameValuePair("field6", deviceLine.split("#")[5]));
            nameValuePair.add(new BasicNameValuePair("field7", deviceLine.split("#")[6]));
            nameValuePair.add(new BasicNameValuePair("field8", deviceLine.split("#")[7]));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                logger.addRecordToLog("Update main channel with new channel : " + jsonresponse);
                if(jsonresponse.equals("0")){
                    status = "ZERO";
                } else {
                    status = "YES";
                }
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("STATUS : "+status);
            return status;
        }

        /*
      Update MAIN USER channel field
       */
        protected String updateMainUserChannel(String deviceLine){

            String status="NO";
            logger.addRecordToLog("**** START UPDATING MAIN CHANNEL ***********");

            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channel_write_key=getString(R.string.user_channel_write_api);

            nameValuePair.add(new BasicNameValuePair("api_key",channel_write_key ));
            nameValuePair.add(new BasicNameValuePair("field1", deviceLine.split("#")[0]));
            nameValuePair.add(new BasicNameValuePair("field2", deviceLine.split("#")[3]));
            nameValuePair.add(new BasicNameValuePair("field3", deviceLine.split("#")[1]));

            nameValuePair.add(new BasicNameValuePair("field4", deviceLine.split("#")[2]+"@"+deviceLine.split("#")[7]));
            nameValuePair.add(new BasicNameValuePair("field5", deviceLine.split("#")[5]));
            nameValuePair.add(new BasicNameValuePair("field6", deviceLine.split("#")[6]));
            nameValuePair.add(new BasicNameValuePair("field7", deviceLine.split("#")[4]+"%"+deviceLine.split("#")[8]));
            nameValuePair.add(new BasicNameValuePair("field8", deviceLine.split("#")[9]));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                logger.addRecordToLog("Update main channel with new channel : " + jsonresponse);
                if(jsonresponse.equals("0")){
                    status = "ZERO";
                } else {
                    status = "YES";
                }
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : " + e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("STATUS : "+status);
            return status;
        }

        /*
        Clear MAIN device request
        */
        protected String clearMAINDeviceChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING MAIN CHANNEL ***********");

            String main_ID=getString(R.string.main_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
       Clear MAIN device request
       */
        protected String clearMAINUserChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING MAIN USER CHANNEL ***********");

            String main_ID=getString(R.string.user_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
        Delete channel and return json response.
         */
        protected String deleteDeviceChannel(String channelID){
            String status = "YES";


            String mainKey=getString(R.string.my_api);


            String device_channel_id=channelID;

            logger.addRecordToLog(" ************* Deleting channel : "+device_channel_id+" ****************");
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+device_channel_id+"?api_key="+mainKey;
            logger.addRecordToLog("REST API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("httpdelete START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                //logger.addRecordToLog("DELETE Channel HTTP Response : "+jsonresponse);
                //Log.d("httpdelete Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("httpdelete END");

            return status;
        }

    } // end of class

    /*
       Clear all ADD requests
      */
    private class ClearAllADDRequest extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Clear All ADD Requests ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            return clearADDRequestChannel();
        }


        @Override
        protected void onPostExecute(String status) {
            pDialog.dismiss();

            if(status.equals("YES")){
                Toast.makeText(getApplicationContext(), "All Requests are cleared !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Request Clear Operation FAILED!!", Toast.LENGTH_LONG).show();
            }

        }

        /*
        clear add device request
        */
        protected String clearADDRequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING ADD REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.add_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }




    }// end of class

    /*
       Clear all DELETE requests
      */
    private class ClearAllDELETERequest extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Clear All DELETE Requests ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            return clearDELETERequestChannel();
        }


        @Override
        protected void onPostExecute(String status) {
            pDialog.dismiss();

            if(status.equals("YES")){
                Toast.makeText(getApplicationContext(), "All Requests are cleared !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Request Clear Operation FAILED!!", Toast.LENGTH_LONG).show();
            }

        }

        /*
        Clear DELETE device request
        */
        protected String clearDELETERequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING DELETE REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.delete_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }






    }// end of class

    /*
        ----------------------------------------------------------------------
         ------------------- RESET Complete system -------------------------
        ----------------------------------------------------------------------
      */
    private class ResetCompleteSystem extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("RESET Complete System ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            logger.addRecordToLog("******* RESET START ************");

            if(processAllRequests().equals("YES")){
                clearDELETERequestChannel();
                clearADDRequestChannel();
                clearMAINChannel();
                clearUSERChannel();
                clearCOUNTRYChannel();

                return "YES";

            } else {
                return "NO";
            }

        }


        @Override
        protected void onPostExecute(String status) {
            pDialog.dismiss();

            if(status.equals("YES")){
                logger.addRecordToLog("******* RESET PASSED ************");
                Toast.makeText(getApplicationContext(), "RESET DONE !!!", Toast.LENGTH_LONG).show();
            } else {
                logger.addRecordToLog("******* RESET FAILED ************");
                Toast.makeText(getApplicationContext(), "RESET FAILED !!!", Toast.LENGTH_LONG).show();
            }

            logger.addRecordToLog(" --------------- RESET PROCESS END ---------------------------------------");

        }

        /*
        Clear DELETE device request
        */
        protected String clearDELETERequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING DELETE REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.delete_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
        clear add device request
        */
        protected String clearADDRequestChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING ADD REQUEST CHANNEL ***********");

            String main_ID=getString(R.string.add_req_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
       Clear MAIN device channel
       */
        protected String clearMAINChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING MAIN CHANNEL ***********");

            String main_ID=getString(R.string.main_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
        Clear USER Channel
       */
        protected String clearUSERChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING MAIN CHANNEL ***********");

            String main_ID=getString(R.string.user_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
       Clear COUNTRY Channel
      */
        protected String clearCOUNTRYChannel(){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING MAIN CHANNEL ***********");

            String main_ID=getString(R.string.INDIA_channel_id);
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

        /*
         Delete channel and return json response.
          */
        protected String deleteDeviceChannel(String device_channel_id){
            String status = "NO";


            String mainKey=getString(R.string.my_api);


            logger.addRecordToLog(" ************* Deleting channel : "+device_channel_id+" ****************");
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+device_channel_id+"?api_key="+mainKey;
            logger.addRecordToLog("REST API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("httpdelete START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                //logger.addRecordToLog("DELETE Channel HTTP Response : "+jsonresponse);
                //Log.d("httpdelete Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : httpdelete IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("httpdelete END");

            return status;
        }

        /*
           Process all DELETE Devices requests
         */
        protected String processAllRequests(){
            String status="YES";

            logger.addRecordToLog("\n\n*************************************************************************");
            logger.addRecordToLog(" --------------- RESET PROCESS START ---------------------------------------");
            logger.addRecordToLog("*************************************************************************\n");

            String deleteStatus="";
            /*
                ALL DEVICES CHANNEL
             */
            if(allMAINDEVICES.isEmpty()){
                logger.addRecordToLog("NO DEVICE CHANNEL DETAILS PRESENT !!");
                status="YES";
            } else {

                Iterator allRequestDetails =allMAINDEVICES.iterator();
                while (allRequestDetails.hasNext()) {

                    String eachRequestDetails = allRequestDetails.next().toString();
                    logger.addRecordToLog(" >> - "+eachRequestDetails);
                    // DELETE CHANNEL
                    String responseStatus_d = deleteDeviceChannel(eachRequestDetails.split("#")[0]);

                    if(responseStatus_d.equals("NO")){
                        deleteStatus=deleteStatus+"\n"+" NOT DELETED : "+eachRequestDetails;
                    } else {
                        logger.addRecordToLog(" DEVICE DELETED : "+eachRequestDetails);
                    }

                }//end of while
            }

            /*
               ALL USER CHANNELS
             */
            if(allMAINUSERS.isEmpty()){
                logger.addRecordToLog("NO USERS CHANNEL DETAILS PRESENT !!");
                status="YES";
            } else {

                Iterator allRequestDetails =allMAINUSERS.iterator();
                while (allRequestDetails.hasNext()) {

                    String eachRequestDetails = allRequestDetails.next().toString();
                    logger.addRecordToLog(" >> - "+eachRequestDetails);
                    // DELETE CHANNEL
                    String responseStatus_d = deleteDeviceChannel(eachRequestDetails.split("#")[1]);

                    if(responseStatus_d.equals("NO")){
                        deleteStatus=deleteStatus+"\n"+" NOT DELETED : "+eachRequestDetails;
                    }

                }//end of while
            }


            logger.addRecordToLog(deleteStatus);
            return status;
        }


        /*
           Create first user

        protected String addFirstUser(){

            logger.addRecordToLog(" *** ******* ADDING FIRST USER ***** ****");
            String responseStatus=createNewUserChannel();
            if(responseStatus.equals("NO")){
                return "NO";
            } else {
                // Update information into MAIN Channel
                return parseJSONandUpdate(responseStatus);
            }
        }

        // Create new channel and return json response.
        protected String createNewUserChannel(){
            String status = "NO";
            logger.addRecordToLog("Creating new user channel..");
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(create_channel_api);


            // Create complete description
            String desc="USER_ID : "+"ADMIN787"+"\nUSER_TYPE : "+"ADMIN"+"\nNAME : "+"ADMIN"+"_"+"V"+"\n EMAIL : "+"thakur.projects787@gmail.com"+"\n PIN_CODE : "+"176045"+"\n MOBILE : "+"8553372177"+"\n IMEI : "+"869589024506463";
            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

            String channelKey=getString(R.string.my_api);

            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("name", "ADMIN787"));
            nameValuePair.add(new BasicNameValuePair("description", desc));
            nameValuePair.add(new BasicNameValuePair("tags", "ADMIN787"+","+"176045"+",USER"));


            //nameValuePair.add(new BasicNameValuePair("public_flag", "true"));
            nameValuePair.add(new BasicNameValuePair("field1", "ID"));
            nameValuePair.add(new BasicNameValuePair("field2", "Name"));
            nameValuePair.add(new BasicNameValuePair("field3", "Tags"));
            nameValuePair.add(new BasicNameValuePair("field4", "Write_Key"));
            nameValuePair.add(new BasicNameValuePair("field5", "Read_Key"));
            nameValuePair.add(new BasicNameValuePair("field6", "Latitude"));
            nameValuePair.add(new BasicNameValuePair("field7", "Longitude"));
            nameValuePair.add(new BasicNameValuePair("field8", "Action"));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : httpPost.setEntity\n"+e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("ADD Channel HTTP Response : "+jsonresponse);
                Log.d("Http Post Response:", jsonresponse);
                status=jsonresponse;
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : Http Post Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog("Creating new user channel END");
            return status;
        }

        //Parse json response format
        protected String parseJSONandUpdate(String response){
            //response="{\"id\":167336,\"name\":\"My New Channel\",\"description\":\"water tank descriptio\",\"latitude\":\"12.454556\",\"longitude\":\"72.46546546\",\"created_at\":\"2016-10-05T05:20:39Z\",\"elevation\":null,\"last_entry_id\":null,\"ranking\":70,\"metadata\":null,\"tags\":[{\"id\":13567,\"name\":\"karnataka\"},{\"id\":13568,\"name\":\"maysore\"}],\"api_keys\":[{\"api_key\":\"HSVHVMK1M4MTYXZB\",\"write_flag\":true},{\"api_key\":\"EUZ27A4B29QVRJ4C\",\"write_flag\":false}]}";

            String data = "NO";

            try {
                JSONObject jsonRootObject = new JSONObject(response);

                // Read all root objects details
                String channelID=jsonRootObject.getString("id");
                String userID=jsonRootObject.getString("name");
                String writeKey="";
                String readKey="";

                String name="ADMIN"+"_"+"V";
                String email="thakur.projects787@gmail.com";
                String type="ADMIN";
                String password="admin787";
                String imei="869589024506463";


                // Read Write key
                JSONArray jsonArray2 = jsonRootObject.optJSONArray("api_keys");
                for(int i=0; i < jsonArray2.length(); i++){
                    JSONObject jsonObject = jsonArray2.getJSONObject(i);

                    // Check for write key
                    if(jsonObject.getBoolean("write_flag")){
                        writeKey=jsonObject.getString("api_key");
                    } else {
                        readKey=jsonObject.getString("api_key");
                    }
                }

                // Combine all Data and update MAIN Channel
                logger.addRecordToLog("Channel Filter Details : \n"+channelID+","+userID+","+password+"@"+imei+","+name+","+email+","+type+","+readKey+","+writeKey);
                data=updateMainChannel(channelID,userID,password+"@"+imei,name,email,type,writeKey,readKey);
                //data=channelID+"\n"+channelname+"\n"+tags+"\n"+writeKey;

                return data;
                //output.setText(data);
            } catch (JSONException e) {e.printStackTrace();}

            return data;

        }

        // Update MAIN channel field
        protected String updateMainChannel(String id,String userid,String password,String name,String email,String type,String write_key,String read_key){

            String status="NO";
            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channelKey=getString(R.string.user_channel_write_api);
            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("field1", id));
            nameValuePair.add(new BasicNameValuePair("field2", userid));
            nameValuePair.add(new BasicNameValuePair("field3", password));
            nameValuePair.add(new BasicNameValuePair("field4", name));
            nameValuePair.add(new BasicNameValuePair("field5", email));
            nameValuePair.add(new BasicNameValuePair("field6", type));
            nameValuePair.add(new BasicNameValuePair("field7", write_key));
            nameValuePair.add(new BasicNameValuePair("field8", read_key));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            //making POST request.
            try {
                HttpResponse response = httpClient.execute(httpPost);
                logger.addRecordToLog("Update main user channel with new channel : "+response);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }

            return status;
        }

        */





    }// end of class

    /*
       Clear all DEVICE Channels
      */
    private class ClearAllDEVICEChannels extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainServerActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Clear All DEVICE Channels ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String status="NO";
            String deviceStatus="";
            /*
                ALL DEVICES CHANNEL
             */
            if(allMAINDEVICES.isEmpty()){
                logger.addRecordToLog("NO DEVICE CHANNEL DETAILS PRESENT !!");
                status="YES";
            } else {

                Iterator allRequestDetails =allMAINDEVICES.iterator();
                while (allRequestDetails.hasNext()) {

                    String eachRequestDetails = allRequestDetails.next().toString();
                    logger.addRecordToLog(" >> - "+eachRequestDetails);
                    // DELETE CHANNEL
                    String responseStatus_d = clearDEVICEChannel(eachRequestDetails.split("#")[0]);

                    if(responseStatus_d.equals("NO")){
                        deviceStatus=deviceStatus+"\n"+" NOT DELETED : "+eachRequestDetails;
                    } else {
                        logger.addRecordToLog(" DEVICE CLEARED : "+eachRequestDetails);
                    }

                }//end of while
            }

            logger.addRecordToLog(" DEVICE NOT CLEARED : "+deviceStatus);

            return status;
        }


        @Override
        protected void onPostExecute(String status) {
            pDialog.dismiss();

            if(status.equals("YES")){
                Toast.makeText(getApplicationContext(), "Device Cleared !!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "FAILED !!", Toast.LENGTH_LONG).show();
            }

        }

        /*
        Clear device data
        */
        protected String clearDEVICEChannel(String ChannelID){

            String status = "NO";

            // Read main channel ID
            logger.addRecordToLog("**** START CLEARING DEVICE CHANNEL ***********");

            String main_ID=ChannelID;
            String mainKey=getString(R.string.my_api);

            logger.addRecordToLog("Clear channel : "+main_ID);
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+main_ID+"/feeds.json?api_key="+mainKey;

            logger.addRecordToLog("Clear API : "+create_channel_api);

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpDelete httpdelete = new HttpDelete(create_channel_api);

            //making DELETE request.
            try {
                logger.addRecordToLog("CLEAR START Response");
                HttpResponse response = httpClient.execute(httpdelete);

                String jsonresponse = EntityUtils.toString(response.getEntity());
                // write response to log
                logger.addRecordToLog("CLEAR Channel HTTP Response : "+jsonresponse);
                Log.d("CLEAR Response:", jsonresponse);
                status="YES";
            } catch (ClientProtocolException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR Responce FAILED!!!\n"+e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                logger.addRecordToLog("ERROR : CLEAR IO exception\n"+e.toString());
                e.printStackTrace();
            }

            logger.addRecordToLog(" ********* CLEAR END ********* ");

            return status;
        }

    }// end of class

}
