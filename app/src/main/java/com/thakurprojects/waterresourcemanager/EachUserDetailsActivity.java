package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EachUserDetailsActivity extends AppCompatActivity {

    private String baseUrl="https://api.thingspeak.com/channels/";

    private String mainURL="";


    private String currentDeviceUserName="";
    private String currentDeviceUserEmail="";

    // Text views
    TextView _userHeadingNameView;
    TextView _usercloudIDView;
    TextView _userNameView;
    TextView _userScopeView;
    TextView _userEmailView;
    TextView _userCreatedBy;
    TextView _userMobileNumber;
    TextView _userIMEI_number;
    TextView _userDetails;

    TextView _userDetailsHeading;

    // Channel Details
    private String channelID="";
    private String channelKey="";

    // List for all channels details
    ArrayList<String> allDeivesF=new ArrayList<String>();



    //JSON Node Names
    private static final String TAG_channel = "channel";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_Desc = "description";

    private static final String TAG_feed = "feeds";
    private static final String TAG_TC = "field1";
    private static final String TAG_CL = "field2";
    private static final String TAG_IN = "field3";
    private static final String TAG_OUT = "field4";
    private static final String TAG_OUT_T = "field5";
    private static final String TAG_OUT_S = "field6";
    private static final String TAG_WQ = "field7";
    private static final String TAG_ACT = "field8";

    // JSON Pbj
    JSONArray feed = null;
    JSONArray feed2 = null;

    // Loger
    Logger logger;


    /*
       ****************************************************************
       * ---- FUNCTION START --------------------
       * **************************************************************
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_user_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        _userHeadingNameView=(TextView) findViewById(R.id.eachuserNameHeadingTV);
        _usercloudIDView=(TextView) findViewById(R.id.eachUserCloudIDTV);
        _userNameView=(TextView) findViewById(R.id.eachUserNameTV);
        _userScopeView=(TextView) findViewById(R.id.eachSCOPETV);
        _userEmailView=(TextView) findViewById(R.id.eachUserEmailTV);
        _userCreatedBy=(TextView) findViewById(R.id.eachUserCreatedbyTV);
        _userMobileNumber=(TextView) findViewById(R.id.eachUserMobileTV);
        _userIMEI_number=(TextView) findViewById(R.id.eachUserIMEITV);
        _userDetails=(TextView) findViewById(R.id.eachUserDevicesTV);
        _userDetailsHeading=(TextView) findViewById(R.id.fieldsDetailsTV);





        // Get USER_ID intent details
        Intent intent = getIntent();
        String intentValue = intent.getStringExtra(MainActivity.USER_ID);
        channelID=intentValue.split("#")[0];
        channelKey=intentValue.split("#")[1];


        mainURL=baseUrl+channelID+"/feeds.json?api_key="+channelKey;

        logger.addRecordToLog("********************************************************");
        logger.addRecordToLog("User Details : "+intentValue);

        // Get each devices details
        new EachUserDetailsActivity.UsersDetails().execute();

    }

    @Override
    public void onBackPressed() {
        // Check if backgound task is running
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_each_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*
          SEND email to DEVICE USER
         */
        if (id == R.id.each_user_contact) {
            Toast.makeText(getApplicationContext(), "Mail Send To : "+currentDeviceUserEmail, Toast.LENGTH_LONG).show();
            return true;
        }

        /*
           Open User All Devices
         */
        if (id == R.id.each_user_Devices) {

            if(MainActivity.USER_scope.equals("DISTRICT")){
                // Start show all device activity
                Intent addIntent = new Intent(this, ShowDevicesActivity.class);
                addIntent.putExtra(MainActivity.DEVICE_DETAILS,channelID+"#"+channelKey);
                startActivity(addIntent);

                return true;

            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
                return true;

            }


        }


        /*
           DELETE OPERATION
         */
        if (id == R.id.each_user_delete) {
            //Toast.makeText(getApplicationContext(), "Delete Device", Toast.LENGTH_LONG).show();

            // Check for ADMIN USER type
            if (MainActivity.USER_name.equals(currentDeviceUserName)) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("USER DELETE");
                alertDialogBuilder.setMessage("Are you sure,You want to DELETE User?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Delete Device Operation
                        new EachUserDetailsActivity.DeleteUpdateUserChannel().execute();
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
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // Read Specific UserDetails details
    private class UsersDetails extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EachUserDetailsActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Getting Users Details ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            JSONParser jParser = new JSONParser();

            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(mainURL);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            try {
                // Getting JSON Array from URL
                Log.i("MSG  : ",json.toString());
                //channel = json.getJSONArray(TAG_channel);
                //for(int i = 0; i < channel.length(); i++) {

                JSONObject c1 = json.optJSONObject(TAG_channel);
                //JSONObject c = json.get(TAG_channel);

                // Storing  JSON item in a Variable
                String cloudid = c1.getString(TAG_ID);

                String userName = c1.getString(TAG_NAME);
                String desc = c1.getString(TAG_Desc).replaceAll("\n","#");

                String combined1=cloudid+"\n"+userName+"\n"+desc;

                //Log.i("MSG : ","------>"+combined1);

                // Update text views
                _userHeadingNameView.setText(userName);
                _userNameView.setText(userName);
                _usercloudIDView.setText(cloudid);

                // Parse description
                _userScopeView.setText(desc.split("#")[0].split(":")[1].trim());
                _userNameView.setText(desc.split("#")[1].split(":")[1].trim());
                _userEmailView.setText(desc.split("#")[2].split(":")[1].trim());
                _userMobileNumber.setText(desc.split("#")[3].split(":")[1].trim());
                _userIMEI_number.setText(desc.split("#")[4].split(":")[1].trim());
                _userCreatedBy.setText(desc.split("#")[5].split(":")[1].trim());

               if(desc.split("#")[0].split(":")[1].trim().equals("COUNTRY")){
                   _userDetailsHeading.setText("STATE USERS Details");
               } else if(desc.split("#")[0].split(":")[1].trim().equals("STATE")){
                   _userDetailsHeading.setText("DISTRICT USERS Details");
               } else {
                   _userDetailsHeading.setText("DEVICE Details");
               }

                currentDeviceUserName=desc.split("#")[5].split(":")[1].trim();
                currentDeviceUserEmail=desc.split("#")[2].split(":")[1].trim();


                logger.addRecordToLog(">>>>>>>>"+combined1);


                String devicesDetails="";

                // Get all fields details
                feed = json.getJSONArray(TAG_feed);
                if(feed.length()==0){
                    devicesDetails="Nothing!!";
                } else {
                    for (int i = 0; i < feed.length(); i++) {
                        JSONObject c = feed.getJSONObject(i);

                        // Storing  JSON item in a Variable
                        String field1 = c.getString(TAG_TC);
                        String field2 = c.getString(TAG_CL);
                        String field3 = c.getString(TAG_IN);
                        String field4 = c.getString(TAG_OUT);
                        String field5 = c.getString(TAG_OUT_T);
                        String field6 = c.getString(TAG_OUT_S);
                        String field7 = c.getString(TAG_WQ);
                        String field8 = c.getString(TAG_ACT);

                        devicesDetails=devicesDetails+Integer.toString(i+1)+" : "+field2+" - "+field1+"\n";
                    }
                }

                _userDetails.setText(devicesDetails);

            } catch (JSONException e) {
                logger.addRecordToLog("ERROR : "+e.toString());
                e.printStackTrace();
            }


        }
    }


    //Delete channel and Update MAIN channel with new details
    private class DeleteUpdateUserChannel extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EachUserDetailsActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Deleting User..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... data) {
            /*
               DELETE CHANNEL
             */
            String responseStatus_d=deleteChannel();
            if(responseStatus_d.equals("NO")){
                return "NO";
            } else {
                return "YES";
            }

        }


        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();

            logger.addRecordToLog("Channel DELETE Status : "+result);

            // Check Result Status
            if(result.equals("NO")){
                Toast.makeText(getApplicationContext(), "FAILED!\n Device Not Deleted..", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("DELETE Channel FAILED");
            } else {
                Toast.makeText(getApplicationContext(), "DEVICE Deleted!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("DELETE Channel PASSED");
                logger.addRecordToLog("**********************************************************");
                finish();
            }
        }


        // Delete channel and return json response.
        protected String deleteChannel(){
            String status = "NO";
            String mainKey=getString(R.string.my_api);
            logger.addRecordToLog(" ************* Deleting user channel : "+channelID+" ****************");
            // Create new channel
            String create_channel_api = "https://api.thingspeak.com/channels/"+channelID+"?api_key="+mainKey;
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
                logger.addRecordToLog("DELETE Channel HTTP Response : "+jsonresponse);
                Log.d("httpdelete Response:", jsonresponse);
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



    }


}
