package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "ChatFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String Loginemail;



    private RecyclerView mRecyclerView;
    private ChatListAdapter  mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ChatMessage> rowListItem;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("TAG","HI");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        Loginemail = bundle.getString("EmailID");
        Log.i(TAG,Loginemail);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.chatList);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient=AndroidChatClient.getInstance();
                chatClient.request_chatMessageCheck=new ChatMessage();
                chatClient.request_chatMessageCheck.command="GET_CHATLIST";
                Log.i("send","Setting to true");
                chatClient.sendchats=true;
                boolean checkresponse=false;
                while(!checkresponse){
                    // Log.i("herer","response");
                    if(chatClient.response_chatMessageCheck!=null && chatClient.response_chatMessageCheck.command!=null) {
                        if (chatClient.response_chatMessageCheck.command.equals("RESPONSE_GET_CHATLIST")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_chatMessageCheck;
                            checkresponse = true;
                            Log.i("CHATLIST",chmessage.message);
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                rowListItem=chatClient.chatmsglist;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //List<ChatMessage> rowListItem = getAllItemList();



                                        // specify an adapter (see also next example)
                                        mAdapter =new ChatListAdapter(getActivity().getApplicationContext(),rowListItem);
                                        // use a linear layout manager
                                        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                                        mRecyclerView.setAdapter(mAdapter);
                                        mRecyclerView.setLayoutManager(mLayoutManager);
                                        // mAdapter.setClickListener(new OnIte);

                                    }
                                });


                            }
                        }
                    }

                }
            }
        }).start();


    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
