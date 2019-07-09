package com.javapapers.android.introslider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberActivity extends AppCompatActivity {
    TextView nameText, phoneText ,emailText , addressText, relationText;
    String name,phone,email ,address,relation , img;
    ImageView memAvatar;
    int member_id, user_id;
    byte [] imgBytes;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        nameText = (TextView) findViewById(R.id.name);
        phoneText = (TextView) findViewById(R.id.phone);
        addressText = (TextView) findViewById(R.id.address);
        emailText = (TextView) findViewById(R.id.email );
        relationText = (TextView) findViewById(R.id.relation );
        memAvatar = (ImageView)findViewById(R.id.memimg);
        //name = nameText.getText().toString();
        //phone = phoneText.getText().toString();
        //email= emailText.getText().toString();
        //address = addressText.getText().toString();
        //relation = relationText.getText().toString();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle == null)
        { }
        else {
            name= bundle.getString("name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            address = bundle.getString("address");
            relation = bundle.getString("relation");
            img = bundle.getString("img");
            user_id= bundle.getInt("user_id");

        }
        nameText.setText(name);
        emailText.setText(email);
        addressText.setText(address);
        phoneText.setText(phone);
        relationText.setText(relation);
        if(img.equals("no image")){
            memAvatar.setImageResource(R.drawable.person);




        }
        else {
            imgBytes = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedImg = BitmapFactory.decodeByteArray(imgBytes,0, imgBytes.length);
            memAvatar.setImageBitmap(decodedImg);

        }



    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent intent = new Intent(MemberActivity.this, EditMemberActivity.class);
            intent.putExtra("username", name);
            intent.putExtra("userphone",  phone);
            intent.putExtra("useraddress",address);
            intent.putExtra("useremail",  email);
            intent.putExtra("userrelation",  relation);
            intent.putExtra("img",img);
            intent.putExtra("user_id",user_id);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()
    {

            super.onBackPressed();
            Intent intent = new Intent(MemberActivity.this, MembersActivity.class);
            intent.putExtra("user_id",user_id);
            startActivity(intent);
            this.finish();

    }
}
