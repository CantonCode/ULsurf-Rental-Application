package com.example.clubapp;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

class pageSections extends FragmentPagerAdapter {

    public pageSections(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {
            case 0:
                chatFragment messages = new chatFragment();
                return messages;

            case 1:
                friendsFragment friendsFragment = new friendsFragment();
                return  friendsFragment;

            default:
                return  null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){

        switch (position) {
            case 0:
                return "CHATS";

            case 1:
                return "FRIENDS";

            default:
                return null;
        }

    }

}

