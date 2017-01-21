package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EachDeviceActivity extends AppCompatActivity {

    private String baseUrl="https://api.thingspeak.com/channels/";

    private String mainURL="";
    private String alldevicesURL="";

    private String lat="";
    private String lng="";
    private String currentdesc="";

    private String currentDeviceUserid="";
    private String currentDeviceUserEmail="";

    // Text views
    TextView nameView;
    TextView idView;
    TextView useridView;
    TextView typeView;
    TextView addView;
    TextView distView;
    TextView pincodeView;
    TextView stateView;
    TextView countryView;
    TextView contactView;
    TextView areaView;
    TextView timingView;



    //Field Values
    TextView field1V;
    TextView field2V;
    TextView field3V;
    TextView field4V;
    TextView field5V;
    TextView field6V;
    TextView field7V;
    TextView field8V;

    //Field Names
    TextView field1N;
    TextView field2N;
    TextView field3N;
    TextView field4N;
    TextView field5N;
    TextView field6N;
    TextView field7N;
    TextView field8N;

    // Channel Details
    private String channelID="";
    private String channelKey="";
    private String channelName="";
    private String channelDetails="";


    // List for all channels details
    ArrayList<String> allDeivesF=new ArrayList<String>();



    //JSON Node Names
    private static final String TAG_channel = "channel";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_Desc = "description";
    private static final String TAG_Lat = "latitude";
    private static final String TAG_Log = "longitude";

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
        setContentView(R.layout.activity_each_device);
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

        idView=(TextView) findViewById(R.id.channelIDTV);
        nameView=(TextView) findViewById(R.id.nameTV);


        typeView=(TextView) findViewById(R.id.resourceTV);
        addView=(TextView) findViewById(R.id.addressTV);
        distView=(TextView) findViewById(R.id.distTV);
        pincodeView=(TextView) findViewById(R.id.pincodeTV);
        stateView=(TextView) findViewById(R.id.stateTV);
        countryView=(TextView) findViewById(R.id.countryTV);
        contactView=(TextView) findViewById(R.id.contactTV);
        areaView=(TextView) findViewById(R.id.areacoverTV);
        timingView=(TextView) findViewById(R.id.timmingTV);
        useridView=(TextView) findViewById(R.id.userIDTV);


        field1V=(TextView) findViewById(R.id.field1TV);
        field2V=(TextView) findViewById(R.id.field2TV);
        field3V=(TextView) findViewById(R.id.field3TV);
        field4V=(TextView) findViewById(R.id.field4TV);
        field5V=(TextView) findViewById(R.id.field5TV);
        field6V=(TextView) findViewById(R.id.field6TV);
        field7V=(TextView) findViewById(R.id.field7TV);
        field8V=(TextView) findViewById(R.id.field8TV);

        field1N=(TextView) findViewById(R.id.field1N);
        field2N=(TextView) findViewById(R.id.field2N);
        field3N=(TextView) findViewById(R.id.field3N);
        field4N=(TextView) findViewById(R.id.field4N);
        field5N=(TextView) findViewById(R.id.field5N);
        field6N=(TextView) findViewById(R.id.field6N);
        field7N=(TextView) findViewById(R.id.field7N);
        field8N=(TextView) findViewById(R.id.field8N);

        // Get DEVICE_ID intent details
        logger.addRecordToLog("********************* SHOW EACH DEVICE **********************************");
        Intent intent = getIntent();
        String intentValue = intent.getStringExtra(ShowDevicesActivity.DEVICE_ID);
        channelID=intentValue.split("#")[0];
        channelKey=intentValue.split("#")[1];

        mainURL=baseUrl+channelID+"/feeds.json?api_key="+channelKey+"&results=1";


        logger.addRecordToLog("Device Details : "+intentValue);

        // Get each devices details
        new EachDeviceActivity.DeviceDetails().execute();

    }

    @Override
    public void onBackPressed() {
        // Check if backgound task is running
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_each_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.each_device_refresh) {
            //Toast.makeText(getApplicationContext(), "Refresh View", Toast.LENGTH_LONG).show();
            // Get each devices details
            new EachDeviceActivity.DeviceDetails().execute();
            return true;
        }

        if (id == R.id.each_device_map_view) {
            //Toast.makeText(getApplicationContext(), "Display devices on Map", Toast.LENGTH_LONG).show();

            // Creates an Intent that will load a map of San Francisco
            // Start Map Activity
            Intent addIntent = new Intent(this, DisplayDevicesOnMapActivity.class);
            addIntent.putExtra(MainActivity.DEVICE_MAP_ID, lat+"#"+lng+"#"+currentDeviceUserid+"#"+currentdesc+"%");
            startActivity(addIntent);

            return true;
        }

        /*
          SEND email to DEVICE USER
         */
        if (id == R.id.each_device_contact) {
            Toast.makeText(getApplicationContext(), "Mail Send To : "+currentDeviceUserEmail, Toast.LENGTH_LONG).show();
            return true;
        }

        /*
          DEVICE Dashboard
         */
        if (id == R.id.each_device_dashboard) {





               String intentDetails=channelID+"%"+channelKey+"%"+channelName+"%"+channelDetails;
               Intent devSatIntent = new Intent(this, DeviceStatisDashboardActivity.class);
               devSatIntent.putExtra(MainActivity.DEVICE_STATISTICS_ID, intentDetails);
               startActivity(devSatIntent);


            return true;
        }

        /*
           UPDATE DEVICE DETAILS
         */
        if (id == R.id.each_device_update) {
            if(MainActivity.USER_name.equals(currentDeviceUserid)) {
                Toast.makeText(getApplicationContext(), "Update Device", Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        /*
           DELETE OPERATION
         */
        if (id == R.id.each_device_delete) {
            //Toast.makeText(getApplicationContext(), "Delete Device", Toast.LENGTH_LONG).show();

            // Check for ADMIN USER type
            if(MainActivity.USER_name.equals(currentDeviceUserid)) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("DELETE DEVICE");
                alertDialogBuilder.setMessage("Send DELETE DEVICE Request to ADMIN.\n Are you sure,You want to DELETE Device?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Delete Device Operation
                        new EachDeviceActivity.DeleteUpdateChannel().execute();
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
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied !!", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Read Specific device details
    private class DeviceDetails extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EachDeviceActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Getting Devices Details ...");
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

            /*
            try {
                String testString="{\"channel\":{\"id\":170016,\"name\":\"AD1233456\",\"description\":\"TYPE : Reservoir\\nADDRESS : vill-andhri,P.O-jisddh\\n DIST : Hamirpur\\n PIN CODE : 576045\\n STATE : Himachal Pradesh\\n COUNTRY : India\\n CONTACT NO : 866552348\\n AREA COVER : ...\\n TIMMING : \",\"latitude\":\"12.3562235704\",\"longitude\":\"76.5991972759\",\"field1\":\"Total_Capacity\",\"field2\":\"Current_level\",\"field3\":\"Inlet\",\"field4\":\"Outlet\",\"field5\":\"Outlet_Timming\",\"field6\":\"Outlet_Supply_status\",\"field7\":\"Water_quality\",\"field8\":\"Action\",\"created_at\":\"2016-10-12T10:47:08Z\",\"updated_at\":\"2016-10-12T10:47:11Z\",\"last_entry_id\":1},\"feeds\":[{\"created_at\":\"2016-10-12T10:47:11Z\",\"entry_id\":1,\"field1\":\"0000\",\"field2\":\"0000\",\"field3\":\"0000\",\"field4\":\"0000\",\"field5\":\"\",\"field6\":\"0000\",\"field7\":\"0000\",\"field8\":\"0000\"}]}";
                JSONObject json=new JSONObject(testString);
                return json;
            } catch (JSONException e) {e.printStackTrace();}

            return null;
            */


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
                    String id = c1.getString(TAG_ID);
                    String name = c1.getString(TAG_NAME);
                    String desc = c1.getString(TAG_Desc).replaceAll("\n","#");
                    lat = c1.getString(TAG_Lat);
                    lng = c1.getString(TAG_Log);

                    //Get field names
                    field1N.setText(c1.getString("field1")+" : ");
                    field2N.setText(c1.getString("field2")+" : ");
                    field3N.setText(c1.getString("field3")+" : ");
                    field4N.setText(c1.getString("field4")+" : ");
                    field5N.setText(c1.getString("field5")+" : ");
                    field6N.setText(c1.getString("field6")+" : ");
                    field7N.setText(c1.getString("field7")+" : ");
                    field8N.setText(c1.getString("field8")+" : ");


                    String combined1=id+"\n"+name+"\n"+desc+"\n"+lat+"\n"+lng;

                    // Update text views
                    idView.setText(id);
                    nameView.setText(name);

                    channelName=name;
                    channelDetails=desc.split("#")[0].split(":")[1]+"-"+desc.split("#")[2].split(":")[1]+","+desc.split("#")[4].split(":")[1];

                    // Parse description
                    typeView.setText(desc.split("#")[0].split(":")[1]);
                    addView.setText(desc.split("#")[1].split(":")[1]);
                    distView.setText(desc.split("#")[2].split(":")[1]);
                    pincodeView.setText(desc.split("#")[3].split(":")[1]);
                    stateView.setText(desc.split("#")[4].split(":")[1]);
                    countryView.setText(desc.split("#")[5].split(":")[1]);
                    contactView.setText(desc.split("#")[6].split(":")[1]);


                    if(desc.split("#")[0].split(":")[1].equals("Reservoir")){
                        areaView.setText("NA");
                        timingView.setText("NA");
                        currentdesc=desc.split("#")[0].split(":")[1]+"-"+desc.split("#")[1].split(":")[1];
                    } else {
                        areaView.setText(desc.split("#")[7].split(":")[1]);
                        timingView.setText(desc.split("#")[8].split(":")[1]);
                        currentdesc=desc.split("#")[0].split(":")[1]+"-"+desc.split("#")[7].split(":")[1];
                    }

                    useridView.setText(desc.split("#")[9].split(":")[1]);
                    currentDeviceUserid=desc.split("#")[9].split(":")[1].trim();
                    currentDeviceUserEmail=desc.split("#")[10].split(":")[1].trim();
                    //currentdesc=desc.split("#")[0].split(":")[1]+"-"+desc.split("#")[7].split(":")[1];


                    logger.addRecordToLog(">>>>>>>>"+combined1);




                // Get all fields details
                feed = json.getJSONArray(TAG_feed);
                for(int i = 0; i < feed.length(); i++) {
                    JSONObject c = feed.getJSONObject(i);

                    // Storing  JSON item in a Variable
                    field1V.setText(c.getString(TAG_TC));
                    field2V.setText(c.getString(TAG_CL));
                    field3V.setText(c.getString(TAG_IN));
                    field4V.setText(c.getString(TAG_OUT));
                    field5V.setText(c.getString(TAG_OUT_T));
                    field6V.setText(c.getString(TAG_OUT_S));
                    field7V.setText(c.getString(TAG_WQ));
                    field8V.setText(c.getString(TAG_ACT));
                }
                } catch (JSONException e) {
                    logger.addRecordToLog("ERROR : "+e.toString());
                    e.printStackTrace();
                }


            }
    }


    //Delete channel and Update MAIN channel with new details
    private class DeleteUpdateChannel extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EachDeviceActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Sending DELETE DEVICE Request ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... data) {
            return updateDELETERequestChannel();
        }

        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();

            logger.addRecordToLog("Channel DELETE Request Status : "+result);

            // Check Result Status
            if(result.equals("NO")){
                Toast.makeText(getApplicationContext(), "Request FAILED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("DELETE Request Channel FAILED");
            } else {
                Toast.makeText(getApplicationContext(), "Request PASSED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("DELETE Request Channel PASSED");
                logger.addRecordToLog("**********************************************************");
                finish();
            }
        }

        // Send delete device request
        protected String updateDELETERequestChannel(){

            String status="NO";
            logger.addRecordToLog("******************* DELETE DEVICE REQUEST *************************");


            String userdetails=MainActivity.USER_name+"#"+MainActivity.USER_email+'#'+MainActivity.USER_scope;


            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);

            String channelKey=getString(R.string.delete_req_channel_write_api);

            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("field1", channelID));
            nameValuePair.add(new BasicNameValuePair("field2", MainActivity.USER_channel_id));
            nameValuePair.add(new BasicNameValuePair("field3", MainActivity.USER_writekey));
            nameValuePair.add(new BasicNameValuePair("field4", MainActivity.USER_readkey));
            nameValuePair.add(new BasicNameValuePair("field5", userdetails));
            nameValuePair.add(new BasicNameValuePair("field6", "NA"));
            nameValuePair.add(new BasicNameValuePair("field7", "NA"));
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
                logger.addRecordToLog("Updating delete request channel : "+response);
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

            logger.addRecordToLog("***************************************************");
            return status;
        } // end of fcn


    }

}
