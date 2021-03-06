package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return new HashSet<>(prefs.getStringSet(stocksKey, new HashSet<String>()));
    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static Set<String> getStocksCopy(Context context) {
        String initializedKey = context.getString(R.string.pref_stocks_copy_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initializedKey, false);
        if (!initialized) {
            throw new RuntimeException("Stocks Copy Not Initialized");
        }
        String stocksKey = context.getString(R.string.pref_stocks_copy_key);
        return new HashSet<>(prefs.getStringSet(stocksKey, new HashSet<String>()));
    }

    public static void addStockUncommitted(Context context, String symbol) {
        Set<String> stocksCopy;
        String stockKey = context.getString(R.string.pref_stocks_copy_key);
        String initializedKey = context.getString(R.string.pref_stocks_copy_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initializedKey, false);
        if (initialized) {
            stocksCopy = getStocksCopy(context);
        } else {
            stocksCopy = getStocks(context);
        }
        stocksCopy.add(symbol);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(stockKey, stocksCopy);
        editor.putBoolean(initializedKey, true);
        editor.apply();
    }

    public static boolean isInitializedCopy(Context context) {
        String initializedKey = context.getString(R.string.pref_stocks_copy_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(initializedKey, false);
    }

    public static void commitStocksCopy(Context context) {
        Set<String> stocksCopy = getStocksCopy(context);
        String stocksKey = context.getString(R.string.pref_stocks_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(stocksKey, stocksCopy);
        editor.apply();
    }

    public static void deInitializeCopy(Context context) {
        String initializedKey = context.getString(R.string.pref_stocks_copy_initialized_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(initializedKey, false);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

    public static String getLastWidgetUpdate(Context context) {
        String updatedLabel = context.getResources().getString(R.string.updated);
        String initKey = context.getResources().getString(R.string.widget_update_init_pref_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean initialized = prefs.getBoolean(initKey, false);
        if (initialized) {
            return updatedLabel + getFormattedLastUpdate();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initKey, true);
            editor.apply();
            return context.getResources().getString(R.string.never);
        }
    }

    private static String getFormattedLastUpdate() {
        Calendar calendar = new GregorianCalendar();
        Long curTime = calendar.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d h:mma", Locale.getDefault());
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[] { "a", "p" });
        sdf.setDateFormatSymbols(symbols);
        return sdf.format(curTime);

    }
}
