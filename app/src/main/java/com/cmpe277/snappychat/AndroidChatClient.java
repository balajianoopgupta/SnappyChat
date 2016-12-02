package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */
import java.net.*;
import java.util.Scanner;
import java.io.*;
import java.util.Stack;

import android.util.Log;

public class AndroidChatClient implements Runnable {
    private Socket socket              = null;
    private Thread thread              = null;

    OutputStream outputStream = null;
    ObjectOutputStream dataOutputStream = null;
    private static AndroidChatClient ChatClient = null;
    //private DataOutputStream streamOut = null;
    private AndroidChatClientThread client    = null;
    public ChatMessage returnmessage=new ChatMessage();
    public Stack<ChatMessage> returnlistmsg=new Stack<ChatMessage>();
    public String Loginemail="";
    public boolean send=false;
    public ChatMessage sendChatMessage=new ChatMessage();
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
        }
    }
    public void handle(ChatMessage chmsg) {
        Log.i("Res","response recvd");
        if (chmsg!=null && chmsg.command.equals("RESPONSE_LOGOUT"))
        {

            Log.i("Connection", "Good bye. Press RETURN to exit ...");
            stop();
        } else {
            returnmessage=chmsg;
            Log.i("Command",returnmessage.command);
            Log.i("Message:",returnmessage.message);

        }

    }
    public void handleList(Stack<ChatMessage> chmsg)
    {
       // send=true;
        returnmessage=chmsg.pop();
        returnlistmsg=chmsg;
        System.out.println("Command:"+returnmessage.command);
        System.out.println("Message:"+returnmessage.message);


       /* while(!returnlistmsg.isEmpty()){
            ChatMessage retmsg=returnlistmsg.pop();
            System.out.println("Email:"+retmsg.email);
            System.out.println("Message:"+retmsg.message);
        }*/
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

}
