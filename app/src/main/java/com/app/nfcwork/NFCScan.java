package com.app.nfcwork;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class NFCScan extends AppCompatActivity {
    String stringTagID;
    String cipher;

    TextView postStatusText;
    TextView dateText;
    TextView timeText;
    TextView plusHours;
    Button refreshButton;
    ProgressBar loadingCircle;
    ImageButton calendarButton;
    LinearLayout messageLinearLayout;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcscan);

        messageLinearLayout = findViewById(R.id.nfc_scan_status_parent);
        postStatusText = findViewById(R.id.nfc_scan_status_text);
        dateText = findViewById(R.id.nfc_scan_date_text);
        timeText = findViewById(R.id.nfc_scan_time_text);
        plusHours = findViewById(R.id.nfc_scan_plus_hours);
        refreshButton = findViewById(R.id.nfc_scan_refresh_button);
        loadingCircle = findViewById(R.id.nfc_scan_progress_bar);
        constraintLayout = findViewById(R.id.nfc_scan_constraint_layout);
        calendarButton = findViewById(R.id.nfc_scan_calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName cn = new ComponentName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
                intent.setComponent(cn);
                startActivity(intent);
            }
        });
        // secretKey = "AQqBRMqQ&D8?wZ";

        getTagData();

        Log.d("SCAN", cipher);
        Log.d("SCAN", stringTagID);

        try {
            sendRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest() throws JSONException {
        final String userId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String URL = "http://sailsation.eu/nfc_work/send_nfc_event";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        messageLinearLayout.setVisibility(View.VISIBLE);
                        loadingCircle.setVisibility(View.GONE);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            // Log.d("SCAN", jsonResponse.toString());
                            if (jsonResponse.getString("response").equals("SUCCESS")) {
                                String type = "";

                                if (jsonResponse.getString("type").equals("arrive")) {
                                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                                    constraintLayout.setBackground(getResources().getDrawable(R.drawable.background_transition_arrive));
                                    calendarButton.setVisibility(View.VISIBLE);
                                    type = getResources().getString(R.string.arrive);
                                }
                                else {
                                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorSecondaryDark));
                                    constraintLayout.setBackground(getResources().getDrawable(R.drawable.background_transition_leave));
                                    calendarButton.setVisibility(View.GONE);
                                    plusHours.setVisibility(View.VISIBLE);
                                    plusHours.setText(jsonResponse.getString("timediff"));
                                    type = getResources().getString(R.string.leave);
                                }
                                postStatusText.setText(type);

                                String serverTimestamp = jsonResponse.getString("timestamp");
                                serverTimestamp = serverTimestamp.split("\\.")[0];
                                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date parsedDate = date.parse(serverTimestamp);

                                String firstDateRow = (String) DateFormat.format("EEEE, dd. MM.", parsedDate);
                                String secondDateRow = (String) DateFormat.format("HH:mm", parsedDate);

                                dateText.setText(firstDateRow);
                                timeText.setText(secondDateRow);

                                refreshButton.setVisibility(View.GONE);
                            }
                            else {
                                postStatusText.setText("404, but not really:");
                                dateText.setText(response.split(": ")[1]);
                                refreshButton.setVisibility(View.VISIBLE);
                                refreshButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            sendRequest();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                            TransitionDrawable transition = (TransitionDrawable) constraintLayout.getBackground();
                            transition.startTransition(100);

                        } catch (JSONException | ParseException e) {}


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        messageLinearLayout.setVisibility(View.VISIBLE);
                        loadingCircle.setVisibility(View.GONE);

                        Log.e("ERR", error.toString());

                        postStatusText.setText("404, but not really:");
                        dateText.setText(error.toString());
                        refreshButton.setVisibility(View.VISIBLE);
                        refreshButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    sendRequest();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            JSONObject jsonObject = new JSONObject("{ \"user_id\": \"" + userId + "\", \"tag_id\": \"" + stringTagID + "\", \"cipher\": \"" + cipher + "\" }");
                            return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                        } catch (Exception e) { }

                        return null;
                    }
                };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getTagData() {
        Intent intent = getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag NFCTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] tagID = NFCTag.getId();
            stringTagID = byteToHexString(tagID);

            String uriString = intent.getDataString();
            String[] uriSplitString = uriString.split("/");
            cipher = uriSplitString[uriSplitString.length - 1];
        }
    }

    private String byteToHexString(byte[] input) {
        String output = "";
        for (int i = 0; i < input.length; i++) {
            String x = Integer.toHexString(((int) input[i]) & 0xff);
            if (x.length() == 1)
                x = "0" + x;
            output += x;
        }
        return output;
    }
}
