package com.javapapers.android.introslider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    SwitchCompat cam,mic;
    boolean state_cam, state_mic;
    SharedPreferences preferences;
    String lastFrag="";
    private SpeechToText speechService;
    private StreamPlayer player = new StreamPlayer();
    private MicrophoneHelper microphoneHelper;
    private MicrophoneInputStream capture;
    private boolean listening = false;
    String AllOfText="";
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        microphoneHelper = new MicrophoneHelper(this);
        speechService = initSpeechToTextService();

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle!=null) {user_id= bundle.getInt("user_id");}
        //lastFrag = i.getStringExtra("item2");

        preferences = getSharedPreferences("PRES",0);
        state_cam = preferences.getBoolean("cam_switch",    false);
        state_cam = preferences.getBoolean("mic_switch",    false);

        cam = (SwitchCompat) findViewById(R.id.camera);
        mic = (SwitchCompat) findViewById(R.id.microphone);

        cam.setChecked(state_cam);
        mic.setChecked(state_mic);

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state_cam = !state_cam;
                cam.setChecked(state_cam);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("cam_switch", state_cam);
                editor.apply();


            }
        });


        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!listening) {
                    // Update the icon background
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mic.setBackgroundColor(Color.GREEN);
                            mic.setChecked(!listening);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("mic_switch", !listening);
                            editor.apply();
                            Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Mic is enabled",
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    });
                    capture = microphoneHelper.getInputStream(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                speechService.recognizeUsingWebSocket(getRecognizeOptions(capture),
                                        new MicrophoneRecognizeDelegate());
                            } catch (Exception e) {
                                showError(e);
                            }
                        }
                    }).start();

                    listening = true;
                } else {
                    // Update the icon background
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mic.setBackgroundColor(Color.LTGRAY);
                        }
                    });
                    microphoneHelper.closeInputStream();
                    String dia= "A: how’s it\n" +
                            "B: fine thank you\n" +
                            "A: did you need help with something\n" +
                            "B: if I could I would like to view the apartment sometime today\n" +
                            "A: that won’t be possible today\n" +
                            "B: why is that\n" +
                            "A: you can only view the apartment with an appointment\n" +
                            "B: I would like to make one right now if I can\n" +
                            "A: are you available this Friday\n" +
                            "B: can we do it at six o'clock\n" +
                            "A: your appointment for Friday at six has been concerned for her\n" +
                            "B: I’ll see you on Friday\n" +
                            "A: right\n";
                    Map<String, String> jsonParams = new HashMap<String, String>();
                    jsonParams.put("text", "A: Hello jery\n" +
                            "B: hi how are you\n" +
                            "A: Fine thanks and you\n" +
                            "B: Fine my lovely girl\n" +
                            "A: what about go to sun city mall\n" +
                            "B: oki that time do you think\n" +
                            "A: at three o’clock\n" +
                            "B: oki but what about change the place to club\n" +
                            "A: I am free lets go but when\n" +
                            "B: oki we will go tomorrow\n" +
                            "A: are u free next week to buy some fruits from super market\n" +
                            "B: no sorry I can’t\n" +
                            "A: oki what about go to cinema next month\n" +
                            "B: oki I don’t mind\n  ");
                    jsonParams.put("user_id", Integer.toString(user_id));
                    Log.d("AllOfTextbefore",dia);
                    postText(new JSONObject(jsonParams));
                    AllOfText="";
                    mic.setChecked(!listening);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("mic_switch", !listening);
                    editor.apply();
                    Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Mic is disabled",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    listening = false;
                }
            }
        });


    }
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        myAlertDialog.setTitle("New Event");
        myAlertDialog.setMessage("Check your events list");
        myAlertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onBackPressed();
                    }
                });
        myAlertDialog.show();
    }
    public void onBackPressed()
    {

        super.onBackPressed();
        Intent trusted_members = new Intent(SettingsActivity.this,HomeActivity.class);
        trusted_members.putExtra("user_id",user_id);
        startActivity(trusted_members);

        this.finish();

    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mic.setChecked(false);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("mic_switch", false);
                editor.apply();
            }
        });
    }



    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mic.setEnabled(true);
            }
        });
    }

    private SpeechToText initSpeechToTextService() {
        IamOptions options = new IamOptions.Builder()
                .apiKey(getString(R.string.speech_text_apikey))
                .build();
        SpeechToText service = new SpeechToText(options);
        service.setEndPoint(getString(R.string.speech_text_url));
        return service;
    }

    private RecognizeOptions getRecognizeOptions(InputStream captureStream) {
        return new RecognizeOptions.Builder()
                .audio(captureStream)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .inactivityTimeout(200)
                .build();
    }

    private abstract class EmptyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        // assumes text is initially empty
        private boolean isEmpty = true;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                isEmpty = true;
                onEmpty(true);
            } else if (isEmpty) {
                isEmpty = false;
                onEmpty(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        public abstract void onEmpty(boolean empty);
    }

    private class MicrophoneRecognizeDelegate extends BaseRecognizeCallback {
        @Override
        public void onTranscription(SpeechRecognitionResults speechResults) {
            System.out.println(speechResults);
            if (speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                int     size=speechResults.getResults().size();
                Log.d("size", String.valueOf(size))  ;
                for(int i=0;i< speechResults.getResults().size();i++) {
                    Log.d("gwaaa","for");
                    Boolean final_var =speechResults.getResults().get(i).isFinalResults();
                    if (final_var) {
                        Log.d("gwaaa","if");
                        String text = speechResults.getResults().get(i).getAlternatives().get(0).getTranscript();
                        AllOfText = AllOfText + text + "\n";
                        Log.d("gwaaa",AllOfText);
                    }
                }
            }

        }

        @Override
        public void onError(Exception e) {
            try {
                capture.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            showError(e);
            enableMicButton();
        }

        @Override
        public void onDisconnected() {
            enableMicButton();
        }
    }


    /**
     * On request permissions result.
     *
     * @param requestCode the request code
     * @param permissions the permissions
     * @param grantResults the grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MicrophoneHelper.REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void postText (JSONObject obj)
    {
        int time_out = 180000;
        String url = "http://192.168.43.176:5000/speech" ;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.POST,
                url,
                obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = null, new_event = null;
                        try {
                            result = response.getString("status");

                            Log.d("result", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            new_event = response.getString("add");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (result.equals("succsess")&& new_event.equals("new_event"))
                        {startDialog();
                        Toast.makeText(getBaseContext(),
                                "success ", Toast.LENGTH_SHORT)  .show();}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.e("error", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);


    }

}