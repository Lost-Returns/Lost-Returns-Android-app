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


    private List<RecyclerViewItem> itemList = new ArrayList<>();
    private LostItemAdapter adapter;

    public static MainMenuMyFragment newInstance(){

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
        getActivity().finish(); // 현재 액티비티를 종료합니다.
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}