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
 //   public ChatMessage returnmessage=new ChatMessage();
  //  public ChatMessage sendChatMessage=new ChatMessage();

   // public ChatMessage getUsermessage=new ChatMessage();
   // public ChatMessage sendUserMessage=new ChatMessage();

    //Offline Message
    public ChatMessage request_offlineMessageCheck=new ChatMessage();
    public ChatMessage response_offlineMessageCheck=new ChatMessage();
    public ArrayList<String> offlinemsglist=new ArrayList<String>();
    public boolean sendofflinecheck=false;

    //ChatList Message
    public ChatMessage request_chatMessageCheck=new ChatMessage();
    public ChatMessage response_chatMessageCheck=new ChatMessage();
    public Stack<ChatMessage> chatmsglist=new Stack<ChatMessage>();
    public boolean sendchats=false;

    //FriendList Message
    public ChatMessage request_friendsMessageCheck=new ChatMessage();
    public ChatMessage response_friendsMessageCheck=new ChatMessage();
    public Stack<ChatMessage> friendsmsglist=new Stack<ChatMessage>();
    public boolean sendfriends=false;

    //Profile Message
    public ChatMessage request_profileMessageCheck=new ChatMessage();
    public ChatMessage response_profileMessageCheck=new ChatMessage();
    public boolean sendprofile=false;

    //Logout Message
    public ChatMessage request_logoutMessageCheck=new ChatMessage();
    public ChatMessage response_logoutMessageCheck=new ChatMessage();
    public boolean sendlogout=false;

    //Chat Activity Message
    public ChatMessage request_sendmessage=new ChatMessage();
    public ChatMessage response_sendmessage=new ChatMessage();
    public boolean sendmessage=false;

    //Chat Message
    public ChatMessage request_historymessage=new ChatMessage();
    public ChatMessage response_historymessage=new ChatMessage();
    public ArrayList<ChatMessage> historymsglist=new ArrayList<ChatMessage>();
    public boolean sendhistory=false;
   // public ArrayList<ChatMessage> onlinemessage=new ArrayList<ChatMessage>();

    //Accept_Reject Message
    public ChatMessage request_accept_reject=new ChatMessage();
    public ChatMessage response_accept_reject=new ChatMessage();
    public boolean sendacceptreject=false;

    // time line data
    public ChatMessage request_timeline = new ChatMessage();
    public ChatMessage response_timeline = new ChatMessage();
    public Stack<ChatMessage> timeLineList=new Stack<ChatMessage>();
    public boolean sendTimeLineList = false;

    public String Loginemail="";

   // public boolean send=false;
   // public boolean sendUser=false;

    //Search
    public ChatMessage request_searchmessage=new ChatMessage();
    public ChatMessage response_searchmessage=new ChatMessage();
    public Stack<ChatMessage> searchList=new Stack<ChatMessage>();
    public boolean sendSearchList=false;

    //Add Friend
    public ChatMessage request_addfriend=new ChatMessage();
    public ChatMessage response_addfriend=new ChatMessage();
    public boolean sendFriendRequest=false;


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

    public void run()
    {
        while (thread != null)
        {
            if(sendprofile==true) {
                try {
                    Log.i("write","write to stream");
                    if(request_profileMessageCheck.command!=null)
                        Log.i("command",request_profileMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_profileMessageCheck);
                    dataOutputStream.flush();
                    sendprofile=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendlogout==true) {
                try {
                    Log.i("write","write to stream");
                    if(request_logoutMessageCheck.command!=null)
                        Log.i("command",request_logoutMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_logoutMessageCheck);
                    dataOutputStream.flush();
                    sendlogout=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendfriends==true) {
                try {
                    Log.i("write","write to stream");
                    if(request_friendsMessageCheck.command!=null)
                        Log.i("command",request_friendsMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_friendsMessageCheck);
                    dataOutputStream.flush();
                    sendfriends=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendchats==true) {
                try {
                    Log.i("write","write to stream");
                    if(request_chatMessageCheck.command!=null)
                        Log.i("command",request_chatMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_chatMessageCheck);
                    dataOutputStream.flush();
                    sendchats=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendmessage==true) {
                try {
                    Log.i("write","write to stream");
                    if(request_sendmessage.command!=null)
                        Log.i("command",request_sendmessage.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_sendmessage);
                    dataOutputStream.flush();
                    sendmessage=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendhistory==true){
                try {
                    Log.i("write","write to stream");
                    if(request_historymessage.command!=null)
                        Log.i("command",request_historymessage.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_historymessage);
                    dataOutputStream.flush();
                    sendhistory=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }
            if(sendofflinecheck==true){
                try {
                    Log.i("write","write to stream");
                    if(request_offlineMessageCheck.command!=null)
                        Log.i("command",request_offlineMessageCheck.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_offlineMessageCheck);
                    dataOutputStream.flush();
                    sendofflinecheck=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }

            if(sendacceptreject==true){
                try {
                    Log.i("write","write to stream");
                    if(request_accept_reject.command!=null)
                        Log.i("command",request_accept_reject.command);
                    else
                        Log.i("command","null command");
                    dataOutputStream.writeObject(request_accept_reject);
                    dataOutputStream.flush();
                    sendacceptreject=false;
                } catch (IOException ioe) {
                    Log.e("Connection", "Sending error: " + ioe.getMessage());
                    stop();
                    break;
                }
            }

            if(sendTimeLineList == true){
                try {
                    if(request_timeline != null){
                        Log.i("Time Line Command", request_timeline.command);
                    }
                    else {

                    }
                    dataOutputStream.writeObject(request_timeline);
                    dataOutputStream.flush();
                    sendTimeLineList = false;
                }
                catch (IOException e){
                    stop();
                    break;
                }
            }


            if(sendSearchList == true){
                try {
                    if(request_searchmessage != null){
                        Log.i("Time Line Command", request_searchmessage.command);
                    }
                    else {

                    }
                    dataOutputStream.writeObject(request_searchmessage);
                    dataOutputStream.flush();
                    sendSearchList = false;
                }
                catch (IOException e){
                    stop();
                    break;
                }
            }
            if(sendFriendRequest == true){
                try {
                    if(request_addfriend != null){
                        Log.i("Time Line Command", request_addfriend.command);
                    }
                    else {

                    }
                    dataOutputStream.writeObject(request_addfriend);
                    dataOutputStream.flush();
                    sendFriendRequest = false;
                }
                catch (IOException e){
                    stop();
                    break;
                }
            }
        }
    }
    public void handle(ChatMessage chmsg) {
        Log.i("Res","response recvd");
  //      Log.i("Command",chmsg.command);
//        Log.i("Message:",chmsg.message);
        if (chmsg!=null && chmsg.command.equals("RESPONSE_LOGOUT"))
        {
            response_logoutMessageCheck=new ChatMessage();
            response_logoutMessageCheck=chmsg;
            Log.i("Connection", "Good bye. Press RETURN to exit ...");
            stop();
        }
        else if(chmsg!=null && (chmsg.command.equals("RESPONSE_GET_PROFILE") || chmsg.command.equals("RESPONSE_EDIT_PROFILE"))){
            response_profileMessageCheck=new ChatMessage();
            response_profileMessageCheck=chmsg;
        }
        else if(chmsg!=null && (chmsg.command.equals("SEND_MESSAGE"))){
            response_sendmessage=new ChatMessage();
            response_sendmessage=chmsg;
        }
        else if(chmsg!=null && chmsg.command.equals("RESPONSE_UPDATE_HISTORY")){
            response_historymessage = new ChatMessage();
            response_historymessage = chmsg;

        }
        else if(chmsg!=null && chmsg.command.equals("RESPONSE_ACCEPT_REJECT_FRIEND_REQUEST")){
            response_accept_reject = new ChatMessage();
            response_accept_reject = chmsg;
        }
        //ADD HERE FOR TIME LINE STUFF;
        else if(chmsg != null && chmsg.command.equals("RESPONSE_GET_TIMELINE")){
            response_timeline = new ChatMessage();
            response_timeline = chmsg;
        }

        else if(chmsg!=null && chmsg.command.equals("RESPONSE_SEARCH_USER")){
            response_searchmessage = new ChatMessage();
            response_searchmessage = chmsg;
        }

        else if(chmsg!=null && chmsg.command.equals("RESPONSE_FRIEND_REQUEST")){
            response_addfriend = new ChatMessage();
            response_addfriend = chmsg;
        }

    }
    public void handle_ChatList(Stack<ChatMessage> chmsg)
    {
        // send=true;
        response_chatMessageCheck = new ChatMessage();
        response_chatMessageCheck=chmsg.pop();
        Log.i("Command:",response_chatMessageCheck.command);
        Log.i("Message:",response_chatMessageCheck.message);
        chatmsglist = new Stack<ChatMessage>();
        chatmsglist=chmsg;

       /* while(!returnlistmsg.isEmpty()){
            ChatMessage retmsg=returnlistmsg.pop();
            System.out.println("Email:"+retmsg.email);
            System.out.println("Message:"+retmsg.message);
        }*/
    }

    public void handle_TimeLineList(Stack<ChatMessage> chmsg){
        response_timeline = new ChatMessage();
        response_timeline = chmsg.pop();
        Log.i("TimeLineCommand:",response_timeline.command);
        Log.i("TimeLineMessage:",response_timeline.message);
        timeLineList = new Stack<ChatMessage>();
        timeLineList = chmsg;

    }

    public void handle_FriendList(Stack<ChatMessage> chmsg)
    {
       // send=true;
        response_friendsMessageCheck = new ChatMessage();
        response_friendsMessageCheck=chmsg.pop();
        Log.i("Command:",response_friendsMessageCheck.command);
        Log.i("Message:",response_friendsMessageCheck.message);
        friendsmsglist = new Stack<ChatMessage>();
        friendsmsglist=chmsg;

       /* while(!returnlistmsg.isEmpty()){
            ChatMessage retmsg=returnlistmsg.pop();
            System.out.println("Email:"+retmsg.email);
            System.out.println("Message:"+retmsg.message);
        }*/
    }
    public void handle_History(ArrayList<ChatMessage> chmsg){
        historymsglist = new ArrayList<ChatMessage>();
        historymsglist=chmsg;
        response_historymessage = new ChatMessage();
        if(historymsglist.size()>1)
        {

            response_historymessage.command="RESPONSE_GET_HISTORY";
            response_historymessage.message="SUCCESS";
        }
        else{
            response_historymessage.command="RESPONSE_GET_HISTORY";
            response_historymessage.message="FAILURE";
        }
        Log.i("Command:",response_historymessage.command);
        Log.i("Message:",response_historymessage.message);
        Log.i("SIZE:",String.valueOf(historymsglist.size()));
    }

    public void handle_OfflineMessage(ArrayList<String> chmsg){
        offlinemsglist = new ArrayList<String>();
        offlinemsglist=chmsg;
        response_offlineMessageCheck = new ChatMessage();
        if(offlinemsglist.size()>1)
        {

            response_offlineMessageCheck.command="RESPONSE_GET_OFFLINE_MSG";
            response_offlineMessageCheck.message="SUCCESS";
        }
        else{
            response_offlineMessageCheck.command="RESPONSE_GET_OFFLINE_MSG";
            response_offlineMessageCheck.message="FAILURE";
        }
        Log.i("Command:",response_offlineMessageCheck.command);
        Log.i("Message:",response_offlineMessageCheck.message);
    }

    public void handle_SearchList(Stack<ChatMessage> chmsg){
        response_searchmessage = new ChatMessage();
        response_searchmessage=chmsg.pop();
        Log.i("Command:",response_searchmessage.command);
        Log.i("Message:",response_searchmessage.message);
        searchList = new Stack<ChatMessage>();
        searchList=chmsg;
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
                    chatClient.request_offlineMessageCheck = new ChatMessage();
                    chatClient.request_offlineMessageCheck.command = "GET_OFFLINE_MSG";
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
