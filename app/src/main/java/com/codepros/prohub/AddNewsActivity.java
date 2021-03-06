package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Toast;

import com.codepros.prohub.model.Chat;
import com.codepros.prohub.model.News;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.codepros.prohub.utils.ToolbarHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddNewsActivity extends AppCompatActivity {
    //Toolbar
    private Button toolbarBtnSettings, toolbarBtnChat, toolbarBtnNews, toolbarBtnForms;
    private ImageButton toolbarBtnSearch, btnHome, toolbarBtnMenu;
    //
    private ImageView addNewsbtn;
    private EditText newsTitleInput, newsContentInput;
    private RadioButton radioBtnAll, radioBtnManage, radioBtnTrue, radioBtnFalse;
    private Button addNewsCancelBtn, addNewsPostBtn;

    private String userPhoneNum, propId, imageUrl, myRole;
    private Uri imguri;
    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "AddNewsActivity";

    // firebase database objects
    private DatabaseReference myNewsRef;
    private StorageReference myStorageRef;

    private ToolbarHelper toolbar;
    List<Chat> allMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        //
        SharedPreferences myPreference = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        myRole = myPreference.getString("myRole", "");
        userPhoneNum = myPreference.getString("phoneNum", "");
        propId = myPreference.getString("propId", "");
        //////////////////////////////////////////////
        // declaring the buttons

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

        //////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////

        // for unread chat messages counter
        new FirebaseDataseHelper().readChats(new FirebaseDataseHelper.ChatDataStatus() {
            @Override
            public void DataIsLoad(List<Chat> chats, List<String> keys) {
                allMessages = chats;
                int count = 0;
                if (allMessages != null) {
                    for (Chat chat : allMessages)
                    {
                        if (!chat.getPhoneNumber().equals(userPhoneNum) && chat.getChatSeen().equals("false")
                                && chat.getChatMessageId().contains(userPhoneNum))
                        {
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    toolbarBtnChat.setText("CHAT (" + count + ")");
                    toolbarBtnChat.setTextColor(Color.parseColor("#FF0000"));
                } else if (count <= 0) {
                    toolbarBtnChat.setText("CHAT");
                    toolbarBtnChat.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////

        myNewsRef = FirebaseDatabase.getInstance().getReference();
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");

        addNewsbtn = findViewById(R.id.addNewsbtn);
        newsTitleInput = findViewById(R.id.newsTitleInput);
        newsContentInput = findViewById(R.id.newsContentInput);
        radioBtnAll = findViewById(R.id.radioBtnAll);
        radioBtnManage = findViewById(R.id.radioBtnManage);
        radioBtnTrue = findViewById(R.id.radioBtnTrue);
        radioBtnFalse = findViewById(R.id.radioBtnFalse);
        addNewsCancelBtn = findViewById(R.id.addNewsCancelBtn);
        addNewsPostBtn = findViewById(R.id.addNewsPostBtn);

        addNewsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        addNewsCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        addNewsPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNews(v);
            }
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader() {
        final StorageReference ref = myStorageRef.child(System.currentTimeMillis() + "." + getExtension(imguri));
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bitmap.getByteCount() > 100000) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "something went wrong when uploading image", Toast.LENGTH_LONG).show();
                        throw task.getException();
                    }
                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        imageUrl = task.getResult().toString();
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void fileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null) {
            imguri = data.getData();
            addNewsbtn.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, NewsViewActivity.class);
        this.startActivity(intent);
    }

    private void addNews(View v) {
        String title = newsTitleInput.getText().toString();
        String content = newsContentInput.getText().toString();

        if (title.isEmpty()) {
            String message = "Sorry, news title cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else if (content.isEmpty()) {
            String message = "Sorry, news content cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        } else {
            String target = "all";
            boolean hide = false;
            if (!radioBtnAll.isChecked() && radioBtnManage.isChecked()) {
                target = "management only";
            }
//            if (radioBtnTrue.isChecked() && !radioBtnFalse.isChecked()) {
//                hide = true;
//            }
            SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy, KK:mm a");
            String createTime = format1.format(Calendar.getInstance().getTime());

            News mNews = new News(propId, userPhoneNum, title, content, imageUrl, createTime, target, hide);

            // need to save to firebase
            DatabaseReference postsRef = myNewsRef.child("news");
            DatabaseReference newPostRef = postsRef.push();
            newPostRef.setValue(mNews);
            Toast.makeText(getApplicationContext(), "News saved!", Toast.LENGTH_LONG).show();

            // redirect to news room
            goBack();
        }
    }
}
