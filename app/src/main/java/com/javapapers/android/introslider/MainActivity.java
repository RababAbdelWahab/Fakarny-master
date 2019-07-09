package com.javapapers.android.introslider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    Button createAccountBtn;
    Button loginBtn;
    int user_id;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    EditText textInputEmail;
    EditText textInputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.login);
        textInputEmail = (EditText) findViewById(R.id.email);
        textInputPassword = (EditText) findViewById(R.id.password);

        createAccountBtn = (Button) findViewById(R.id.createanaccounr);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!validateEmail() ) {
                    return;
                }
                final String email = textInputEmail.getText().toString();
                String password = textInputPassword.getText().toString();
                Map<String, String> jsonParams = new HashMap<String, String>();
                jsonParams.put("email", email);
                jsonParams.put("password",password);
                String url = "http://192.168.43.176:5000/login" ;
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
                                        user_id= response.getInt("user_id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    i.putExtra("user_id",user_id);
                                    startActivity(i);
                                }
                                else {
                                    Log.d("gwaaaa", result);
                                    Toast.makeText(getBaseContext(),
                                            "wrong email or password", Toast.LENGTH_SHORT)  .show();
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
//                VolleyApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
                requestQueue.add(jsonObjectRequest);

            }
        });

    }
    private boolean validateEmail() {
        String emailInput = textInputEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Password too weak at least 4 characters and special character");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
}