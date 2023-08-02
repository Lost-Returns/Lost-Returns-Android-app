package com.example.graduation_proj1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class FindPasswordActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private Button buttonResetPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                    Toast.makeText(FindPasswordActivity.this, "이름과 이메일을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    checkNameAndResetPassword(name, email);
                }
            }
        });
    }

    private void checkNameAndResetPassword(final String name, final String email) {
        db.collection("users")
                .whereEqualTo("name", name)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                // 이름과 이메일이 일치하는 사용자가 존재하는 경우
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // 이메일 재설정 메일 전송 성공
                                                    Toast.makeText(FindPasswordActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                                    finish(); // 다시 로그인 화면으로 전환.
                                                } else {
                                                    // 이메일 재설정 메일 전송 실패
                                                    Toast.makeText(FindPasswordActivity.this, "비밀번호 재설정 이메일을 보내는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // 이름과 이메일이 일치하는 사용자가 존재하지 않는 경우
                                Toast.makeText(FindPasswordActivity.this, "입력한 이름과 이메일이 일치하는 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Firestore 쿼리 실행 실패
                            Toast.makeText(FindPasswordActivity.this, "Firestore 쿼리 실행 실패.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
