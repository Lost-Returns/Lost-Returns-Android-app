package com.example.graduation_proj1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainMenuMyFragment extends Fragment {

    private TextView usernameTextView;
    private DatabaseReference userRef;

    public static MainMenuMyFragment newInstance(){
        return new MainMenuMyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_my, container, false);

        usernameTextView = rootView.findViewById(R.id.usernameTextView);

        // 사용자 이름 불러와 TextView에 표시
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(userId);

        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    usernameTextView.setText("사용자 이름: " + userName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });

        // 프로필 수정 버튼 클릭 시 액티비티 전환
        Button edit_profile_button = rootView.findViewById(R.id.editProfile_btn);
        edit_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 Fragment로 이동
                ((MainMenuActivity)getActivity()).replaceFragment(MyProfileFragment.newInstance());
            }
        });

        return rootView;
    }

}