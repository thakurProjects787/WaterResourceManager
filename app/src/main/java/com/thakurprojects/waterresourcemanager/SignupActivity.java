package com.thakurprojects.waterresourcemanager;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private GoogleApiClient client2;

    private Button _signupButton;
    private Button _setlocationButton;

    private int REQUEST_CODE=1;
    private String completeAddress="";
    private String Latitude="";
    private String Longitude="";

    // logger object
    Logger logger;


   // Details
    String name = "";
    String address = "";
    String dist = "";
    String state = "";
    String country = "";
    String pincode = "";
    String areacover = "";
    String timming = "";
    String contactno = "";

    private boolean isBackgound=false;
    private String resource_type="Supplier";
    private RadioGroup _resourceTypes;

    private EditText _nameText;
    private EditText _addressText;
    private EditText _distText;
    private EditText _stateText;
    private EditText _countryText;
    private EditText _pincodeText;
    private EditText _areacoverText;
    private EditText _timmingText;
    private EditText _mobileText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _resourceTypes=(RadioGroup) findViewById(R.id.resource_types_rb);
        _nameText = (EditText) findViewById(R.id.input_name);
        _addressText = (EditText) findViewById(R.id.input_address);
        _distText = (EditText) findViewById(R.id.dist_name);
        _stateText = (EditText) findViewById(R.id.state_name);
        _pincodeText = (EditText) findViewById(R.id.pincode_name);
        _countryText = (EditText) findViewById(R.id.country);
        _areacoverText = (EditText) findViewById(R.id.areacover_name);
        _timmingText = (EditText) findViewById(R.id.timming_name);
        _mobileText = (EditText) findViewById(R.id.input_mobile);


        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _setlocationButton = (Button) findViewById(R.id.btn_setMap);
        _setlocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Map activity
                Intent addIntent=new Intent(SignupActivity.this,MapsActivity.class);
                  //startActivity(addIntent);
                  startActivityForResult(addIntent, REQUEST_CODE);


            }
        });


        // SET default values
        _countryText.setText("INDIA");
        _stateText.setText(MainActivity.USER_createdby);
        _distText.setText(MainActivity.USER_name);

    }

    // Call Back method  to get the Message form other Activity    override the method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);


        // check if the request code is same as what is passed  here it is REQUEST_CODE
        if(requestCode==REQUEST_CODE)
        {
            // fetch the message String
            completeAddress=data.getStringExtra("MESSAGE");

            if(!completeAddress.equals("NON")) {
                // Set the message string in textView
                _addressText.setText(completeAddress.split("#")[2]);
               // _distText.setText(completeAddress.split("#")[3]);
                //_stateText.setText(completeAddress.split("#")[4]);
                //_countryText.setText(completeAddress.split("#")[5]);
                _pincodeText.setText(completeAddress.split("#")[6]);

                Latitude = completeAddress.split("#")[0];
                Longitude = completeAddress.split("#")[1];
            }

        }

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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.natural:
                if (checked)
                    resource_type="Reservoir";

                    areacover="NA";
                    timming="NA";
                    _areacoverText.setText(areacover);
                     _timmingText.setText(timming);
                    _areacoverText.setEnabled(false);
                    _timmingText.setEnabled(false);
                    break;
            case R.id.storage:
                if (checked)
                    resource_type="Supplier";

                    areacover="";
                    timming="";
                    _areacoverText.setText(areacover);
                    _timmingText.setText(timming);
                    _areacoverText.setEnabled(true);
                    _timmingText.setEnabled(true);
                    break;
        }
    }

    public void signup() {

        logger.addRecordToLog("**********************************************************");
        logger.addRecordToLog(TAG+" : "+"Signup START");


        // check internet connection
        if(!isConn()){
            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "No Internet Connection");
            return;
        }

        // Validation step
        if(!validate()){
            return;
        }

        // Calling add device fcn and disable view

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
        alertDialogBuilder.setTitle("ADD DEVICE");
        alertDialogBuilder.setMessage("Send ADD DEVICE Request to ADMIN.\nAre you sure,You want to ADD New Device?");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                isBackgound=true;
                new addNewDevice().execute("");
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

    /*
      Validate all input fields.
     */
    public boolean validate() {
        boolean valid = true;

        name = _nameText.getText().toString();
        address = _addressText.getText().toString();
        dist = _distText.getText().toString();
        state = _stateText.getText().toString();
        country = _countryText.getText().toString();
        pincode = _pincodeText.getText().toString();
        areacover = _areacoverText.getText().toString();
        timming = _timmingText.getText().toString();
        contactno = _mobileText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);

        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);

        }


        if (dist.isEmpty()) {
            _distText.setError("enter a valid District Name");
            valid = false;
        } else {
            _distText.setError(null);

        }

        if (state.isEmpty()) {
            _stateText.setError("Enter Valid State Name");
            valid = false;
        } else {
            _stateText.setError(null);

        }

        if (pincode.isEmpty() || pincode.length() < 4) {
            _pincodeText.setError("Enter Valid PINCODE");
            valid = false;
        } else {
            _pincodeText.setError(null);

        }

        if (country.isEmpty() || pincode.length() < 4) {
            _countryText.setError("Enter Valid PINCODE");
            valid = false;
        } else {
            _countryText.setError(null);

        }

        // Only check for resources
        if(resource_type.equals("Supplier")) {
            if (areacover.isEmpty()) {
                _areacoverText.setError("Enter Valid Area Details");
                valid = false;
            } else {
                _areacoverText.setError(null);

            }

            if (timming.isEmpty()) {
                _timmingText.setError("Enter Valid Timming Details");
                valid = false;
            } else {
                _timmingText.setError(null);

            }
        }

        if (contactno.isEmpty()) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);

        }

        // Check GPS location field
        if (Latitude.isEmpty()&&Longitude.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Latitude and Latitude value not provided", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
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
     Asyc inner class
     Use to perfrom HTTP POST and GET task in background.
     */
    private class addNewDevice extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SignupActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Adding New Device Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... data) {
            return updateADDRequestChannel();
        }

        // Execute after the completion of background task.
        protected void onPostExecute(String result){

            pDialog.dismiss();

            isBackgound=false;

            logger.addRecordToLog("ADD Channel Request Status : "+result);
            Log.d(TAG, "PROGRESS Done : "+result);

            // Check Result Status
            if(result.equals("NO")){
                Toast.makeText(getApplicationContext(), "Request FAILED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ADD Channel Request FAILED");
            } else {
                Toast.makeText(getApplicationContext(), "Request PASSED !!", Toast.LENGTH_LONG).show();
                logger.addRecordToLog("ADD Channel Request PASSED");
                logger.addRecordToLog("**********************************************************");

                finish();

                /*
                // Clear all the fields
                _nameText.setText("");
                _addressText.setText("");
                _distText.setText("");
                _stateText.setText("");
                _pincodeText.setText("");
                _areacoverText.setText("");
                _timmingText.setText("");
                _mobileText.setText("");
                _countryText.setText("");
                */


            }
        }

        // Send add device request
        protected String updateADDRequestChannel(){

            String status="NO";
            logger.addRecordToLog("******************* ADD DEVICE REQUEST *************************");

            // Get Input details
            // Create complete description
            String desc=resource_type+"#"+address+"#"+dist+"#"+pincode+"#"+state+"#"+country+"#"+contactno;

            String userdetails=MainActivity.USER_name+"#"+MainActivity.USER_email;


            // Update Channels Fields.
            String update_channel_api = "https://api.thingspeak.com/update.json";

            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost(update_channel_api);


            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);

            String channelKey=getString(R.string.add_req_channel_write_api);

            nameValuePair.add(new BasicNameValuePair("api_key", channelKey));
            nameValuePair.add(new BasicNameValuePair("field1", userdetails));
            nameValuePair.add(new BasicNameValuePair("field2", name));
            nameValuePair.add(new BasicNameValuePair("field3", desc));
            nameValuePair.add(new BasicNameValuePair("field4", Latitude));
            nameValuePair.add(new BasicNameValuePair("field5", Longitude));
            nameValuePair.add(new BasicNameValuePair("field6", MainActivity.USER_writekey));
            nameValuePair.add(new BasicNameValuePair("field7", areacover));
            nameValuePair.add(new BasicNameValuePair("field8", timming));

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
                logger.addRecordToLog("Updating add request channel : "+response);
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

    } // end of class
}



