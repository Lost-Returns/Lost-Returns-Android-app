package com.example.graduation_proj1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation_proj1.MyActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfileFragment extends Fragment {


    public static MyProfileFragment newInstance() {
        return new MyProfileFragment();
    }

    private Button reviseButton;
    private TextView userEmailTextView;
    private TextView userNameTextView;
    private TextView userContactTextView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        Button MainMenuMyFragment_btn = rootView.findViewById(R.id.button7);
        MainMenuMyFragment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 Fragment로 이동
                ((MainMenuActivity)getActivity()).replaceFragment(MainMenuMyFragment.newInstance());
            }
        });

        userEmailTextView = rootView.findViewById(R.id.userEmailTextView);
        userNameTextView = rootView.findViewById(R.id.userNameTextView);
        userContactTextView = rootView.findViewById(R.id.userContactTextView);

        // Firebase 사용자 정보 가져오기
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail(); // 사용자 이메일
            String uid = user.getUid(); // 사용자 UID

            // 사용자 이메일 표시
            userEmailTextView.setText(userEmail);

            // Firestore에서 사용자 이름 및 전화번호 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(uid);

            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name"); // 사용자 이름
                        String userContract = document.getString("contact"); // 사용자 전화번호

                        // 사용자 이름 및 전화번호 표시
                        if (userName != null) {
                            userNameTextView.setText(userName);
                        } else {
                            userNameTextView.setText("이름 없음");
                        }

                        if (userContract != null) {
                            userContactTextView.setText(userContract);
                        } else {
                            userContactTextView.setText("전화번호 없음");
                        }
                    }
                }
            });

        } else {
            // 사용자가 인증되지 않았거나 로그인되지 않은 상태
        }

        return rootView;
    }
}
