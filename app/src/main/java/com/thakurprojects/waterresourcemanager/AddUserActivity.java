package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import java.util.Random;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import java.util.List;


public class AddUserActivity extends AppCompatActivity {

    private boolean isBackgound=false;
    // logger object
    Logger logger;

    // Edit text field
    private Button _adduserButton;

    private EditText _scopeText;
    private EditText _scopeDetailsText;
    private EditText _nameText;
    private EditText _emailText;
    private EditText _mobileText;
    private EditText _imeiText;

    // User input values
    private String scope="";
    private String password="";
    private String name="";
    private String email="";
    private String mobile="";
    private String imei="";

    private String created_by="";
    private String created_by2="";

    // USER PARAMETERS
    String logged_user_scope=MainActivity.USER_scope;
    String logged_user_name=MainActivity.USER_name;
    String logged_user_channelID=MainActivity.USER_channel_id;
    String logged_user_write_key=MainActivity.USER_writekey;
    String logged_user_read_key=MainActivity.USER_readkey;
    String logged_user_email=MainActivity.USER_email;
    String logged_user_mobile=MainActivity.USER_mobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New User");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        _scopeText = (EditText) findViewById(R.id.user_input_scope);
        _scopeDetailsText=(EditText) findViewById(R.id.user_input_scopedetails);

        _nameText = (EditText) findViewById(R.id.user_input_name);

        _emailText = (EditText) findViewById(R.id.user_input_email);
        _mobileText = (EditText) findViewById(R.id.user_input_mobile);
        _imeiText = (EditText) findViewById(R.id.user_input_imei);

        _adduserButton = (Button) findViewById(R.id.btn_adduser);
        _adduserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adduser();
            }
        });

        // Checking logged user scope

        if(logged_user_scope.equals("COUNTRY")){
            _scopeText.setText("STATE");
             created_by="\nCOUNTRY : INDIA";
            created_by2="INDIA";

            _scopeDetailsText.setHint("COUNTRY");
            _scopeDetailsText.setText("INDIA");

        } else if (logged_user_scope.equals("STATE")){
            _scopeText.setText("DISTRICT");
             created_by="\nSTATE : "+logged_user_name+"\nCOUNTRY : "+"INDIA";
            created_by2=logged_user_name;

            _scopeDetailsText.setHint("STATE/COUNTRY");
            _scopeDetailsText.setText(logged_user_name+"/INDIA");

        } else {
            _scopeText.setText("NOTHING");
        }


    }

    /*
      ADD new User
     */

    private void adduser() {

        logger.addRecordToLog("**********************************************************");
        logger.addRecordToLog("> Adding new user");


        // check internet connection
        if(!isConn()){
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            logger.addRecordToLog("No Internet Connection");
            return;
        }

        // Validation step
        if(!validateInputs()){
            return;
        }

        // Calling add device fcn and disable view

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
        alertDialogBuilder.setTitle("ADD USER");
        alertDialogBuilder.setMessage("Are you sure,You want to ADD New User?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                /*
                   Generate Password
                 */
                char[] pswd = new RandomPasswordGenerator().generatePswd(6, 8,
                        1, 2, 0);
                password=new String(pswd);

                isBackgound=true;
                new AddUserActivity.addNewUser().execute("");

            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Cancel!", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        return;
    }

    @Override
    public void onBackPressed() {
        // Check if backgound task is running
        if(isBackgound){
            Toast.makeText(this, "Background task is running !!\n Please WAIT !!!", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


    /*
     Check internet is enabled or not.
    */
    private boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    /*
      Validate Unser Inputs
     */
    private boolean validateInputs(){

        boolean valid=true;

        // Get dat from user form
        scope=_scopeText.getText().toString();
        name=_nameText.getText().toString();

        email=_emailText.getText().toString();
        mobile=_mobileText.getText().toString();
        imei=_imeiText.getText().toString();

        if (scope.isEmpty()) {
            _scopeText.setError("Please provide SCOPE value");
            valid = false;

        } else {
            _scopeText.setError(null);
        }

        if (name.isEmpty() || name.length() < 4) {
            _nameText.setError("at least 4 characters");
            valid = false;
        } else {
            _nameText.setError(null);

        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() !=10) {
            _mobileText.setError("10 digit number");
            valid = false;
        } else {
            _mobileText.setError(null);

        }

        if (imei.isEmpty() || imei.length() <4) {
            _imeiText.setError("at least 4 characters");
            valid = false;
        } else {
            _imeiText.setError(null);

        }

        return valid;
    }

    /*
    Asyc inner class
    Use to perfrom HTTP POST and GET task in background.
    */
    private class addNewUser extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isBackgound=true;
            pDialog = new ProgressDialog(AddUserActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Adding New User ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... data) {
            String responseStatus=createNewUserChannel();
            if(responseStatus.equals("NO")){
                return "NO";
            } else {
                // Update information into MAIN Channel
                return parseJSONandUpdate(responseStatus);
            }
        }

        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();

            isBackgound=false;

            logger.addRecordToLog("ADD USER Status : "+result);

            // Check Result Status
            if(result.equals("NO")){
                Toast.makeText(getApplicationContext(), "FAILED!\n New User Not Added..", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ADD Channel FAILED");
            } else {
                Toast.makeText(getApplicationContext(), "New User Added!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ADD Channel PASSED");
                logger.addRecordToLog("**********************************************************");

               finish();

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

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(8);
            String desc="";

            /*
               STATE SCOPE
             */
            if(scope.equals("STATE")) {
                logger.addRecordToLog("-- STATE SCOPE --");
                // Create complete description
                desc = "SCOPE : " + scope + "\nNAME : " + name + "\nEMAIL : " + email + "\n MOBILE : " + mobile + "\n IMEI : " + imei +"\n CREATED BY : "+ created_by2;
                //Post Data
                String channelKey = getString(R.string.my_api);

                nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
                nameValuePair.add(new BasicNameValuePair("name", name));
                nameValuePair.add(new BasicNameValuePair("description", desc));
                nameValuePair.add(new BasicNameValuePair("tags", scope + "," + name));


                //nameValuePair.add(new BasicNameValuePair("public_flag", "true"));
                nameValuePair.add(new BasicNameValuePair("field1", "SCOPE"));
                nameValuePair.add(new BasicNameValuePair("field2", "NAME"));
                nameValuePair.add(new BasicNameValuePair("field3", "CHANNEL_ID"));
                nameValuePair.add(new BasicNameValuePair("field4", "PASSWORD_IMEI"));
                nameValuePair.add(new BasicNameValuePair("field5", "WRITE_KEY"));
                nameValuePair.add(new BasicNameValuePair("field6", "READ_KEY"));
                nameValuePair.add(new BasicNameValuePair("field7", "EMAIL_ID_CONTACT_NO"));
                nameValuePair.add(new BasicNameValuePair("field8", "CREATED_BY"));

            } else {
                /*
                   DISTRICT SCOPE
                 */
                logger.addRecordToLog("-- DISTRICT SCOPE --");
                // Create complete description
                desc = "SCOPE : " + scope + "\nNAME : " + name + "\nEMAIL : " + email + "\n MOBILE : " + mobile + "\n IMEI : " + imei + "\n CREATED BY : "+ created_by2;
                //Post Data
                String channelKey = getString(R.string.my_api);

                nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
                nameValuePair.add(new BasicNameValuePair("name", name));
                nameValuePair.add(new BasicNameValuePair("description", desc));
                nameValuePair.add(new BasicNameValuePair("tags", scope + "," + name));


                //nameValuePair.add(new BasicNameValuePair("public_flag", "true"));
                nameValuePair.add(new BasicNameValuePair("field1", "DEVICE_CHANNEL_ID"));
                nameValuePair.add(new BasicNameValuePair("field2", "DEVICE_ID"));
                nameValuePair.add(new BasicNameValuePair("field3", "TAGS"));
                nameValuePair.add(new BasicNameValuePair("field4", "WRITE_KEY"));
                nameValuePair.add(new BasicNameValuePair("field5", "READ_KEY"));
                nameValuePair.add(new BasicNameValuePair("field6", "LATITUDE"));
                nameValuePair.add(new BasicNameValuePair("field7", "LONGITUDE"));
                nameValuePair.add(new BasicNameValuePair("field8", "ACTION"));


            }


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

            logger.addRecordToLog("ADD NEW USER TASK END");
            return status;
        }


        //Parse json response format
        protected String parseJSONandUpdate(String response){

            String data = "YES";

            try {
                JSONObject jsonRootObject = new JSONObject(response);

                // Read all root objects details
                String channelID=jsonRootObject.getString("id");
                String userID=jsonRootObject.getString("name");
                String writeKey="";
                String readKey="";

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
                logger.addRecordToLog("Channel Filter Details : \n"+channelID+","+","+password+"@"+imei+","+name+","+email+","+scope+","+readKey+","+writeKey);
                data=updateADMINUserChannel(channelID,password+"@"+imei,name,email,scope,writeKey,readKey);
                //data=channelID+"\n"+channelname+"\n"+tags+"\n"+writeKey;

                // Send MAIL
                String userBody="Hi "+name+"\n \n You Account has been activated in Water Resource Manager System.\n You can add your IoT devices and monitor real time data." +
                        "\n\n Details : \n User ID : "+name+"\n Password : "+password+"\n IMEI : "+imei+"\n\n Thanks.\nADMIN";

                String userSub="Water Resource Manager Account Activatation!!";

                String[] emaildetails={email};

                 boolean mailStatus=new SendMail(userSub, userBody, emaildetails).send();
                 if(mailStatus){
                     logger.addRecordToLog("Mail Send PASSED!!");
                 } else {
                     logger.addRecordToLog("Mail Send FAILED!!");
                 }
                return data;
                //output.setText(data);
            } catch (JSONException e) {e.printStackTrace();data="NO";}

            return data;

        }

        // Update MAIN channel field
        protected String updateADMINUserChannel(String channel_id,String password,String name,String email,String scope,String write_key,String read_key){

            String status="NO";
            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);

            String channelKey=logged_user_write_key;

            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("field1", scope));
            nameValuePair.add(new BasicNameValuePair("field2", name));
            nameValuePair.add(new BasicNameValuePair("field3", channel_id));
            nameValuePair.add(new BasicNameValuePair("field4", password));
            nameValuePair.add(new BasicNameValuePair("field5", write_key));
            nameValuePair.add(new BasicNameValuePair("field6", read_key));
            nameValuePair.add(new BasicNameValuePair("field7", email+"%"+mobile));
            nameValuePair.add(new BasicNameValuePair("field8", created_by2));

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


    }


    /*
        Random Password generator
     */
    private class RandomPasswordGenerator {
        private static final String ALPHA_CAPS  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static final String ALPHA   = "abcdefghijklmnopqrstuvwxyz";
        private static final String NUM     = "0123456789";
        private static final String SPL_CHARS   = "!@#$%^&*_=+-/";

        public char[] generatePswd(int minLen, int maxLen, int noOfCAPSAlpha,
                                          int noOfDigits, int noOfSplChars) {
            if(minLen > maxLen)
                throw new IllegalArgumentException("Min. Length > Max. Length!");
            if( (noOfCAPSAlpha + noOfDigits + noOfSplChars) > minLen )
                throw new IllegalArgumentException
                        ("Min. Length should be atleast sum of (CAPS, DIGITS, SPL CHARS) Length!");
            Random rnd = new Random();
            int len = rnd.nextInt(maxLen - minLen + 1) + minLen;
            char[] pswd = new char[len];
            int index = 0;
            for (int i = 0; i < noOfCAPSAlpha; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = ALPHA_CAPS.charAt(rnd.nextInt(ALPHA_CAPS.length()));
            }
            for (int i = 0; i < noOfDigits; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = NUM.charAt(rnd.nextInt(NUM.length()));
            }
            for (int i = 0; i < noOfSplChars; i++) {
                index = getNextIndex(rnd, len, pswd);
                pswd[index] = SPL_CHARS.charAt(rnd.nextInt(SPL_CHARS.length()));
            }
            for(int i = 0; i < len; i++) {
                if(pswd[i] == 0) {
                    pswd[i] = ALPHA.charAt(rnd.nextInt(ALPHA.length()));
                }
            }
            return pswd;
        }

        private int getNextIndex(Random rnd, int len, char[] pswd) {
            int index = rnd.nextInt(len);
            while(pswd[index = rnd.nextInt(len)] != 0);
            return index;
        }
    }

}
