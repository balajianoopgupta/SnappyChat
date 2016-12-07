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

/**
 * Created by balaji.byrandurga on 12/6/16.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder>{

    private List<ChatMessage> itemList;
    private Context context;
    private TimeLineAdapter.ItemClickListener clickListener;
    Context ApplicationContext;
    Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userStatusMessage, timeStamp;
        ImageView statusImage;

        public ViewHolder(View timelinelistlayout) {
            super(timelinelistlayout);
            statusImage = (ImageView) itemView.findViewById(R.id.statusImage);
            userStatusMessage = (TextView) itemView.findViewById(R.id.userStatusMess);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
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
        TimeLineAdapter.ViewHolder vh = new TimeLineAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final TimeLineAdapter.ViewHolder holder, final int position) {

        ByteArrayInputStream is = new ByteArrayInputStream(itemList.get(position).pic);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        holder.statusImage.setImageBitmap(bitmap);

        holder.statusImage.setTag(holder);

        //Display User Status
        holder.userStatusMessage.setText(itemList.get(position).statusMessage);
        holder.timeStamp.setText((CharSequence) itemList.get(position).senddatetime);
        //holder.usertype.setText(itemList.get(position).usertype);
//        if (itemList.get(position).usertype.equals("FRIENDS")) {
//            holder.friends.setVisibility(View.VISIBLE);
//            holder.pending.setVisibility(View.INVISIBLE);
//            holder.acceptBtn.setVisibility(View.INVISIBLE);
//            holder.rejectBtn.setVisibility(View.INVISIBLE);
//        } else if (itemList.get(position).usertype.equals("PENDING")) {
//            holder.friends.setVisibility(View.INVISIBLE);
//            holder.pending.setVisibility(View.VISIBLE);
//            holder.acceptBtn.setVisibility(View.INVISIBLE);
//            holder.rejectBtn.setVisibility(View.INVISIBLE);
//        } else if (itemList.get(position).usertype.equals("ACCEPT_REJECT")) {
//            holder.friends.setVisibility(View.INVISIBLE);
//            holder.pending.setVisibility(View.INVISIBLE);
//            holder.acceptBtn.setVisibility(View.VISIBLE);
//            holder.rejectBtn.setVisibility(View.VISIBLE);
//        }

            final TimeLineAdapter.ViewHolder viewH = holder;
        Send_Accept_Reject(viewH.getAdapterPosition(), viewH);
//        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean Accept_Reject = true;
//                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
//                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
//                //  Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
//                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));
//
//                Send_Accept_Reject(Accept_Reject, viewH.getAdapterPosition(), viewH);
//            }
//        });
//
//        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean Accept_Reject = false;
//                //TextView ustype = (TextView) v.findViewById(R.id.usertype);
//                //Log.i("EMAIL ON CLICK",ustype.getText().toString());
//                // Log.i("Accept_Reject userImage",String.valueOf(Accept_Reject));
//                //Log.i("position = ", String.valueOf( viewH.getAdapterPosition()));
//                Send_Accept_Reject(Accept_Reject, viewH.getAdapterPosition(), viewH);
//            }
//        });
    }

    public void setClickListener(TimeLineAdapter.ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public interface ItemClickListener {
        public void itemClick(View view, int position);
    }

    public void Send_Accept_Reject(final int pos, final TimeLineAdapter.ViewHolder viewHolder) {
        final String usremail = itemList.get(pos).email;

        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_timeline = new ChatMessage();
                chatClient.request_timeline.command = "SEND_TIMELINE_DATA";
                chatClient.request_timeline.email = usremail;
                Log.i("send", "Setting to true");
                //chatClient.sendacceptreject = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_timeline != null && chatClient.response_timeline.command != null) {
                        if (chatClient.response_accept_reject.command.equals("RESPONSE_TIMELINE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_timeline;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //if accept then displays friends image and hide accept and reject buttons
//                                        if (response.equals("ACCEPT")) {
//                                            viewHolder.friends.setVisibility(View.VISIBLE);
//                                            viewHolder.acceptBtn.setVisibility(View.INVISIBLE);
//                                            viewHolder.rejectBtn.setVisibility(View.INVISIBLE);
//                                        }

                                        //if reject was selected remove item from itemlist
//                                        if (response.equals("REJECT")) {
//                                            itemList.remove(pos);
//                                            notifyDataSetChanged();
//                                        }
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
