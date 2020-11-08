package com.example.whatsapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.ActivityMainBinding;
import com.example.whatsapp.menu.CallsFragment;
import com.example.whatsapp.menu.ChatsFragment;
import com.example.whatsapp.menu.GroupsFragment;
import com.example.whatsapp.menu.StatusFragment;
import com.example.whatsapp.view.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

        private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        setSupportActionBar(binding.toolbar);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabIcon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpWithViewPager(ViewPager viewPager) {
        MainActivity.SectionPagerAdapter adapter=new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new GroupsFragment(), "Groups");
        adapter.addFragment(new StatusFragment(), "Status");
        adapter.addFragment(new CallsFragment(), "Calls");
        viewPager.setAdapter(adapter);
    }

    private static class SectionPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public SectionPagerAdapter(@NonNull FragmentManager manager) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_search: Toast.makeText(MainActivity.this,"Action Search",Toast.LENGTH_LONG).show();
            break;
            case R.id.action_new_group: Toast.makeText(MainActivity.this,"Action new group",Toast.LENGTH_LONG).show();
                break;
            case R.id.action_settings: startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void changeFabIcon(final int index){
        binding.fabAction.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index){
                    case 0: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_chat_24));
                    break;
                    case 1: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_camera_alt_24));
                    break;
                    case 2: binding.fabAction.setImageDrawable(getDrawable(R.drawable.ic_baseline_call_24));
                    break;
                }
            }
        },400);
        binding.fabAction.show();
    }
}