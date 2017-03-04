package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * RemoteViewService for Stock Widget
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;
            private DecimalFormat dollarFormat;
            private DecimalFormat dollarFormatWithPlus;
            private DecimalFormat percentageFormat;



            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(
                          Contract.Quote.URI,
                          Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                          null,
                          null,
                          Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                      data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_list_item);


                int symbol = (R.id.symbol);
                int price = (R.id.price);
                int change = (R.id.change);

                String sym = data.getString(Contract.Quote.POSITION_SYMBOL);
                Float pri = data.getFloat(Contract.Quote.POSITION_PRICE);
                Float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
                Float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                dollarFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus. setPositivePrefix("+$");
                percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                views.setTextViewText(symbol, sym);
                views.setTextViewText(price, dollarFormat.format(pri));

                if (rawAbsoluteChange > 0) {
                    views.setInt(change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                String changes = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                if (PrefUtils.getDisplayMode(getApplicationContext())
                      .equals(getString(R.string.pref_display_mode_absolute_key))) {
                    views.setTextViewText(change, changes);
                } else {
                    views.setTextViewText(change, percentage);
                }

                // Next, set a fill-intent, which will be used to fill in the pending intent template
                // that is set on the collection view in StackWidgetProvider.
                Bundle extras = new Bundle();
                extras.putString(Contract.Quote.COLUMN_SYMBOL, sym);
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                // Make it possible to distinguish the individual on-click
                // action of a given item
                views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(Contract.Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
