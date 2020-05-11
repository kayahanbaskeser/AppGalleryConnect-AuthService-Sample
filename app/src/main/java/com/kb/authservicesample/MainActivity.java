package com.kb.authservicesample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Auth Service";
    private AGConnectAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        auth = AGConnectAuth.getInstance();

        findViewById(R.id.btn_huawei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithHuaweiAccount();
            }
        });

        findViewById(R.id.btn_Anonymously).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithAnonymousAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AGConnectAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
            finish();
        }
    }

    private void signInWithHuaweiAccount() {
        HuaweiIdAuthParams mHuaweiIdAuthParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        HuaweiIdAuthService mHuaweiIdAuthService = HuaweiIdAuthManager.getService(MainActivity.this, mHuaweiIdAuthParams);
        startActivityForResult(mHuaweiIdAuthService.getSignInIntent(), 1001);
    }

    private void signInWithAnonymousAccount() {
        AGConnectAuth.getInstance().signInAnonymously().addOnSuccessListener(new OnSuccessListener<SignInResult>() {
            @Override
            public void onSuccess(SignInResult signInResult) {
                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error " + e);
                Toast.makeText(MainActivity.this, "Login with Anonymous Account Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                Log.i(TAG, "signIn success Access Token = " + huaweiAccount.getAccessToken());
                Log.i(TAG, "signIn success User Name = " + huaweiAccount.getDisplayName());
                transmitTokenIntoAppGalleryConnect(huaweiAccount.getAccessToken());
            } else {
                Log.i(TAG, "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void transmitTokenIntoAppGalleryConnect(String accessToken) {
        AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
            @Override
            public void onSuccess(SignInResult signInResult) {
                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error " + e);
            }
        });
    }

}
