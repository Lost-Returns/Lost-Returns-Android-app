package com.example.graduation_proj1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MyProfileActivity extends LoginActivity {

    private Button reviseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        reviseButton = findViewById(R.id.button7);
        reviseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                startActivity(intent);
            }
        });

        // Firebase 사용자 정보 가져오기
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail(); // 사용자 이메일
            String userName = user.getDisplayName(); // 사용자 이름

            // 사용자 연락처 가져오기
            for (UserInfo profile : user.getProviderData()) {
                if (profile.getProviderId().equals("phone")) {
                    String userPhoneNumber = profile.getPhoneNumber();
                    // 여기서 사용자 연락처를 사용하도록 처리
                }
            }
        } else {
            // 사용자가 인증되지 않았거나 로그인되지 않은 상태
        }
        //수정 버튼 클릭 시 액티비티 전환
        Button revis_buttion = (Button) findViewById(R.id.button7);
        revis_buttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                startActivity(intent);
            }
        });
    }
}
