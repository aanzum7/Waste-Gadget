package com.example.sellwastepaper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private List<Post> postList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView _quan,_date;
        ImageView _image;

        public MyViewHolder(View view) {
            super(view);
            _quan = view.findViewById(R.id.quan);
            _date = view.findViewById(R.id.date);
            _image = view.findViewById(R.id.thumbnail);
        }
    }


    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Post post = postList.get(position);
        holder._quan.setText(post.getQuantity().trim());
        holder._date.setText(post.getDate().toString().trim());

        Glide.with(context)
                .load(post.getImgUrl())
                .into(holder._image);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}
