package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */
import java.net.*;
import java.util.Scanner;
import java.io.*;
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
    public String Loginemail="";
    public boolean send=true;
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
            socket = new Socket(serverName, serverPort);
            Log.i("Connection","Connected: " + socket);
            Loginemail=email;
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
        if (chmsg!=null && chmsg.message.equals(".bye"))
        {
            Log.i("Connection", "Good bye. Press RETURN to exit ...");
            stop();
        } else {
            returnmessage=chmsg;
            Log.i("Command",returnmessage.command);
            Log.i("Message:",returnmessage.message);
        }
    }
    public void start() throws IOException
    {
        outputStream = socket.getOutputStream();
        dataOutputStream = new ObjectOutputStream(outputStream);
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
    public void stop()
    {
        if (thread != null)
        {
            thread = null;
        }
        try
        {
            if (dataOutputStream != null)  dataOutputStream.close();
            if (socket    != null)  socket.close();
        }
        catch(IOException ioe)
        {
            Log.i("Connection", "Error closing ...");
        }
        client.close();
    }

}
