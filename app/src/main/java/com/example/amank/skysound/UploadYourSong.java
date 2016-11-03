package com.example.amank.skysound;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadYourSong extends AppCompatActivity {
    private Button ChooseFile;
    private Button Upload;
    private ImageView imageView;
    private int PICK_SONG_REQUEST = 2;
    private int serverResponseCode = 0;

    private Uri audioFileUri;
    private String audioFilePath;

    public String androidId;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_your_song);

        ChooseFile = (Button)findViewById(R.id.btn_ChooseFile);
        Upload = (Button)findViewById(R.id.btn_Upload);
        imageView = (ImageView) findViewById(R.id.imageView);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        androidId = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        ChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_SONG_REQUEST);
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audioFileUri!=null) {
                    //UploadFile("/sdcard/Download/bensound-cute.mp3");
                    createFolderAndTable(androidId);
                    UploadFile(audioFilePath,androidId);
                }
                else
                    Toast.makeText(getApplicationContext(),"Please select a file",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void createFolderAndTable (final String androidId){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;
                try{
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/uploads/createfolders.php?id="+ androidId);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    int statuscode = urlConnection.getResponseCode();
                    if(statuscode == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        String response = convertInputStreamToString(inputStream);
                        Log.i("Created Folder/Table",response);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    if(urlConnection !=null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }
        if(inputStream != null){
            inputStream.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_SONG_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            audioFileUri = data.getData();
            //File audioFile = new File(audioFileUri.getPath());
            audioFilePath = audioFileUri.getPath();
            //Bitmap bitmap = BitmapFactory.decodeFile(audioFileUri.toString());
            //imageView.setImageBitmap(bitmap);
            //audioFilePath = getRealPathFromURI(getApplicationContext(),audioFileUri);
            Toast.makeText(getApplicationContext(), audioFilePath,Toast.LENGTH_LONG).show();
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Audio.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getpathring(Uri path) {
        String result;
        Cursor cursor = getApplicationContext().getContentResolver()
                .query(path, null, null, null, null);
        if (cursor == null) {
            result = path.getPath();
            int name = result.lastIndexOf("/");
            String mp3_name = result.substring(name + 1);
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            result = cursor.getString(idx);
            cursor.close();
            int name = result.lastIndexOf("/");
            String mp3_name = result.substring(name + 1);
        }
        return result;
    }

    //public void UploadFile(String audioFilePath){

    //}

    public int UploadFile(String sourceFileUri, String androidId) {

        String fileName = sourceFileUri;
        String[] bits = fileName.split("/");
        String lastOne = bits[bits.length-1];

        String upLoadServerUri = "http://79.170.40.180/cloudatlas.com/upload.php";
        upLoadServerUri = upLoadServerUri + "?id=" + androidId;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            //dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :" + audioFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :" + filepath);
                }
            });

            return 0;

        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + " c:/wamp/www/echo/uploads";
                            //messageText.setText(msg);
                            Toast.makeText(UploadYourSong.this,
                                    "File Upload Complete.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                    populateUserSongs(androidId,lastOne);
                }


                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText
                        //        .setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UploadYourSong.this,
                                "MalformedURLException", Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(UploadYourSong.this,
                                "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception","Exception : " + e.getMessage(), e);
            }
            //dialog.dismiss();
            return serverResponseCode;
        }
    }

    private void populateUserSongs (final String androidId , final String lastone){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream inputStream = null;
                HttpURLConnection urlConnection = null;

                try{
                    URL url = new URL("http://79.170.40.180/cloudatlas.com/uploads/populateUserSongs.php?id="+ androidId + "&id2=" + lastone);
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.getResponseCode();
                }catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    if(urlConnection !=null)
                        urlConnection.disconnect();
                }
            }
        });
        thread.start();
    }



}
