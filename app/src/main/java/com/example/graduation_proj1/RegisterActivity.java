package com.example.graduation_proj1;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.graduation_proj1.models.Users;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword, editTextName, editTextContact;
    private Button buttonSignUp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.confirmPasswordEditText);
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
        // LoginActivity 클래스의 onCreate 메서드 내부에 추가
        ImageView showPasswordButton = findViewById(R.id.showpassword); // 눈동자 버튼
        EditText passwordView = findViewById(R.id.confirmPasswordEditText); // 비밀번호 입력 필드

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordView.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    // 비밀번호 보이게 설정
                    passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_eye_off);
                } else {
                    // 비밀번호 감추게 설정
                    passwordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordButton.setImageResource(R.drawable.ic_eye);
                }
                // 커서 위치 복원
                passwordView.setSelection(passwordView.getText().length());
            }
        });

        // LoginActivity 클래스의 onCreate 메서드 내부에 추가
        ImageView showPasswordButton2 = findViewById(R.id.showpassword2); // 눈동자 버튼
        EditText passwordView2 = findViewById(R.id.editTextConfirmPassword); // 비밀번호 입력 필드

        showPasswordButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordView2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    // 비밀번호 보이게 설정
                    passwordView2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPasswordButton2.setImageResource(R.drawable.ic_eye_off);
                } else {
                    // 비밀번호 감추게 설정
                    passwordView2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswordButton2.setImageResource(R.drawable.ic_eye);
                }
                // 커서 위치 복원
                passwordView2.setSelection(passwordView2.getText().length());
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
                                saveAdditionalUserInfo(user.getUid(), email, name, contact);
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

    private void saveAdditionalUserInfo( String userId, String email, String name, String contact) {
        // Firestore에 추가 정보를 저장하는 코드를 작성합니다.
        // 예시로 users 컬렉션을 생성하고 해당 사용자의 문서에 추가 정보를 저장합니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Users user = new Users(email, name, contact);
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
