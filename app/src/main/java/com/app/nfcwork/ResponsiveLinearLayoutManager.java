package com.app.nfcwork;

import android.content.Context;
import android.icu.text.DateFormatSymbols;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class ResponsiveLinearLayoutManager extends LinearLayoutManager {
    RecyclerView recyclerView;
    WorkTimeAdapter recyclerViewAdapter;
    TextView totalText;
    TextView totalHoursText;
    TextView lastArrivalText;
    int defaultArrivalTextHeight;
    boolean isCurrentMonth;
    String currentMonth;

    public ResponsiveLinearLayoutManager(Context context, RecyclerView recyclerView1, TextView totalText1, TextView totalHoursText1, TextView lastArrivalText1, boolean isCurrentMonth1) {
        super(context);
        this.recyclerView = recyclerView1;
        this.totalText = totalText1;
        this.totalHoursText = totalHoursText1;
        this.lastArrivalText = lastArrivalText1;
        this.defaultArrivalTextHeight = -1;
        this.isCurrentMonth = isCurrentMonth1;

        Calendar date = Calendar.getInstance();
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();

        this.currentMonth = months[date.get(Calendar.MONTH) - 1];
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (defaultArrivalTextHeight == -1) {
            defaultArrivalTextHeight = lastArrivalText.getHeight();
            recyclerViewAdapter = (WorkTimeAdapter) recyclerView.getAdapter();
        }

        if (recyclerViewAdapter.workingEntries.size() > 0) {
            // handle lastArrive text.
            int arrivalTextHeight = lastArrivalText.getHeight();
            if (arrivalTextHeight - dy >= 0 && arrivalTextHeight - dy <= defaultArrivalTextHeight) {
                lastArrivalText.setHeight(arrivalTextHeight - dy);
            }
            else if (arrivalTextHeight - dy < 0){
                lastArrivalText.setHeight(0);
            }
            else if (arrivalTextHeight - dy > defaultArrivalTextHeight) {
                lastArrivalText.setHeight(defaultArrivalTextHeight);
            }

            // handle proper summation of worktimes.
            int firstPosition = findFirstVisibleItemPosition();
            int totalTimeInMinutes = 0;
            for (int i = firstPosition; i < recyclerViewAdapter.workingEntries.size(); i++) {
                totalTimeInMinutes += recyclerViewAdapter.workingEntries.get(i).getWorktimeInMinutes();
            }

            if (firstPosition == 0) {
                if (isCurrentMonth)
                    totalText.setText(R.string.total_this_month);
                else {
                    totalText.setText(R.string.total_in);
                    // this is ugly.
                    String titleText = (String) totalText.getText();
                    titleText += " " + currentMonth;
                    totalText.setText(titleText);
                }
            }
            else {
                totalText.setText(R.string.total_until);
                // this is ugly.
                String titleText = (String) totalText.getText();
                titleText += " " + recyclerViewAdapter.workingEntries.get(firstPosition).getDate().split(", ")[1];
                totalText.setText(titleText);
            }

            int hours = totalTimeInMinutes / 60;
            int minutes = totalTimeInMinutes % 60;
            String formattedWorkTime = (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
            totalHoursText.setText(formattedWorkTime);
        }

        return super.scrollVerticallyBy(dy, recycler, state);
    }
}
