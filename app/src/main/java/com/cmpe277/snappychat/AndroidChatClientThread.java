package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import android.util.Log;
public class AndroidChatClientThread extends Thread
{
    private Socket           socket   = null;
    private AndroidChatClient       client   = null;

    private InputStream inputStream = null;
    private ObjectInputStream  streamIn = null;
    public AndroidChatClientThread(AndroidChatClient _client, Socket _socket)
    {
        client   = _client;
        socket   = _socket;
        open();
        start();
    }
    public void open()
    {
        try
        {
            inputStream = socket.getInputStream();
            streamIn  = new ObjectInputStream(inputStream);
        }
        catch(IOException ioe)
        {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }
    public void close()
    {
        try
        {
            if (streamIn != null) streamIn.close();
        }
        catch(IOException ioe)
        {
            System.out.println("Error closing input stream: " + ioe);
        }
    }
    public void run()
    {
        while (true)
        {
            try
            {
                Object resobj=streamIn.readObject();
                if(resobj!=null) {
                    Queue que = new LinkedList();
                    que.offer(resobj);
                    while (!que.isEmpty()) {
                        Object obj = que.poll();
                        if (obj instanceof ChatMessage) {
                            client.handle((ChatMessage) obj);
                        } else if (obj instanceof Stack<?>) {
                            Stack<ChatMessage> stckmessage = new Stack<ChatMessage>();
                            stckmessage = (Stack<ChatMessage>) obj;
                            if (!stckmessage.isEmpty()) {
                                Log.i("Command handle:", stckmessage.peek().command);
                                // Log.i("Message:",response_chatMessageCheck.message);
                                if (stckmessage.peek().command.equals("RESPONSE_GET_CHATLIST")) {
                                    client.handle_ChatList((Stack<ChatMessage>) obj);
                                } else if (stckmessage.peek().command.equals("RESPONSE_GET_FRIENDLIST")) {
                                    client.handle_FriendList((Stack<ChatMessage>) obj);
                                }
                            } else {
                                ChatMessage chms = new ChatMessage();
                                chms.command = "RESPONSE_LOGOUT";
                                client.handle(chms);
                            }
                            //client.handleList((Stack<ChatMessage>)obj);
                        } else if (obj instanceof ArrayList<?>) {

                            if (((ArrayList<?>) obj).get(0) instanceof ChatMessage) {
                                client.handle_History((ArrayList<ChatMessage>) obj);
                            } else {
                                client.handle_OfflineMessage((ArrayList<String>) obj);
                            }
                        }
                    }
                }
            }
            catch(IOException ioe)
            {
                Log.i("Listening error: ",  ioe.getMessage());
                client.stop();
                break;
            }catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}