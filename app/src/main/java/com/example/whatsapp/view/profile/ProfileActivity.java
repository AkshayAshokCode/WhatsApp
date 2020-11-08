package com.example.whatsapp.view.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.example.whatsapp.R;
import com.example.whatsapp.common.Common;
import com.example.whatsapp.databinding.ActivityProfileBinding;
import com.example.whatsapp.view.display.ViewImageActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bottomSheetDialog, bsDialogEditName;
    private ProgressDialog progressDialog;
    private int IMAGE_GALLERY_REQUEST= 111;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        if(firebaseUser!=null){
            getInfo();
        }
        initActionClick();
    }

    private void initActionClick() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });
        binding.lnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetEditName();
            }
        });
        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageProfile.invalidate();
                Drawable dr= binding.imageProfile.getDrawable();
                Common.IMAGE_BITMAP=((GlideBitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat= ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this,binding.imageProfile,"image");
                Intent intent=new Intent(ProfileActivity.this,ViewImageActivity.class);
                startActivity(intent,activityOptionsCompat.toBundle());
            }
        });
    }

    private void showBottomSheetEditName() {
        @SuppressLint("InflateParams") View  view=getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsDialogEditName.dismiss();
            }
        });
        final EditText edUserName= view.findViewById(R.id.ed_username);
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edUserName.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Name can't be empty",Toast.LENGTH_SHORT).show();
                }else {
                    updateName(edUserName.getText().toString());
                    bsDialogEditName.dismiss();
                }
            }
        });

        bsDialogEditName=new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsDialogEditName=null;
            }
        });
        bsDialogEditName.show();
    }

    private void showBottomSheetPickPhoto() {
       @SuppressLint("InflateParams") View  view=getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);
        view.findViewById(R.id.ln_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhoto();
                bottomSheetDialog.dismiss();
            }
        });
        view.findViewById(R.id.ln_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });
        view.findViewById(R.id.ln_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"camera",Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });
        bottomSheetDialog.show();
    }

    private void removePhoto() {
        Toast.makeText(getApplicationContext(),"removed",Toast.LENGTH_LONG).show();
    }

    private void getInfo() {
        firestore.collection("Users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userName= documentSnapshot.getString("userName");
                String userPhone= documentSnapshot.getString("userPhone");
                String imageProfile= documentSnapshot.getString("imageProfile");
                Glide.with(ProfileActivity.this).load(imageProfile).into(binding.imageProfile);

                binding.tvUsername.setText(userName);
                binding.tvPhone.setText(userPhone);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void openGallery(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select image"),IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_GALLERY_REQUEST
        && resultCode==RESULT_OK
        && data !=null
        && data.getData()!=null){
            imageUri=data.getData();
            uploadToFirebase();
          /*  try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                binding.imageProfile.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        */}
    }
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadToFirebase() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if(imageUri!=null){
            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesProfile/"+ System.currentTimeMillis()+"."+getFileExtention(imageUri));
            riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());
                    Uri downloadUrl= uriTask.getResult();
                    final String sdownload_url = String.valueOf(downloadUrl);
                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("imageProfile",sdownload_url);
                    progressDialog.dismiss();
                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Uploaded successfully",Toast.LENGTH_SHORT).show();
                            getInfo();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }
    private void updateName(String newName){
        firestore.collection("Users").document(firebaseUser.getUid()).update("userName",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Username Updated",Toast.LENGTH_SHORT).show();
                getInfo();
            }
        });
    }
}