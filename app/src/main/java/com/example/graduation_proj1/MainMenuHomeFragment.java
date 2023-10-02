package com.example.graduation_proj1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

        //spinner 추가
        List<String> categoryList = new ArrayList<>();
        categoryList.add("전체");
        categoryList.add("태블릿");
        categoryList.add("스마트워치");
        categoryList.add("무선이어폰");
        categoryList.add("신분증");
        categoryList.add("가방");
        categoryList.add("지갑");
        categoryList.add("카드");
        categoryList.add("휴대폰");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner categorySpinner = rootView.findViewById(R.id.categorySpinner);
        categorySpinner.setAdapter(categoryAdapter);

        // 1. 스피너에서 카테고리 선택 시 호출되는 이벤트 리스너 설정
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 2. 선택된 카테고리 값을 가져옴
                String selectedCategory = (String) parentView.getSelectedItem();
                if(selectedCategory.equals("전체")){
                    adapter.setItems(itemList);
                } else {
                    // 3. 선택된 카테고리를 기반으로 아이템을 필터링하고 RecyclerView에 표시
                    List<RecyclerViewItem> filteredItems = filterItemsByCategory(selectedCategory);
                    adapter.setItems(filteredItems);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때 처리 (옵션이 필요하면 추가)
            }
        });



        //recycler view.
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);


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

        fetchLostItems(); // Firebase에서 데이터 가져오기

        return rootView;
    }

    private List<RecyclerViewItem> filterItemsByCategory(String category) {
        List<RecyclerViewItem> filteredItems = new ArrayList<>();

        for (RecyclerViewItem item : itemList) {
            if (item.getItemType().equals(category)) { // 예를 들어, 카테고리 필터링을 아이템의 itemType과 연결하는 경우
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    private void fetchLostItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Context context = getActivity();

        //유저에 해당하는 정보만 가져오기
        //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        db.collectionGroup("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String userId = documentSnapshot.getReference().getParent().getParent().getId();
                        String title = documentSnapshot.getString("title");
                        String itemType = documentSnapshot.getString("itemType");
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        //추가정보
                        String owner = documentSnapshot.getString("owner");
                        String location = documentSnapshot.getString("location");
                        String contact = documentSnapshot.getString("contact");
                        String foundDate = documentSnapshot.getString("foundDate");
                        String foundLocation = documentSnapshot.getString("foundLocation");


                        Log.d("MyTag", "User: " + userId + ", Title: " + title + ", ItemType: " + itemType + ", ImageUrl: " + imageUrl);

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

    }


}