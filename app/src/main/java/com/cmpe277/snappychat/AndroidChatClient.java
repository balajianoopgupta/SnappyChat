package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.Stack;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

public class AndroidChatClient implements Runnable {
    private Socket socket              = null;
    private Thread thread              = null;
    CountDownTimer timer;
    int radiobuttonid;
    OutputStream outputStream = null;
    ObjectOutputStream dataOutputStream = null;
    private static AndroidChatClient ChatClient = null;
    private boolean runHandler = true;
    //private DataOutputStream streamOut = null;
    private AndroidChatClientThread client    = null;
    public ChatMessage returnmessage=new ChatMessage();
    public ChatMessage sendChatMessage=new ChatMessage();

    public ChatMessage getUsermessage=new ChatMessage();
    public ChatMessage sendUserMessage=new ChatMessage();

    public ChatMessage newMessageCheck=new ChatMessage();
    public ChatMessage res_newMessageCheck=new ChatMessage();

    public ArrayList<ChatMessage> onlinemessage=new ArrayList<ChatMessage>();


    public Stack<ChatMessage> returnlistmsg=new Stack<ChatMessage>();
    public String Loginemail="";

    public boolean send=false;
    public boolean sendUser=false;
    public boolean sendofflinecheck=false;

    public ArrayList<String> offlinemsglist=new ArrayList<String>();
    protected AndroidChatClient(){

    }
    public static synchronized  AndroidChatClient getInstance() {
        if(ChatClient == null) {
            ChatClient = new AndroidChatClient();
        }
        return ChatClient;
    }
    public void createconnection(String serverName, int serverPort,String email)
    {
        Log.i("Connection","Establishing connection. Please wait ...");
        try
        {
            if(socket==null){
                socket = new Socket(serverName, serverPort);
            }
            else{
                Log.i("socket","socket active");
            }
            Log.i("Connection","Connected: " + socket);
            Loginemail=email;
            open();
            start();
            /*AndroidChatClient chatClient = AndroidChatClient.getInstance();
            chatClient.sendChatMessage = new ChatMessage();
            chatClient.sendChatMessage.command = "GET_OFFLINE_MSG";
            // Log.i("send", "Setting to true");
            chatClient.send = true;*/
           calltimer();








        }
        catch(UnknownHostException uhe)
        {
            Log.e("Connection","Host unknown: " + uhe.getMessage());
        }
        catch(IOException ioe)
        {
            Log.e("Connection","Unexpected exception: " + ioe.getMessage());
        }
    }
    public void setSendChatMessage(ChatMessage chmsg){
        sendChatMessage=chmsg;
    }
    public ChatMessage getSendChatMessage(ChatMessage chmsg){
        return sendChatMessage;
    }
    public void run()
    {
        while (thread != null)
        {
            if(send==true) {
                try {
                    Log.i("write","write to stream");
                    if(sendChatMessage.command!=null)
                        Log.i("command",sendChatMessage.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(sendChatMessage);
                    dataOutputStream.flush();
                    send=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            else if(sendUser==true){
                try {
                    Log.i("write","write to stream");
                    if(sendUserMessage.command!=null)
                        Log.i("command",sendUserMessage.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(sendUserMessage);
                    dataOutputStream.flush();
                    sendUser=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            else if(sendofflinecheck==true){
                try {
                    Log.i("write","write to stream");
                    if(newMessageCheck.command!=null)
                        Log.i("command",newMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(newMessageCheck);
                    dataOutputStream.flush();
                    sendofflinecheck=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
        }
    }
    public void handle(ChatMessage chmsg) {
        Log.i("Res","response recvd");
        returnmessage = new ChatMessage();
        getUsermessage = new ChatMessage();
        if (chmsg!=null && chmsg.command.equals("RESPONSE_LOGOUT"))
        {

            Log.i("Connection", "Good bye. Press RETURN to exit ...");
            stop();
        }
        else if(chmsg!=null && chmsg.command.equals("RESPONSE_UPDATE_HISTORY")){

            getUsermessage = chmsg;
            Log.i("Command",getUsermessage.command);
            Log.i("Message:",getUsermessage.message);
        }
        else{
            returnmessage=chmsg;
            Log.i("Command",returnmessage.command);
            Log.i("Message:",returnmessage.message);
        }

    }
    public void handleList(Stack<ChatMessage> chmsg)
    {
       // send=true;
        returnmessage = new ChatMessage();
        returnmessage=chmsg.pop();
        Log.i("Command:",returnmessage.command);
        Log.i("Message:",returnmessage.message);
        returnlistmsg = new Stack<ChatMessage>();
        returnlistmsg=chmsg;

       /* while(!returnlistmsg.isEmpty()){
            ChatMessage retmsg=returnlistmsg.pop();
            System.out.println("Email:"+retmsg.email);
            System.out.println("Message:"+retmsg.message);
        }*/
    }
    public void handleUserList(ArrayList<ChatMessage> chmsg){
        onlinemessage = new ArrayList<ChatMessage>();
        onlinemessage=chmsg;
        getUsermessage = new ChatMessage();
        if(onlinemessage.size()>1)
        {

            getUsermessage.command="RESPONSE_GET_HISTORY";
            getUsermessage.message="SUCCESS";
        }
        else{
            getUsermessage.command="RESPONSE_GET_HISTORY";
            getUsermessage.message="FAILURE";
        }
        Log.i("Command:",getUsermessage.command);
        Log.i("Message:",getUsermessage.message);
    }
    public void handleArrayList(ArrayList<String> chmsg){
        offlinemsglist = new ArrayList<String>();
        offlinemsglist=chmsg;
        res_newMessageCheck = new ChatMessage();
        if(offlinemsglist.size()>1)
        {

            res_newMessageCheck.command="RESPONSE_GET_OFFLINE_MSG";
            res_newMessageCheck.message="SUCCESS";
        }
        else{
            res_newMessageCheck.command="RESPONSE_GET_OFFLINE_MSG";
            res_newMessageCheck.message="FAILURE";
        }
        Log.i("Command:",res_newMessageCheck.command);
        Log.i("Message:",res_newMessageCheck.message);
    }
    public void start() throws IOException
    {

        ChatMessage chatmsg=new ChatMessage();
        chatmsg.email=Loginemail;
        try {
            //dataOutputStream.writeObject(chatmsg);
            dataOutputStream.writeObject(chatmsg);
            //streamOut.writeUTF("sudhindra");
            dataOutputStream.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (thread == null)
        {
            client = new AndroidChatClientThread(this, socket);
            thread = new Thread(AndroidChatClient.getInstance());
            thread.start();
        }
    }
    public void open()
    {
        try
        {
            if(outputStream==null) {
                outputStream = socket.getOutputStream();
            }
            if(dataOutputStream==null) {
                dataOutputStream = new ObjectOutputStream(outputStream);
            }
        }
        catch(IOException ioe)
        {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }
    public void stop()
    {
        if (thread != null)
        {
            thread = null;
        }
        try
        {
            if (dataOutputStream != null) {
                dataOutputStream.close();
                dataOutputStream=null;
            }
            if(outputStream!=null)
            {
                outputStream.close();
                outputStream=null;
            }
            if (socket    != null)  {
                socket.close();
                socket=null;
            }
        }
        catch(IOException ioe)
        {
            Log.i("Connection", "Error closing ...");
        }
        client.close();
    }

    public void calltimer(){

        HandlerThread thread = new HandlerThread("OfflineThread");
        thread.start();
        final Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            public void run() {
                if(runHandler){
                    AndroidChatClient chatClient = AndroidChatClient.getInstance();
                    chatClient.newMessageCheck = new ChatMessage();
                    chatClient.newMessageCheck.command = "GET_OFFLINE_MSG";
                    Log.i("send", "Call from timer");
                    chatClient.sendofflinecheck = true;
                    handler.postDelayed(this, 10000);
                }
            }
        });

     /*
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                CountDownTimer cdt5  = new CountDownTimer(30000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        AndroidChatClient chatClient = AndroidChatClient.getInstance();
                        chatClient.newMessageCheck = new ChatMessage();
                        chatClient.newMessageCheck.command = "GET_OFFLINE_MSG";
                         Log.i("send", "Call from timer");
                        chatClient.sendofflinecheck = true;
                    }
                }.start();
            }
        });*/
    }

    public  void destroyHandler(){
        runHandler=false;
    }
}
