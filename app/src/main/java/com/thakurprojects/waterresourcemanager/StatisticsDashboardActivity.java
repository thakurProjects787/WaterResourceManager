package com.thakurprojects.waterresourcemanager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class StatisticsDashboardActivity extends AppCompatActivity implements
        OnChartGestureListener, OnChartValueSelectedListener{

    private LineChart mChart;

    // logger object
    Logger logger;

    private TextView heading;
    private TextView information;

    private int total_datasets=3;

    private ArrayList<String> yvaluesList=new ArrayList<String>();

    private String datTimeDetails="";

    private String statisticsfor="";

    // Upper limits
    String limitfor="UPPER";
    String currentChartStatus="TW";
    private float TWUpperlimit=2500f;
    private float TWLowerlimit=10f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Statistics");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        heading=(TextView) findViewById(R.id.headingStatisticTextView);
        information=(TextView) findViewById(R.id.inforstatisticTextView);

        //Get Intents Details
        logger.addRecordToLog("--- Statistics Analysis details -----");
        Intent intent = getIntent();
        statisticsfor = intent.getStringExtra(MainActivity.STATISTICS_ID);

        //statisticsfor="USER DEVICES%KAMYS4748#KAMYS7356#KAMYS4747#";
        logger.addRecordToLog(">> STATISTIC Intent Value : "+statisticsfor);

        /*
           Get all Analysis details
         */

        new StatisticsDashboardActivity.ReadAllAnalysisChannels().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_statistics_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statistics_dashboard_total_water_usages) {
            // Set chart properties for Water usages
            heading.setText("Water Usages");
            information.setText("Last 10 days water usages.");
            currentChartStatus="TW";
            createChart(datTimeDetails,yvaluesList,"TW");
            return true;
        }

        if (id == R.id.statistics_dashboard_total_water_quality) {
            // Set chart properties for Water quality
            heading.setText("Water Quality");
            information.setText("Last 10 days water quality.");
            currentChartStatus="WQ";
            createChart(datTimeDetails,yvaluesList,"WQ");
            return true;
        }

        if (id == R.id.statistics_dashboard_adjust_upper_limit) {
            limitfor="UPPER";
            getLimitsInputs();
            return true;
        }

        if (id == R.id.statistics_dashboard_adjust_lower_limit) {
            limitfor="LOWER";
            getLimitsInputs();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void createChart(String xpoints,ArrayList<String> ypointslists,String updateFor){
        mChart = (LineChart) findViewById(R.id.chart1);
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
        ll1.setLabelPosition(LimitLabelPosition.POS_RIGHT);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(TWLowerlimit, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.POS_RIGHT);
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
        setData(xpoints, ypointslists,updateFor);

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
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
                        createChart(datTimeDetails,yvaluesList,currentChartStatus);
                    } else {
                        TWLowerlimit = Float.valueOf(value);
                        createChart(datTimeDetails,yvaluesList,currentChartStatus);
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

    /*
      ---------------------------------------------
      --  SET DATA -----
      ---------------------------------------------

     */
    private void setData(String xpoints, ArrayList<String> ypointslists,String updateFor) {

        String[] xpointsList=xpoints.split("#");

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

        // Filter ypoint values
        Iterator alldetails = ypointslists.iterator();
        while (alldetails.hasNext()) {
            String eachDetails = alldetails.next().toString();
            // Check for display conditions
            if (eachDetails.split("%")[0].equals(updateFor)) {
                ypointsList.add(eachDetails.split("%")[2]);
                dataSheetNames.add(eachDetails.split("%")[1]);

            }//if end
        }//while end


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
    private class ReadAllAnalysisChannels extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog;
        JSONArray feed = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StatisticsDashboardActivity.this,R.style.AppTheme_Dark_Dialog);
            pDialog.setMessage("Please Wait ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {

            logger.addRecordToLog("-- Getting All Analysis channesl Details --  ");
            logger.addRecordToLog(": Analysis START : "+statisticsfor);

            // check channel id and other details
            if(statisticsfor.equals("ALL STATES")) {
                yvaluesList = getAnalysis(getString(R.string.STATE_ANALYSIS_channel_id), getString(R.string.STATE_ANALYSIS_channel_read_api));
            } else if(statisticsfor.equals("ALL DISTRICTS")){
                yvaluesList = getAnalysis(getString(R.string.DISTRICT_ANALYSIS_channel_id), getString(R.string.DISTRICT_ANALYSIS_channel_read_api));
            } else if(statisticsfor.equals("ALL DEVICES")){
                yvaluesList = getAnalysis(getString(R.string.DEVICE_ANALYSIS_channel_id), getString(R.string.DEVICE_ANALYSIS_channel_read_api));
            } else if(statisticsfor.split("%")[0].equals("USER DEVICES")){
                statisticsfor=statisticsfor.split("%")[1];
                yvaluesList = getAnalysis(getString(R.string.DEVICE_ANALYSIS_channel_id), getString(R.string.DEVICE_ANALYSIS_channel_read_api));

            } else {
                yvaluesList = getAnalysis(getString(R.string.DISTRICT_ANALYSIS_channel_id), getString(R.string.DISTRICT_ANALYSIS_channel_read_api));
            }

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

            heading.setText("Water Usages");
            information.setText("Last 10 days water usages.");
            createChart(datTimeDetails,yvaluesList,"TW");

        }

        /*
         Read MAIN DEVICE Channel
        */
        protected ArrayList<String> getAnalysis(String channelID, String readKey){

            ArrayList<String> allDetails=new ArrayList<String>();

            ArrayList<String> names=new ArrayList<String>();
            ArrayList<String> totalWater=new ArrayList<String>();
            ArrayList<String> waterquality=new ArrayList<String>();

            logger.addRecordToLog("-- Reading ANALYSIS Channels Details ----------");

            JSONParser jParser = new JSONParser();



            // Update url
            String url = "https://api.thingspeak.com/channels/";
            url=url+channelID+"/feeds.json?api_key="+readKey+"&results=10";

            JSONObject json = jParser.getJSONFromUrl(url);
            //String jsonS="{\"channel\":{\"id\":176153,\"name\":\"ANALYSIS_DEVICE_LEVEL\",\"description\":\"Do analysis of  all the devices.\",\"latitude\":\"0.0\",\"longitude\":\"0.0\",\"field1\":\"NAMES\",\"field2\":\"TOTTAL_CURRENT_LEVEL\",\"field3\":\"TOTAL_INLET\",\"field4\":\"TOTAL_OUTLET\",\"field5\":\"AVG_QUALITY\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\",\"created_at\":\"2016-10-28T06:25:24Z\",\"updated_at\":\"2016-11-04T03:30:09Z\",\"last_entry_id\":8},\"feeds\":[{\"created_at\":\"2016-11-03T13:24:42Z\",\"entry_id\":1,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4568#1719#3077#2281#1691#2941#3702#2944#1871#1328#3915#3006#1310#3209#4592#2323#2065#1802\",\"field3\":\"890#854#290#583#452#956#374#686#635#410#938#100#359#807#557#281#114#237\",\"field4\":\"301#771#217#670#785#551#894#191#825#895#147#842#916#137#354#353#603#689\",\"field5\":\"80#27#89#23#30#29#13#96#13#12#74#50#61#69#11#23#68#78\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T13:26:24Z\",\"entry_id\":2,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4568#1719#3077#2281#1691#2941#3702#2944#1871#1328#3915#3006#1310#3209#4592#2323#2065#1802\",\"field3\":\"890#854#290#583#452#956#374#686#635#410#938#100#359#807#557#281#114#237\",\"field4\":\"301#771#217#670#785#551#894#191#825#895#147#842#916#137#354#353#603#689\",\"field5\":\"80#27#89#23#30#29#13#96#13#12#74#50#61#69#11#23#68#78\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T13:50:31Z\",\"entry_id\":3,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4568#1719#3077#2281#1691#2941#3702#2944#1871#1328#3915#3006#1310#3209#4592#2323#2065#1802\",\"field3\":\"890#854#290#583#452#956#374#686#635#410#938#100#359#807#557#281#114#237\",\"field4\":\"301#771#217#670#785#551#894#191#825#895#147#842#916#137#354#353#603#689\",\"field5\":\"80#27#89#23#30#29#13#96#13#12#74#50#61#69#11#23#68#78\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T13:51:12Z\",\"entry_id\":4,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4568#1719#3077#2281#1691#2941#3702#2944#1871#1328#3915#3006#1310#3209#4592#2323#2065#1802\",\"field3\":\"890#854#290#583#452#956#374#686#635#410#938#100#359#807#557#281#114#237\",\"field4\":\"301#771#217#670#785#551#894#191#825#895#147#842#916#137#354#353#603#689\",\"field5\":\"80#27#89#23#30#29#13#96#13#12#74#50#61#69#11#23#68#78\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T15:30:08Z\",\"entry_id\":5,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4497#3684#2598#1872#2804#2232#2290#2938#3443#2835#4102#3514#3565#3634#3925#3128#2921#3305\",\"field3\":\"409#125#356#613#666#338#714#852#240#648#631#951#511#990#277#722#827#161\",\"field4\":\"131#654#330#445#991#639#670#142#763#856#122#416#762#136#533#945#582#908\",\"field5\":\"41#37#80#89#90#44#40#51#59#49#35#50#52#39#54#78#29#74\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T19:30:15Z\",\"entry_id\":6,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"3270#2450#1419#4189#2984#2088#2472#4056#1693#3704#3046#3018#1712#3860#2334#2174#3001#1967\",\"field3\":\"498#152#683#794#192#222#118#960#488#215#441#328#235#787#567#507#287#337\",\"field4\":\"798#601#916#505#686#988#443#511#715#372#911#254#623#571#808#308#369#881\",\"field5\":\"69#88#45#56#32#13#48#36#21#36#10#13#77#89#87#54#58#92\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-03T23:30:15Z\",\"entry_id\":7,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"2932#2452#3565#3292#3896#3294#4641#3855#2746#2648#3099#3783#2480#3368#1955#1144#4028#4622\",\"field3\":\"133#991#832#215#702#555#758#715#129#888#977#478#268#619#509#165#203#632\",\"field4\":\"787#399#448#447#939#209#658#514#477#210#818#249#459#815#183#526#482#160\",\"field5\":\"44#54#72#50#38#36#75#76#36#61#56#91#78#61#40#49#47#18\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"},{\"created_at\":\"2016-11-04T03:30:09Z\",\"entry_id\":8,\"field1\":\"KAMYS4748#KAMYS7356#KAMYS4747#KABNG47372#KABNG3747#KABNG47373#HPHM48473#HPHM5336#HPHM8656#HPSL47362#HPSL5743#HPDL86767#GJKH4835#GJKH98532#GJKH75498#GJVD64346#GJVD25456#GJVD98765\",\"field2\":\"4209#1785#3557#2996#4462#2706#448#3317#1854#2928#2908#4323#1490#1691#2823#1882#3212#4358\",\"field3\":\"386#468#1000#252#933#125#123#608#135#233#465#865#825#966#438#985#521#865\",\"field4\":\"414#963#175#304#280#114#909#526#566#745#827#896#425#892#874#677#390#402\",\"field5\":\"80#24#45#14#63#47#21#72#77#12#80#31#52#79#73#68#19#94\",\"field6\":\"NA\",\"field7\":\"NA\",\"field8\":\"NA\"}]}";


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


                    //Log.i("MSG : ",statisticsfor);
                    if(statisticsfor.equals("ALL STATES")) {
                        // Updating arrayList
                        names.add(field1);
                        totalWater.add(field2);
                        waterquality.add(field4);
                        datTimeDetails=datTimeDetails+entryID+"#";
                    } else if((statisticsfor.equals("ALL DISTRICTS"))||(statisticsfor.equals("ALL DEVICES"))){
                        // Updating arrayList
                        names.add(field1);
                        totalWater.add(field2);
                        waterquality.add(field5);
                        datTimeDetails=datTimeDetails+entryID+"#";
                    } else {

                        // Updating arrayList
                        names.add(field1);
                        totalWater.add(field2);
                        waterquality.add(field5);
                        datTimeDetails=datTimeDetails+entryID+"#";
                    }



                }//end of for

                /*
                   Process information
                 */

                String[] nameslist=names.get(names.size()-1).split("#");

                // loop on each name
                for(int allnames=0;allnames<nameslist.length;allnames++){
                    boolean consider=false;

                    if((statisticsfor.equals("ALL STATES"))||(statisticsfor.equals("ALL DISTRICTS"))||(statisticsfor.equals("ALL DEVICES"))) {
                        consider=true;
                        total_datasets=nameslist.length;
                    } else {
                        String[] selectedUser=statisticsfor.split("#");
                        total_datasets=selectedUser.length;

                        for(int eachN=0;eachN<selectedUser.length;eachN++) {
                             if(selectedUser[eachN].equals(nameslist[allnames])){
                                 consider=true;
                                 break;
                             }
                        }//enf
                    }//eni

                    if(consider) {
                        String twValues = "";
                        String wqValues = "";

                        // loop on all entries
                        for (int eachEntry = 0; eachEntry < names.size(); eachEntry++) {
                            twValues = twValues + totalWater.get(eachEntry).split("#")[allnames] + "#";
                            wqValues = wqValues + waterquality.get(eachEntry).split("#")[allnames] + "#";
                        }

                        allDetails.add("TW%" + nameslist[allnames] + "%" + twValues);
                        allDetails.add("WQ%" + nameslist[allnames] + "%" + wqValues);
                    }

                }//enf



            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return allDetails;
        }


    }



}
