package com.example.blurryface.storagedemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StorageDemoActivity extends AppCompatActivity {
    private EditText editText;

    //variables for the request code for each operation
    private static final int CREATE_REQUEST_CODE = 20;
    private static final int OPEN_REQUEST_CODE = 21;
    private static final int SAVE_REQUEST_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_demo);
        editText = (EditText)findViewById(R.id.fileText);
    }
    //method to create a new file to the cloud
    public void newFile(View view)
    {
        //opens up a picker from google drive with intent to create a file
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        //filter to only have files that can be opened
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //set the MIME type for plaintext only
        intent.setType("text/plain");
        //having a default fileName to save
        intent.putExtra(Intent.EXTRA_TITLE,"newFile.txt");
        //start the intent
        startActivityForResult(intent,CREATE_REQUEST_CODE);
    }
    //method to save data to a file
    public void saveFile(View view)
    {
        //OPENS up a picker from google drive
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //filter to only have openable files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //set the MIME type for plaintext only
        intent.setType("text/plain");
        startActivityForResult(intent,SAVE_REQUEST_CODE);
    }
    //method to open a file
    public void openFile(View view)
    {
        //opens up a picker from google drive
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //filter to only open openable files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //set the MIME TYPE to plaintext
        intent.setType("text/plain");
        startActivityForResult(intent,OPEN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri contentUri = null;
        //check if the activity ran
        if(resultCode== Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case CREATE_REQUEST_CODE:
                    editText.setText("");
                    break;
                case SAVE_REQUEST_CODE:
                    contentUri = data.getData();
                    saveDetailstoFile(contentUri);
                    break;
                case OPEN_REQUEST_CODE:
                    contentUri = data.getData();
                    try {
                        String shit = retrieveDataFile(contentUri);
                        editText.setText(shit);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    //method to write data on a file from my editText
    public void saveDetailstoFile(Uri uri)
    {
        try {
            //get the file selected
            ParcelFileDescriptor fileDescriptor = this.getContentResolver().openFileDescriptor(uri,"w");
            FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
            String myData = editText.getText().toString();
            //writing data on the selected file
            fileOutputStream.write(myData.getBytes());
            fileOutputStream.close();
            fileDescriptor.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //method to retrieve data from a selected file and display it on the edit text
    public String retrieveDataFile(Uri uri)throws IOException
    {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine;
        StringBuilder stringBuilder = new StringBuilder();
        while ((currentLine= reader.readLine())!=null){
            stringBuilder.append(currentLine+"\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

}
