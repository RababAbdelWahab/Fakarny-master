package com.javapapers.android.introslider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class Profile_Fragment extends Fragment {
    TextView nameText, phoneText ,emailText , addressText;
    String name,phone,email ,address,img;
    ImageView avatar;
    int user_id;
    byte [] imgBytes;
    @SuppressLint("ValidFragment")
    public Profile_Fragment(int user_id,String img ,String name, String phone, String email, String address) {
        this.user_id = user_id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.img = img;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        nameText = (TextView) view.findViewById(R.id.name);
        phoneText = (TextView) view.findViewById(R.id.phone);
        addressText = (TextView) view.findViewById(R.id.address);
        emailText = (TextView) view.findViewById(R.id.email );
        avatar= (ImageView) view.findViewById(R.id.avatar);
        nameText.setText("   "+name);
        phoneText.setText("   "+phone);
        emailText.setText("   "+email);
        addressText.setText("   "+address);
        if(!img.equals("no image")){
            imgBytes = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedImg = BitmapFactory.decodeByteArray(imgBytes,0, imgBytes.length);
            avatar.setImageBitmap(decodedImg);

        }
        return view;
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // TODO Add your menu entries here
        inflater.inflate(R.menu.mymenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mybutton) {
            Intent intent = new Intent(getContext(), EditProfile.class);
            intent.putExtra("username", name);
            intent.putExtra("userphone",  phone);
            intent.putExtra("useraddress",address);
            intent.putExtra("useremail",  email);
            intent.putExtra("img", img);
            intent.putExtra("user_id",user_id);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

}