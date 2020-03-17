package com.app.nfcwork;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class WorkTimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    List<WorkingEntry> workingEntries;

    public WorkTimeAdapter(Activity activity, List<WorkingEntry> workingEntries) {
        this.activity = activity;
        this.workingEntries = workingEntries;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        /* if (viewType == WorkingEntry.NORMAL_TYPE) {
            view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.main_recycler_normal_entry, parent, false);
            return new NormalViewHolder(view);
        }
        else if (viewType == WorkingEntry.TOP_TYPE) {
            view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.main_recycler_title_entry, parent, false);
            return new TopViewHolder(view);
        }
        else {
            view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.main_recycler_error_entry, parent, false);
            return new ErrorViewHolder(view);
        }*/
        view = LayoutInflater.from(activity.getBaseContext()).inflate(R.layout.main_recycler_normal_entry, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return workingEntries.get(position).getEntryType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // if (getItemViewType(position) == WorkingEntry.NORMAL_TYPE)
        ((NormalViewHolder) holder).setNormalEntryDetails(workingEntries.get(position));
        /* else if (getItemViewType(position) == WorkingEntry.TOP_TYPE)
            ((TopViewHolder) holder).setTopEntryDetails(workingEntries.get(position));
        else
            ((ErrorViewHolder) holder).setErrorEntryDetails(workingEntries.get(position));*/
    }

    @Override
    public int getItemCount() {
        return workingEntries.size();
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        private TextView arrivalText;
        private TextView departureText;
        private TextView totalTimeText;
        private TextView dateText;
        private View delimiterView;

        NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            arrivalText = itemView.findViewById(R.id.main_recycler_arrival_timestamp);
            departureText = itemView.findViewById(R.id.main_recycler_departure_timestamp);
            totalTimeText = itemView.findViewById(R.id.main_recycler_total_time);
            dateText = itemView.findViewById(R.id.main_recycler_date);
            delimiterView = itemView.findViewById(R.id.normal_entry_delimiter);
        }

        void setNormalEntryDetails(WorkingEntry workingEntry) {
            arrivalText.setText(workingEntry.getArriveTime());
            departureText.setText(workingEntry.getDepartureTime());
            totalTimeText.setText(workingEntry.getTotalTime());
            dateText.setText(workingEntry.getDate());
            if (workingEntry.getIsSunday()) {
                // delimiterView.setBackground(R.color.lightDelimiterColor);
            }
        }
    }
    /*
    public class ErrorViewHolder extends RecyclerView.ViewHolder {


        public ErrorViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setErrorEntryDetails(WorkingEntry workingEntry) {

        }
    }

    public class TopViewHolder extends RecyclerView.ViewHolder {

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setTopEntryDetails(WorkingEntry workingEntry) {
            totalHoursText.setText(workingEntry.getTotalTime());
            if (TextUtils.isEmpty(workingEntry.getLastArriveTime())) {
                lastArrivalText.setVisibility(View.GONE);
            }
            else {
                lastArrivalText.setVisibility(View.VISIBLE);
                lastArrivalText.setText("Last arrival: " + workingEntry.getLastArriveTime());
            }
        }
    }*/
}
