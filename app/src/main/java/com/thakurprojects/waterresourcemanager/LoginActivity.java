package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

//import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private int backButtonCount=0;
    public final static String USER_DETAILS="com.thakurprojects.waterresourcemanager.USER_DETAILS";

    private static String input_login_name="";
    private static String input_login_password="";
    private static String input_state_name="";
    private static boolean validate_imei=false;

    // Get email and password details
    EditText _useridText=null;
    EditText _passwordText=null;
    //AlertDialog.Builder alert =null;

    // *************************************************************
    // List for all channels details
    private static ArrayList<String> allCountryUsers=null;
    private static ArrayList<String> allStateUsers=new ArrayList<String>();

    // Loger
    Logger logger;

    //JSON Node Names
    private static final String TAG_feed = "feeds";

    JSONArray feed = null;
    //**************************************************************


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // Get email and password details
        allCountryUsers=new ArrayList<String>();
       _useridText=(EditText) findViewById(R.id.input_userid);
        /*
        _useridText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // Check which radio button was clicked
                    RadioButton state=(RadioButton) findViewById(R.id.login_state);
                    RadioButton district=(RadioButton) findViewById(R.id.login_district);

                    state.setChecked(true);
                    district.setChecked(false);
                }
            }
        });
        */

        _passwordText=(EditText) findViewById(R.id.input_password);

        /* Alert Dialog Code Start*/
        //alert = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);


        /*
          Login button callback function.
         */
        Button _loginButton=(Button) findViewById(R.id.btn_login);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        if(!isConn()){
            Toast.makeText(getApplicationContext(), "No Internet!\nPlease enable Internet and Restart app!!", Toast.LENGTH_LONG).show();
            Button _loginButton2=(Button) findViewById(R.id.btn_login);
            _loginButton2.setEnabled(false);

        } else {
            // Get all user details from cloud
            new LoginActivity.ReadCountryUsers().execute();
        }

    }

    // Getter for user details
    public static ArrayList<String> getAllCountryUsers(){
        return allCountryUsers;
    }

   /*
    public void onLoginRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.login_state:
                if (checked)
                    input_login_scope="STATE";
                break;
            case R.id.login_district:
                if (checked)

                    alert.setTitle("INPUT"); //Set Alert dialog title here
                    alert.setMessage("STATE NAME :"); //Message here

                    // Set an EditText view to get user input
                    final EditText input = new EditText(getApplicationContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    input.setTextColor(R.color.black);
                    alert.setView(input);

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            input_state_name = input.getEditableText().toString();
                            // Verify
                            if (input_state_name.isEmpty() || input_state_name.length()<3) {
                                Toast.makeText(getApplicationContext(), "Please Provide STATE Name!", Toast.LENGTH_LONG).show();
                                input_login_scope="STATE";
                                RadioButton state=(RadioButton) findViewById(R.id.login_state);
                                state.setChecked(true);

                            } else {

                                input_login_scope="DISTRICT";
                                // Get all user details from cloud
                                new LoginActivity.ReadSTATEUsers().execute();
                            }


                        } // End of onClick(DialogInterface dialog, int whichButton)
                    }); //End of alert.setPositiveButton
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            Toast.makeText(getApplicationContext(), "Please Provide STATE Name!", Toast.LENGTH_LONG).show();
                            input_login_scope="STATE";
                            RadioButton state=(RadioButton) findViewById(R.id.login_state);
                            state.setChecked(true);
                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                break;
        }
    }
    */




    // login Operation
    public void login() {
        logger.addRecordToLog("**********************************************************");
        logger.addRecordToLog(TAG+" : "+"LOGIN START");


        // check internet connection
        if(!isConn()){
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "No Internet Connection");
            return;
        }

        /*
          GET IMEI Number
         */
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String IMEINO=telephonyManager.getDeviceId();
        logger.addRecordToLog("DEVICE IMEI NUMBER : "+IMEINO);
        logger.addRecordToLog("YOUR IMEI NUMBER : "+getString(R.string.my_imei_no));

        if(IMEINO.equals(getString(R.string.my_imei_no))){
            validate_imei=true;
        }

        /*
           Validate ;login details.
         */
        if (!validate()) {
            onLoginFailed();
            return;
        }

        /*
           FOR ADMINISTRATOR
         */
        if((input_login_name.equals("./ADMINISTRATOR"))&&(input_login_password.equals("SaveWater"))&&(validate_imei)){
            logger.addRecordToLog("Login as ADMINISTRATOR");

            Button _loginButton=(Button) findViewById(R.id.btn_login);
            _loginButton.setEnabled(true);

            // Call Main Server activity
            Intent intent = new Intent(this, MainServerActivity.class);
            startActivity(intent);

        } /*
             FOR COUNTRY
          */
        else if((input_login_name.equals("INDIA"))&&(input_login_password.equals("MyIndia"))&&(validate_imei)){
            logger.addRecordToLog("Login as INDIA");
            String eacUserDetails="COUNTRY" + "#" + getString(R.string.INDIA_channel_id) + "#" + "MyIndia" + "#" + "INDIA" + "#" + "thakur.projects787@gmail.com" + "#"+ getString(R.string.INDIA_channel_write_api) + "#" + getString(R.string.INDIA_channel_read_api) + "#" + "801234567895" + "#" + "8553372177"+"#"+"ADMIN";
            onLoginSuccess(eacUserDetails);
        }
        /*
          FOR STATE AND DISTRICT
         */
        else {

            logger.addRecordToLog("Login as STATE AND DISTRICT");

            /*
            ArrayList<String> allDetails= null;
            if(input_login_scope.equals("STATE")){
                allDetails=allCountryUsers;
            } else {
                allDetails=allStateUsers;
            }
            */
            boolean loginPassed=false;

            if(allCountryUsers.isEmpty()){
                logger.addRecordToLog("NO USERS DETAILS PRESENT!!");
                onLoginFailed();
            } else {

                Iterator allUserDetails =allCountryUsers.iterator();
                while (allUserDetails.hasNext()) {

                    String eacUserDetails = allUserDetails.next().toString();

                    // Check IMEI no.
                    boolean user_imei=false;
                    if(eacUserDetails.split("#")[7].equals(IMEINO)){
                        user_imei=true;
                        logger.addRecordToLog("USER IMEI MATCHED ...");
                    } else {
                        logger.addRecordToLog("USER IMEI NOT MATCHED ...");
                    }

                    // Check with admin
                    if(validate_imei){
                        user_imei=true;
                        logger.addRecordToLog("ADMIN IMEI MATCHED ...");
                    } else {
                        logger.addRecordToLog("ADMIN IMEI NOT MATCHED ...");
                    }

                    // Testing
                    user_imei=true;

                    // Validate users
                    if((input_login_name.equals(eacUserDetails.split("#")[3]))&&(input_login_password.equals(eacUserDetails.split("#")[2]))&&(user_imei)){
                        logger.addRecordToLog("LOGIN PASSED!!");
                        loginPassed=true;
                        onLoginSuccess(eacUserDetails);
                    }

                }//end of while
                if(!loginPassed) {
                    logger.addRecordToLog("LOGIN FAILED!!");
                    onLoginFailed();
                }

            }

        }


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



    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);

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

    /*
       Go to home page after login success.
     */
    public void onLoginSuccess(String user_details) {
        Button _loginButton=(Button) findViewById(R.id.btn_login);
        _loginButton.setEnabled(true);

        // Call Main activity
        // Start login activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER_DETAILS,user_details);
        startActivity(intent);
        //finish();
    }

    /*
      Stay on same activity if login failed.
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        Button _loginButton=(Button) findViewById(R.id.btn_login);
        _loginButton.setEnabled(true);
    }

    /*
       Validate entered email and password, return status
     */
    public boolean validate() {
        boolean valid = true;


        input_login_name = _useridText.getText().toString();
        input_login_password = _passwordText.getText().toString();

        if (input_login_name.isEmpty() || input_login_name.length()<4) {
            _useridText.setError("enter a valid user ID");
            valid = false;
        } else {
            _useridText.setError(null);
        }

        if (input_login_password.isEmpty() || input_login_password.length() < 4 ) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    // Read all user details from COUNTRY user channel
    private class ReadCountryUsers extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Please Wait ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            logger.addRecordToLog("\n\n ++++ LOGIN START ++++");
            logger.addRecordToLog("-- Getting all Country User Details ");

            JSONParser jParser = new JSONParser();
            // Default URL
           String url = "https://api.thingspeak.com/channels/";

            // Update url
            url=url+getString(R.string.user_channel_id)+"/feeds.json?api_key="+getString(R.string.user_channel_read_api);
            logger.addRecordToLog("URL : "+url);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }


        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            allCountryUsers.clear();
            logger.addRecordToLog("- Parse json fromat..");

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
                    allCountryUsers.add(scope+"#"+user_channel_id+"#"+password+"#"+user_name+"#"+email+"#"+writekey+"#"+readkey+"#"+imei+"#"+mobile+"#"+createdby);
                    logger.addRecordToLog("-- > "+allCountryUsers);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    // Read all user details from COUNTRY user channel
    private class ReadSTATEUsers extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Please Wait ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {

            logger.addRecordToLog("-- Getting State User details ");

            JSONParser jParser = new JSONParser();
            String url = "https://api.thingspeak.com/channels/";
            boolean found=false;

            if(allCountryUsers.isEmpty()){
                logger.addRecordToLog("NO COUNTRY USERS DETAILS PRESENT!!");
            } else {

                Iterator allUserDetails =allCountryUsers.iterator();
                while (allUserDetails.hasNext()) {

                    String eacUserDetails = allUserDetails.next().toString();

                    if(eacUserDetails.split("#")[3].equals(input_state_name)){
                        // Update url
                        url=url+eacUserDetails.split("#")[1]+"/feeds.json?api_key="+eacUserDetails.split("#")[6];
                        found=true;
                        break;

                    }
                }//end of while
            }

            logger.addRecordToLog(" URL : "+url);

            if (found) {
                // Getting JSON from URL
                logger.addRecordToLog("USER FOUND ");
                JSONObject json = jParser.getJSONFromUrl(url);
                return json;
            } else {
                logger.addRecordToLog("USER NOT FOUND ");
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            if(json!=null) {
                pDialog.dismiss();
                allStateUsers.clear();

                try {
                    // Getting JSON Array from URL
                    feed = json.getJSONArray(TAG_feed);

                    for (int i = 0; i < feed.length(); i++) {
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
                        allStateUsers.add(scope + "#" + user_channel_id + "#" + password + "#" + user_name + "#" + email + "#" + writekey + "#" + readkey + "#" + imei + "#" + mobile+"#"+createdby);
                        logger.addRecordToLog(" -- "+allStateUsers);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pDialog.dismiss();
                allStateUsers.clear();
            }

        }
    }
}
