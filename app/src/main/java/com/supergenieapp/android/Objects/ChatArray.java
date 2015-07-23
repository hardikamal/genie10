package com.supergenieapp.android.Objects;

import java.util.ArrayList;

/**
 * Created by Raviteja on 7/21/2015.
 */
public class ChatArray {
    ArrayList<Messages> chats;

    public ChatArray(ArrayList<Messages> chats) {
        this.chats = chats;
    }

    public ArrayList<Messages> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Messages> chats) {
        this.chats = chats;
    }
}
