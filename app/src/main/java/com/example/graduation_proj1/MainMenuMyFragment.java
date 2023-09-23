package com.example.graduation_proj1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduation_proj1.models.RecyclerViewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainMenuMyFragment extends Fragment {

    private TextView usernameTextView;
    private DatabaseReference userRef;
    private TextView userNameTextView;

    private List<RecyclerViewItem> itemList = new ArrayList<>();
    private LostItemAdapter adapter;

    public static MainMenuMyFragment newInstance(){
        return new MainMenuMyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_my, container, false);

        usernameTextView = rootView.findViewById(R.id.userNameTextView);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView2);

        //item list 출력
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LostItemAdapter(getActivity(), itemList, new LostItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewItem item) {
                // 아이템을 클릭했을 때 호출되는 메서드
                // 상세 페이지로 이동
                ItemDetailFragment fragment = ItemDetailFragment.newInstance(item);

                // Fragment를 트랜잭션을 사용하여 교체
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null); // 백 스택에 추가하여 뒤로 가기 버튼을 사용할 수 있도록 함
                transaction.commit();
            }
        });

        recyclerView.setAdapter(adapter);

        fetchLostItems(rootView); // Firebase에서 데이터 가져오기


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

        // 로그아웃 버튼 클릭 시 로그아웃 처리 및 메인 화면으로 이동
        Button logoutButton = rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                navigateToMainActivity();
            }
        });

        return rootView;
    }

    private void fetchLostItems(ViewGroup rootView ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Context context = getActivity();

        //유저에 해당하는 정보만 가져오기
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String title = documentSnapshot.getString("title");
                        String itemType = documentSnapshot.getString("itemType");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        Log.d("MyTag", "Title: " + title + ", ItemType: " + itemType + ", ImageUrl: " + imageUrl);

                        // title과 itemType을 사용하거나 저장
                        RecyclerViewItem recyclerViewItem = new RecyclerViewItem();
                        recyclerViewItem.setTitle(title);
                        recyclerViewItem.setItemType(itemType);
                        recyclerViewItem.setImageUrl(imageUrl);
                        itemList.add(recyclerViewItem);
                    }
                    adapter.notifyDataSetChanged(); // 데이터 변경 알림

                    // 아이템 목록이 비어있는지 확인하고 TextView를 처리
                    if (itemList.isEmpty()) {
                        // 아이템 목록이 비어있을 때
                        TextView noItemsTextView = rootView.findViewById(R.id.textView25);
                        noItemsTextView.setVisibility(View.VISIBLE); // TextView를 표시
                    } else {
                        // 아이템 목록이 비어있지 않을 때
                        TextView noItemsTextView = rootView.findViewById(R.id.textView25);
                        noItemsTextView.setVisibility(View.GONE); // TextView를 숨김
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터 가져오기 실패 처리
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 기존 엑티비티 스택을 모두 비웁니다.
        startActivity(intent);
    }
}