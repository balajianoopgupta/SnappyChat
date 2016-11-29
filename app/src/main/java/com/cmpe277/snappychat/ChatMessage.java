package com.cmpe277.snappychat;

/**
 * Created by sudhindrathangavelu on 11/28/16.
 */


import java.io.Serializable;


public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public String command;
    public String message;
    public String nickname;
    public String email;
    public byte[] pic;
    public String location;
    public String profession;
    public String aboutme;
    public String interests;
}

