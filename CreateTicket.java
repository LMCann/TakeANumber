package com.takeanumber;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class CreateTicket extends AppCompatActivity {

    TextView textView;
    EditText nameInput;
    EditText phoneNumInput;
    EditText groupSizeInput;
    SharedPreferences sharedPreferences;
    boolean edit;
    Integer nonStaticInt;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        nameInput = findViewById(R.id.nameInput);
        phoneNumInput = findViewById(R.id.phoneNumInput);
        saveButton = findViewById(R.id.saveButton);
        textView = findViewById(R.id.textView);
        groupSizeInput = findViewById(R.id.groupSizeInput);

        if(MainActivity.itemClickedPos!=1000000){
            edit = true;
            nonStaticInt = MainActivity.itemClickedPos;
            MainActivity.ticketNumber = MainActivity.itemClickedPos;
            nameInput.setText(MainActivity.peopleList.get(MainActivity.itemClickedPos).name);
            phoneNumInput.setText(MainActivity.peopleList.get(MainActivity.itemClickedPos).phoneNum);
            textView.setText(String.valueOf(MainActivity.peopleList.get(MainActivity.itemClickedPos).ticketNum));
            groupSizeInput.setText(String.valueOf(MainActivity.peopleList.get(MainActivity.itemClickedPos).groupSize));
            MainActivity.itemClickedPos=1000000;
        } else{
            edit=false;
            nameInput.setText("");
            phoneNumInput.setText("");
            groupSizeInput.setText("");
            textView.setText(String.valueOf(MainActivity.peopleList.size()+1));
        }

    }

    public void onClick(View View){

        if(nameInput.getText().toString().matches("") || TextUtils.isEmpty(phoneNumInput.getText()) || TextUtils.isEmpty(groupSizeInput.getText())){
            Toast.makeText(this, "Ensure all fields are filled in", Toast.LENGTH_SHORT).show();
        } else{
            String name = nameInput.getText().toString();
            String phoneNum = phoneNumInput.getText().toString();
            int groupSize = Integer.parseInt(groupSizeInput.getText().toString());
            if(!edit){
                MainActivity.ticketNumber = MainActivity.peopleList.size()+1;

                MainActivity.next = new Person(MainActivity.ticketNumber, name, phoneNum, groupSize);
                MainActivity.peopleList.add(MainActivity.next);

                sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(MainActivity.peopleList);
                editor.putString("people list", json);
                editor.apply();

            } else{
                MainActivity.peopleList.get(nonStaticInt).setName(name);
                MainActivity.peopleList.get(nonStaticInt).setPhoneNum(phoneNum);
                MainActivity.peopleList.get(nonStaticInt).setGroupSize(groupSize);

                sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(MainActivity.peopleList);
                editor.putString("people list", json);
                editor.apply();
            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            MainActivity.itemClickedPos = 0;

            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if(edit){
            if(
                    !nameInput.getText().toString().equals(MainActivity.peopleList.get(nonStaticInt).getName())
                    ||
                    !phoneNumInput.getText().toString().equals(MainActivity.peopleList.get(nonStaticInt).getPhoneNum())
                    ||
                    Integer.parseInt(groupSizeInput.getText().toString()) != (MainActivity.peopleList.get(nonStaticInt).getGroupSize()))
            {
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to exit without saving?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CreateTicket.this.finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else{
                CreateTicket.this.finish();
            }
        } else{
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit without saving?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            CreateTicket.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
