package com.example.amank.skysound;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UploadYourSong extends AppCompatActivity {
    private Button ChooseFile;
    private Button Upload;
    private int PICK_SONG_REQUEST = 2;

    private Uri filePath;
    private String audioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_your_song);

        ChooseFile = (Button)findViewById(R.id.btn_ChooseFile);
        Upload = (Button)findViewById(R.id.btn_Upload);

        ChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_SONG_REQUEST);
            }
        });

        //bitmap = MediaStore.Audio.Media.getContentUriForPath()




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_SONG_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            filePath = data.getData();
            //audioPath = data.getData().getPath();

            Toast.makeText(getApplicationContext(),filePath.toString(),Toast.LENGTH_LONG).show();
        }
    }
}
