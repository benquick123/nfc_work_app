package com.app.nfcwork;

import android.content.Context;
import android.text.format.DateFormat;
import android.icu.text.DateFormatSymbols;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TabFragment extends Fragment {
    ViewGroup rootView;
    String month;
    String monthNumber;
    String yearNumber;
    String currentMonthNumber;
    String userId;
    TextView errorMessageText;
    Button refreshButton;
    TextView totalHoursText;
    TextView lastArrivalText;
    TextView totalText;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    Context mContext;
    List<WorkingEntry> mDataset;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public TabFragment(int position, int num_tabs, Context context) {
        mContext = context;

        Calendar date = Calendar.getInstance();
        currentMonthNumber = "" + (date.get(Calendar.MONTH) + 1);
        date.add(Calendar.MONTH, -num_tabs + position + 1);

        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();

        month = months[date.get(Calendar.MONTH)];
        monthNumber = "" + (date.get(Calendar.MONTH) + 1);
        yearNumber = "" + date.get(Calendar.YEAR);

        userId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.tab_fragment, container, false);

        progressBar = rootView.findViewById(R.id.main_view_progress_bar);

        errorMessageText = rootView.findViewById(R.id.main_recycler_error_message);
        refreshButton = rootView.findViewById(R.id.main_recycler_refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                sendRequest();
            }
        });

        totalHoursText = rootView.findViewById(R.id.main_recycler_total_hours);
        lastArrivalText = rootView.findViewById(R.id.main_recycler_last_arrival);
        totalText = rootView.findViewById(R.id.main_recycler_total_text);

        recyclerView = rootView.findViewById(R.id.main_recycler_view);

        swipeRefreshLayout = rootView.findViewById(R.id.main_view_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRequest();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        sendRequest();
        return rootView;
    }

    public void sendRequest() {
        mDataset = new ArrayList<>();
        final MainActivity activity = (MainActivity)mContext;

        recyclerView.setLayoutManager(new ResponsiveLinearLayoutManager(getActivity(), recyclerView, totalText, totalHoursText, lastArrivalText, currentMonthNumber.equals(monthNumber)));

        String JSON_URL = "http://sailsation.eu/nfc_work/manage_hours";
        final String params = "user_id=" + userId + "&month=" + monthNumber + "&year=" + yearNumber;
        String fullURL = JSON_URL + "?" + params;
        Log.d("TAB", fullURL);

        StringRequest stringRequest = new StringRequest(fullURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        totalHoursText.setVisibility(View.VISIBLE);
                        totalText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        refreshButton.setVisibility(View.GONE);
                        errorMessageText.setVisibility(View.GONE);

                        if (currentMonthNumber.equals(monthNumber)) {
                            totalText.setText(getResources().getString(R.string.total_this_month));
                            lastArrivalText.setVisibility(View.VISIBLE);
                        }
                        else {
                            totalText.setText(getResources().getString(R.string.total_in) + " " + month);
                            lastArrivalText.setVisibility(View.GONE);
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            WorkingEntry entry;
                            int totalWorktimeSeconds = 0;

                            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                                JSONObject jsonEntry = jsonArray.getJSONObject(i);
                                // Log.d("TAB", jsonEntry.toString());
                                if (i == jsonArray.length() - 1) {
                                    if (jsonEntry.getString("timediff").equals("NA")) {
                                        Date arrival = stringToDate.parse(jsonEntry.getString("arrive_timestamp"));
                                        lastArrivalText.setText(getResources().getString(R.string.last_arrival) + " " + DateFormat.format("HH:mm", arrival));
                                        lastArrivalText.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                        totalHoursText.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                                    }
                                    else {
                                        lastArrivalText.setText(getResources().getString(R.string.checkout));
                                        lastArrivalText.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
                                        if (currentMonthNumber.equals(monthNumber))
                                            totalHoursText.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
                                    }
                                }
                                if (!jsonEntry.getString("timediff").equals("NA")) {
                                    Date arrival = stringToDate.parse(jsonEntry.getString("arrive_timestamp"));
                                    Date departure = stringToDate.parse(jsonEntry.getString("leave_timestamp"));
                                    entry = new WorkingEntry();

                                    entry.setDate((String) DateFormat.format("EEE, dd. MM.", arrival));
                                    entry.setArriveTime((String) DateFormat.format("HH:mm", arrival));
                                    entry.setDepartureTime((String) DateFormat.format("HH:mm", departure));
                                    entry.setTotalTime(jsonEntry.getString("timediff"));
                                    entry.setIsSunday(DateFormat.format("u", arrival).equals("7"));

                                    String[] parsedTimeDiff = jsonEntry.getString("timediff").split(":");
                                    int worktimeInMinutes = (Integer.parseInt(parsedTimeDiff[0]) * 60) + (Integer.parseInt(parsedTimeDiff[1]));
                                    entry.setWorktimeInMinutes(worktimeInMinutes);
                                    totalWorktimeSeconds += worktimeInMinutes;

                                    mDataset.add(entry);
                                }
                            }

                            int hours = totalWorktimeSeconds / 60;
                            int minutes = totalWorktimeSeconds % 60;

                            if (mDataset.size() > 0) {
                                String formattedWorkTime = (hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
                                totalHoursText.setText(formattedWorkTime);
                            } else {
                                totalHoursText.setText("00:00");
                                lastArrivalText.setVisibility(View.GONE);
                            }

                        } catch (JSONException | ParseException e) {
                            Log.e("JSONERROR", e.toString());

                            refreshButton.setVisibility(View.VISIBLE);
                            errorMessageText.setVisibility(View.VISIBLE);
                            try {
                                errorMessageText.setText(response.split(": ")[1]);
                            } catch (Exception e1) {
                                errorMessageText.setText(getResources().getString(R.string.na));
                                Log.e("ERROR", e1.toString());
                            }

                            progressBar.setVisibility(View.GONE);
                            lastArrivalText.setVisibility(View.GONE);
                            totalHoursText.setVisibility(View.GONE);
                            totalText.setVisibility(View.GONE);
                        }

                        WorkTimeAdapter adapter = new WorkTimeAdapter(activity, mDataset);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshButton.setVisibility(View.VISIBLE);
                        errorMessageText.setVisibility(View.VISIBLE);
                        errorMessageText.setText(error.toString());

                        progressBar.setVisibility(View.GONE);
                        lastArrivalText.setVisibility(View.GONE);
                        totalHoursText.setVisibility(View.GONE);
                        totalText.setVisibility(View.GONE);

                        WorkTimeAdapter adapter = new WorkTimeAdapter(activity, mDataset);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }
}
