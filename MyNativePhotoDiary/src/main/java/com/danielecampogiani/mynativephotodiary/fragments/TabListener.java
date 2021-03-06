package com.danielecampogiani.mynativephotodiary.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;


public class TabListener<T extends Fragment> implements ActionBar.TabListener{

    private Fragment fragment;
    private final Activity activity;
    private final Class<T> fragmentClass;
    private final int frameContainer;

    public TabListener(Activity activity, int frameContainer, Class<T> fragmentClass){
        this.activity=activity;
        this.frameContainer=frameContainer;
        this.fragmentClass=fragmentClass;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment==null){
            String fragmentName = fragmentClass.getName();
            fragment = Fragment.instantiate(activity,fragmentName);
            ft.add(frameContainer,fragment,fragmentName);
        }
        else
            ft.attach(fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment!=null)
            ft.detach(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (fragment!=null)
            ft.attach(fragment);
    }
}
