package com.javapapers.android.introslider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rabab on 6/11/2019.
 */

public class HomeActivity extends AppCompatActivity  {

    protected static final int CAMERA_REQUEST = 0;
    private static final String fileName="example.txt";
    private TextView mTextMessage;
    File file = new File("/home/menna/GP_Final7/Fakarny/app/src/main/java/com/javapapers/android/introslider/events.txt");

    int user_id , day , month , year , hour , minute;
    String username= "", phone ="", useremail="",address="", img="";
    int [] confirmed= null;
    boolean toRead= false;
    ArrayList<User> arrayOfUsers = new ArrayList<User>();
    String [] contents= null;  String [] locations= null;  String [] times=null;  String [] dates= null;
    String [] times2 = null; String [] summaries = null;
    List<Integer> years = new ArrayList<Integer>();
    List<Integer> months = new ArrayList<Integer>();
    List<Integer> days = new ArrayList<Integer>();
    List<Integer> hours = new ArrayList<Integer>();
    List<Integer> minutes = new ArrayList<Integer>();
    List<String> clks = new ArrayList<String>();
    List<Integer> years2 = new ArrayList<Integer>();
    List<Integer> months2 = new ArrayList<Integer>();
    List<Integer> days2 =new ArrayList<Integer>();
    List<Integer> hours2 = new ArrayList<Integer>();
    List<Integer> minutes2 = new ArrayList<Integer>();
    List<String> clks2 = new ArrayList<String>();

    String edit="";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Events_Fragment(user_id,contents,dates,times, times2,locations, confirmed,summaries)).commit();
                    return true;

                case R.id.navigation_recognizer:

                    user_id = bundle.getInt("user_id");
                    //getImg();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Recognizer_Fragment(user_id)).commit();
                    return true;


                case R.id.navigation_profile:

                    user_id = bundle.getInt("user_id");
                    getProfile();

                    return true;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Events_Fragment(user_id,contents,dates,times, times2,locations, confirmed,summaries)).commit();
                    break;


            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        // first open

        if (bundle != null){
            user_id = bundle.getInt("user_id");
            edit = bundle.getString("item");
            if(edit!= null)
            {
                View view = (View)findViewById(R.id.main_view);
                BottomNavigationView navigation = view.getRootView().findViewById(R.id.navigation);
                navigation.setSelectedItemId(R.id.navigation_profile);
                getProfile();

            }

            else{
                View view = (View)findViewById(R.id.main_view);
                BottomNavigationView navigation = view.getRootView().findViewById(R.id.navigation);
                navigation.setSelectedItemId(R.id.navigation_home);
                getUser();

            }
            edit=null;

        }


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        View view = (View)findViewById(R.id.main_view);

    }

    private void Read() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder  sb = new StringBuilder();
            String text ;
            int it=0;
            String [] arrOfStr;
            while ((text = br.readLine())!=null){
                sb.append(text).append("\n");
                arrOfStr = text.toString().split(",");
                years.add(Integer.parseInt(arrOfStr[0]));
                months.add(Integer.parseInt(arrOfStr[1]));
                days.add(Integer.parseInt(arrOfStr[2]));
                hours.add(Integer.parseInt(arrOfStr[3]));
                minutes.add(Integer.parseInt(arrOfStr[4]));
                clks.add(arrOfStr[5]);

                Log.d("this is event ", String.valueOf(it )+" ,  "+ sb.toString());

            }
            //Toast.makeText(this,"read from"+ getFilesDir()+"/"+fileName , Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public  void scheduleAlarms(int i , int year, int month , int day , int hour , int minute, String ti )
    {
        Calendar cal_alarm  = Calendar.getInstance();

        // cal_alarm.setTimeInMillis(System.currentTimeMillis() );
        cal_alarm.set(Calendar.YEAR, year);
        cal_alarm.set(Calendar.MONTH, month-1);
        cal_alarm.set(Calendar.DAY_OF_MONTH, day);
        cal_alarm.set(Calendar.HOUR, hour);

        cal_alarm.set(Calendar.MINUTE, minute );
        cal_alarm.set(Calendar.SECOND, 0 );
        if (ti.equals("A.M")){

            cal_alarm.set(Calendar.AM_PM, Calendar.AM );
        }
        else  if (ti.equals("P.M")){
            cal_alarm.set(Calendar.AM_PM, Calendar.PM );
        }

        Intent intent = new Intent(this, MyAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(this.getApplicationContext(), i , intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);


        Log.d("time", String.valueOf(cal_alarm.getTimeInMillis()));
        if(cal_alarm.getTimeInMillis() >= (System.currentTimeMillis()+(30*60*1000))){
            am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis()-(30*60*1000),pi);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        Fragment  f = getSupportFragmentManager().findFragmentById(R.id.frag_container);

        switch (id) {
            case R.id.trusted_members:
                Intent trusted_members = new Intent(HomeActivity.this, MembersActivity.class);
                if(f instanceof Events_Fragment) {trusted_members.putExtra("item1", "Events_Fragment"); trusted_members.putExtra("user_id",user_id);}
                else  if(f instanceof Recognizer_Fragment) {trusted_members.putExtra("item1", "Recognizer_Fragment"); trusted_members.putExtra("user_id",user_id);}
                else  if(f instanceof Profile_Fragment){ trusted_members.putExtra("item1", "Profile_Fragment");trusted_members.putExtra("user_id",user_id);}
                startActivity(trusted_members);
                break;

            case R.id.settings:
                Intent settings = new Intent(HomeActivity.this, SettingsActivity.class);
                if(f instanceof Events_Fragment) {settings.putExtra("item2", "Events_Fragment"); settings.putExtra("user_id", user_id); }
                else  if(f instanceof Recognizer_Fragment) {settings.putExtra("item2", "Recognizer_Fragment"); settings.putExtra("user_id", user_id); }
                else  if(f instanceof Profile_Fragment) {settings.putExtra("item2", "Profile_Fragment"); settings.putExtra("user_id", user_id); }
                startActivity(settings);
                break;

            case R.id.logout:
                Intent login = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(login);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }
    @Override
    public void onBackPressed() {
        Fragment  f = getSupportFragmentManager().findFragmentById(R.id.frag_container);
        if (f instanceof Events_Fragment) {
            finish();

            HomeActivity.super.finishAffinity();
        } else if (f instanceof Recognizer_Fragment) {
            View view = (View)findViewById(R.id.main_view);
            BottomNavigationView navigation = view.getRootView().findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Events_Fragment(user_id,contents,dates,times, times2,locations, confirmed,summaries)).commit();




        }  else if (f instanceof Profile_Fragment) {
            // do operations
            View view = (View)findViewById(R.id.main_view);
            BottomNavigationView navigation = view.getRootView().findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_home);
            Log.d("cont", String.valueOf(user_id));
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Events_Fragment(user_id,contents,dates,times, times2,locations, confirmed,summaries)).commit();


        }else {
            super.onBackPressed();
        }

    }
    public void write(List<Integer> years , List<Integer> months , List<Integer> days , List<Integer> hours , List<Integer> minutes, List<String> clks){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            for (int i = 0; i<years.size();i++){

                String te= String.valueOf(years.get(i))+","+String.valueOf(months.get(i))+","+String.valueOf(days.get(i))+","+String.valueOf(hours.get(i))+","+String.valueOf(minutes.get(i))+","+clks.get(i)+","+"\n";
                fos.write(te.getBytes());
            }
            Log.d("file dir", String.valueOf(getFilesDir()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public  void getUser(){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Integer.toString(user_id));
        final String url = "http://192.168.43.176:5000/get/user";
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
                            JSONArray events;
                            try {
                                username = response.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                phone = response.getString("phone");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = response.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                useremail = response.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                img=response.getString("img");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                events = response.getJSONArray("events");
                                String[] arrOfStrs2= null;
                                contents = new String[events.length()];
                                locations = new String[events.length()];
                                dates = new String[events.length()];
                                confirmed = new  int[events.length()];
                                times = new  String[events.length()];
                                times2 = new String [events.length()];
                                summaries = new String[events.length()];
                                for (int i = 0; i < events.length(); i++) {
                                    JSONObject event = events.getJSONObject(i);
                                    contents[i] = event.getString("content");
                                    locations[i] = event.getString("location");
                                    dates[i]=event.getString("date");
                                    times[i] = event.getString("time");
                                    times2[i]=event.getString("time2");
                                    confirmed[i]=event.getInt("confirmed");
                                    summaries[i]=event.getString("summary");
                                    if (!dates[i].equals("undefined") && confirmed[i]==1){
                                        arrOfStrs2 = dates[i].split("/");
                                        year = Integer.valueOf(arrOfStrs2[0]);
                                        month = Integer.valueOf(arrOfStrs2[1]);
                                        day = Integer.valueOf(arrOfStrs2[2]);
                                        hour = Integer.valueOf(times[i].substring(0,2));
                                        minute = Integer.valueOf(times[i].substring(3,5));
                                        years2.add(year);
                                        months2.add(month);
                                        days2.add(day);
                                        hours2.add(hour);
                                        minutes2.add(minute);
                                        clks2.add(times2[i]);
                                    }






                                }
                                write(years2,months2,days2,hours2,minutes2, Arrays.asList(times2));
                                Read();
                                for (int i = 0; i< years.size();i++){
                                    scheduleAlarms(i, years.get(i),months.get(i),days.get(i),hours.get(i),minutes.get(i),clks.get(i));
                                }                                //setCurrentFrag("Profile_Fragment", username, useremail, phone, address, contents, times, locations);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Events_Fragment(user_id,contents,dates,times, times2,locations, confirmed,summaries)).commit();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



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
        edit=null;

    }

    public  void getProfile(){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Integer.toString(user_id));
        final String url = "http://192.168.43.176:5000/get/user";
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
                            JSONArray events;
                            try {
                                username = response.getString("name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                phone = response.getString("phone");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                address = response.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                useremail = response.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                img=response.getString("img");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                events = response.getJSONArray("events");
                                contents = new String[events.length()];
                                locations = new String[events.length()];
                                dates = new String[events.length()];
                                confirmed = new  int[events.length()];
                                times = new  String[events.length()];
                                times2 = new String [events.length()];
                                summaries = new  String[events.length()];
                                for (int i = 0; i < events.length(); i++) {
                                    JSONObject event = events.getJSONObject(i);
                                    contents[i] = event.getString("content");
                                    locations[i] = event.getString("location");
                                    dates[i]=event.getString("date");
                                    times[i] = event.getString("time");
                                    times2[i]=event.getString("time2");
                                    confirmed[i]=event.getInt("confirmed");
                                    summaries[i]=event.getString("summary");

                                }
                                //setCurrentFrag("Profile_Fragment", username, useremail, phone, address, contents, times, locations);
                                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new Profile_Fragment(user_id,img,username,phone ,useremail,address)).commit();


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



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
        edit=null;
    }



}