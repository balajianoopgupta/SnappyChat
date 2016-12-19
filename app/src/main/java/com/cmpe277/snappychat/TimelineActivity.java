package com.cmpe277.snappychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class TimelineActivity extends AppCompatActivity {

    String nickname, userEmail;
    RecyclerView userTimeline;
    private DisplayTimelineAdapter userTimelineAdapter;
    private RecyclerView.LayoutManager userTimelineLayoutManager;
    List<ChatMessage> rowListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            userEmail = (String) b.get("UserEmail");
            Log.i("UserEmail",userEmail);

            nickname=(String) b.get("UserNickName");
            Log.i("UserNickName",nickname);
        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nickname+"'s Timeline");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //setContentView(R.layout.activity_chat);
        initControls();
    }

    private void initControls() {

        userTimeline = (RecyclerView) findViewById(R.id.searchTimeLineActivityList);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        userTimeline.setHasFixedSize(true);

        loadTimeLine(userEmail);
    }

    public void displayMessage(ChatMessage message) {
        rowListItem.add(message);
        userTimelineAdapter.notifyDataSetChanged();

    }

    private void loadTimeLine(final String userEmail) {

//        userTimelineAdapter =new TimeLineAdapter(this,rowListItem,this);
//        userTimelineLayoutManager = new LinearLayoutManager(this.getApplicationContext());
//        userTimeline.setAdapter(userTimelineAdapter);
//        userTimeline.setLayoutManager(userTimelineLayoutManager);

        new Thread(new Runnable() {
            public void run() {
                final AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_timeline = new ChatMessage();
                chatClient.request_timeline.command = "GET_TIMELINE_DATA";
                chatClient.request_timeline.email = userEmail;
                Log.i("timeline", "Setting to true");
                chatClient.sendTimeLineList = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_timeline != null && chatClient.response_timeline.command != null) {
                        if (chatClient.response_timeline.command.equals("RESPONSE_GET_TIMELINE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_timeline;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {

                                rowListItem=chatClient.timeLineList;

                                TimelineActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        userTimelineAdapter =new DisplayTimelineAdapter(getApplicationContext(),rowListItem,TimelineActivity.this);
                                        userTimelineLayoutManager = new LinearLayoutManager(TimelineActivity.this.getApplicationContext());
                                        userTimeline.setAdapter(userTimelineAdapter);
                                        userTimeline.setLayoutManager(userTimelineLayoutManager);
                                    }
                                });
                            }
                        }
                    }

                }
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
