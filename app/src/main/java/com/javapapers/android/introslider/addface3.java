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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class addface3 extends AppCompatActivity {

    ImageView profilePicture;
    //ImageView profileCamera;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    int user_id;
    TextView txt_image_path;
    String selectedImagePath;
    Button addFaces;
    int person_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addface3);
        profilePicture =(ImageView)  findViewById(R.id.profile_picture3);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            user_id=bundle.getInt("user_id");
            person_id = bundle.getInt("person_id");
        }
        addFaces = (Button) findViewById(R.id.Add_face3);
        addFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFaceToCV(user_id,person_id);


            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(addface3.this, new String[] {
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
                }
                startDialog();
            }
        });

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

                //bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

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
                //       bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                //             bitmap.getHeight(), matrix, true);

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
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
//                    txt_image_path.setText(selectedImagePath);
                }

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                //bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

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
                // bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                //        bitmap.getHeight(), matrix, true);


                bitmap = getResizedBitmap(bitmap);


                profilePicture.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }/*
    @Override
    public void onBackPressed(){


        Toast.makeText(getBaseContext(), "You must add another face", Toast.LENGTH_LONG)  .show();

    }*/
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
    public  void addFaceToCV(final int user_id , final int person_id) {

        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", Integer.toString(user_id));
        jsonParams.put("name", String.valueOf(person_id));
        // lw bitmap b null ytl3 error
        if (bitmap == null) {

            Toast.makeText(getBaseContext(),
                    "Please add image ", Toast.LENGTH_SHORT).show();
        } else {
            jsonParams.put("image", BitMapToString(bitmap));
            int time_out = 240000;
            String url = "http://192.168.43.176:5000/CV/add";

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
                                if (result.equals("success")) {

                                    Toast.makeText(getBaseContext(),
                                            "success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(addface3.this, HomeActivity.class);
                                    intent.putExtra("user_id", user_id);
                                    intent.putExtra("person_id", person_id);
                                    startActivity(intent);
                                } else {

                                    Toast.makeText(getBaseContext(),
                                            "No Face is Detected ", Toast.LENGTH_SHORT).show();
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
                    Log.d("TimeoutError: ", String.valueOf(e instanceof TimeoutError));

                    Log.d("NoConnectionError: ", String.valueOf(e instanceof NoConnectionError));
                    Log.d("NetworkError: ", String.valueOf(e instanceof NetworkError));
                    Log.d("AuthFailureError: ", String.valueOf(e instanceof AuthFailureError));
                    Log.d("ServerError: ", String.valueOf(e instanceof ServerError));

                    Toast.makeText(getBaseContext(),
                            "Error", Toast.LENGTH_SHORT).show();
                    VolleyLog.DEBUG = true;
                    VolleyLog.e("rababresp", error.getMessage());
                }

            });
            RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);
            requestQueue.add(jsonObjectRequest);


        }
    }
}
