package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static int RESULT_LOAD_IMAGE = 1;
    private OnFragmentInteractionListener mListener;

    Button logOut;
    ImageView imageView;
    Context ApplicationContext;
    String Loginemail;
    EditText nickname;
    EditText email;
    EditText location;

    String strnickname="";
    View andview;
    public ProfileFragment() {
        // Required empty public constructor
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
        Log.i("Bundle_Login",Loginemail);



        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/
    public void UpdateProfileUI(String res_name,String res_email){
        //nickname = (EditText) andview.findViewById(R.id.Nickname);
        //email = (EditText) andview.findViewById(R.id.Email);
        nickname.setText(res_name);
        email.setText(res_email);
    }
    @Override
    public void onViewCreated (View view,
                               Bundle savedInstanceState){

        logOut = (Button) view.findViewById(R.id.logOutBtn);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        Button buttonLoadImage = (Button) view.findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        andview=view;
        nickname = (EditText) view.findViewById(R.id.Nickname);
        email = (EditText) view.findViewById(R.id.Email);
        imageView = (ImageView) view.findViewById(R.id.imgView);

        location = (EditText) view.findViewById(R.id.Location);

        new Thread(new Runnable() {
            public void run() {
                AndroidChatClient chatClient=AndroidChatClient.getInstance();
                chatClient.sendChatMessage=new ChatMessage();
                chatClient.sendChatMessage.command="GET_PROFILE";
                Log.i("send","Setting to true");
                chatClient.send=true;
                boolean checkresponse=false;
                while(!checkresponse){
                    // Log.i("herer","response");
                    if(chatClient.returnmessage!=null && chatClient.returnmessage.command!=null) {
                        if (chatClient.returnmessage.command.equals("RESPONSE_GET_PROFILE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.returnmessage;
                            checkresponse = true;

                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                Log.i("Nname", chmessage.nickname);
                                // nickname.setText(chmessage.nickname);
                                //email.setText(chmessage.email);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                        UpdateProfileUI(chmessage.nickname,chmessage.email);
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
            ApplicationContext=context;
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

    public void logOut(){

        LoginActivity.mGoogleApiClient.connect();
        LoginActivity.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if(LoginActivity.mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(LoginActivity.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "User Logged out");
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();



                                new Thread(new Runnable() {
                                    public void run() {
                                        AndroidChatClient chatClient=AndroidChatClient.getInstance();
                                        chatClient.sendChatMessage=new ChatMessage();
                                        chatClient.sendChatMessage.command="LOGOUT";
                                        Log.i("send","Setting to true");
                                        chatClient.send=true;

                                    }
                                }).start();


                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "Google API Client Connection Suspended");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = ApplicationContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();


            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("FILEPATH",picturePath);

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }
}
