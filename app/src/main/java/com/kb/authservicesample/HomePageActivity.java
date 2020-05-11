package com.kb.authservicesample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.huawei.agconnect.auth.AGConnectAuth;

import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {
    private AGConnectAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        setContentView(R.layout.activity_homepage);

        auth = AGConnectAuth.getInstance();

        TextView name = findViewById(R.id.textUserName);
        name.setText((auth.getCurrentUser().getDisplayName() != null) ? auth.getCurrentUser().getDisplayName() : "Anonymous");

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AGConnectAuth.getInstance().signOut();
                startActivity(new Intent(HomePageActivity.this, MainActivity.class));
                finish();
            }
        });

        findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.deleteUser();
                startActivity(new Intent(HomePageActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
