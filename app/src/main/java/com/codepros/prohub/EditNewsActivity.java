package com.codepros.prohub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.codepros.prohub.model.News;
import com.codepros.prohub.utils.FirebaseDataseHelper;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class EditNewsActivity extends AppCompatActivity {

    private ImageView addNewsbtn, addNewsImageView;
    private EditText newsTitleInput, newsContentInput;
    private RadioButton radioBtnAll, radioBtnManage, radioBtnTrue, radioBtnFalse;
    private Button addNewsCancelBtn, addNewsPostBtn;

    private String userPhoneNum, propId, imageUrl, newsTitle, newsContent;
    private Uri imguri;

    private int newsPosition;
    private String newsKey;
    private News selectedNews = new News();

    private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "AddNewsActivity";

    // firebase database objects
    private DatabaseReference myNewsRef;
    private StorageReference myStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_news);

        myNewsRef = FirebaseDatabase.getInstance().getReference();
        myStorageRef = FirebaseStorage.getInstance().getReference("Images");

        SharedPreferences myPref = getSharedPreferences("myUserSharedPref", MODE_PRIVATE);
        userPhoneNum = myPref.getString("phoneNum", "");
        propId = myPref.getString("propId", "");

        newsPosition = getIntent().getIntExtra("newsPosition", -1);
        new FirebaseDataseHelper().readNews(new FirebaseDataseHelper.NewsDataStatus() {
            @Override
            public void DataIsLoad(List<News> newsList, List<String> keys) {
                if(newsPosition > -1){
                    selectedNews = newsList.get(newsPosition);
                    newsKey = keys.get(newsPosition);

                    if(!selectedNews.getPropId().isEmpty()){
                        fillUpInformation();
                    }
                }
                else{
                    goBack();
                }
            }
        });

        addNewsbtn = findViewById(R.id.addNewsbtn);
        addNewsImageView = findViewById(R.id.addNewsImageView);
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

    private void fillUpInformation(){
        // fill in the information to variable
        newsTitle = selectedNews.getNewsTitle();
        newsContent = selectedNews.getContent();
        imageUrl = selectedNews.getImageUrl();
        // load the input fields
        this.newsTitleInput.setText(newsTitle);
        this.newsContentInput.setText(newsContent);
        // load the radio buttons
        if(selectedNews.getHideFlag()){
            radioBtnTrue.setChecked(true);
        }
        if(selectedNews.getTargetViewer().equals("all")){
            radioBtnAll.setChecked(true);
        }
        // load the images
        if(!imageUrl.isEmpty()){
            Picasso.get().load(imageUrl).into(this.addNewsImageView);
        }
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private void FileUploader(){
        final StorageReference ref = myStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
        UploadTask uploadTask = ref.putFile(imguri);

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
    }

    private void fileChooser(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null){
            imguri = data.getData();
            addNewsImageView.setImageURI(imguri);
            // upload the image to firebase storage
            FileUploader();
        }
    }

    private void goBack(){
        Intent intent = new Intent(this, NewsViewActivity.class);
        this.startActivity(intent);
    }

    private void addNews(View v){
        String title = newsTitleInput.getText().toString();
        String content = newsContentInput.getText().toString();

        if(title.isEmpty()){
            String message = "Sorry, news title cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else if(content.isEmpty()){
            String message = "Sorry, news content cannot be empty!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        else{
            String target = "all";
            boolean hide = false;
            if(!radioBtnAll.isChecked() && radioBtnManage.isChecked()){
                target = "management only";
            }
            if(radioBtnTrue.isChecked() && !radioBtnFalse.isChecked()){
                hide = true;
            }
            String createTime = Calendar.getInstance().getTime().toString();

            News mNews = new News(propId, userPhoneNum, title, content, imageUrl, createTime, target, hide);

            // need to save to firebase
            myNewsRef.child("news").child(newsKey).setValue(mNews);
            Toast.makeText(getApplicationContext(), "News saved!", Toast.LENGTH_LONG).show();

            // redirect to news room
            goBack();
        }
    }
}
