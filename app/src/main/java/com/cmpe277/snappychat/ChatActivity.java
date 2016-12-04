package com.cmpe277.snappychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ChatActivity extends AppCompatActivity {
    String toEmail;
    String fromemail;
    String nickname;
    int count=200;
    volatile boolean stop = false;
    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private SimpleMessageAdapter adapter;
    private ArrayList<SimpleChatMessage> chatHistory;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            toEmail =(String) b.get("ToEmail");
            fromemail=AndroidChatClient.getInstance().Loginemail;
            nickname=(String) b.get("Nickname");
            Log.i("Fromemail",fromemail);
            Log.i("ToEmail",toEmail);

        }
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nickname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //setContentView(R.layout.activity_chat);
        initControls();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        onBackPressed();
        return true;
    }


    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        meLabel.setText("me");
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText(nickname);// Hard Coded
        loadHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                Queue<String> que=new LinkedList<String>();
                que.offer(messageText);
                String msgtext=que.peek();
                while(!que.isEmpty()) {
                    final String text=que.poll();
                    Thread	thread=new Thread(new Runnable() {
                        public void run() {
                            AndroidChatClient chatClient=AndroidChatClient.getInstance();
                            chatClient.sendChatMessage=new ChatMessage();
                            chatClient.sendChatMessage.command="SEND_MESSAGE";
                            chatClient.sendChatMessage.message=text;
                            chatClient.sendChatMessage.email=toEmail;
                            chatClient.send=true;
                            boolean checkresponse=false;
                            while(!checkresponse){
                                if(chatClient.returnmessage!=null && chatClient.returnmessage.command!=null) {
                                    if (chatClient.returnmessage.command.equals("RESPONSE_SEND_MESSAGE")) {

                                        ChatMessage chmessage = chatClient.returnmessage;
                                        if (!chmessage.message.equals("FAILURE")) {
                                            checkresponse = true;
                                        }
                                        else{
                                            chatClient.sendChatMessage=new ChatMessage();
                                            chatClient.sendChatMessage.command="SEND_MESSAGE";
                                            chatClient.sendChatMessage.message=text;
                                            chatClient.sendChatMessage.email=toEmail;
                                            chatClient.send=true;
                                        }
                                    }
                                }

                            }
                        }
                    });

                    thread.start();


                }
                SimpleChatMessage chatMessage = new SimpleChatMessage();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(msgtext);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                messageET.setText("");
                displayMessage(chatMessage);



            }
        });
    }

    public void displayMessage(SimpleChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {

        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadHistory(){

        chatHistory = new ArrayList<SimpleChatMessage>();

        adapter = new SimpleMessageAdapter(ChatActivity.this, new ArrayList<SimpleChatMessage>());
        messagesContainer.setAdapter(adapter);

        thread=new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient=AndroidChatClient.getInstance();
                chatClient.sendUserMessage=new ChatMessage();
                chatClient.sendUserMessage.command="GET_HISTORY";
                chatClient.sendUserMessage.email=toEmail;
                Log.i("send","Setting to true");
                chatClient.sendUser=true;
                String IDs="";
                while(!stop){
                    // Log.i("herer","response");

                    if(chatClient.getUsermessage!=null && chatClient.getUsermessage.command!=null && chatClient.sendUser==false) {
                        if (chatClient.getUsermessage.command.equals("RESPONSE_GET_HISTORY")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.getUsermessage;
                            chatClient.sendUser=true;
                            Log.i("HISTORY",chmessage.message);
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg
                                ArrayList<ChatMessage> retmsg=chatClient.onlinemessage;


                                IDs="";
                                for(int i=1;i<retmsg.size();i++){
                                    SimpleChatMessage msg = new SimpleChatMessage();
                                    msg.setId(count);
                                    IDs=IDs+retmsg.get(i).ID+",";
                                    msg.setMe(false);
                                    msg.setMessage(retmsg.get(i).message);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    msg.setDate(sdf.format(retmsg.get(i).senddatetime));
                                    chatHistory.add(msg);
                                    Log.i("EMPTY","NOT EMPTY");
                                    count++;

                                }

                                Log.i("COUNT",String.valueOf(count));
                                ChatActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapter = new SimpleMessageAdapter(ChatActivity.this, new ArrayList<SimpleChatMessage>());
                                        messagesContainer.setAdapter(adapter);
                                        for(int i=0; i<chatHistory.size(); i++) {
                                            SimpleChatMessage message = chatHistory.get(i);
                                            displayMessage(message);
                                        }

                                    }
                                });





                                chatClient.sendUserMessage=new ChatMessage();
                                chatClient.sendUserMessage.command="UPDATE_HISTORY";
                                chatClient.sendUserMessage.email=toEmail;
                                chatClient.sendUserMessage.message=IDs;
                                chatClient.sendUser=true;

                            }
                            else{

                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    chatClient.sendUserMessage = new ChatMessage();
                                    chatClient.sendUserMessage.command = "GET_HISTORY";
                                    chatClient.sendUserMessage.email = toEmail;
                                    //chatClient.getUsermessage=new ChatMessage();
                                    chatClient.sendUser = true;

                            }
                        }
                        else if(chatClient.getUsermessage.command.equals("RESPONSE_UPDATE_HISTORY")  && chatClient.sendUser==false){
                            IDs="";

                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            chatClient.sendUserMessage = new ChatMessage();
                            chatClient.sendUserMessage.command = "GET_HISTORY";
                            chatClient.sendUserMessage.email = toEmail;
                            // chatClient.getUsermessage=new ChatMessage();
                            chatClient.sendUser = true;


                        }
                    }

                }
            }
        });
        thread.start();



    }

    @Override
    public void onDestroy() {
        stop = true;
        super.onDestroy();
        thread.interrupt();

    }
}
