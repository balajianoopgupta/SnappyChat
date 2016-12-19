package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.content.ContextCompat;
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

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by balaji.byrandurga on 12/6/16.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    Context ApplicationContext;
    Activity activity;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userStatusMessage, timeStamp, timelineNickName, likeCount, dislikeCount;
        ImageView timelineImage;
        ImageButton likeBtn, dislikeBtn;

        public ViewHolder(View friendlistlayout) {
            super(friendlistlayout);
            timelineNickName = (TextView) itemView.findViewById(R.id.timelineNickName);
            userStatusMessage = (TextView) itemView.findViewById(R.id.userStatusMess);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            timelineImage = (ImageView) itemView.findViewById(R.id.timelineImage);
            likeBtn = (ImageButton) itemView.findViewById(R.id.likeBtn);
            dislikeBtn = (ImageButton) itemView.findViewById(R.id.dislikeBtn);
            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
            dislikeCount = (TextView) itemView.findViewById(R.id.dislikeCount);
        }
    }

    public TimeLineAdapter(Context context, List<ChatMessage> itemList, Activity activity) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        ApplicationContext = context;
    }

    @Override
    public TimeLineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timelinelistlayout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final TimeLineAdapter.ViewHolder holder, final int position) {

        holder.timelineNickName.setText(itemList.get(position).nickname);
        holder.userStatusMessage.setText(itemList.get(position).message);
        holder.timeStamp.setText(itemList.get(position).senddatetime.toString());
        holder.timeStamp.setText(itemList.get(position).senddatetime.toString());
        if(itemList.get(position).pic == null){
            Drawable draw = ContextCompat.getDrawable(context, R.drawable.image);
            holder.timelineImage.setImageDrawable(draw);
            holder.timelineImage.setTag(holder);
        }
        else{
            ByteArrayInputStream is = new ByteArrayInputStream(itemList.get(position).pic);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.timelineImage.setImageBitmap(bitmap);
            holder.timelineImage.setTag(holder);
        }

//        holder.likeCount.setText(itemList.get(position).likeCount);
//        holder.likeBtn.setVisibility(View.VISIBLE);
//
//        holder.dislikeCount.setText(itemList.get(position).dislikeCount);
//        holder.dislikeBtn.setVisibility(View.VISIBLE);
//
//        final ViewHolder viewH = holder;
//        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean Like_Dislike = true;
//                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
//                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
//                //  Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
//                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));
//
//                //Send_Like_Dislike(Like_Dislike, viewH.getAdapterPosition(), viewH);
//            }
//        });
//
//        holder.dislikeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean Like_Dislike = false;
//                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
//                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
//                // Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
//                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));
//
//                //Send_Like_Dislike(Like_Dislike, viewH.getAdapterPosition(), viewH);
//            }
//        });
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


    public void Send_Like_Dislike(boolean l_d, final int pos, final FriendsListAdapter.ViewHolder viewHolder) {

        Log.i("EMAIL", itemList.get(pos).email);
        Log.i("Like_Dislike", String.valueOf(l_d));
        final String usremail = itemList.get(pos).email;
        final String response;
        if (l_d) {
            response = "LIKE";
        } else {
            response = "DISLIKE";
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
