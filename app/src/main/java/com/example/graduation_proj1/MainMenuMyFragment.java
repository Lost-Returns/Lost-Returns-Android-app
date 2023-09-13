package com.example.graduation_proj1;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.graduation_proj1.models.RecyclerViewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainMenuMyFragment extends Fragment {

    private TextView usernameTextView;
    private RecyclerView recyclerView;
    private TextView noActivityTextView;
    private UserActivityAdapter userActivityAdapter;

    // 클래스 내에 itemList를 정의
    private List<RecyclerViewItem> itemList = new ArrayList<>();

    private UserActivityAdapter adapter;


    public static MainMenuMyFragment newInstance() {
        return new MainMenuMyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_my, container, false);

        recyclerView = rootView.findViewById(R.id.activitiesRecyclerView);
        noActivityTextView = rootView.findViewById(R.id.noActivityTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userActivityAdapter = new UserActivityAdapter();
        recyclerView.setAdapter(userActivityAdapter);

        // 사용자 활동 내역 불러오기
        fetchUserActivity();

        // 프로필 수정 버튼 클릭 시 액티비티 전환
        Button editProfileButton = rootView.findViewById(R.id.editProfile_btn);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainMenuActivity) getActivity()).replaceFragment(MyProfileFragment.newInstance());
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

    private void fetchUserActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(userId);

            // 기존의 사용자 활동 내역 불러오는 코드와 유저에 해당하는 정보를 가져오는 코드를 섞어서 사용합니다.
            // 사용자 활동 내역 불러오기
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 사용자의 활동 내역이 있는 경우
                        // 활동 내역 데이터를 가져와 어댑터에 추가
                        List<UserActivity> activities = new ArrayList<>();
                        String userActivityData = document.getString("activity_data");

                        if (userActivityData != null) {
                            // 활동 내역을 파싱하여 리스트에 추가
                            String[] activityArray = userActivityData.split(",");
                            for (String activity : activityArray) {
                                activities.add(new UserActivity(activity));
                            }

                            // RecyclerView에 데이터를 설정하고 표시
                            userActivityAdapter.setActivities(activities);
                            recyclerView.setVisibility(View.VISIBLE); // RecyclerView를 보이도록 설정
                            noActivityTextView.setVisibility(View.GONE); // TextView를 숨김
                        } else {
                            // 활동 내역 데이터가 없을 때
                            recyclerView.setVisibility(View.GONE); // RecyclerView를 숨김
                            noActivityTextView.setVisibility(View.VISIBLE); // TextView를 보이도록 설정
                        }

                        // 유저에 해당하는 정보 가져오기
                        db.collectionGroup("items")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    itemList.clear();
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String userId2 = documentSnapshot.getReference().getParent().getParent().getId();
                                        String title = documentSnapshot.getString("title");
                                        String itemType = documentSnapshot.getString("itemType");
                                        String imageUrl = documentSnapshot.getString("imageUrl");
                                        // 추가정보
                                        String owner = documentSnapshot.getString("owner");
                                        String location = documentSnapshot.getString("location");
                                        String contact = documentSnapshot.getString("contact");
                                        String foundDate = documentSnapshot.getString("foundDate");
                                        String foundLocation = documentSnapshot.getString("foundLocation");

                                        Log.d("MyTag", "User: " + userId2 + ", Title: " + title + ", ItemType: " + itemType + ", ImageUrl: " + imageUrl);

                                        // title과 itemType을 사용하거나 저장
                                        RecyclerViewItem recyclerViewItem = new RecyclerViewItem();
                                        recyclerViewItem.setTitle(title);
                                        recyclerViewItem.setItemType(itemType);
                                        recyclerViewItem.setImageUrl(imageUrl);

                                        recyclerViewItem.setOwner(owner);
                                        recyclerViewItem.setLocation(location);
                                        recyclerViewItem.setContact(contact);
                                        recyclerViewItem.setFoundDate(foundDate);
                                        recyclerViewItem.setFoundLocation(foundLocation);
                                        itemList.add(recyclerViewItem);
                                    }
                                    adapter.notifyDataSetChanged(); // 데이터 변경 알림
                                })
                                .addOnFailureListener(e -> {
                                    // 데이터 가져오기 실패 처리
                                });
                    } else {
                        // 문서가 없을 때의 처리
                        recyclerView.setVisibility(View.GONE); // RecyclerView를 숨김
                        noActivityTextView.setVisibility(View.VISIBLE); // TextView를 보이도록 설정
                        noActivityTextView.setText("활동 내역이 없습니다.");
                    }
                } else {
                    // 데이터 가져오기 실패 시 처리
                    recyclerView.setVisibility(View.GONE); // RecyclerView를 숨김
                    noActivityTextView.setVisibility(View.VISIBLE); // TextView를 보이도록 설정
                    noActivityTextView.setText("데이터를 불러올 수 없습니다.");
                }
            });
        } else {
            // 사용자가 인증되지 않았거나 로그인되지 않은 상태
            // 이 부분에 대한 처리가 필요하다면 여기에 작성
        }
    }



    private void navigateToMainActivity() {
        getActivity().finish(); // 현재 액티비티를 종료합니다.
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
