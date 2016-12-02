package com.cmpe277.snappychat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by balaji.byrandurga on 12/1/16.
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userEmail;
        ImageView userImage;
        public ViewHolder(View friendlistlayout) {
            super(friendlistlayout);
            userName = (TextView) itemView.findViewById(R.id.userNickName);
            userEmail = (TextView) itemView.findViewById(R.id.userEmail);
            userImage = (ImageView)itemView.findViewById(R.id.userImage);
        }
    }
    public FriendsListAdapter(Context context, List<ChatMessage> itemList) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlistlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(FriendsListAdapter.ViewHolder holder, final int position){
        holder.userName.setText(itemList.get(position).nickname);
        holder.userEmail.setText(itemList.get(position).email);

        ByteArrayInputStream is = new ByteArrayInputStream(itemList.get(position).pic);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        holder.userImage.setImageBitmap(bitmap);
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) clickListener.itemClick(v, position);
            }
        });

        holder.userImage.setTag(holder);
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
    public interface ItemClickListener {
        public void itemClick(View view, int position);
    }
}