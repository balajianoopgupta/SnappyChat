package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by balaji.byrandurga on 12/18/16.
 */

public class DisplayTimelineAdapter extends RecyclerView.Adapter<DisplayTimelineAdapter.ViewHolder> {

    private List<ChatMessage> itemList;
    private Context context;
    private ItemClickListener clickListener;
    Context ApplicationContext;
    Activity activity;


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userTimelineStatusMess, userTimelineTimeStamp, userTimelineNickName;
        ImageView userTimelineImage;
        //ImageButton likeBtn, dislikeBtn;

        public ViewHolder(View displaytimelinelayout) {
            super(displaytimelinelayout);
            //userTimelineNickName = (TextView) itemView.findViewById(R.id.userTimelineNickName);
            userTimelineStatusMess = (TextView) itemView.findViewById(R.id.userTimelineStatusMess);
            userTimelineTimeStamp = (TextView) itemView.findViewById(R.id.userTimelineTimeStamp);
            userTimelineImage = (ImageView) itemView.findViewById(R.id.userTimelineImage);
//            likeBtn = (ImageButton) itemView.findViewById(R.id.likeBtn);
//            dislikeBtn = (ImageButton) itemView.findViewById(R.id.dislikeBtn);
//            likeCount = (TextView) itemView.findViewById(R.id.likeCount);
//            dislikeCount = (TextView) itemView.findViewById(R.id.dislikeCount);
        }
    }

    public DisplayTimelineAdapter(Context context, List<ChatMessage> itemList, Activity activity) {
        this.itemList = itemList;
        this.context = context;
        this.activity = activity;
        ApplicationContext = context;
    }

    @Override
    public DisplayTimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.displaytimelinelayout, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final DisplayTimelineAdapter.ViewHolder holder, final int position) {

        //holder.userTimelineNickName.setText(itemList.get(position).nickname);
        holder.userTimelineStatusMess.setText(itemList.get(position).message);
        holder.userTimelineTimeStamp.setText(itemList.get(position).senddatetime.toString());

        if(itemList.get(position).pic == null){
            Drawable draw = ContextCompat.getDrawable(context, R.drawable.image);
            holder.userTimelineImage.setImageDrawable(draw);
            holder.userTimelineImage.setTag(holder);
        }
        else{
            ByteArrayInputStream is = new ByteArrayInputStream(itemList.get(position).pic);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.userTimelineImage.setImageBitmap(bitmap);
            holder.userTimelineImage.setTag(holder);
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


}
