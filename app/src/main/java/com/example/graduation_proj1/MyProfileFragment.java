package com.example.graduation_proj1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

        // 비밀번호 수정
        EditText newPasswordEditText = rootView.findViewById(R.id.confirmPasswordEditText);
        EditText confirmPasswordEditText = rootView.findViewById(R.id.editTextConfirmPassword);
        Button changePasswordButton = rootView.findViewById(R.id.button7);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    // 비밀번호 입력 확인
                    return;
                }

                if (!(newPassword.equals(confirmPassword))) {
                    // 새 비밀번호와 확인 비밀번호가 일치하지 않을 때 처리
                    //비밀번호 일치하지 않아도 변경되는 문제 해결하기


                    Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;  // 비밀번호가 일치하지 않으면 여기서 함수를 종료하여 변경하지 않도록 처리
                }

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "비밀번호 변경 성공.", Toast.LENGTH_SHORT).show();
                                    // 비밀번호 변경 성공 처리
                                } else {
                                    // 비밀번호 변경 실패 처리
                                    Toast.makeText(requireContext(), "비밀번호 변경 실패.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
                // 비밀번호 보이기/감추기 기능 추가
                EditText passwordEditText = rootView.findViewById(R.id.confirmPasswordEditText);
                ImageView showPasswordButton = rootView.findViewById(R.id.showPasswordButton);

                showPasswordButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectionStart = passwordEditText.getSelectionStart();
                        int selectionEnd = passwordEditText.getSelectionEnd();

                        if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // 비밀번호 보이게 설정
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            showPasswordButton.setImageResource(R.drawable.ic_eye_off);
                        } else {
                            // 비밀번호 감추게 설정
                            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            showPasswordButton.setImageResource(R.drawable.ic_eye);
                        }

                        // 커서 위치 복원
                        passwordEditText.setSelection(selectionStart, selectionEnd);
                    }
                });
                // 비밀번호 보이기/감추기 기능 추가
                EditText passwordEditText2 = rootView.findViewById(R.id.editTextConfirmPassword);
                ImageView showPasswordButton2 = rootView.findViewById(R.id.showPasswordButton2);

                showPasswordButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectionStart2 = passwordEditText2.getSelectionStart();
                        int selectionEnd2 = passwordEditText2.getSelectionEnd();

                        if (passwordEditText2.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                            // 비밀번호 보이게 설정
                            passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            showPasswordButton2.setImageResource(R.drawable.ic_eye_off);
                        } else {
                            // 비밀번호 감추게 설정
                            passwordEditText2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            showPasswordButton2.setImageResource(R.drawable.ic_eye);
                        }

                        // 커서 위치 복원
                        passwordEditText2.setSelection(selectionStart2, selectionEnd2);
                    }
                });

        return rootView;
    }
}
