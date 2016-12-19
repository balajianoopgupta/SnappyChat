package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ChatActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
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
        //onBackPressed();
        switch(menuItem.getItemId()) {
            case R.id.file_attach:
                Log.i("Clicked","Clicked attach");
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_attach, menu); //inflate our menu
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
                sendtoServer(messageText,"");

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
                chatClient.request_historymessage=new ChatMessage();
                chatClient.request_historymessage.command="GET_HISTORY";
                chatClient.request_historymessage.email=toEmail;

                Log.i("send","Setting to true");
                chatClient.sendhistory=true;
                String IDs="";
                while(!stop){
                    // Log.i("herer","response");

                    if(chatClient.response_historymessage!=null && chatClient.response_historymessage.command!=null && chatClient.sendhistory==false) {
                        if (chatClient.response_historymessage.command.equals("RESPONSE_GET_HISTORY")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_historymessage;
                            chatClient.sendhistory=true;
                            Log.i("HISTORY",chmessage.message);
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                ArrayList<ChatMessage> retmsg=new ArrayList<ChatMessage>();
                                retmsg=chatClient.historymsglist;
                              //  Log.i("chatClient.historymsglist","NOT EMPTY");
                                chatClient.historymsglist=new ArrayList<ChatMessage>();
                                IDs="";
                                for(int i=1;i<retmsg.size();i++){
                                    SimpleChatMessage msg = new SimpleChatMessage();
                                    msg.setId(count);
                                    IDs=IDs+retmsg.get(i).ID+",";
                                    msg.setMe(false);
                                    if(retmsg.get(i).files==null) {
                                        msg.setMessage(retmsg.get(i).message);
                                    }
                                    else{
                                        try {
                                                String root = Environment.getExternalStorageDirectory().toString();
                                                root = root + "/saved_images";
                                                //myDir=new File("/mnt/sdcard/saved_images");
                                                File myDir = new File(root);

                                                if (!myDir.exists()) {
                                                    Log.i("Directory", "does not exists");
                                                    myDir.mkdir();
                                                } else {
                                                    Log.i("Directory", "exists");
                                                }
                                                Log.i("Root", root);
                                                // String filename=root+"/"+chmessage.email.split("@")[0]+".png";
                                                String filename="Unchanged";
                                                if(toEmail!=null && !toEmail.isEmpty())
                                                    filename   = "/" + toEmail.split("@")[0] + "23432.png";
                                                else
                                                    Log.i("FILENAME",filename);
                                                Log.i("FILENAME",filename);
                                                File file = new File(myDir, filename);
                                                if (file.exists()) {
                                                    file.delete();
                                                    Log.i("file", "exists");
                                                } else {
                                                    Log.i("file", "does not exists");
                                                }

                                                FileOutputStream fos = new FileOutputStream(file);
                                                Log.i("Root1", filename);
                                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                                Log.i("Root2", filename);
                                                bos.write(retmsg.get(i).files);
                                                Log.i("Root3", filename);
                                                bos.flush();
                                                bos.close();
                                                fos.flush();
                                                fos.close();
                                                String imageloc=root+ filename;
                                                Log.i("imageloc",imageloc);
                                                msg.setMessage("file received");
                                                msg.setFilepath(imageloc);
                                        } catch (Exception e) {
                                            Log.i("File Exception chat",e.toString());
                                        }


                                    }
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
                                            Log.i("check message",String.valueOf(i));
                                            displayMessage(message);
                                        }

                                    }
                                });




                                if(!IDs.isEmpty()) {
                                    chatClient.request_historymessage = new ChatMessage();
                                    chatClient.request_historymessage.command = "UPDATE_HISTORY";
                                    chatClient.request_historymessage.email = toEmail;
                                    chatClient.request_historymessage.message = IDs;
                                    chatClient.sendhistory = true;
                                }

                            }
                            else{

                                    try {
                                        Thread.sleep(10000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        Log.i("EXCEPTION",e.toString());
                                    }
                                    chatClient.request_historymessage = new ChatMessage();
                                    chatClient.request_historymessage.command = "GET_HISTORY";
                                    chatClient.request_historymessage.email = toEmail;
                                    //chatClient.getUsermessage=new ChatMessage();
                                    chatClient.sendhistory = true;

                            }
                        }
                        else if(chatClient.response_historymessage.command.equals("RESPONSE_UPDATE_HISTORY")  && chatClient.sendhistory==false){
                            IDs="";

                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            chatClient.request_historymessage = new ChatMessage();
                            chatClient.request_historymessage.command = "GET_HISTORY";
                            chatClient.request_historymessage.email = toEmail;
                            // chatClient.getUsermessage=new ChatMessage();
                            chatClient.sendhistory = true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("FILEPATH", picturePath);
                /*ImageView imgView = (ImageView) findViewById(R.id.transferfile);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(picturePath));




                imgView.setImageBitmap(BitmapFactory.decodeFile(picturePath));*/

                SimpleChatMessage chatMessage = new SimpleChatMessage();
                chatMessage.setId(102);//dummy
                chatMessage.setMessage("File");
                chatMessage.setFilepath(picturePath);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                messageET.setText("");
                displayMessage(chatMessage);
                sendtoServer("",picturePath);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void sendtoServer(String messageText,final String Filepath){

        Queue<String> que=new LinkedList<String>();
        que.offer(messageText);
        String msgtext=que.peek();
        while(!que.isEmpty()) {
            final String text=que.poll();
            Thread	thread=new Thread(new Runnable() {
                public void run() {
                    AndroidChatClient chatClient=AndroidChatClient.getInstance();
                    chatClient.request_sendmessage=new ChatMessage();
                    chatClient.request_sendmessage.command="SEND_MESSAGE";
                    if(!Filepath.isEmpty()){
                        try {
                            File myFile = new File (Filepath);
                            byte [] mybytearray  = new byte [(int)myFile.length()];
                            FileInputStream fis = null;
                            fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(mybytearray,0,mybytearray.length);
                            chatClient.request_sendmessage.files=mybytearray;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        chatClient.request_sendmessage.message = text;
                        chatClient.request_sendmessage.files=null;
                    }
                    chatClient.request_sendmessage.email=toEmail;
                    chatClient.sendmessage=true;
                    boolean checkresponse=false;
                    while(!checkresponse){
                        if(chatClient.response_sendmessage!=null && chatClient.response_sendmessage.command!=null) {
                            if (chatClient.response_sendmessage.command.equals("RESPONSE_SEND_MESSAGE")) {

                                ChatMessage chmessage = chatClient.response_sendmessage;
                                if (!chmessage.message.equals("FAILURE")) {
                                    checkresponse = true;
                                }
                                else{
                                    chatClient.request_sendmessage=new ChatMessage();
                                    chatClient.request_sendmessage.command="SEND_MESSAGE";
                                    chatClient.request_sendmessage.message=text;
                                    chatClient.request_sendmessage.email=toEmail;
                                    chatClient.sendmessage=true;
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
}

