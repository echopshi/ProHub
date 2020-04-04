package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepros.prohub.model.User;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat,toolbarBtnNews,toolbarBtnForms ;
    private ImageButton toolbarBtnSearch,btnHome,toolbarBtnMenu;
    private ToolbarHelper toolbar;
    //

    private Button btnUserInfo, btnStaff;
    private RelativeLayout layoutUserInfo, layoutStaff,layoutChatSettings;
    private TextView tvUserName;
    private ImageView ivUserIconBtn;
    private String userName, imageUrl, userPhoneNum;
    String myRole;

    private User myUser;

    // database reference
    private DatabaseReference myUserRef;
    private StorageReference myStorageRef;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPreferences = getSharedPreferences("myUserSharedPref", Context.MODE_PRIVATE);
        myRole= sharedPreferences.getString("myRole", "");
        userPhoneNum = sharedPreferences.getString("phoneNum", "");
        userName = sharedPreferences.getString("username", "");

        //////////////////////////////////////////////
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        btnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);
        toolbarBtnSettings.setBackgroundColor(getResources().getColor(R.color.btnBackground));

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        //////////////////////////////////////////////
        // Retrieving User Icon
        myUserRef = FirebaseDatabase.getInstance().getReference();
        myStorageRef = FirebaseStorage.getInstance().getReference("userIcons");

        new FirebaseDataseHelper().readUsers(new FirebaseDataseHelper.UserDataStatus() {
            @Override
            public void DataIsLoad(List<User> users, List<String> keys) {
                for (User user : users) {
                    if (user.getPhone().equals(userPhoneNum)) {
                        myUser = user;
                        imageUrl = user.getImageUrl();
                        loadUserImage();
                        break;
                    }
                }
            }
        });

        //////////////////////////////////////////////
        // finding and setting user's name and icon
        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(userName);
        ivUserIconBtn = findViewById(R.id.ivUserIconBtn);

        // Buttons for "Account Settings" and "Staff"
//        btnUserInfo = findViewById(R.id.btnUserInfo);
//        btnStaff = findViewById(R.id.btnStaff);

        layoutUserInfo = findViewById(R.id.btnUserInfo);
        layoutStaff = findViewById(R.id.btnStaff);
        layoutChatSettings=findViewById(R.id.layoutChatSettings);

        layoutUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUserInfo();
            }
        });
        layoutStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStaff();
            }
        });
        layoutChatSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat();
            }
        });

        // sets visibility of the "Staff" button depending on the user role
        switch (myRole) {
            case "Landlord":
                break;
            case "Tenant":
                btnStaff.setVisibility(View.GONE);
        }
    }

    // loads the user image according to imageURL
    private void loadUserImage(){
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(this.ivUserIconBtn);
        }
    }

    private void goUserInfo(){
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }
    private void goStaff(){
        Intent intent = new Intent(this, ViewStaffActivity.class);
        startActivity(intent);
    }
    private void goChat(){
        Intent intent = new Intent(this, ChatList.class);
        startActivity(intent);
    }
}
