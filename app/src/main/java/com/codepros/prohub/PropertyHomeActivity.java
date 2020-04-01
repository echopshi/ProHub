package com.codepros.prohub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.codepros.prohub.utils.ToolbarHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.codepros.prohub.model.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PropertyHomeActivity extends AppCompatActivity {

    // Toolbar items
    private Button toolbarBtnSettings, settingsButton, toolbarBtnChat, chatButton, toolbarBtnNews;
    private Button newsroomButton, toolbarBtnForms, formsButton, btnDashboard, btnStaff, btnUnits;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    //Toolbar helper
    ToolbarHelper toolbar;

    // Firebase database objects
    private static final String TAG = "PropertyHomeActivity";
    private DatabaseReference myPropRef;

    // Chat database ref for export chat history
    private DatabaseReference myChatRef;
    public static final String CHAT_CHILD = "chat";

    // Export chat History
    private static final int PERMISSION_REQUEST_CODE = 100;
    private String EXPORT_FILENAME;
    private JSONObject jsonData = new JSONObject(); // tentative output

    // property ID
    private String propId, propName, userName;

    // user role
    private String myRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        //Shard Prederences
        // get propId from intent, save to shared preference
        //propId = getIntent().getStringExtra("propId");
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        propId = myPreference.getString("propId", "");

        myPropRef = FirebaseDatabase.getInstance().getReference();
        myRole = myPreference.getString("myRole", "");
        userName = myPreference.getString("username", "");
        btnDashboard = findViewById(R.id.btnDashboard);
        btnDashboard.setText(userName);

        // references to the buttons on view
        chatButton = findViewById(R.id.chatButton);
        newsroomButton = findViewById(R.id.newsroomButton);
        formsButton = findViewById(R.id.formsButton);
        settingsButton = findViewById(R.id.settingsButton);
        btnStaff = findViewById(R.id.staffButton);
        btnUnits = findViewById(R.id.unitButton);
        /////////////////////////////////////////////////////
        // declaring the buttons

        // define the actions for each button
        // Button for top toolbar
        toolbarBtnChat = findViewById(R.id.toolbarBtnChat);
        toolbarBtnNews = findViewById(R.id.toolbarBtnNews);
        toolbarBtnForms = findViewById(R.id.toolbarBtnForms);
        toolbarBtnSettings = findViewById(R.id.toolbarBtnSettings);
        btnHome = findViewById(R.id.ImageButtonHome);
        toolbarBtnSearch = findViewById(R.id.ImageButtonSearch);
        toolbarBtnMenu = findViewById(R.id.ImageButtonMenu);

        toolbar = new ToolbarHelper(this, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms,
                toolbarBtnSettings, btnHome, toolbarBtnSearch, toolbarBtnMenu);

        // click CHAT on dashboard
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goChat(v);
            }
        });

        // click NEWS on dashboard
        newsroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNews(v);
            }
        });

        //click FORMS on dashboard
        formsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goForms(v);
            }
        });
        //
        btnUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goUnits(v);
            }
        });
        //
        btnStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStaff(v);
            }
        });
        //
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSettings(v);
            }
        });

    }

    public void goNews(View view) {
        Intent intent = new Intent(this, NewsViewActivity.class);
        this.startActivity(intent);
    }

    public void goChat(View view) {
        Intent intent = new Intent(this, ChatList.class);
        this.startActivity(intent);
    }

    public void goForms(View view) {
        Intent intent = new Intent(this, FormsActivity.class);
        this.startActivity(intent);
    }

    public void goSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        this.startActivity(intent);
    }

    public void goSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void goStaff(View view) {
        Intent intent = new Intent(this, ViewStaffActivity.class);
        this.startActivity(intent);
    }

    public void goUnits(View view) {
        Intent intent = new Intent(this, ViewUnitsActivity.class);
        this.startActivity(intent);
    }

}