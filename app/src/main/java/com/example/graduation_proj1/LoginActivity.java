package com.example.graduation_proj1;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends MainActivity {
    FirebaseAuth mAuth;

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
        TextView emailView = (TextView) findViewById(R.id.editTextTextEmailAddress2);
        TextView passwordView = (TextView) findViewById(R.id.editTextTextPassword);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 체크

                String email = emailView.getText().toString(); //사용자가 입력한 이메일
                String password = passwordView.getText().toString(); //사용자가 입력한 비밀번호

                mAuth = FirebaseAuth.getInstance();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String userId = user.getUid();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();


                                    db.collection("users").document(userId)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String userName = documentSnapshot.getString("name");
                                                        // 가져온 userName으로 로그인 환영 메세지 출력.
                                                        Toast.makeText(LoginActivity.this, userName +" 님 방문을 환영합니다.",
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "사용자 정보가 없습니다.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, "db 연결 실패",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });

            }
        });
        // LoginActivity 클래스의 onCreate 메서드 내부에 추가
        ImageView showPasswordButton = findViewById(R.id.showpassword); // 눈동자 버튼
        EditText passwordView2 = findViewById(R.id.editTextTextPassword); // 비밀번호 입력 필드

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordView2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    // 비밀번호 보이게 설정
                    passwordView2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_eye_off);
                } else {
                    // 비밀번호 감추게 설정
                    passwordView2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_eye);
                }
                // 커서 위치 복원
                passwordView2.setSelection(passwordView2.getText().length());
            }
        });


    }

    //화면 전환
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // 사용자가 로그인되어 있는 경우
            Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
            startActivity(intent);
        } else {
            // 사용자가 로그아웃되었거나 인증이 실패한 경우
            Toast.makeText(LoginActivity.this, "유저 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}