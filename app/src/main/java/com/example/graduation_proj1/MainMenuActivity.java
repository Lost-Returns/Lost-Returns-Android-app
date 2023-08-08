package com.example.graduation_proj1;

import android.content.ClipData;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.graduation_proj1.databinding.ActivityMainBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainMenuActivity extends AppCompatActivity {

    //ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;

    private static final int MENU_HOME = R.id.fragment_main_menu_home;
    private static final int MENU_REGIST = R.id.fragment_main_menu_regist;
    private static final int MENU_MY = R.id.fragment_main_menu_my;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if(isUserLoggedIn()) {

            replaceFragment(new MainMenuHomeFragment()); // 기본으로 HomeFragment 화면 표시

            bottomNavigationView = findViewById(R.id.bottomNavigationView);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == MENU_HOME) {
                    replaceFragment(new MainMenuHomeFragment());
                } else if (id == MENU_REGIST) {
                    replaceFragment(new MainMenuRegistFragment());
                } else if (id == MENU_MY) {
                    replaceFragment(new MainMenuMyFragment());
                }

                return true;

            });
        }
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private boolean isUserLoggedIn(){
        //로그인 상태를 체크하는 로직 구현


        // 로그인 성공시
        return true;
        // 로그인 살패 시
        //return false;
    }
}