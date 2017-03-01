package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import timber.log.Timber;

/**
 * Fetch Symbols AsyncTask goes in getSymbols() method
 */
class SymbolSyncJob {
    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final String ACTION_DATA_UPDATED_EXTRA_KEY = "stockUpdatedKey";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;

    private SymbolSyncJob() {
    }

    static void getSymbols(Context context) {
        Timber.d("Running get Symbols Job");

    }

    public static synchronized void initialize(final Context context) {
        schedulePeriodic(context);
    }

    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, SymbolJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
              .setPeriodic(PERIOD)
              .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }

}
