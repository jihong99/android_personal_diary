package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ThirdActivity extends Activity {
    Button btnDelList, btnBack2;
    ListView listView2;
    List<String> itemList2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third);

        btnBack2 = (Button) findViewById(R.id.btnBack2);
        btnDelList = (Button) findViewById(R.id.btnDelList);

        String[] filenames = fileList();
        listView2 = (ListView) findViewById(R.id.listView2);
        itemList2 = new ArrayList<>(Arrays.asList(filenames));
        itemList2.remove("password");

        Collections.sort(itemList2, new Comparator<String>() {
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
                this, android.R.layout.simple_list_item_multiple_choice, itemList2);
        listView2.setAdapter(adapter);

        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDelList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listView2.getCheckedItemPositions();

                if(checkedItems.size() == 0){
                    Toast.makeText(ThirdActivity.this, "아무것도 선택되지 않았습니다"
                            , Toast.LENGTH_SHORT).show();
                }
                else{
                    for (int i=checkedItems.size()-1; i>=0; i--) {
                        int position = checkedItems.keyAt(i);
                        if (checkedItems.valueAt(i)) {
                            deleteFile(itemList2.get(position));
                        }
                    }

                    finish();
                }
            }
        });
    }
}
