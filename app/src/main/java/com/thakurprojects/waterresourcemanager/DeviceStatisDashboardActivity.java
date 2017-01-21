package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DeviceStatisDashboardActivity extends AppCompatActivity implements
        OnChartGestureListener, OnChartValueSelectedListener {


    private LineChart mChart;

    // logger object
    Logger logger;

    private TextView heading;
    private TextView information;

    private int total_datasets=3;

    private ArrayList<String> yvaluesList=new ArrayList<String>();

    String[] nameslist={"CL","In","Out","OutTo","InS","OuS","WQ","AC"};
    boolean[] displaystatus={true,true,true,true,true,true,true,true};

    private String datTimeDetails="";

    private String statisticsfor="";
    private String statisticsValue="10";

    // Upper limits
    String limitfor="UPPER";
    private float TWUpperlimit=1000f;
    private float TWLowerlimit=100f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_statis_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Device Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        heading=(TextView) findViewById(R.id.deviceheadingStatisticTextView);
        information=(TextView) findViewById(R.id.deviceinforstatisticTextView);

        //Get Intents Details
        logger.addRecordToLog("--- Device Statistics Analysis details -----");
        Intent intent = getIntent();
        statisticsfor = intent.getStringExtra(MainActivity.DEVICE_STATISTICS_ID);

        //statisticsfor="176691%Q5VRR7DKDWSTQKWT%10%DEVICE_NAME%DEVICE_DETAILS";

        logger.addRecordToLog(">> STATISTIC Intent Value : "+statisticsfor);
        logger.addRecordToLog(">> STATISTIC Entries Value : "+statisticsValue);

        /*
           Get range value and show details
         */
        getentriesRange();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_device_statis_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //----------------------------------
            case R.id.current_level_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[0] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[0] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.total_inlet_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[1] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[1] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.total_outlet_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[2] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[2] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.outlet_to_other_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[3] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[3] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.inlet_status_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[4] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[4] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.outlet_status_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[5] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[5] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.water_quality_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[6] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[6] = true;
                }

                createChart();
                return true;
            //----------------------------------
            case R.id.action_chk:
                if(item.isChecked()) {
                    if(getCheckboxStatus()) {
                        item.setChecked(false);
                        displaystatus[7] = false;
                    }
                } else {
                    item.setChecked(true);
                    displaystatus[7] = true;
                }

                createChart();
                return true;


            // Adjust Upper limit
            case R.id.upper_limit_chart:
                limitfor="UPPER";
                getLimitsInputs();
                return true;

            // Adjust Upper limit
            case R.id.lower_limit_chart:
                limitfor="LOWER";
                getLimitsInputs();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }//ens
    }


    public void createChart(){
        mChart = (LineChart) findViewById(R.id.device_chart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        //mChart.setBackgroundColor(Color.GRAY);

        // enable/disable highlight indicators (the lines that indicate the
        // highlighted Entry)
        mChart.setHighlightIndicatorEnabled(false);

        // x-axis limit line
//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLabelPosition.POS_RIGHT);
//        llXAxis.setTextSize(10f);
//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.addLimitLine(llXAxis);

        LimitLine ll1 = new LimitLine(TWUpperlimit, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.POS_RIGHT);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(TWLowerlimit, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.POS_RIGHT);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);

        //Check for upperlimit status
        leftAxis.setAxisMaxValue(TWUpperlimit+10f);

        leftAxis.setAxisMinValue(TWLowerlimit-10f);
        leftAxis.setStartAtZero(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData();

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        //l.setForm(LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();
    }

    // Check all checkbox status
    private boolean getCheckboxStatus(){
        int count=0;
        for(int i=0;i<displaystatus.length;i++){
            if(displaystatus[i]){
                count++;
            }
        }
        if(count==1)
            return false;
        else
            return true;
    }

    // get upper nad lower limit input and update chart
    private void getLimitsInputs(){

        String msg="Please provide Lower limit!!";

        if(limitfor.equals("UPPER")){
           msg="Please provide Upper limit!!";
        }

        /* Alert Dialog Code Start*/
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        alert.setTitle("INPUT"); //Set Alert dialog title here
        alert.setMessage(msg); //Message here

        // Set an EditText view to get user input
        final EditText input = new EditText(getApplicationContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTextColor(R.color.black);
        alert.setView(input);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String value=input.getEditableText().toString();
                if(value.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please provide limit value !!", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                } else {
                    if(limitfor.equals("UPPER")) {
                        TWUpperlimit = Float.valueOf(value);
                        createChart();
                    } else {
                        TWLowerlimit = Float.valueOf(value);
                        createChart();
                    }
                    dialog.cancel();
                }


            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
                /* Alert Dialog Code End*/
    }

    // get entries range
    private void getentriesRange(){

        /* Alert Dialog Code Start*/
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        alert.setTitle("STATISTICS"); //Set Alert dialog title here
        alert.setMessage("How many entries you want to display on chart.\nDefault no. is 10."); //Message here

        // Set an EditText view to get user input
        final EditText input = new EditText(getApplicationContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setTextColor(R.color.black);
        alert.setView(input);


        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String value=input.getEditableText().toString();
                if(value.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Feching last 10 entries!!", Toast.LENGTH_LONG).show();
                    // Getting all Details
                    new DeviceStatisDashboardActivity.ReadDeviceChannels().execute();
                    dialog.cancel();
                } else {
                    statisticsValue=value;
                    Toast.makeText(getApplicationContext(), "Feching last "+value+" entries!!", Toast.LENGTH_LONG).show();
                    // Getting all Details
                    new DeviceStatisDashboardActivity.ReadDeviceChannels().execute();

                    dialog.cancel();
                }


            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Default.

                // Getting all Details
                new DeviceStatisDashboardActivity.ReadDeviceChannels().execute();

                Toast.makeText(getApplicationContext(), "Feching last 10 entries!!", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
                /* Alert Dialog Code End*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /*
      ---------------------------------------------
      --  SET DATA -----
      ---------------------------------------------

     */
    private void setData() {

        String[] xpointsList=datTimeDetails.split("#");

        //Log.i("MSG : ",updateFor);
        int count=xpointsList.length;

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((xpointsList[i]) + "");
        }

        // Generate colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());



        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        ArrayList<String> ypointsList = new ArrayList<String>();
        ArrayList<String> dataSheetNames = new ArrayList<String>();

        total_datasets=0;
        for(int allnames=0;allnames<nameslist.length;allnames++){
            if(displaystatus[allnames]){
                total_datasets++;
                ypointsList.add(yvaluesList.get(allnames).split("%")[1]);
                dataSheetNames.add(yvaluesList.get(allnames).split("%")[0]);
            }
        }


        /*
           Create Multiple datasets
         */
        for(int dt=0;dt<total_datasets;dt++){
            /*
               Data SET 1
             */

            // Generate color
            //int color = mColors[dt % mColors.length];
            int color=colors.get(dt);

            ArrayList<Entry> yVals = new ArrayList<Entry>();

            for (int i = 0; i < count; i++) {
                yVals.add(new Entry((Float.parseFloat(ypointsList.get(dt).split("#")[i])), i));
            }

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(yVals, dataSheetNames.get(dt));
            // set1.setFillAlpha(110);
            //set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            //set1.enableDashedLine(10f, 5f, 0f);
            set1.setColor(color);
            set1.setCircleColor(color);
            set1.setLineWidth(1f);
            set1.setCircleSize(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setFillAlpha(65);
            set1.setFillColor(color);
            //set1.setDrawFilled(true);
            // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
            // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

            dataSets.add(set1); // add the datasets

        }//end of for

        // make the first DataSet dashed
        dataSets.get(0).enableDashedLine(10, 10, 0);
        //dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        //Log.i("Entry selected", e.toString());
        Log.i("Entry selected", String.valueOf(e.getVal()));
        //Toast.makeText(getApplicationContext(), "Value : "+String.valueOf(e.getVal()), Toast.LENGTH_SHORT).show();
        //Log.i("", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }


    /*
       Read All Analysis channels Details
     */
    private class ReadDeviceChannels extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONArray feed = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DeviceStatisDashboardActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Please Wait ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            logger.addRecordToLog("-- Getting Device channel Details --  ");
            logger.addRecordToLog(": Analysis START : "+statisticsfor);
            yvaluesList = getAnalysis(statisticsfor.split("%")[0], statisticsfor.split("%")[1],statisticsValue);

            Log.i("MSG  : ",yvaluesList.toString());
            Log.i("MSG  : ",datTimeDetails);

            logger.addRecordToLog(": Analysis Done : "+yvaluesList.toString());
            if(yvaluesList.isEmpty()){
                logger.addRecordToLog("ERROR : No Analysis present ..  ");
            }

            return "YES";

        }


        @Override
        protected void onPostExecute(String json) {
            pDialog.dismiss();

            logger.addRecordToLog("Post Execution :  "+json);

            heading.setText(statisticsfor.split("%")[2]);
            information.setText(statisticsfor.split("%")[3]);
            createChart();

        }

        /*
         Read MAIN DEVICE Channel
        */
        protected ArrayList<String> getAnalysis(String channelID, String readKey,String upto){

            ArrayList<String> allDetails=new ArrayList<String>();

            ArrayList<String> TC_CL=new ArrayList<String>();
            ArrayList<String> TInlet=new ArrayList<String>();
            ArrayList<String> TOutlet=new ArrayList<String>();
            ArrayList<String> OutletToOther=new ArrayList<String>();
            ArrayList<String> InletStatus=new ArrayList<String>();
            ArrayList<String> OutletStatus=new ArrayList<String>();
            ArrayList<String> WATER_QUALITY=new ArrayList<String>();
            ArrayList<String> ACTION=new ArrayList<String>();

            logger.addRecordToLog("-- Reading ANALYSIS Channels Details ----------");

            JSONParser jParser = new JSONParser();



            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey+"&results="+upto;

            JSONObject json = jParser.getJSONFromUrl(url);
            //String jsonS="{\"channel\":{\"id\":176691,\"name\":\"GJVD25456\",\"description\":\"TYPE : Supplier\\nADDRESS : godhra\\n DIST : VADODARA\\n PIN CODE : 160085\\n STATE : GUJARAT\\n COUNTRY : INDIA\\n CONTACT NO : 8553372177\\n AREA COVER : 37466,36363,373636,36363\\n TIMMING : 3TO9\\n USERID : VADODARA\\n USEREMAIL : vivek787.thakur@live.com\",\"latitude\":\"22.4543507974\",\"longitude\":\"73.6933849007\",\"field1\":\"TC_CL\",\"field2\":\"TInlet\",\"field3\":\"TOutlet\",\"field4\":\"OutletToOther\",\"field5\":\"InletStatus\",\"field6\":\"OutletStatus\",\"field7\":\"WATER_QUALITY\",\"field8\":\"ACTION\",\"created_at\":\"2016-10-29T12:26:03Z\",\"updated_at\":\"2016-11-04T05:29:05Z\",\"last_entry_id\":48},\"feeds\":[{\"created_at\":\"2016-11-03T02:29:03Z\",\"entry_id\":39,\"field1\":\"4081\",\"field2\":\"788\",\"field3\":\"864\",\"field4\":\"180\",\"field5\":\"1\",\"field6\":\"1\",\"field7\":\"72\",\"field8\":\"7\"},{\"created_at\":\"2016-11-03T05:29:04Z\",\"entry_id\":40,\"field1\":\"2346\",\"field2\":\"628\",\"field3\":\"460\",\"field4\":\"298\",\"field5\":\"0\",\"field6\":\"0\",\"field7\":\"44\",\"field8\":\"9\"},{\"created_at\":\"2016-11-03T08:29:03Z\",\"entry_id\":41,\"field1\":\"3318\",\"field2\":\"374\",\"field3\":\"165\",\"field4\":\"855\",\"field5\":\"0\",\"field6\":\"0\",\"field7\":\"58\",\"field8\":\"3\"},{\"created_at\":\"2016-11-03T11:29:03Z\",\"entry_id\":42,\"field1\":\"2065\",\"field2\":\"114\",\"field3\":\"603\",\"field4\":\"199\",\"field5\":\"0\",\"field6\":\"0\",\"field7\":\"68\",\"field8\":\"10\"},{\"created_at\":\"2016-11-03T14:29:04Z\",\"entry_id\":43,\"field1\":\"2921\",\"field2\":\"827\",\"field3\":\"582\",\"field4\":\"590\",\"field5\":\"1\",\"field6\":\"1\",\"field7\":\"29\",\"field8\":\"8\"},{\"created_at\":\"2016-11-03T17:29:04Z\",\"entry_id\":44,\"field1\":\"3001\",\"field2\":\"287\",\"field3\":\"369\",\"field4\":\"940\",\"field5\":\"0\",\"field6\":\"0\",\"field7\":\"58\",\"field8\":\"2\"},{\"created_at\":\"2016-11-03T20:29:03Z\",\"entry_id\":45,\"field1\":\"4228\",\"field2\":\"224\",\"field3\":\"114\",\"field4\":\"692\",\"field5\":\"0\",\"field6\":\"1\",\"field7\":\"73\",\"field8\":\"1\"},{\"created_at\":\"2016-11-03T23:29:03Z\",\"entry_id\":46,\"field1\":\"4028\",\"field2\":\"203\",\"field3\":\"482\",\"field4\":\"573\",\"field5\":\"0\",\"field6\":\"1\",\"field7\":\"47\",\"field8\":\"6\"},{\"created_at\":\"2016-11-04T02:29:03Z\",\"entry_id\":47,\"field1\":\"3212\",\"field2\":\"521\",\"field3\":\"390\",\"field4\":\"606\",\"field5\":\"0\",\"field6\":\"1\",\"field7\":\"19\",\"field8\":\"3\"},{\"created_at\":\"2016-11-04T05:29:05Z\",\"entry_id\":48,\"field1\":\"3882\",\"field2\":\"585\",\"field3\":\"934\",\"field4\":\"768\",\"field5\":\"0\",\"field6\":\"1\",\"field7\":\"55\",\"field8\":\"10\"}]}";


            try {
                //JSONObject json = new JSONObject(jsonS);


                // Getting JSON Array from URL
                feed = json.getJSONArray("feeds");
                for (int i = 0; i < feed.length(); i++) {
                    JSONObject c = feed.getJSONObject(i);

                    String entryID = c.getString("created_at");

                    String field1 = c.getString("field1");
                    String field2 = c.getString("field2");
                    String field3 = c.getString("field3");
                    String field4 = c.getString("field4");
                    String field5 = c.getString("field5");
                    String field6 = c.getString("field6");
                    String field7 = c.getString("field7");
                    String field8 = c.getString("field8");

                    // Updating arrayList

                    TC_CL.add(field1);
                    TInlet.add(field2);
                    TOutlet.add(field3);
                    OutletToOther.add(field4);
                    InletStatus.add(field5);
                    OutletStatus.add(field6);
                    WATER_QUALITY.add(field7);
                    ACTION.add(field8);

                    datTimeDetails=datTimeDetails+entryID+"#";
                }//end of for

                /*
                   Process information
                 */

                String TC_CL_s = "";
                String TInlet_s = "";
                String TOutlet_s = "";
                String OutletToOther_s = "";
                String InletStatus_s = "";
                String OutletStatus_s = "";
                String WATER_QUALITY_s = "";
                String ACTION_s = "";


                // loop on all entries
                for (int eachEntry = 0; eachEntry < TC_CL.size(); eachEntry++) {

                    TC_CL_s = TC_CL_s + TC_CL.get(eachEntry) + "#";
                    TInlet_s = TInlet_s + TInlet.get(eachEntry) + "#";
                    TOutlet_s = TOutlet_s + TOutlet.get(eachEntry) + "#";
                    OutletToOther_s = OutletToOther_s + OutletToOther.get(eachEntry) + "#";
                    InletStatus_s = InletStatus_s + InletStatus.get(eachEntry)+ "#";
                    OutletStatus_s = OutletStatus_s + OutletStatus.get(eachEntry) + "#";
                    WATER_QUALITY_s = WATER_QUALITY_s + WATER_QUALITY.get(eachEntry) + "#";
                    ACTION_s = ACTION_s + ACTION.get(eachEntry)+ "#";

                }

                allDetails.add(nameslist[0] + "%" + TC_CL_s);
                allDetails.add(nameslist[1] + "%" + TInlet_s);
                allDetails.add(nameslist[2] + "%" + TOutlet_s);
                allDetails.add(nameslist[3] + "%" + OutletToOther_s);
                allDetails.add(nameslist[4] + "%" + InletStatus_s);
                allDetails.add(nameslist[5] + "%" + OutletStatus_s);
                allDetails.add(nameslist[6] + "%" + WATER_QUALITY_s);
                allDetails.add(nameslist[7] + "%" + ACTION_s);





            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allDetails;
        }


    }



}
