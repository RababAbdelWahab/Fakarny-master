package com.javapapers.android.introslider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class facess extends AppCompatActivity {
    TextView mytext;
    ImageView img1 , img2 , img3 ;
    Bitmap bitmap1 , bitmap2 , bitmap3;
    int user_id, person_id;
    String name, phone,des,relation;
    Button addface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facess);
        img1 = (ImageView) findViewById(R.id.face1);
        img2 = (ImageView) findViewById(R.id.face2);
        img3 = (ImageView) findViewById(R.id.face3);
        mytext = (TextView)findViewById(R.id.tetttt);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            user_id = bundle.getInt("user_id");

            name= bundle.getString("name");
            relation= bundle.getString("relation");
            des= bundle.getString("des");
            phone = bundle.getString("phone");

            try {
                bitmap1 = BitmapFactory.decodeStream(openFileInput("bitmap1"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bitmap2 = BitmapFactory.decodeStream(openFileInput("bitmap2"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bitmap3 = BitmapFactory.decodeStream(openFileInput("bitmap3"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            img1.setImageBitmap(bitmap1);
            img2.setImageBitmap(bitmap2);
            img3.setImageBitmap(bitmap3);
            mytext.setText(name+ "   "+ phone + " "+ des+" "+relation+" ");
        }


        addface = (Button) findViewById(R.id.addface11);
        addface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  addFaceToCV(user_id,person_id);

                addFace();


            }
        });


    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.NO_WRAP);
        return temp;
    }
    public void addFaceToCV3(final int user_id, int person_id, Bitmap bitmap){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id",Integer.toString(user_id));
        jsonParams.put("name", String.valueOf(person_id));
        // lw bitmap b null ytl3 error
        if(bitmap == null ) jsonParams.put("images","no image");
        else jsonParams.put("image", BitMapToString(bitmap));
        int time_out = 240000;
        String url = "http://192.168.43.176:5000/CV/add";

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
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
                            result= response.getString("status");
                            if(result.equals("success")){
                                Intent ii = new Intent(facess.this, HomeActivity.class);
                                ii.putExtra("user_id",user_id);
                                startActivity(ii);
                                Toast.makeText(getBaseContext(),
                                        "success", Toast.LENGTH_SHORT)  .show();
                            }
                            else {

                                Toast.makeText(getBaseContext(),
                                        "No Face is Detected ", Toast.LENGTH_SHORT)  .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("rababresp", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //              Log.d("st code", String.valueOf(error.networkResponse.statusCode));
                Object e = null;
                Log.d("TimeoutError: " , String.valueOf(e instanceof TimeoutError));

                Log.d( "NoConnectionError: " , String.valueOf(e instanceof NoConnectionError));
                Log.d("NetworkError: " , String.valueOf(e instanceof NetworkError));
                Log.d( "AuthFailureError: " , String.valueOf(e instanceof AuthFailureError));
                Log.d( "ServerError: " , String.valueOf(e instanceof ServerError));

                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.DEBUG=true;
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);


    }
    public void addFaceToCV2(final int user_id, final int person_id , Bitmap bitmap ){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id",Integer.toString(user_id));
        jsonParams.put("name", String.valueOf(person_id));
        // lw bitmap b null ytl3 error
        if(bitmap == null ) jsonParams.put("images","no image");
        else jsonParams.put("image", BitMapToString(bitmap));
        int time_out = 240000;
        String url = "http://192.168.43.176:5000/CV/add";

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
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
                            result= response.getString("status");
                            if(result.equals("success")){
                                addFaceToCV3(user_id,person_id,bitmap3);
                                Toast.makeText(getBaseContext(),
                                        "success", Toast.LENGTH_SHORT)  .show();
                            }
                            else {

                                Toast.makeText(getBaseContext(),
                                        "No Face is Detected ", Toast.LENGTH_SHORT)  .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("rababresp", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //              Log.d("st code", String.valueOf(error.networkResponse.statusCode));
                Object e = null;
                Log.d("TimeoutError: " , String.valueOf(e instanceof TimeoutError));

                Log.d( "NoConnectionError: " , String.valueOf(e instanceof NoConnectionError));
                Log.d("NetworkError: " , String.valueOf(e instanceof NetworkError));
                Log.d( "AuthFailureError: " , String.valueOf(e instanceof AuthFailureError));
                Log.d( "ServerError: " , String.valueOf(e instanceof ServerError));

                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.DEBUG=true;
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);




    }
    public  void addFaceToCV(final int user_id, final int person_id , Bitmap bitmap ){

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id",Integer.toString(user_id));
        jsonParams.put("name", String.valueOf(person_id));
        // lw bitmap b null ytl3 error
        if(bitmap == null ) jsonParams.put("images","no image");
        else jsonParams.put("image", BitMapToString(bitmap));
        int time_out = 240000;
        String url = "http://192.168.43.176:5000/CV/add";

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
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
                            result= response.getString("status");
                            if(result.equals("success")){
                                addFaceToCV2(user_id,person_id,bitmap2);
                                Toast.makeText(getBaseContext(),
                                        "success", Toast.LENGTH_SHORT)  .show();
                            }
                            else {

                                Toast.makeText(getBaseContext(),
                                        "No Face is Detected ", Toast.LENGTH_SHORT)  .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("rababresp", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //              Log.d("st code", String.valueOf(error.networkResponse.statusCode));
                Object e = null;
                Log.d("TimeoutError: " , String.valueOf(e instanceof TimeoutError));

                Log.d( "NoConnectionError: " , String.valueOf(e instanceof NoConnectionError));
                Log.d("NetworkError: " , String.valueOf(e instanceof NetworkError));
                Log.d( "AuthFailureError: " , String.valueOf(e instanceof AuthFailureError));
                Log.d( "ServerError: " , String.valueOf(e instanceof ServerError));

                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.DEBUG=true;
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);



    }

    public  void addFace(){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("name",name);
        jsonParams.put("phone",phone);
        jsonParams.put("relation",relation);
        jsonParams.put("description",des);
        // if(bitmap == null ) jsonParams.put("img","no image");
        //else jsonParams.put("img", BitMapToString(bitmap));
        jsonParams.put("user_id",Integer.toString(user_id));
        String url = "http://192.168.43.176:5000/add/face";
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
                        String result=null, msg = null ;
                        try {
                            msg = response.getString("msg");
                            if (msg.equals("add face")){
                                try {
                                    result = response.getString("status");
                                    if(result.equals("success")){
                                        person_id= response.getInt("person_id");
                                         addFaceToCV(user_id,person_id, bitmap1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(msg.equals("mogod")){

                                Toast.makeText(getBaseContext(),
                                        "This person already exits ", Toast.LENGTH_SHORT)  .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getBaseContext(),
                                "success", Toast.LENGTH_SHORT)  .show();
                        Log.d("rababresp", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getBaseContext(),
                        "Error", Toast.LENGTH_SHORT)  .show();
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        requestQueue.add(jsonObjectRequest);
       /* Intent intent = new Intent(AddFacesActivity.this, HomeActivity.class);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
*/

    }

}
