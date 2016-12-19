package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private OnFragmentInteractionListener mListener;
    private List<ChatMessage> timeLineList = new ArrayList<>();
    private TimeLineAdapter timeLineAdapter;
    private RecyclerView.LayoutManager timeLineLayoutManager;
    List<ChatMessage> rowListItem;
    String Loginemail;
    private static int RESULT_LOAD_IMG = 1;
    Context ApplicationContext;

    EditText statusText;
    ImageButton fileAttachBtn;
    Button updateStatusBtn;
    RecyclerView mRecyclerView;
    byte[] imgArray;

    String picturePath;


    public HomeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity) {
            ApplicationContext = context;
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

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {

        statusText = (EditText) getView().findViewById(R.id.statusText);;
        fileAttachBtn = (ImageButton) getView().findViewById(R.id.fileAttachBtn);
        updateStatusBtn = (Button) getView().findViewById(R.id.updateStatusBtn);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.timelineList);

        fileAttachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

            }
        });

        updateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = statusText.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                Log.i("statusmsg",""+messageText);
                sendtoServer(messageText);
            }
        });

        new Thread(new Runnable() {
            public void run() {
                final AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_timeline = new ChatMessage();
                chatClient.request_timeline.command = "GET_TIMELINE_DATA";
                //Send the logged in user name for getting his email
                chatClient.request_timeline.email = AndroidChatClient.getInstance().Loginemail;
                Log.i("timeline", "Setting to true");
                chatClient.sendTimeLineList = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_timeline != null && chatClient.response_timeline.command != null) {
                        if (chatClient.response_timeline.command.equals("RESPONSE_GET_TIMELINE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_timeline;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {

                                rowListItem=chatClient.timeLineList;

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        timeLineAdapter =new TimeLineAdapter(getActivity().getApplicationContext(),rowListItem,getActivity());
                                        timeLineLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                                        mRecyclerView.setAdapter(timeLineAdapter);
                                        mRecyclerView.setLayoutManager(timeLineLayoutManager);
                                    }
                                });
                            }
                        }
                    }

                }
            }
        }).start();


    }

    private void sendtoServer(final String messageText) {

            Thread	thread=new Thread(new Runnable() {
                public void run() {
                    AndroidChatClient chatClient=AndroidChatClient.getInstance();
                    chatClient.request_timeline=new ChatMessage();
                    chatClient.request_timeline.command="POST_STATUS";
                    if(!picturePath.isEmpty()){
                        try {
                            File myFile = new File (picturePath);
                            byte [] mybytearray  = new byte [(int)myFile.length()];
                            FileInputStream fis = null;
                            fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(mybytearray,0,mybytearray.length);
                            chatClient.request_timeline.email = AndroidChatClient.getInstance().Loginemail;
                            chatClient.request_timeline.files=imgArray;
                            chatClient.request_timeline.message = messageText;
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        chatClient.request_timeline.email = AndroidChatClient.getInstance().Loginemail;
                        chatClient.request_timeline.message = messageText;
                        Log.i("MessageTimeline",""+messageText);
                        chatClient.request_timeline.files=null;
                    }

                    chatClient.sendTimeLineList=true;
                    boolean checkresponse=false;
                    while(!checkresponse){
                        if(chatClient.request_timeline!=null && chatClient.request_timeline.command!=null) {
                            if (chatClient.request_timeline.command.equals("RESPONSE_POST_STATUS")) {

                                ChatMessage chmessage = chatClient.request_timeline;
                                if (!chmessage.message.equals("FAILURE")) {
                                    checkresponse = true;
                                    picturePath = "";
                                }
                                else{
                                    chatClient.request_timeline=new ChatMessage();
                                    chatClient.request_timeline.command="POST_STATUS";
                                    chatClient.request_timeline.message=messageText;
                                    chatClient.request_timeline.files = imgArray;
                                    chatClient.sendmessage=true;
                                }
                            }
                        }

                    }
                }
            });

            thread.start();
//
//        SimpleChatMessage chatMessage = new SimpleChatMessage();
//        //chatMessage.setId(122);//dummy
//        chatMessage.setMessage(messageText);
//        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//        messageET.setText("");
//        displayMessage(chatMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = ApplicationContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();


            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("FILEPATH", picturePath);

            Bitmap imgPath= BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgPath.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imgArray = baos.toByteArray();
            //Bitmap bitmap = BitmapFactory.decodeByteArray(imgArray , 0, imgArray .length);
            //picture.setImageBitmap(bitmap);

            // picture.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }
}
