package com.example.graduation_proj1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.graduation_proj1.MainMenuActivity;
import com.example.graduation_proj1.MyProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenuMyFragment extends Fragment {

    private TextView usernameTextView;
    private DatabaseReference userRef;
    private TextView userNameTextView;

    public static MainMenuMyFragment newInstance(){
        return new MainMenuMyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_my, container, false);

        usernameTextView = rootView.findViewById(R.id.userNameTextView);

        // 사용자 이름 불러와 TextView에 표시
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            userRef = database.getReference("users").child(userId);
            userNameTextView = rootView.findViewById(R.id.userNameTextView);

            // Firestore에서 사용자 이름 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(userId); // 변경: uid 대신 userId 사용

            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name"); // 사용자 이름

                        // 사용자 이름 및 전화번호 표시
                        if (userName != null) {
                            userNameTextView.setText(userName);
                        } else {
                            userNameTextView.setText("이름 없음");
                        }
                    }
                }
            });

        } else {
            // 사용자가 인증되지 않았거나 로그인되지 않은 상태
            // 이 부분에 대한 처리가 필요하다면 여기에 작성
        }

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
