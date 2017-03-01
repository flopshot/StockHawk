package com.udacity.stockhawk.sync;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVReader;
import timber.log.Timber;

/**
 * This asyncTAsk class goes to the NASDAQ server and fetches all stock symbols for all three major
 * exchanges
 */

public class FetchStockSymbolsTask extends AsyncTask<Void, Void, Void> {
    private static final String SCHEME = "http";
    private static final String AUTHORITY = "www.nasdaq.com";
    private static final String[] PATH = {"screening","companies-by-name.aspx"};
    private static final String[] PARAM_VALUES = {"0", "all","download"};
    private static final String[] PARAM = {"letter", "exchange","render"};

    // private Context mContext;

    public FetchStockSymbolsTask(//Context c
    ) {
        //this.mContext = c;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        List<String[]> symbolsList = new ArrayList<>();
        Timber.w("Starting Symbols Task");
        // Build URI "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=all&render=download"
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
               .authority(AUTHORITY);
        for (String path : PATH) {
            builder.appendPath(path);
        }
        for (int i=0; i<PARAM.length; i++) {
            builder.appendQueryParameter(PARAM[i], PARAM_VALUES[i]);
        }

        String requestUrl = builder.build().toString();
        Timber.w("Query Built: " + requestUrl);
        try {
            URL url = new URL(requestUrl);
            // Create the request to Server, and open the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Timber.w("Connection Made");
            // Read the input stream into a String
            InputStream csvStream = urlConnection.getInputStream();
            if (csvStream == null) {
                // Nothing to do.
                return null;
            }
            Timber.w("Stream Not Null");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            // Skip header
            csvReader.readNext();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                symbolsList.add(line);
            }

            //TODO: Remove after test
            int j=0;
            for (String[] s:symbolsList) {
                j++;
                Timber.w(s[0]+", " +s[1]+", "+ s[2]+", "+ s[3]+", "+ s[4]+", "+ s[5]+", "+ s[6]+", "+ s[7]);
                if (j>10) {
                    break;
                }
            }

        } catch (IOException e){
            Timber.w("IOException, Logic Not Reached");
            return null;
        }

        return null;
    }
}
