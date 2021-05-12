package com.example.seeslot;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String date, age, availability, centername;
    TextView textView, counttext,note,lastcheck;
    JSONArray jarray, sesn;
    EditText pin;
    Button button, button2, addcount, subcount;

    Handler handler = new Handler();
    Runnable runnable;
    int count, delay = 15000;
    ToggleButton tgl18, tgl45;
    MediaPlayer mp;
    Context mContext;


    public void datafetch(Editable pin) {


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode=" + pin + "&date=" + currentDateandTime, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView.setText("\n");
                    lastcheck.setText("Slot Details: Last Checked On: [" + new SimpleDateFormat("hh:mm:ss aa").format(new Date())+"]");
                    for (int i = 0; i < response.getJSONArray("centers").length(); i++) {
                        centername = response.getJSONArray("centers").getJSONObject(i).getString("name");
                        sesn = response.getJSONArray("centers").getJSONObject(i).getJSONArray("sessions");
                        textView.append("\nCENTER NAME      : " + centername);

                        for (int j = 0; j < sesn.length(); j++) {
                            date = sesn.getJSONObject(j).getString("date");
                            availability = sesn.getJSONObject(j).getString("available_capacity");
                            age = sesn.getJSONObject(j).getString("min_age_limit");

                            textView.append("\n DATE                    : " + date);
                            textView.append("\n AGE                      : " + age+"+");
                            Spannable word = new SpannableString(availability);
                            if(Integer.parseInt(availability)>0)
                            {
                                word.setSpan(new ForegroundColorSpan(Color.GREEN), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            else
                            {
                                word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            }

                            textView.append("\n AVAILABILITY : " + word);



                            if(tgl18.isChecked() && age.equals("18") && !availability.equals("0") ){
                                mp.start();
                            }
                            if(tgl45.isChecked() && age.equals("45") && !availability.equals("0") ){

                                mp.start();
                            }



                        }
                        textView.append("\n");
                    }

                    jarray = response.getJSONArray("centers");
                    Log.d("myapp", "The Response is : " + response.getJSONArray("centers").getJSONObject(0).getJSONArray("session").getString(0));
                    Log.d("Jarray", "jason array is : " + jarray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                textView.setMovementMethod(new ScrollingMovementMethod());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "something went Wrong" + error);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.76 Safari/537.36");


                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        note = findViewById(R.id.note);
        note.append("\n    1. SET THE MEDIA VOLUME UP");
        note.append("\n    2. KEEP THE APP OPEN IN BACKGROUND");
        note.append("\n    3. EDIT PIN > SET TIMER > PRESS THE BELL > PRESS GET SLOT");
        note.append("\nWith regards: HRITIK KHATRI");
        pin = findViewById(R.id.pincode);
        datafetch(pin.getText());
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        subcount = findViewById(R.id.subcount);
        addcount = findViewById(R.id.addcount);
        lastcheck = findViewById(R.id.lastcheck);
        counttext = findViewById(R.id.counttext);
        tgl45 = findViewById(R.id.tgl45);
        tgl45.setChecked(false);

        tgl18 = findViewById(R.id.tgl18);
        tgl18.setChecked(false);
        mContext = getApplicationContext();
        mp = MediaPlayer.create(mContext,R.raw.siren_alert);
        AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mp.setLooping(true);
        addcount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        count = Integer.parseInt(String.valueOf(counttext.getText()).split(" ")[0]);
                        count++;
                        counttext.setText(""+String.valueOf(count)+" Sec.");
                        delay = count * 1000;

                    }
                });

        subcount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            count = Integer.parseInt(String.valueOf(counttext.getText()).split(" ")[0]);
                        if(!(count <= 4))
                        {
                            count--;
                            counttext.setText("" + String.valueOf(count) + " Sec.");
                            delay = count * 1000;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"You cannot set timer less then 4 seocnds as you can only make 100 request per 5 minutes",Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                handler.removeCallbacks(runnable);
                if(mp.isPlaying()){mp.pause();}
                Editable pn = pin.getText();
                datafetch(pn);
                handler.postDelayed(runnable = new Runnable() {
                    public void run() {
                        handler.postDelayed(runnable, delay);
                        datafetch(pn);
                    }
                }, delay);
                break;

            case R.id.button2:
                handler.removeCallbacks(runnable);
                tgl18.setChecked(false);
                tgl45.setChecked(false);
                if(mp.isPlaying()){mp.pause();}
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
}



