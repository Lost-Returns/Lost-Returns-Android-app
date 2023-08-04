package com.example.graduation_proj1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends MainActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // 비밀번호 찾기 버튼 클릭 시 액티비티 전환
        TextView pw_find_button = (TextView) findViewById(R.id.FindPasswordView);
        pw_find_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FindPasswordActivity.class);
                startActivity(intent);
            }


        });

        // 회원가입 버튼 클릭 시 액티비티 전환
        Button register_button = (Button) findViewById(R.id.button6);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        //로그인 버튼 클릭 시 액티비티 전환
        Button login_button = (Button) findViewById(R.id.button4);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v {
                Intent intent = new Intent(getApplicationContext(), ItemlistActivity.class);
                startActivity(intent);
            })
        });

    }
}