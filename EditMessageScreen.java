package com.takeanumber;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class EditMessageScreen extends AppCompatActivity {
    EditText messageEditText;
    Button saveButton;
    static String messageContent;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message_screen);

        saveButton = findViewById(R.id.saveButton);
        messageEditText = findViewById(R.id.editText);

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("message content", null);
        Type type = new TypeToken<String>() {}.getType();
        messageContent = gson.fromJson(json, type);

        intent = new Intent(getApplicationContext(), MainActivity.class);

        if(messageContent!=null){
            messageEditText.setText(messageContent);
        } else{
            messageContent = "";
            messageEditText.setText(messageContent);
        }
    }

    public void onClick(View view){
        saveText();
    }

    public void saveText(){
        if(messageEditText.getText().toString().matches("")){
            Toast.makeText(this, "Enter a Message", Toast.LENGTH_SHORT).show();
        } else{
            messageContent = messageEditText.getText().toString();
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(messageContent);
            editor.putString("message content", json);
            editor.apply();
            startActivity(intent);
        }
    }

    public void areYouSureDialog(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit without saving?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intent);
                    }
                })
                .setNeutralButton("Save and Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveText();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if(!messageContent.equals(messageEditText.getText().toString())){
            areYouSureDialog();
        } else{
            startActivity(intent);
        }
    }
}
