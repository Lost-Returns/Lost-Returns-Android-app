package com.example.graduation_proj1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MyProfileFragment extends Fragment {

    public static MyProfileFragment newInstance() {
        return new MyProfileFragment();
    }

    private Button reviseButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_profile, container, false);

        //필요한 코드 넣기 - MyProfileActivity.java에 있던 코드를 Fragment 형식으로 변형해서 가져오기.



        return rootView;
    }
}