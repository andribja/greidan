package com.greidan.greidan.greidan.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greidan.greidan.greidan.R;

public class UserProfileFragment extends Fragment {

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    public UserProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }
}
