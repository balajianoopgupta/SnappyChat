package com.cmpe277.snappychat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by sudhindrathangavelu on 12/2/16.
 */


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> implements View.OnClickListener {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    AndroidChatClient chatClient=AndroidChatClient.getInstance();
    @Override
    public void onClick(View view) {
        TextView userEmail = (TextView) view.findViewById(R.id.chatuserEmail);
        TextView userNickName = (TextView) view.findViewById(R.id.chatuserNickName);
        TextView userMessageType = (TextView) view.findViewById(R.id.chatusermessageType);
        ImageView userImage = (ImageView) view.findViewById(R.id.chatuserImage);
        Log.i("EMAIL ON CLICK NEW CHAT",userEmail.getText().toString());
        userMessageType.setText("");
        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("ToEmail",userEmail.getText().toString());
        i.putExtra("Nickname",userNickName.getText().toString());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

     /*   AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment myFragment = new ProfileFragment();
        activity.getSupportFragmentManager().beginTransaction().replace(R.layout.fragment_profile, myFragment).addToBackStack(null).commit();
        */

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userEmail,userMessage,userMessageType;
        ImageView userImage;
        public ViewHolder(View friendlistlayout) {
            super(friendlistlayout);
            friendlistlayout.setOnClickListener(ChatListAdapter.this);
            userName = (TextView) itemView.findViewById(R.id.chatuserNickName);
            userEmail = (TextView) itemView.findViewById(R.id.chatuserEmail);
            userImage = (ImageView)itemView.findViewById(R.id.chatuserImage);
            userMessage = (TextView) itemView.findViewById(R.id.chatuserMessage);
            userMessageType = (TextView) itemView.findViewById(R.id.chatusermessageType);

        }
    }
    public ChatListAdapter(Context context, List<ChatMessage> itemList) {
        this.itemList = itemList;
        this.context = context;
    }
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatlistlayout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder holder, final int position){

        String email=itemList.get(position).email;
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

        //Display User Status
        holder.userMessage.setText(itemList.get(position).message);

        if(chatClient.offlinemsglist.contains(email)){
            holder.userMessageType.setText("NEW");
        }
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