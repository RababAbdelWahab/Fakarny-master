package com.javapapers.android.introslider;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembersActivity extends Activity {
    private static final String DEFAULT = "Events_Fragment";
    int[] IMAGES ={R.drawable.person};
    List<String> Names = new ArrayList<String>();
    List<String> relations = new ArrayList<String>();
    List<String> emails = new ArrayList<String>();
    List<String> phones = new ArrayList<String>();
    List<String> addresses = new ArrayList<String>();
    List<String> imgs = new ArrayList<String>();

    ArrayList<User> arrayOfUsers = new ArrayList<User>();

    int user_id;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if(bundle != null){
            user_id= bundle.getInt("user_id");
        }

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id",Integer.toString(user_id));
        final String url = "http://192.168.43.176:5000/get/members";
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
                            result = response.getString("status");
                            Log.d("result", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (result.toString().equals("success"))
                        {
                            try {
                                JSONArray members = response.getJSONArray("members");

                                for(int i = 0; members.length() > i; i++){
                                    JSONObject member = members.getJSONObject(i);
                                    User newUser;
                                    String name = member.getString("name") ;
                                    String email = member.getString("email") ;
                                    String phone = member.getString("phone") ;
                                    String address = member.getString("address") ;
                                    String relation = member.getString("relation") ;
                                    String img = member.getString("img");

                                    newUser = new User(img,name,email,phone,address,relation);
                                    arrayOfUsers.add(newUser);

                                }


                                ListView listView = (ListView)findViewById(R.id.listView);
                                UsersAdapter adapter = new UsersAdapter(MembersActivity.this, arrayOfUsers);
                                // Attach the adapter to a ListView
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        Intent member = new Intent(view.getContext(), MemberActivity.class);
                                        User user = arrayOfUsers.get(i);
                                        member.putExtra("name",user.name);
                                        member.putExtra("email",user.email);
                                        member.putExtra("phone", user.phone);
                                        member.putExtra("address",user.address);
                                        member.putExtra("relation",user.relation);
                                        member.putExtra("img",user.img);
                                        member.putExtra("user_id",user_id);
                                        startActivityForResult(member,i);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

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
        RetryPolicy policy = new DefaultRetryPolicy(60*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MembersActivity.this, AddTrustedMembers.class);
                i.putExtra("user_id",user_id);
                startActivity(i);

            }
        });



    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent ii = new Intent(MembersActivity.this,HomeActivity.class);
        ii.putExtra("user_id", user_id);
        startActivity(ii);
        this.finish();

    }
}