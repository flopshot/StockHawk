package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity
      implements LoaderManager.LoaderCallbacks<Cursor> {

    private LineChart chart;
    private String mSymbol;
    private static final int HISTORY_LOADER = 101;
    String[] mHistoryItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intentFromMain = getIntent();
        mSymbol = intentFromMain.getStringExtra(Contract.Quote.COLUMN_SYMBOL);
        chart = (LineChart) findViewById(R.id.historical_trend);
        getSupportLoaderManager().initLoader(HISTORY_LOADER, null, this);
        String activityTitle = mSymbol + getResources().getString(R.string.history_title);
        setTitle(activityTitle);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
              Contract.Quote.URI,
              new String[]{Contract.Quote.COLUMN_HISTORY},
              Contract.Quote.COLUMN_SYMBOL+"=?",
              new String[]{mSymbol},
              null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            String legendDesc = getResources().getString(R.string.legend);

            String history = cursor.getString(0);
            mHistoryItems = history.split("\\s*,\\s*");
            ArrayList<Entry> entries = getDataSet(mHistoryItems);
            LineDataSet dataSet = new LineDataSet(entries, legendDesc);

            LineData chartData = new LineData(dataSet);
            chart.setData(chartData);
            chart.getDescription().setEnabled(false);
            chart.getAxisLeft().setTextColor(Color.WHITE);
            chart.getAxisRight().setTextColor(Color.WHITE);
            chart.getLegend().setTextColor(Color.WHITE);
            XAxis xAxis = chart.getXAxis();
            xAxis.setGranularity(1f);
            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    String[] labels = getLabels(mHistoryItems);
                    return labels[(int) value];
                }
            };
            xAxis.setValueFormatter(formatter);
            xAxis.setTextColor(Color.WHITE);
            chart.invalidate();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<Entry> getDataSet(String[] data) {
        int dataSize = data.length;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i=1; i<dataSize; i+=2) {
            entries.add(new Entry((i - 1) / 2, Float.valueOf(data[dataSize-i])));
        }
        return entries;
    }

    private String[] getLabels(String[] data) {
        int dataSize = data.length;
        String[] labels = new String[dataSize/2];
        for (int i=0; i<dataSize; i+=2) {
            labels[(i-1)/2] = getFormattedDate(data[dataSize-2-i]);
        }
        return labels;
    }

    private String getFormattedDate(String unixtime) {
        Date date = new Date(Long.valueOf(unixtime));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d''yy", Locale.US);
        return sdf.format(date);
    }
}
