package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button btnAddList;
    List<String> itemList = new ArrayList<>();

    /*리스트 초기화*/
    private void initializeList(ListView listView){
        String[] filenames = fileList();
        itemList = new ArrayList<>(Arrays.asList(filenames));
        itemList.remove("password");

        Collections.sort(itemList, new Comparator<String>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
            @Override
            public int compare(String o1, String o2) {
                try{
                    Date date1 = dateFormat.parse(o1);
                    Date date2 = dateFormat.parse(o2);
                    return date1.compareTo(date2);
                }catch (ParseException e){
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);
    }

    /*비밀번호*/
    void Login(){
        String password = readDiary("password"); //실제 비밀번호

        if(password == null){   //비밀번호가 없는 경우
            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setTitle("비밀번호 등록");
            dlg.setCancelable(false);
            EditText edt = new EditText(MainActivity.this);
            edt.setHint("비밀번호를 등록하세요");
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            dlg.setView(edt);
            dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    writeDiary("password", edt.getText().toString());
                    listView.setVisibility(View.VISIBLE);
                    btnAddList.setVisibility(View.VISIBLE);
                }
            });
            dlg.show();
        }
        else{   //비밀번호가 이미 있는 경우
            AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
            dlg.setTitle("로그인");
            dlg.setCancelable(false);
            EditText edt = new EditText(MainActivity.this);
            edt.setHint("비밀번호를 입력하세요");
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            dlg.setView(edt);
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(edt.getText().toString().equals(password)){
                        listView.setVisibility(View.VISIBLE);
                        btnAddList.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                        Login();
                    }
                }
            });
            dlg.show();
        }
    }

    /*메인*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("최종 프로젝트 - 'Personal Diary'");

        btnAddList = (Button) findViewById(R.id.btnAddList);
        listView = (ListView) findViewById(R.id.listView);

        initializeList(listView);

        Login();

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

    /*양방향 액티비티*/
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

    /*파일 입출력*/
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

    /*옵션*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "선택 삭제");
        menu.add(0,2,0,"비밀번호 변경");

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
            case 2:
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("비밀번호 변경");
                EditText edt = new EditText(MainActivity.this);
                edt.setHint("비밀번호를 변경하세요");
                edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                dlg.setView(edt);
                dlg.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        writeDiary("password", edt.getText().toString());
                        Toast.makeText(MainActivity.this, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
        }
        return super.onOptionsItemSelected(item);
    }
}