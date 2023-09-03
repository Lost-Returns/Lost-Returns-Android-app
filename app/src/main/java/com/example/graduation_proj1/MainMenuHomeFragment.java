package com.example.graduation_proj1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduation_proj1.models.RecyclerViewItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainMenuHomeFragment extends Fragment {

    public static MainMenuHomeFragment newInstance(){
        return new MainMenuHomeFragment();
    }

    private List<RecyclerViewItem> itemList = new ArrayList<>();
    private LostItemAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_home, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LostItemAdapter(getActivity(), itemList);
        recyclerView.setAdapter(adapter);

        fetchLostItems(); // Firebase에서 데이터 가져오기

        return rootView;
    }

    private void fetchLostItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Context context = getActivity();

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
                })
                .addOnFailureListener(e -> {
                    // 데이터 가져오기 실패 처리
                });

    }

}