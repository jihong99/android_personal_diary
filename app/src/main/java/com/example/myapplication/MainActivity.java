package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button btnAddList;
    List<String> itemList = new ArrayList<>();

    private void initializeList(ListView listView){
        String[] filenames = fileList();
        itemList = new ArrayList<>(Arrays.asList(filenames));

        ArrayAdapter<String> adapter = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("최종 프로젝트 - 'Personal Diary'");

        btnAddList = (Button) findViewById(R.id.btnAddList);
        listView = (ListView) findViewById(R.id.listView);

        initializeList(listView);

        btnAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);

                startActivityForResult(intent, 0);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = itemList.get(position);

                String fDetail = readDiary(selectedItem);

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle(selectedItem + "의 일기");
                dlg.setMessage(fDetail);
                dlg.setPositiveButton("닫기", null);
                dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            deleteFile(selectedItem);
                            initializeList(listView);
                        }catch (Exception e){
                        }
                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String diary = data.getStringExtra("Diary");
            String date = data.getStringExtra("Date");

            itemList.add(date);
            writeDiary(date, diary);

            initializeList(listView);
        }
        else{
            initializeList(listView);
        }
    }

    void writeDiary(String fName, String fDetail){
        try{
            FileOutputStream outFs = openFileOutput(fName, Context.MODE_PRIVATE);
            outFs.write(fDetail.getBytes());
            outFs.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    String readDiary(String fName){
        String diaryStr = null;
        FileInputStream inFs;
        try {
            inFs = openFileInput(fName);
            byte[] txt = new byte[500];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt)).trim();
        } catch (IOException e) {
        }
        return diaryStr;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "선택 삭제");

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:
                if(!itemList.isEmpty()){
                    Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);

                    startActivityForResult(intent, 0);
                }
                else{
                    Toast.makeText(getApplicationContext(), "삭제할 일기가 없습니다", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}