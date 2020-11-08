package com.example.whatsapp.view.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.databinding.ActivityPhoneLoginBinding;
import com.example.whatsapp.model.user.Users;
import com.example.whatsapp.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity{

    private ActivityPhoneLoginBinding binding;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static String TAG="PhoneLoginActivity";
    private ProgressDialog progressDialog;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_phone_login);
        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            startActivity(new Intent(this,SetUserInfoActivity.class));
        }

        progressDialog=new ProgressDialog(this);
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.btnNext.getText().equals("Next")) {
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    String phone = "+"+ binding.edCodeCountry.getText().toString() + binding.edPhone.getText().toString();
                    startPhoneNumberVerification(phone);
                    binding.edCode.setVisibility(v.getVisibility());
                }else{
                    progressDialog.setMessage("Verifying... ");
                    progressDialog.show();

                    verifyPhoneNumberWithCode(mVerificationId,binding.edCode.getText().toString());
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG,"onComplete: Verification Successful");
            signInWithPhoneAuthCredential(phoneAuthCredential);
            progressDialog.dismiss();

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e(TAG,"onVerification:"+e.getMessage());
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;
                binding.btnNext.setText("Confirm");
                progressDialog.dismiss();
               }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        progressDialog.setMessage("Code Sent to "+phoneNumber);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            startActivity(new Intent(PhoneLoginActivity.this,SetUserInfoActivity.class));

                         /*   if (user != null) {
                                String userID=user.getUid();
                                Users users=new Users(userID,
                                        "",
                                        user.getPhoneNumber(),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "");
                                firestore.collection("Users").document("UserInfo").collection(userID).add(users).
                                        addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                            }
                                        });
                            }else{
                                Toast.makeText(getApplicationContext(),"Some Error",Toast.LENGTH_LONG).show();
                            }
                            //startActivity(new Intent(PhoneLoginActivity.this,SetUserInfoActivity.class));
                      */  } else {
                            progressDialog.dismiss();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Log.e(TAG,"onComplete: Error Code");
                            }

                        }
                    }
                });
    }
}