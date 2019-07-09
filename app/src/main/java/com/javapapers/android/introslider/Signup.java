package com.javapapers.android.introslider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * Created by Rabab on 3/23/2019.
 */

public class Signup extends AppCompatActivity {
    Button signupBtn;
    String username="", email ="", password="";
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-zA-Z])" +      //any letter
//                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");
    EditText textInputUsername;
    EditText textInputEmail;
    EditText textInputPassword;
    int user_id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        signupBtn = (Button) findViewById(R.id.signup);
        textInputUsername = (EditText) findViewById(R.id.username);
        textInputEmail = (EditText) findViewById(R.id.email);
        textInputPassword = (EditText) findViewById(R.id.password);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!validateEmail() | !validateUsername() || !validatePassword() ) {
                    return;
           }
                    username = textInputUsername.getText().toString();
                    email = textInputEmail.getText().toString();
                    password = textInputPassword.getText().toString();

                    addUser();

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

    private boolean validateUsername() {
        String usernameInput = textInputUsername.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            textInputUsername.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            textInputUsername.setError("Username too long");
            return false;
        } else {
            textInputUsername.setError(null);
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



    private void startDialog2(final int user_id) {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        myAlertDialog.setTitle("Choose an action");
        myAlertDialog.setMessage("Do you want to add trusted members now?");
        myAlertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        toadd(user_id);
                    }
                });
        myAlertDialog.setNegativeButton("Later",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        getUser(user_id);
                    }
                });
        myAlertDialog.show();
    }
    private  void addUser(){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("username", username);
        jsonParams.put("email", email);
        jsonParams.put("password",password);
        String url = "http://192.168.43.176:5000/sign_up" ;
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
                        String msg= null;
                        try {
                            result = response.getString("status");
                            Log.d("result", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(result.equals("success")){
                            try {
                                msg= response.getString("msg");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(msg.toString().equals("you sign up")){
                                try {
                                    user_id= response.getInt("user_id");
                                    startDialog2(user_id);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    }
                            }
                            else if (msg.equals("this is an exist account")){
                                Toast.makeText(getBaseContext(),
                                        "this is an existing account", Toast.LENGTH_SHORT)  .show();
                            }

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
    private void getUser(int user_id){
        Intent i = new Intent(Signup.this, HomeActivity.class);
        i.putExtra("user_id",user_id);
        startActivity(i);
    }
    private  void toadd(int user_id) {
        Intent i = new Intent(Signup.this, AddTrustedMembers.class);
        i.putExtra("user_id", user_id);
        startActivity(i);
    }

}