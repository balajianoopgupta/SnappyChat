package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */

import java.net.*;
import java.io.*;

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
                Object obj=streamIn.readObject();
                client.handle((ChatMessage)obj);
            }
            catch(IOException ioe)
            {
                System.out.println("Listening error: " + ioe.getMessage());
                client.stop();
                break;
            }catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}