package com.example.graduation_proj1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import com.example.graduation_proj1.models.Users;

public class RegisterActivity extends MainActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName, editTextContact;
    private Button buttonSignUp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                String name = editTextName.getText().toString();
                String contact = editTextContact.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    signUpWithEmailPassword(email, password, name, contact);
                }
            }
        });
    }

    private void signUpWithEmailPassword(final String email, final String password, final String name, final String contact) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            FirebaseUser user = mAuth.getCurrentUser();

                            // 여기서 추가 정보(이름, 연락처 등)를 저장할 수도 있습니다.
                            // 예를 들어, Firestore 등을 사용하여 데이터베이스에 저장할 수 있습니다.
                            if (user != null) {
                                // 사용자가 로그인한 경우, 추가 정보를 Firestore에 저장합니다.
                                saveAdditionalUserInfo(user.getUid(), name, contact);
                            }

                            // 이 예제에서는 간단하게 Toast 메시지로 확인해봅니다.
                            Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            finish(); // 회원가입 성공 시, 현재 액티비티 종료
                        } else {
                            // 회원가입 실패
                            Toast.makeText(RegisterActivity.this, "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveAdditionalUserInfo(String userId, String name, String contact) {
        // Firestore에 추가 정보를 저장하는 코드를 작성합니다.
        // 예시로 users 컬렉션을 생성하고 해당 사용자의 문서에 추가 정보를 저장합니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Users user = new Users(name, contact);
        final String TAG = "RegisterActivity";

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "정보 저장 성공!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "error: 정보 저장 실패!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
