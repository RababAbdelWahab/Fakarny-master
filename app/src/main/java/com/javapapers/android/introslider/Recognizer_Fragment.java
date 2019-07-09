package com.javapapers.android.introslider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class Recognizer_Fragment extends Fragment {
    int user_id;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    TextView txt_image_path;
    String selectedImagePath;
    ImageView recognizedImage;
    TextView recognizedName , recognizedRelation , recognizedDetails ;
    int[] x1; int []x2; int [] y1; int []y2;
    String [] person;
    @SuppressLint("ValidFragment")
    public Recognizer_Fragment(int user_id) {
        this.user_id = user_id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recognizer, container,false);
        recognizedImage = (ImageView) view.findViewById(R.id.imageView9 );
        recognizedName = (TextView) view.findViewById(R.id.Name );
        recognizedRelation = (TextView) view.findViewById(R.id.prelation );
        recognizedDetails = (TextView) view.findViewById(R.id.pdetails );
        startDialog_ADD_REC();
        //startDialog();
        return view;
    }
    private void startDialog_ADD_REC(){
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        myAlertDialog.setTitle("Choose an action");
        myAlertDialog.setMessage("Do you want to add Faces or Recognize face?");
        myAlertDialog.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(getActivity(), AddFacesActivity.class);
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }
                });
        myAlertDialog.setNegativeButton("Recognize",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        startDialog();
                    }
                });
        myAlertDialog.show();



    }
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                Toast.makeText(getContext(),

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
               // bitmap = Bitmap.createBitmap(bitmap, 0, 0,400,
                 //       550, matrix, true);
                bitmap = getResizedBitmap(bitmap);
                uploadImage(bitmap);
                //Draw (30,30,300,300);
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };


                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
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
               // bitmap = Bitmap.createBitmap(bitmap, 0, 0,400,
              //         550, matrix, true);
                bitmap = getResizedBitmap(bitmap);
                uploadImage(bitmap);
               // Draw (30,30,300,300);

            } else {
                Toast.makeText(getContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }


    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.NO_WRAP);
        return temp;
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

    private void getInfo(int person_id, int user_id){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("person_id", String.valueOf(person_id));
        jsonParams.put("user_id", String.valueOf(user_id));

        String url = "http://192.168.43.176:5000/get/person";
        int time_out = 180000;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                        String result = null, name = null, relation = null, details=null;
                        try {
                            result = response.getString("status");
                            Log.d("result", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (result.toString().equals("success")) {
                            try {
                                name = response.getString("name");
                                Log.d("name", name);
                                if (name.length()!=0 && name!= null){
                                    recognizedName.setText("Name: "+name );
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                relation = response.getString("relation");
                                Log.d("relation", relation);

                                if (relation.length()!=0 && relation != null ){
                                    recognizedRelation.setText("Relation: "+ relation );

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                details = response.getString("details");
                                Log.d("details", details);

                                if(details.length()!=0 && details != null){

                                    recognizedDetails.setText("Details: "+ details);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }




                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),
                        "Error", Toast.LENGTH_SHORT).show();
                Log.d("st code", String.valueOf(error.networkResponse.statusCode));
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);




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
    private void uploadImage(Bitmap bitmap){
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("user_id", String.valueOf(user_id));
        jsonParams.put("image", BitMapToString(bitmap));
        String url = "http://192.168.43.176:5000/CV/recognize";
        int time_out = 180000;
        final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

                            String imgs= null;

                            try {
                                String cur_person = response.getString("prediction");
                                int top1= (int) response.getDouble("x1");
                                int top2 = (int) response.getDouble("x2");
                                int bottom1= (int) response.getDouble("y1");
                                int bottom2 = (int) response.getDouble("y2");
                                Draw(top1,bottom1,top2,bottom2);
                                if(!cur_person.equals("unknown")) getInfo(Integer.parseInt(cur_person), user_id);
                                else {
                                    recognizedName.setText("unknown");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),
                        "Error", Toast.LENGTH_SHORT).show();
                Log.d("st code", String.valueOf(error.networkResponse.statusCode));
                VolleyLog.e("rababresp", error.getMessage());
            }

        });
        RetryPolicy policy = new DefaultRetryPolicy(time_out, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }


    public void Draw (int left,int top,int right,int bottom)
    {
        Bitmap new_bitmap =  bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(new_bitmap);
        //canvas.drawColor(Color.LTGRAY);
        // Initialize a new Paint instance to draw the Rectangle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        Rect rectangle = new Rect(
                left, // Left
                top, // Top
                right, // Right
                bottom // Bottom
        );
        canvas.drawRect(rectangle,paint);
        recognizedImage.setImageBitmap(new_bitmap);
    }
}
