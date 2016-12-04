package com.cmpe277.snappychat;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import com.google.android.gms.common.api.GoogleApiClient;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HomeFragment Acitivity";

    GoogleApiClient mGoogleApiClient;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String Loginemail="";
    private static final String EMailTAG = "cmpe277.snappychat.EmailID";
    private Bundle fragmentbundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Loginemail = intent.getStringExtra(EMailTAG);

        Log.i("EmailID:",Loginemail);

        fragmentbundle=new Bundle();
        fragmentbundle.putString("EmailID", Loginemail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        HomeFragment homefragment = new HomeFragment();
        homefragment.setArguments(fragmentbundle);

        FriendsFragment friendsfragment=new FriendsFragment();
        friendsfragment.setArguments(fragmentbundle);

        ChatFragment chatfragment=new ChatFragment();
        chatfragment.setArguments(fragmentbundle);

        ProfileFragment profilefragment=new ProfileFragment();
        profilefragment.setArguments(fragmentbundle);

        adapter.addFragment(homefragment, "Home");
        adapter.addFragment(friendsfragment, "Friends");
        adapter.addFragment(chatfragment, "Chat");
        adapter.addFragment(profilefragment, "Profile");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndroidChatClient.getInstance().destroyHandler();


    }

}
