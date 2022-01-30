package com.takeanumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button button_add;
    static PeopleListAdapter adapter;
    static int ticketNumber;
    static ArrayList<Person> peopleList;
    static Person next;
    static Integer itemClickedPos;
    static int groupSize;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        button_add = findViewById(R.id.button_add);
        toolbar = findViewById(R.id.ticketsToolbar);

        setSupportActionBar(toolbar);

        itemClickedPos = 1000000;

        loadData();

        adapter = new PeopleListAdapter(this, R.layout.listitem, peopleList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
                return true;
            }
        });

    }

    private void deleteItem(final int pos) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Item");
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?");
        alertDialogBuilder.setCancelable(true);

        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"Item Deleted",Toast.LENGTH_SHORT).show();
                peopleList.remove(pos);
                rearrangeNumbers();
                saveData();
                adapter.notifyDataSetChanged();
            }
        });

        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.peopleList);
        editor.putString("people list", json);
        editor.apply();
    }

    private void rearrangeNumbers() {
        for(int n = 0; n<peopleList.size(); n++){
            Person rearrangeItem = peopleList.get(n);
            rearrangeItem.setTicketNum(n+1);
        }
    }

    private void itemClick(final Integer pos) {

        new AlertDialog.Builder(this)

                .setTitle("Ticket " + (pos+1))
                .setMessage("What would you like to do?")
                .setCancelable(true)
                //SEND
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                        Gson gson = new Gson();
                        String json = sharedPreferences.getString("message content", null);
                        Type type = new TypeToken<String>() {}.getType();
                        EditMessageScreen.messageContent = gson.fromJson(json, type);

                        if(EditMessageScreen.messageContent == null || EditMessageScreen.messageContent.equals("")){
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage("You have not set a default message, are you sure you would like to continue?")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            sendSMS(pos);
                                        }
                                    })
                                    .setNegativeButton("Set Message", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent editMessageIntent = new Intent(getApplicationContext(), EditMessageScreen.class);
                                            startActivity(editMessageIntent);
                                        }
                                    })
                                    .setNeutralButton("Cancel", null)
                                    .show();
                        } else{
//                                sendEmail(EditMessageScreen.messageContent, peopleList.get(pos));
                            sendSMS(pos);
                        }
                    }
                })
                //EDIT
                .setNegativeButton("Edit Ticket", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent editTicketIntent = new Intent(MainActivity.this, CreateTicket.class);
                        itemClickedPos = pos;
                        startActivity(editTicketIntent);

                    }
                })
//                CANCEL
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })

                .show();
    }

    public void sendSMS(int pos)
    {
        Uri uri = Uri.parse("smsto:"+peopleList.get(pos).phoneNum);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", EditMessageScreen.messageContent);
        startActivity(it);
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("people list", null);
        Type type = new TypeToken<ArrayList<Person>>() {}.getType();
        peopleList = gson.fromJson(json, type);

        if(peopleList == null){
            peopleList = new ArrayList<>();
        }
    }

    public void addNewTicket(View view){
        Intent addTicketintent = new Intent(getApplicationContext(), CreateTicket.class);
        startActivity(addTicketintent);
    }

    public void clearList(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to clear the list?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAllItems();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void deleteAllItems(){
        for(int i=0; i<peopleList.size(); i++){
            peopleList.clear();
            ticketNumber = peopleList.size();

            saveData();
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.editButton){
            Intent editMessageIntent = new Intent(getApplicationContext(), EditMessageScreen.class);
            startActivity(editMessageIntent);
        }
        else if(item.getItemId()==R.id.clearButton){
            clearList();
        }
        return super.onOptionsItemSelected(item);
    }


}
