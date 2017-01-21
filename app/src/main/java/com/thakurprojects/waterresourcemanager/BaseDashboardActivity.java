package com.thakurprojects.waterresourcemanager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;

public class BaseDashboardActivity extends AppCompatActivity implements
        OnChartValueSelectedListener {

    private PieChart water_level_piechart;
    private TextView heading;
    private TextView information;

    private String dashboardDetails="";
    private String userDetails="";

    private String NAMES="";
    private String TOTAL_CURRENT_LEVEL="";
    private String TOTAL_INLETS="";
    private String TOTAL_USAGES="";
    private String AVG_QUALITY="";

    private String statistic_option="STATE";

    // logger object
    Logger logger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For FULL Screen
        /*
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                */
        setContentView(R.layout.activity_base_dashboard);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        heading=(TextView) findViewById(R.id.headingTextView);
        information=(TextView) findViewById(R.id.inforTextView);

        Intent intent = getIntent();
        dashboardDetails = intent.getStringExtra(MainActivity.DASHBOARD_ID);

        logger.addRecordToLog("----- Dashboard Activity ------------------ ");
        logger.addRecordToLog("Dashboard Details : "+dashboardDetails);

        userDetails=dashboardDetails.split("%")[1];
        /*
            Split information according to the user's..
         */
        if(userDetails.equals("ALL STATES")){

            statistic_option="ALL STATES";

            logger.addRecordToLog("Show COUNTRY Details ... ");
            NAMES=MainActivity.allStatesAnalysis.split("%")[0];
            TOTAL_CURRENT_LEVEL=MainActivity.allStatesAnalysis.split("%")[1];
            TOTAL_INLETS="";
            TOTAL_USAGES=MainActivity.allStatesAnalysis.split("%")[2];
            AVG_QUALITY=MainActivity.allStatesAnalysis.split("%")[3];

        } /*
             SHOW USER DISTRICTS Details..
          */
        else if(userDetails.equals("USER DISTRICT")){

            logger.addRecordToLog("Show User Districts Details ... ");
            NAMES="";
            TOTAL_CURRENT_LEVEL="";
            TOTAL_INLETS="";
            TOTAL_USAGES="";
            AVG_QUALITY="";

            String filteredUsers="";

            String[] eachNames=MainActivity.allDistrictAnalysis.split("%")[0].split("#");
            String[] totalCurrentLevel=MainActivity.allDistrictAnalysis.split("%")[1].split("#");
            String[] totalInlet=MainActivity.allDistrictAnalysis.split("%")[2].split("#");
            String[] totalUsages=MainActivity.allDistrictAnalysis.split("%")[3].split("#");
            String[] waterQuality=MainActivity.allDistrictAnalysis.split("%")[4].split("#");

            logger.addRecordToLog("All Districts : "+eachNames.toString());
            // Compare Names with User
            for(int i=0;i<eachNames.length;i++){
                logger.addRecordToLog(MainActivity.allUsersDetails+" = "+eachNames[i].toUpperCase());
                if(MainActivity.allUsersDetails.contains(eachNames[i].toUpperCase())){
                    logger.addRecordToLog("DISTRICT FOUND : "+eachNames[i]);
                    filteredUsers=filteredUsers+eachNames[i]+"#";
                    NAMES=NAMES+eachNames[i]+"#";
                    TOTAL_CURRENT_LEVEL=TOTAL_CURRENT_LEVEL+totalCurrentLevel[i]+"#";
                    TOTAL_INLETS=TOTAL_INLETS+totalInlet[i]+"#";
                    TOTAL_USAGES=TOTAL_USAGES+totalUsages[i]+"#";
                    AVG_QUALITY=AVG_QUALITY+waterQuality[i]+"#";
                }
            }//end of for
            statistic_option=filteredUsers;

        }/*
            For ALL DISTRICTS
         */
        else if (userDetails.equals("ALL DISTRICTS")){

            statistic_option="ALL DISTRICTS";

            logger.addRecordToLog("Show ALL DISTRICTS Details ... ");
            NAMES=MainActivity.allDistrictAnalysis.split("%")[0];
            TOTAL_CURRENT_LEVEL=MainActivity.allDistrictAnalysis.split("%")[1];
            TOTAL_INLETS=MainActivity.allDistrictAnalysis.split("%")[2];
            TOTAL_USAGES=MainActivity.allDistrictAnalysis.split("%")[3];
            AVG_QUALITY=MainActivity.allDistrictAnalysis.split("%")[4];
        } /*
            For ALL DEVICES
         */
        else if (userDetails.equals("ALL DEVICES")){

            statistic_option="ALL DEVICES";

            logger.addRecordToLog("Show ALL DEVICES Details ... ");
            NAMES=MainActivity.allDeviceAnalysis.split("%")[0];
            TOTAL_CURRENT_LEVEL=MainActivity.allDeviceAnalysis.split("%")[1];
            TOTAL_INLETS=MainActivity.allDeviceAnalysis.split("%")[2];
            TOTAL_USAGES=MainActivity.allDeviceAnalysis.split("%")[3];
            AVG_QUALITY=MainActivity.allDeviceAnalysis.split("%")[4];
        } /*
             SHOW USER DEVICES Details..
          */
        else if(userDetails.equals("USER DEVICES")){

            logger.addRecordToLog("Show User DEVICES Details ... ");
            NAMES="";
            TOTAL_CURRENT_LEVEL="";
            TOTAL_INLETS="";
            TOTAL_USAGES="";
            AVG_QUALITY="";

            String filteredUsers="";

            String[] eachNames=MainActivity.allDeviceAnalysis.split("%")[0].split("#");
            String[] totalCurrentLevel=MainActivity.allDeviceAnalysis.split("%")[1].split("#");
            String[] totalInlet=MainActivity.allDeviceAnalysis.split("%")[2].split("#");
            String[] totalUsages=MainActivity.allDeviceAnalysis.split("%")[3].split("#");
            String[] waterQuality=MainActivity.allDeviceAnalysis.split("%")[4].split("#");

            logger.addRecordToLog("All DEVICES : "+eachNames.toString());
            // Compare Names with User
            for(int i=0;i<eachNames.length;i++){
                logger.addRecordToLog(MainActivity.allUsersDetails+" = "+eachNames[i].toUpperCase());
                if(MainActivity.allUsersDetails.contains(eachNames[i].toUpperCase())){
                    logger.addRecordToLog("DEVICES FOUND : "+eachNames[i]);
                    filteredUsers=filteredUsers+eachNames[i]+"#";
                    NAMES=NAMES+eachNames[i]+"#";
                    TOTAL_CURRENT_LEVEL=TOTAL_CURRENT_LEVEL+totalCurrentLevel[i]+"#";
                    TOTAL_INLETS=TOTAL_INLETS+totalInlet[i]+"#";
                    TOTAL_USAGES=TOTAL_USAGES+totalUsages[i]+"#";
                    AVG_QUALITY=AVG_QUALITY+waterQuality[i]+"#";
                }
            }//end of for
            statistic_option="USER DEVICES%"+filteredUsers;

        }//eni

        logger.addRecordToLog(" >>> \n"+NAMES+"  -  "+TOTAL_CURRENT_LEVEL+"  -  "+TOTAL_INLETS+"  -  "+TOTAL_USAGES+"  -  "+AVG_QUALITY);



        // Set chart properties for Water level
        heading.setText("Total Water Level");
        information.setText("Some Information relted to it.");
        water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
        createPieChart(dashboardDetails.split("%")[0]+"\nWater Total",dashboardDetails.split("%")[1],NAMES,TOTAL_CURRENT_LEVEL,water_level_piechart);



    }//onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_base_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.base_dashboard_total_water) {
            // Set chart properties for Water level
            heading.setText("Total Water Level");
            information.setText("Some Information relted to it.");
            water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
            createPieChart(dashboardDetails.split("%")[0]+"\nWater Total",dashboardDetails.split("%")[1],NAMES,TOTAL_CURRENT_LEVEL,water_level_piechart);
            return true;
        }

        if (id == R.id.base_dashboard_water_quality) {

            // Set chart properties for Water level
            heading.setText("Water Quality Level");
            information.setText("Some Information relted to it.");
            water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
            createPieChart(dashboardDetails.split("%")[0]+"\nWater Quality",dashboardDetails.split("%")[1],NAMES,AVG_QUALITY,water_level_piechart);
            return true;
        }

        if (id == R.id.base_dashboard_water_resources) {

            // Set chart properties for Water level
            heading.setText("Total Water Resources");
            information.setText("Some Information relted to it.");
            water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
            createPieChart(dashboardDetails.split("%")[0]+"\nWater Resources",dashboardDetails.split("%")[1],NAMES,TOTAL_CURRENT_LEVEL,water_level_piechart);
            return true;
        }

        if (id == R.id.base_dashboard_water_storage) {

            // Set chart properties for Water level
            heading.setText("Total Water Storage");
            information.setText("Some Information relted to it.");
            water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
            createPieChart(dashboardDetails.split("%")[0]+"\nWater Storage",dashboardDetails.split("%")[1],NAMES,TOTAL_CURRENT_LEVEL,water_level_piechart);
            return true;
        }

        if (id == R.id.base_dashboard_water_used) {

            // Set chart properties for Water level
            heading.setText("Total Water Usages");
            information.setText("Some Information relted to it.");
            water_level_piechart=(PieChart) findViewById(R.id.water_level_pie_chart);
            createPieChart(dashboardDetails.split("%")[0]+"\nWater Usages",dashboardDetails.split("%")[1],NAMES,TOTAL_USAGES,water_level_piechart);
            return true;
        }

        if (id == R.id.base_dashboard_statistics) {

            Intent addIntent = new Intent(this, StatisticsDashboardActivity.class);
            addIntent.putExtra(MainActivity.STATISTICS_ID,statistic_option);
            startActivity(addIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
        //String details ="Value: " + e.getVal() + ", xIndex: " + e.getXIndex()+ ", DataSet index: " + dataSetIndex;
        String details ="Value: " + e.getVal();
        Toast.makeText(getApplicationContext(), details, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected() {

    }


    // Create Pie Chart
    private void createPieChart(String centerText,String contentName,String contentLine,String contentValues,PieChart mChart){


           //Pie Chart details


        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        //mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        mChart.setCenterText(centerText);

        // Update Pie Chart values
        updatePieValue(contentLine,contentName,contentValues,mChart);

        //setData(contentList.length, 100);

        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);


    }


    // Update piechart value
    private void updatePieValue(String contentLine,String contentName,String contentValues,PieChart mChart){

         String[] contentList=contentLine.split("#");
        String[] contentValueList=contentValues.split("#");

        int items=contentList.length;

        // Each Iten Value
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < items; i++){
            if (!contentValueList[i].isEmpty()) {
                float value = Float.parseFloat(contentValueList[i]);
                //Log.i("MSG ",String.valueOf(value));
                yVals1.add(new Entry((Float.parseFloat(contentValueList[i])), i));
            }
        }


        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < items; i++) {
            if (!contentList[i].isEmpty()) {
                xVals.add(contentList[i % contentList.length]);
            }
        }

        PieDataSet dataSet = new PieDataSet(yVals1, contentName);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

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

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        //data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

}
