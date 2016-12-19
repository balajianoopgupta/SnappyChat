package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.cmpe277.snappychat.R.id.acceptBtn;
import static com.cmpe277.snappychat.R.id.time;
import static com.cmpe277.snappychat.R.id.view_offset_helper;

/**
 * Created by balaji.byrandurga on 12/1/16.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    Context ApplicationContext;
    Activity activity;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userStatus;
        ImageView userImage, friends, pendingFriend;
        ImageButton chat, timeline, addFriend;

        public ViewHolder(View searchlistlayout) {
            super(searchlistlayout);
            //  searchlistlayout.setOnClickListener(this);
            userName = (TextView) itemView.findViewById(R.id.searchUserNickName);
            userEmail = (TextView) itemView.findViewById(R.id.searchUserEmail);
            userImage = (ImageView) itemView.findViewById(R.id.searchUserImage);
            userStatus = (TextView) itemView.findViewById(R.id.searchUserStatus);
            //usertype = (TextView) itemView.findViewById(R.id.usertype);
            friends = (ImageView) itemView.findViewById(R.id.searchFriends);
            addFriend = (ImageButton) itemView.findViewById(R.id.searchAddFriend);

            pendingFriend = (ImageView) itemView.findViewById(R.id.searchPendingFriend);

            chat = (ImageButton) itemView.findViewById(R.id.searchChat);
            timeline = (ImageButton) itemView.findViewById(R.id.searchTimelineBtns);

//            timeline.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(context, TimelineActivity.class);
//                    i.putExtra("ToEmail",userEmail.getText().toString());
//
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(i);
//                }
//            });

        }
    }

    public SearchListAdapter(Context context, List<ChatMessage> itemList, Activity activity) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        ApplicationContext = context;
    }

    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchlistlayout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final SearchListAdapter.ViewHolder holder, final int position) {
        holder.userName.setText(itemList.get(position).nickname);
        holder.userEmail.setText(itemList.get(position).email);

        ByteArrayInputStream is = new ByteArrayInputStream(itemList.get(position).pic);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        holder.userImage.setImageBitmap(bitmap);

        holder.userImage.setTag(holder);

        //Display User Status
        if (itemList.get(position).status == true)
            holder.userStatus.setText("ONLINE");
        else
            holder.userStatus.setText("OFFLINE");


        if (itemList.get(position).usertype.equals("FRIEND")) {
            holder.friends.setVisibility(View.VISIBLE);
            holder.addFriend.setVisibility(View.INVISIBLE);
            holder.pendingFriend.setVisibility(View.INVISIBLE);
            holder.timeline.setVisibility(View.VISIBLE);
            holder.chat.setVisibility(View.VISIBLE);
        } else {
            holder.friends.setVisibility(View.INVISIBLE);
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.timeline.setVisibility(View.VISIBLE);
            holder.chat.setVisibility(View.INVISIBLE);
            holder.pendingFriend.setVisibility(View.INVISIBLE);
        }


        final ViewHolder viewH = holder;
        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startChat(viewH.getAdapterPosition(), viewH);
            }
        });

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addFriend(viewH.getAdapterPosition(), viewH);
            }
        });

        holder.timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, TimelineActivity.class);
                i.putExtra("UserEmail",holder.userEmail.getText().toString());
                i.putExtra("UserNickName",holder.userName.getText().toString());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });



    }

    private void addFriend(final int adapterPosition, final ViewHolder viewH) {

        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_addfriend = new ChatMessage();
                chatClient.request_addfriend.command = "FRIEND_REQUEST";
                chatClient.request_addfriend.email = itemList.get(adapterPosition).email;

                Log.i("send", "Setting to true");
                chatClient.sendFriendRequest = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_addfriend != null && chatClient.response_addfriend.command != null) {
                        if (chatClient.response_addfriend.command.equals("RESPONSE_FRIEND_REQUEST")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_addfriend;
                            checkresponse = true;

                            final ViewHolder v = viewH;
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.addFriend.setVisibility(View.INVISIBLE);
                                        v.pendingFriend.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void startChat(int pos, ViewHolder viewH) {

        Intent i = new Intent(context, ChatActivity.class);
        i.putExtra("ToEmail", itemList.get(pos).email);
        i.putExtra("Nickname", itemList.get(pos).nickname);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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