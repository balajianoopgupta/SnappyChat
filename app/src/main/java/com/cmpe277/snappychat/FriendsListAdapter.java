package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import static com.cmpe277.snappychat.R.id.view_offset_helper;

/**
 * Created by balaji.byrandurga on 12/1/16.
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    Context ApplicationContext;
    Activity activity;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userEmail, userStatus, usertype;
        ImageView userImage, friends, pending;
        ImageView acceptBtn, rejectBtn;

        public ViewHolder(View friendlistlayout) {
            super(friendlistlayout);
            //  friendlistlayout.setOnClickListener(this);
            userName = (TextView) itemView.findViewById(R.id.userNickName);
            userEmail = (TextView) itemView.findViewById(R.id.userEmail);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userStatus = (TextView) itemView.findViewById(R.id.userStatus);
            //usertype = (TextView) itemView.findViewById(R.id.usertype);
            friends = (ImageView) itemView.findViewById(R.id.friends);
            pending = (ImageView) itemView.findViewById(R.id.pending);
            acceptBtn = (ImageView) itemView.findViewById(R.id.acceptBtn);
            rejectBtn = (ImageView) itemView.findViewById(R.id.rejectBtn);

        }
    }

    public FriendsListAdapter(Context context, List<ChatMessage> itemList, Activity activity) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        ApplicationContext = context;
    }

    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlistlayout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final FriendsListAdapter.ViewHolder holder, final int position) {
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


        //holder.usertype.setText(itemList.get(position).usertype);
        if (itemList.get(position).usertype.equals("FRIENDS")) {
            holder.friends.setVisibility(View.VISIBLE);
            holder.pending.setVisibility(View.INVISIBLE);
            holder.acceptBtn.setVisibility(View.INVISIBLE);
            holder.rejectBtn.setVisibility(View.INVISIBLE);
        } else if (itemList.get(position).usertype.equals("PENDING")) {
            holder.friends.setVisibility(View.INVISIBLE);
            holder.pending.setVisibility(View.VISIBLE);
            holder.acceptBtn.setVisibility(View.INVISIBLE);
            holder.rejectBtn.setVisibility(View.INVISIBLE);
        } else if (itemList.get(position).usertype.equals("ACCEPT_REJECT")) {
            holder.friends.setVisibility(View.INVISIBLE);
            holder.pending.setVisibility(View.INVISIBLE);
            holder.acceptBtn.setVisibility(View.VISIBLE);
            holder.rejectBtn.setVisibility(View.VISIBLE);
        }

        final ViewHolder viewH = holder;
        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Accept_Reject = true;
                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
                //  Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));

                Send_Accept_Reject(Accept_Reject, viewH.getAdapterPosition(), viewH);
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean Accept_Reject = false;
                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
                // Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));
                Send_Accept_Reject(Accept_Reject, viewH.getAdapterPosition(), viewH);
            }
        });
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

    public void Send_Accept_Reject(boolean a_r, final int pos, final FriendsListAdapter.ViewHolder viewHolder) {

        Log.i("EMAIL", itemList.get(pos).email);
        Log.i("ACCEPT_REJECT", String.valueOf(a_r));
        final String usremail = itemList.get(pos).email;
        final String response;
        if (a_r) {
            response = "ACCEPT";
        } else {
            response = "REJECT";
        }
        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_accept_reject = new ChatMessage();
                chatClient.request_accept_reject.command = "ACCEPT_REJECT_FRIEND_REQUEST";
                chatClient.request_accept_reject.email = usremail;
                chatClient.request_accept_reject.message = response;
                Log.i("send", "Setting to true");
                chatClient.sendacceptreject = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_accept_reject != null && chatClient.response_accept_reject.command != null) {
                        if (chatClient.response_accept_reject.command.equals("RESPONSE_ACCEPT_REJECT_FRIEND_REQUEST")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_accept_reject;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //if accept then displays friends image and hide accept and reject buttons
                                        if (response.equals("ACCEPT")) {
                                            viewHolder.friends.setVisibility(View.VISIBLE);
                                            viewHolder.acceptBtn.setVisibility(View.INVISIBLE);
                                            viewHolder.rejectBtn.setVisibility(View.INVISIBLE);
                                        }

                                        //if reject was selected remove item from itemlist
                                        if (response.equals("REJECT")) {
                                            itemList.remove(pos);
                                            notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }).start();
    }
}