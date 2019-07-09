package com.javapapers.android.introslider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {
    byte[] imgBytes;
    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_member, parent, false);
        }
        // Lookup view for data population
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image1);
        TextView memname = (TextView) convertView.findViewById(R.id.text1);
        TextView memrelation = (TextView) convertView.findViewById(R.id.text2);
        // Populate the data into the template view using the data object
        if (user.img.equals("no image")){
            imageView.setImageResource(R.drawable.person);


        }
        else {
            imgBytes = Base64.decode(user.img, Base64.DEFAULT);
            Bitmap decodedImg = BitmapFactory.decodeByteArray(imgBytes,0, imgBytes.length);
            imageView.setImageBitmap(decodedImg);
        }
        //imageView.setImageResource(user.img);
        memname.setText(user.name);
        memrelation.setText(user.relation);
        // Return the completed view to render on screen
        return convertView;
    }
}