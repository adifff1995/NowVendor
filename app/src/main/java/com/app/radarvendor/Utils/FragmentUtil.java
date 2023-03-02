package com.app.radarvendor.Utils;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.radarvendor.R;


public class FragmentUtil {
    public static void addFragment(Context context, Fragment fragment, int layout) {

        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        );
        transaction.add(layout, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(Context context, Fragment fragment, int layout, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(layout, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(Context context, Fragment fragment, int layout, String tag) {
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (manager.findFragmentByTag(tag) == null) {
            transaction.add(layout, fragment, tag);
            transaction.commitAllowingStateLoss();
        }
    }

    public static void addFragmentWithBackStack(Context context, Fragment fragment, int layout, String name) {
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out  // popExit
        );
//        if (manager.findFragmentByTag(name) == null) {
            transaction.add(layout, fragment, name);
            transaction.addToBackStack(name);
            transaction.commitAllowingStateLoss();
//        }

    }

    public static void addFragmentWithBackStack(Context context, Fragment fragment, int layout, String name, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(layout, fragment);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    public static void replaceFragment(Context context, Fragment fragment, int layout) {
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(layout, fragment).commit();
    }


    public static void replaceFragment(Context context, Fragment fragment, int layout, Bundle bundle) {
        fragment.setArguments(bundle);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(layout, fragment).commit();
    }

    public static void replaceFragmentWithBackStack(Context context, Fragment fragment, int layout, String name) {
//        todo change AR EN
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(layout, fragment)
                .addToBackStack(name).commit();
    }

    public static void replaceFragmentWithBackStack(Context context, Fragment fragment, int layout, String name, Bundle bundle) {
        fragment.setArguments(bundle);
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(layout, fragment).addToBackStack(name).commit();
    }

    /*
    public static void replaceFragmentWithSlideAnimationWithStack(Context context,Fragment fragment, int layout,String name){
        FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(layout, fragment).addToBackStack(name).commit();
    }
    */
}
