package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment implements FriendsListAdapter.ItemClickListener {
    private static final String TAG = "ProfileFragment";
    private OnFragmentInteractionListener mListener;
    private List<ChatMessage> friendList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private FriendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ChatMessage> rowListItem;
    String Loginemail;
    public FriendsFragment() {
        // Required empty public constructor
    }

    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        Loginemail = bundle.getString("EmailID");
        Log.i(TAG,Loginemail);
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.suggestionList);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);



        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient=AndroidChatClient.getInstance();
                chatClient.request_friendsMessageCheck=new ChatMessage();
                chatClient.request_friendsMessageCheck.command="GET_FRIENDLIST";
                Log.i("send","Setting to true");
                chatClient.sendfriends=true;
                boolean checkresponse=false;
                while(!checkresponse){
                    // Log.i("herer","response");
                    if(chatClient.response_friendsMessageCheck!=null && chatClient.response_friendsMessageCheck.command!=null) {
                        if (chatClient.response_friendsMessageCheck.command.equals("RESPONSE_GET_FRIENDLIST")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_friendsMessageCheck;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                rowListItem=chatClient.friendsmsglist;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //List<ChatMessage> rowListItem = getAllItemList();

                                        // use a linear layout manager


                                        // specify an adapter (see also next example)
                                        mAdapter =new FriendsListAdapter(getActivity().getApplicationContext(),rowListItem);
                                        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                                        mRecyclerView.setAdapter(mAdapter);
                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                    }
                                });


                            }
                        }
                    }

                }
            }
        }).start();


    }
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity) {
            a=(Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void itemClick(View view, int position) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   /* private List<UserObject> getAllItemList() {

        List<UserObject> allItems = new ArrayList<UserObject>();
        allItems.add(new UserObject("playlist1","5 oct", R.drawable.image));
        allItems.add(new UserObject("playlist2","6 oct", R.drawable.image));
        allItems.add(new UserObject("playlist3","7 oct",R.drawable.image));
        allItems.add(new UserObject("playlist4","9 oct", R.drawable.image));
        allItems.add(new UserObject("playlist5","10 oct", R.drawable.image));
        return allItems;
    }*/
}
