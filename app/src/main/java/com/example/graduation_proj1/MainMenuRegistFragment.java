package com.example.graduation_proj1;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainMenuRegistFragment extends Fragment {

    private Button btn_picture;
    private ImageView imageView;
    private EditText titleEditText, itemTypeEditText, ownerEditText, locationEditText, contactEditText, foundDateEditText, foundLocationEditText;
    private Button registerButton;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;
    private Uri selectedImageUri; // 이미지 URI를 저장할 변수


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_main_menu_regist, container, false);
        //View rootView = inflater.inflate(R.layout.fragment_main_menu_regist, container, false);

        btn_picture = rootView.findViewById(R.id.btn_picture);
        imageView = rootView.findViewById(R.id.imageView);
        titleEditText = rootView.findViewById(R.id.titleEditText);
        itemTypeEditText = rootView.findViewById(R.id.itemTypeEditText);
        ownerEditText = rootView.findViewById(R.id.ownerEditText);
        locationEditText = rootView.findViewById(R.id.locationEditText);
        contactEditText = rootView.findViewById(R.id.contactEditText);
        foundDateEditText = rootView.findViewById(R.id.foundDateEditText);
        foundLocationEditText = rootView.findViewById(R.id.foundLocationEditText);
        registerButton = rootView.findViewById(R.id.registerButton);

        btn_picture.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri = selectedImageUri;
                String title = titleEditText.getText().toString();
                String itemType = itemTypeEditText.getText().toString();
                String owner = ownerEditText.getText().toString();
                String loc = locationEditText.getText().toString();
                String contact = contactEditText.getText().toString();
                String foundDate = foundDateEditText.getText().toString();
                String foundLoc = foundLocationEditText.getText().toString();

                // DB에 item 정보 저장
                saveDataToFirestoreWithImage(imageUri,title, itemType, owner, loc, contact, foundDate, foundLoc);
            }
        });

        return rootView;
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "앨범",
                "카메라"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없는 경우, 권한 요청
            requestCameraPermission();
        } else {
            // 권한이 있는 경우, 카메라 실행
            startCamera();
        }
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            // 권한 요청 설명을 보여줄 수 있는 경우
            // 사용자에게 권한이 필요한 이유를 설명하는 다이얼로그 등을 표시하고 권한 요청
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Camera Permission Required")
                    .setMessage("This app needs camera permission to take photos.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {
            // 권한 요청 설명을 보여줄 수 없는 경우, 직접 권한 요청
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우, 카메라 실행
                startCamera();
            } else {
                // 권한이 거부된 경우 아무 처리도 하지 않음.
                return;
            }
        }
    }

    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 결과가 취소되었을 경우에는 아무것도 처리하지 않음.
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }

        //requestCode에 따라 다르게 처리
        if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                // 갤러리에서 선택한 이미지의 Uri 가져오기
                selectedImageUri = data.getData();
                try {
                    // Uri를 이용하여 비트맵으로 변환
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    //이미지뷰에 비트맵 설정하여 표시
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA) {
            if (data != null) {
                //이미지 uri 저장 - 나중에 저장 시 필요
                selectedImageUri = data.getData();

                // 카메라로 찍은 이미지의 비트맵 가져오기
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //이미지뷰에 비트맵 설정하여 표시
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //" 이미지를 제외한 나머지 정보 입력하기 구현"

    // 1. 물건 종류 수동 선택

    // 2. 보관 장소, 습득 장소 선택 (지도)

    // 3. 습득 일자 (달력)




    // "DB에 물건 정보 저장하기"

    // 1. 이미지 모델 서버에 전송 => 카테고리 값 받아와서 카테고리 자동 추가

    // 2. (이미지, 제목, 물건 종류, 분실자명, 보관 장소, 연락처, 습득 일자, 습득 장소) DB에 저장
    private void saveDataToFirestoreWithImage(Uri imageUri, String title, String itemType, String owner, String location, String contact, String foundDate, String foundLocation) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            String imageName = "image_" + System.currentTimeMillis() + ".jpg"; // 이미지 이름 생성

            StorageReference imageRef = storage.getReference().child("images/" + userId + "/" + imageName);
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 이미지 업로드 성공 시
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String imageUrl = task.getResult().toString();

                                Map<String, Object> data = new HashMap<>();
                                data.put("title", title);
                                data.put("itemType", itemType);
                                data.put("owner", owner);
                                data.put("location", location);
                                data.put("contact", contact);
                                data.put("foundDate", foundDate);
                                data.put("foundLocation", foundLocation);
                                data.put("imageUrl", imageUrl);

                                db.collection("users").document(userId)
                                        .collection("items").add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // 데이터 저장 성공 시 처리
                                                Toast.makeText(getActivity(), "데이터 저장 성공!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // 데이터 저장 실패 시 처리
                                                Toast.makeText(getActivity(), "데이터 저장 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 이미지 업로드 실패 시
                    Toast.makeText(getActivity(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{ //user 정보가 없을 때 (확인용)
            Toast.makeText(getActivity(), "user 정보가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}