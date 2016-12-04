package com.cmpe277.snappychat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.os.Environment;
import android.graphics.Bitmap;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.io.*;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static int RESULT_LOAD_IMAGE = 1;
    EditText aboutme, nickname, email, location, profession, interests;
    Context ApplicationContext;

    private OnFragmentInteractionListener mListener;
    Button logOut, editProfile, save, discard;
    Switch notificationSetting;
    ImageButton picture;
    RadioButton r;
    RadioGroup rg;
    boolean isEnabled = false;
    String Loginemail;

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
        Log.i(TAG, Loginemail);

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/
    public void UpdateProfileUI(String res_name, String res_email) {
        //nickname = (EditText) andview.findViewById(R.id.Nickname);
        //email = (EditText) andview.findViewById(R.id.Email);
        nickname.setText(res_name);
        email.setText(res_email);
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {

        picture = (ImageButton) getView().findViewById(R.id.profilePicture);
        aboutme = (EditText) getView().findViewById(R.id.aboutme);
        nickname = (EditText) getView().findViewById(R.id.nickname);
        email = (EditText) getView().findViewById(R.id.email);
        location = (EditText) getView().findViewById(R.id.location);
        profession = (EditText) getView().findViewById(R.id.profession);
        interests = (EditText) getView().findViewById(R.id.interests);
        notificationSetting = (Switch) getView().findViewById(R.id.getNotification);
        rg = (RadioGroup) getView().findViewById(R.id.visibilityOptions);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                Log.d("chec id", String.valueOf(checkedId));
            }
        });
        //update
        //notificationSetting.setChecked(true);

        aboutme.setEnabled(isEnabled);
        nickname.setEnabled(isEnabled);
        email.setEnabled(isEnabled);
        location.setEnabled(isEnabled);
        profession.setEnabled(isEnabled);
        interests.setEnabled(isEnabled);

        logOut = (Button) view.findViewById(R.id.logOutBtn);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        editProfile = (Button) view.findViewById(R.id.editProfileBtn);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEnabled = true;
                showHideProfile();
            }
        });

        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save changes
                isEnabled = false;
                editMessage();
                showHideProfile();
            }
        });

        discard = (Button) view.findViewById(R.id.discard);
        discard.setVisibility(view.VISIBLE);

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //put back previous values
                isEnabled = false;
                showHideProfile();
            }
        });
        showHideProfile();

        new Thread(new Runnable() {
            public void run() {
                final AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_profileMessageCheck = new ChatMessage();
                chatClient.request_profileMessageCheck.command = "GET_PROFILE";
                Log.i("send", "Setting to true");
                chatClient.sendprofile = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_profileMessageCheck != null && chatClient.response_profileMessageCheck.command != null) {
                        if (chatClient.response_profileMessageCheck.command.equals("RESPONSE_GET_PROFILE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_profileMessageCheck;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {
                                //update profile
                                ///storage/sdcard/DCIM/Camera/IMG_20161128_085625.jpg
                                Log.i("Nname", chmessage.nickname);
                                try {
                                    String root = Environment.getExternalStorageDirectory().toString();
                                    root = root + "/saved_images";
                                    //myDir=new File("/mnt/sdcard/saved_images");
                                    File myDir = new File(root);

                                    if (!myDir.exists()) {
                                        Log.i("Directory", "does not exists");
                                        myDir.mkdir();
                                    } else {
                                        Log.i("Directory", "exists");
                                    }
                                    Log.i("Root", root);
                                    // String filename=root+"/"+chmessage.email.split("@")[0]+".png";
                                    String filename = "/" + chmessage.email.split("@")[0] + ".png";

                                    File file = new File(myDir, filename);
                                    if (file.exists()) {
                                        file.delete();
                                        Log.i("file", "exists");
                                    } else {
                                        Log.i("file", "does not exists");
                                    }
                                    Log.i("Root", filename);
                                    FileOutputStream fos = new FileOutputStream(file);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                                    bos.write(chmessage.pic);
                                    bos.flush();
                                    bos.close();
                                    fos.flush();
                                    fos.close();

                                } catch (Exception e) {
                                    // some action
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                        //UpdateProfileUI(chmessage.nickname,chmessage.email);
                                        nickname.setText(chmessage.nickname);
                                        email.setText(chmessage.email);
                                        aboutme.setText(chmessage.aboutme);
                                        location.setText(chmessage.location);
                                        profession.setText(chmessage.profession);
                                        interests.setText(chmessage.interests);
                                        notificationSetting.setChecked(chmessage.notifications);
                                        //notificationSetting.setEnabled(chmessage.notifications);
                                        switch (chmessage.visibility) {
                                            case 0:
                                                AndroidChatClient.getInstance().radiobuttonid=0;
                                                rg.check(R.id.visibility_friends);
                                                break;
                                            case 1:
                                                AndroidChatClient.getInstance().radiobuttonid=1;
                                                rg.check(R.id.visibility_private);
                                                break;
                                            case 2:
                                                AndroidChatClient.getInstance().radiobuttonid=2;
                                                rg.check(R.id.visibility_public);
                                                break;

                                        }



                                       /* String root = Environment.getExternalStorageDirectory().toString();
                                        root=root+"/snappychatpics/";
                                        String filename=chmessage.email.split("@")[0];
                                        imageView.setImageBitmap(BitmapFactory.decodeFile(root+"/"+filename+".png"));*/


                                        ByteArrayInputStream inputStream = new ByteArrayInputStream(chmessage.pic);
                                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                        picture.setImageBitmap(bitmap);

                                    }
                                });


                            }
                        }
                    }

                }
            }
        }).start();


    }

    public void onRadioButtonSelected(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.visibility_private:
                if (checked) {
                    AndroidChatClient.getInstance().radiobuttonid=1;
                    Log.d("checked :", String.valueOf(view.getId()));
                }
                break;
            case R.id.visibility_friends:
                if (checked) {
                    AndroidChatClient.getInstance().radiobuttonid=0;
                    Log.d("checked :", String.valueOf(view.getId()));

                }
                break;
            case R.id.visibility_public:
                if (checked) {
                    AndroidChatClient.getInstance().radiobuttonid=2;
                    Log.d("checked :", String.valueOf(view.getId()));
                }
                break;

        }
    }

    public void showHideProfile() {
        aboutme.setEnabled(isEnabled);
        nickname.setEnabled(isEnabled);
        email.setEnabled(isEnabled);
        location.setEnabled(isEnabled);
        profession.setEnabled(isEnabled);
        interests.setEnabled(isEnabled);
        picture.setEnabled(isEnabled);
        if (isEnabled) {
            editProfile.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);
            discard.setVisibility(View.VISIBLE);
        } else {
            editProfile.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            discard.setVisibility(View.VISIBLE);

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

    public void logOut() {

        LoginActivity.mGoogleApiClient.connect();
        LoginActivity.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (LoginActivity.mGoogleApiClient.isConnected()) {
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
                                        AndroidChatClient chatClient = AndroidChatClient.getInstance();
                                        chatClient.request_logoutMessageCheck = new ChatMessage();
                                        chatClient.request_logoutMessageCheck.command = "LOGOUT";
                                        Log.i("send", "Setting to true");
                                        chatClient.sendlogout = true;

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
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = ApplicationContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();


            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("FILEPATH", picturePath);

            picture.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    public void editMessage()  {

        final ChatMessage send_chmsg=new ChatMessage();
        send_chmsg.command="EDIT_PROFILE";
        send_chmsg.nickname=nickname.getText().toString();
        send_chmsg.aboutme=aboutme.getText().toString();
        send_chmsg.location=location.getText().toString();
        send_chmsg.profession=profession.getText().toString();
        send_chmsg.interests=interests.getText().toString();
        send_chmsg.notifications=notificationSetting.isEnabled();
        send_chmsg.visibility=AndroidChatClient.getInstance().radiobuttonid;
        send_chmsg.email=AndroidChatClient.getInstance().Loginemail;

        Drawable drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.image, null);

        Bitmap bitmap1=((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream bytearrayoutputstream=new ByteArrayOutputStream();;

        bitmap1.compress(Bitmap.CompressFormat.PNG,100,bytearrayoutputstream);

        send_chmsg.pic = bytearrayoutputstream.toByteArray();


        new Thread(new Runnable() {
            public void run() {
                final AndroidChatClient chatClient = AndroidChatClient.getInstance();
                chatClient.request_profileMessageCheck = new ChatMessage();
                chatClient.request_profileMessageCheck=send_chmsg;
                Log.i("send", "Setting to true");
                chatClient.sendprofile = true;
                boolean checkresponse = false;
                while (!checkresponse) {
                    // Log.i("herer","response");
                    if (chatClient.response_profileMessageCheck != null && chatClient.response_profileMessageCheck.command != null) {
                        if (chatClient.response_profileMessageCheck.command.equals("RESPONSE_EDIT_PROFILE")) {
                            //ChatMessage chmessage = new ChatMessage();
                            final ChatMessage chmessage = chatClient.response_profileMessageCheck;
                            checkresponse = true;
                            if (!chmessage.message.equals("FAILURE")) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                        //UpdateProfileUI(chmessage.nickname,chmessage.email);
                                        Toast.makeText(getContext(), "Updated Profile",Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                        }
                    }

                }
            }
        }).start();

    }
}
