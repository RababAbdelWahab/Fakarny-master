package com.javapapers.android.introslider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rabab on 3/23/2019.
 */

public class AddTrustedMembers extends AppCompatActivity {
    ImageView profilePicture;
    //ImageView profileCamera;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    TextView txt_image_path;
    String selectedImagePath;
    Spinner spinner;
    Button addTrustedMembers;
    EditText textInputEmail;
    EditText textInputname;
    EditText textInputphone;
    EditText textInputAddress;
    EditText textInputRelation;
    int user_id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_trusted_members);
        profilePicture =(ImageView)  findViewById(R.id.profile_picture);
        // profileCamera =(ImageView)  findViewById(R.id.profile_camera);
        // profileCamera.bringToFront();
        addTrustedMembers = (Button) findViewById(R.id.Add_trusted);
        textInputEmail = (EditText) findViewById(R.id.email);
        textInputname = (EditText) findViewById(R.id.name);
        textInputphone = (EditText) findViewById(R.id.phone);
        textInputAddress = (EditText) findViewById(R.id.address);
        textInputRelation= (EditText) findViewById(R.id.relation);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        user_id= bundle.getInt("user_id");



        addTrustedMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               if (!validateEmail() | !validateUsername() | !validatePhone() | !validateAddress() ) {
//                   return;
//                }
                String input = "Email: " + textInputEmail.getText().toString();
                input += "\n";
                input += "Username: " + textInputname.getText().toString();
                input += "\n";
                input += "Phone: " + textInputphone.getText().toString();
                input += "\n";
                input += "Address: " + textInputAddress.getText().toString();
                addMember();

            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddTrustedMembers.this, new String[] {
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
                }
                startDialog();
            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

    }

    public  void  addMember(){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("name",textInputname.getText().toString());
        jsonParams.put("email",  textInputEmail.getText().toString());
        jsonParams.put("phone",textInputphone.getText().toString());
        jsonParams.put("address",  textInputAddress.getText().toString());
        jsonParams.put("address",  textInputAddress.getText().toString());
        jsonParams.put("relation",  textInputRelation.getText().toString());
        if(bitmap == null ) jsonParams.put("img","no image");
        else jsonParams.put("img", BitMapToString(bitmap));
        jsonParams.put("user_id",Integer.toString(user_id));
        String url = "http://192.168.43.176:5000/add/member";
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
                        String result=null, msg=null;
                        try {
                            result= response.getString("status");
                            if (result.equals("success")){
                                try {
                                    msg= response.getString("msg");
                                    if (msg.equals("this member is already exisiting")){
                                        Toast.makeText(getBaseContext(),
                                                msg, Toast.LENGTH_LONG)  .show();

                                    }

                                    else if (msg.equals("add member")){
                                        Toast.makeText(getBaseContext(),
                                                "new member is added", Toast.LENGTH_LONG)  .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
//                VolleyApplication.getInstance().getRequestQueue().add(jsonObjectRequest);

        RetryPolicy policy = new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);

        Intent intent = new Intent(AddTrustedMembers.this, MembersActivity.class);
        intent.putExtra("activity","AddTrustedMembers");
        intent.putExtra("user_id",user_id);
        startActivity(intent);
        onBackPressed();
    }
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        myAlertDialog.setTitle("Upload Picture Option");
        myAlertDialog.setMessage("How do you want to set your picture?");
        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent,
                                GALLERY_PICTURE);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(f));

                        startActivityForResult(intent,
                                CAMERA_REQUEST);

                    }
                });
        myAlertDialog.show();
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.NO_WRAP);
        return temp;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(AddTrustedMembers.this, MembersActivity.class);
        intent.putExtra("user_id",user_id);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        //startActivity();
        this.finish();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(),

                        "Error while capturing image", Toast.LENGTH_LONG)

                        .show();

                return;

            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

               // bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


                bitmap = getResizedBitmap(bitmap);

                profilePicture.setImageBitmap(bitmap);
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
//                    txt_image_path.setText(selectedImagePath);
                }

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
              //  bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(selectedImagePath);
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);


                bitmap = getResizedBitmap(bitmap);

                profilePicture.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startDialog();
            }
        }
    }


    public Bitmap getResizedBitmap(Bitmap image) {
        int maxSize = 1024;
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
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
        String usernameInput = textInputname.getText().toString().trim();

        if (usernameInput.isEmpty()) {
            textInputname.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            textInputname.setError("Username too long");
            return false;
        } else {
            textInputname.setError(null);
            return true;
        }
    }



    private boolean validatePhone() {
        String PhoneInput = textInputphone.getText().toString().trim();

        if (PhoneInput.isEmpty()) {
            textInputphone.setError("Field can't be empty");
            return false;
        } else if (PhoneInput.length() > 11) {
            textInputphone.setError("Number too long");
            return false;
        } else {
            textInputphone.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        String AdressInput = textInputAddress.getText().toString().trim();

        if (AdressInput.isEmpty()) {
            textInputAddress.setError("Field can't be empty");
            return false;
        }
        else {
            textInputAddress.setError(null);
            return true;
        }
    }


}