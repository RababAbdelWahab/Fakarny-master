package com.javapapers.android.introslider;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class event_edit extends AppCompatActivity {
    int user_id,event_id=0 , confirmed;
    String name ="", place="",date="" , time="", time2 ="", summary="";
    Button editEvent;
    EditText textInputLocation;
    EditText textInputname;
    EditText textInputDate;
    EditText textInputTime;
    EditText textInputTime2;
    EditText textInputSummary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            name = bundle.getString("name");
            place = bundle.getString("place");
            date = bundle.getString("date");
            user_id= bundle.getInt("user_id");
            event_id= bundle.getInt("event_id");
            time = bundle.getString("time");
            time2= bundle.getString("time2");
            summary= bundle.getString("summary");
        }


        editEvent = (Button) findViewById(R.id.edit_event);
        textInputLocation = (EditText) findViewById(R.id.location);
        textInputname = (EditText) findViewById(R.id.name);
        textInputTime = (EditText) findViewById(R.id.time1);
        textInputDate = (EditText) findViewById(R.id.date);
        textInputTime2= (EditText) findViewById(R.id.time2);
        textInputSummary = (EditText)findViewById(R.id.summaryed) ;

        textInputLocation.setText(place);
        textInputname.setText(name);
        textInputDate.setText(date);
        textInputTime.setText(time);
        textInputTime2.setText(time2);
        textInputSummary.setText(summary);
        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();

            }
        });
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        myAlertDialog.setTitle("Confirmation");
        myAlertDialog.setMessage("Are you sure about this event ?");
        myAlertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        confirmed =1;
                        updateEvent();
                    }
                });
        myAlertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        confirmed =0;
                        updateEvent();
                    }
                });
        myAlertDialog.show();
    }
    public  void updateEvent(){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", String.valueOf(user_id));
        jsonParams.put("content",  textInputname.getText().toString());
        jsonParams.put("location",textInputLocation.getText().toString());
        jsonParams.put("date", textInputDate.getText().toString());
        jsonParams.put("event_id", String.valueOf(event_id));
        jsonParams.put("confirmed", String.valueOf(confirmed));
        jsonParams.put("time",textInputTime.getText().toString());
        jsonParams.put("time2", textInputTime2.getText().toString());
        jsonParams.put("summary", textInputSummary.getText().toString());
        String url = "http://192.168.43.176:5000/update/event" ;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(jsonParams),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = null;
                        try {
                            result = response.getString("status");
                            Log.d("result", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (result.toString().equals("success"))
                        {
                            try {
                                user_id = response.getInt("user_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(event_edit.this, EventActivity.class);
                            intent.putExtra("content",  textInputname.getText().toString());
                            intent.putExtra("location",textInputLocation.getText().toString());
                            intent.putExtra("date",textInputDate.getText().toString());
                            intent.putExtra("event_id",event_id);
                            intent.putExtra("confirmed",confirmed);
                            intent.putExtra("user_id",user_id);
                            intent.putExtra("time",textInputTime.getText().toString());
                            intent.putExtra("time2",textInputTime2.getText().toString());
                            intent.putExtra("summary",textInputSummary.getText().toString());
                            startActivity(intent);
                            Toast.makeText(getBaseContext(),
                                    "Edits are done ", Toast.LENGTH_SHORT)  .show();

                        }
                        else {
                            Log.d("gwaaaa", result);
                            Toast.makeText(getBaseContext(),
                                    "edit fails ", Toast.LENGTH_SHORT)  .show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.e("rababresp", error.getMessage());
            }

        });



        RetryPolicy policy = new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);


    }
}