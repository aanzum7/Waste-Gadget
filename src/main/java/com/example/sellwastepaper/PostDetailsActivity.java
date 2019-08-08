package com.example.sellwastepaper;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PostDetailsActivity extends AppCompatActivity {

    private TextView desc,address,phn;
    private ImageView imageView;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_details);

        desc = findViewById(R.id.des);
        address = findViewById(R.id.address);
        phn = findViewById(R.id.phone);
        imageView = findViewById(R.id.thumbnail);

        post = (Post) getIntent().getSerializableExtra("post");

        Glide.with(this)
                .load(post.getImgUrl())
                .into(imageView);
        desc.setText(post.getDesc().trim()+ "\n\n Quantity: "+post.getQuantity().trim());
        phn.setText(post.getPhone().trim());
        address.setText(post.getAddress().trim());

    }

    public void call(View view) {
        String phone = post.getPhone().trim();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }
}
