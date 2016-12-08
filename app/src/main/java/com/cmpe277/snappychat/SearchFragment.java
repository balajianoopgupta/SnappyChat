package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

public class SearchFragment extends Fragment implements FriendsListAdapter.ItemClickListener{

    private FriendsFragment.OnFragmentInteractionListener mListener;
    //    private List<ChatMessage> friendList = new ArrayList<>();
    private RecyclerView searchRecyclerView;
    private SearchListAdapter searchAdapter;
    private RecyclerView.LayoutManager searchLayoutManager;
    List<ChatMessage> rowListItem;
    String Loginemail;
    EditText searchText;
    ImageButton searchButton;
    Editable searchItem;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //Inflate the elements in the fragment

        searchRecyclerView = (RecyclerView) view.findViewById(R.id.searchList);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        searchRecyclerView.setHasFixedSize(true);

        searchText  = (EditText) getView().findViewById(R.id.searchText);
        searchButton = (ImageButton) getView().findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItem = searchText.getText();

                search(searchItem.toString());
            }
        });

        //super.onViewCreated(view, savedInstanceState);
    }

    public void search(final String searchItem){
        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_searchmessage = new ChatMessage();
                chatClient.request_searchmessage.command = "SEARCH_USER";
                chatClient.request_searchmessage.message = searchItem;
                Log.i("send", "Setting to true");
                chatClient.sendSearchList = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_searchmessage != null && chatClient.response_searchmessage.command != null) {
                        if (chatClient.response_searchmessage.command.equals("RESPONSE_SEARCH_USER")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_searchmessage;
                            checkresponse = true;
                            if (!chatClient.response_searchmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg

                                rowListItem = chatClient.searchList;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //List<ChatMessage> rowListItem = getAllItemList();

                                        // specify an adapter
                                        searchAdapter = new SearchListAdapter(getActivity().getApplicationContext(), rowListItem, getActivity());

                                        // use a linear layout manager
                                        searchLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

                                        searchRecyclerView.setLayoutManager(searchLayoutManager);
                                        searchRecyclerView.setAdapter(searchAdapter);
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
            a = (Activity) context;
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
}
