package com.moods.bikersrides.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.moods.bikersrides.fragments.AddRideFragment;


@Deprecated
public class BaseNavigationDummy {

    public static final int HOME_FRAG = 0;

    public  void goHome(int param){
            redirect(null,HOME_FRAG);
    }

    private void redirect(FragmentManager fragmentManager, int nav) {
        Fragment frag = null;
        switch (nav) {
            case 0:
                frag = AddRideFragment.newInstance(0);
                break;
        }
        //    fragmentManager.beginTransaction().replace(R.id.content_frame, AddRideFragment.newInstance(getItemId(position)), "EDIT_RIDE").addToBackStack(null).commit();
    }
}
