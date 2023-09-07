package com.example.graduation_proj1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduation_proj1.MainActivity;
import com.example.graduation_proj1.MainMenuActivity;
import com.example.graduation_proj1.MyProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainMenuMyFragment extends Fragment {

    private TextView usernameTextView;
    private TextView userNameTextView;
    private RecyclerView recyclerView;
    private TextView noActivitiesTextView;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private List<ActivityModel> activitiesList;
    private ActivitiesAdapter adapter;
    public class ActivityModel {
        private String activityId;
        private String userId;
        private String activityName;
        // 다른 필요한 필드 추가

        public ActivityModel() {
            // 기본 생성자
        }

        public ActivityModel(String activityId, String userId, String activityName /* 다른 필드 */) {
            this.activityId = activityId;
            this.userId = userId;
            this.activityName = activityName;
            // 다른 필드 초기화
        }

        public String getActivityId() {
            return activityId;
        }

        public void setActivityId(String activityId) {
            this.activityId = activityId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        // 다른 필드에 대한 Getter 및 Setter 메서드 추가

        // 필요한 다른 메서드 추가
    }
    public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {

        private List<ActivityModel> activitiesList;

        public ActivitiesAdapter(List<ActivityModel> activitiesList) {
            this.activitiesList = activitiesList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // 사용할 레이아웃을 설정하세요 (예: R.layout.item_activity)
            View activityView = inflater.inflate(R.layout.item_activity, parent, false);

            // 뷰 홀더 객체를 생성하고 반환하세요
            ViewHolder viewHolder = new ViewHolder(activityView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ActivityModel activity = activitiesList.get(position);

            // 활동 데이터를 화면에 표시하세요
            holder.activityNameTextView.setText(activity.getActivityName());
            // 다른 필드를 표시하는 코드 추가
        }


        @Override
        public int getItemCount() {
            return activitiesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView activityNameTextView;
            // 다른 뷰를 여기에 추가

            public ViewHolder(View itemView) {
                super(itemView);

                // 뷰에서 사용할 위젯들을 찾아서 초기화하세요
                activityNameTextView = itemView.findViewById(R.id.activityNameTextView);
                // 다른 위젯을 초기화하는 코드 추가
            }
        }
    }


    public static MainMenuMyFragment newInstance(){
        return new MainMenuMyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_menu_my, container, false);

        usernameTextView = rootView.findViewById(R.id.userNameTextView);
        recyclerView = rootView.findViewById(R.id.activitiesRecyclerView);
        noActivitiesTextView = rootView.findViewById(R.id.noActivitiesTextView);

        // 사용자 이름 불러와 TextView에 표시
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Firestore에서 사용자 이름 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = db.collection("users").document(userId);

            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name"); // 사용자 이름

                        // 사용자 이름 및 전화번호 표시
                        if (userName != null) {
                            usernameTextView.setText(userName);
                        } else {
                            usernameTextView.setText("이름 없음");
                        }
                    }
                }
            });

            // 활동 데이터 가져오기
            firestore = FirebaseFirestore.getInstance();
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            activitiesList = new ArrayList<>();
            adapter = new ActivitiesAdapter(activitiesList);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            fetchActivities(userId);
        } else {
            // 사용자가 인증되지 않았거나 로그인되지 않은 상태
            // 이 부분에 대한 처리가 필요하다면 여기에 작성
        }

        // 프로필 수정 버튼 클릭 시 액티비티 전환
        Button editProfileButton = rootView.findViewById(R.id.editProfile_btn);
        editProfileButton.setOnClickListener(v -> {
            // 새로운 Fragment로 이동
            ((MainMenuActivity) requireActivity()).replaceFragment(MyProfileFragment.newInstance());
        });

        // 로그아웃 버튼 클릭 시 로그아웃 처리 및 메인 화면으로 이동
        Button logoutButton = rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            navigateToMainActivity();
        });

        return rootView;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void fetchActivities(String userId) {
        if (currentUser == null) {
            return;
        }

        // Firestore에서 사용자의 활동 데이터를 가져옵니다.
        firestore.collection("activities")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // 오류 처리
                        return;
                    }

                    activitiesList.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        ActivityModel activity = document.toObject(ActivityModel.class);
                        if (activity != null) {
                            activitiesList.add(activity);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    // 활동이 없는 경우 메시지 표시
                    if (activitiesList.isEmpty()) {
                        noActivitiesTextView.setVisibility(View.VISIBLE);
                    } else {
                        noActivitiesTextView.setVisibility(View.GONE);
                    }
                });
    }
}
