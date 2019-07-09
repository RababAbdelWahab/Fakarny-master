package com.javapapers.android.introslider;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {
    TextView nameText, placeText, dateText , timeText , summaryText ;

    String name, place, date , time1 , time2 ,summary;
    int user_id , confirmed , event_id ;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        nameText = (TextView) findViewById(R.id.name);
        placeText = (TextView) findViewById(R.id.place);
        dateText = (TextView) findViewById(R.id.date);
        timeText = (TextView) findViewById(R.id.time);
        summaryText = (TextView) findViewById(R.id.summary);
        FloatingActionButton edit = (FloatingActionButton) findViewById(R.id.edev);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
        } else {
            name = bundle.getString("content");
            place = bundle.getString("location");
            date = bundle.getString("date");
            user_id= bundle.getInt("user_id");
            confirmed = bundle.getInt("confirmed");
            event_id=bundle.getInt("event_id");
            time1 = bundle.getString("time");
            time2 = bundle.getString("time2");
            summary = bundle.getString("summary");
        }
        nameText.setText(name);
        placeText.setText("in " + place);
        dateText.setText("at " + date);
        timeText.setText("at "+ time1 +" "+ time2);
        summaryText.setText(summary);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteEvent();


            }
        });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent ii = new Intent(EventActivity.this,HomeActivity.class);
        ii.putExtra("user_id", user_id);
        startActivity(ii);
        this.finish();
    }
    public  void deleteEvent(){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Integer.toString(user_id));
        jsonParams.put("content", name);
        jsonParams.put("location", place);
        jsonParams.put("date",date);
        jsonParams.put("summary",summary);
        final String url = "http://192.168.43.176:5000/delete/event";
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest
                jsonObjectRequest
                = new JsonObjectRequest(
                Request.Method.POST,
                url,
//                        (String) null,
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
                        if (result.toString().equals("success")) {
                            try {
                                user_id = response.getInt("user_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            onBackPressed();


                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT).show();
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        requestQueue.add(jsonObjectRequest);


    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editEv) {
            Intent intent = new Intent(EventActivity.this, event_edit.class);
            intent.putExtra("user_id", user_id);
            intent.putExtra("event_id",event_id);
            intent.putExtra("name", name);
            intent.putExtra("place",place);
            intent.putExtra("date",date);
            intent.putExtra("time", time1);
            intent.putExtra("time2",time2);
            intent.putExtra("summary", summary);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);


    }

}