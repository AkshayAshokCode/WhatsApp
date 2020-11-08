package com.example.whatsapp.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.ActivitySetUserInfoBinding;
import com.example.whatsapp.model.user.Users;
import com.example.whatsapp.view.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetUserInfoActivity extends AppCompatActivity {
    private ActivitySetUserInfoBinding binding;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_set_user_info);
        progressDialog=new ProgressDialog(this);
    initCButtonClick();
    }

    private void initCButtonClick() {
    binding.btnNext.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(binding.edName.getText().toString())){
                Toast.makeText(getApplicationContext(),"Please input your Name",Toast.LENGTH_SHORT).show();
            }else {
                doUpdate();
            }
        }
    });
    binding.imageProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"This function is not ready to use",Toast.LENGTH_SHORT).show();

        }
    });
    }

    private void doUpdate() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        if(firebaseUser!=null){
            String userID=firebaseUser.getUid();

            Users users=new Users(userID,
                    binding.edName.getText().toString(),
                    firebaseUser.getPhoneNumber(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(users)
                    //.update("userName",binding.edName.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Update Successful",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();

                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"You need to login first",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
}