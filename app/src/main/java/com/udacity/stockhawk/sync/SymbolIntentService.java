package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import timber.log.Timber;

/**
 * Service for AsyncTask of Symbol Fetch Job
 */

public class SymbolIntentService extends IntentService {
    public SymbolIntentService() {
        super(SymbolIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        SymbolSyncJob.getSymbols(getApplicationContext());
    }
}
