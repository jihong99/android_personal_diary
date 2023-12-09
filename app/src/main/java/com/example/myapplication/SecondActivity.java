package com.example.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.io.File;
import java.util.Calendar;

public class SecondActivity extends Activity {
    DatePicker datePicker;
    EditText edtText;
    Button btnDone;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnDone = (Button) findViewById(R.id.btnDone);
        edtText = (EditText) findViewById(R.id.edtText);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        /*오늘 날짜로 달력 초기화*/
        Calendar cal = Calendar.getInstance();
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH), null);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent outIntent = new Intent(getApplicationContext(), MainActivity.class);
                String diary = edtText.getText().toString();
                String date = Integer.toString(datePicker.getYear())
                        + "년 " + Integer.toString(datePicker.getMonth()+1)
                        + "월 " + Integer.toString(datePicker.getDayOfMonth()) + "일";

                if(diary.isEmpty()){
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(isFileExists(date)){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(SecondActivity.this);
                    dlg.setMessage("해당 날짜에 일기가 이미 존재합니다\n수정하시겠습니까?");
                    dlg.setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            outIntent.putExtra("Diary", diary);
                            outIntent.putExtra("Date", date);
                            setResult(RESULT_OK, outIntent);

                            finish();
                        }
                    });
                    dlg.setNegativeButton("아니오", null);
                    dlg.show();

                }
                else {
                    outIntent.putExtra("Diary", diary);
                    outIntent.putExtra("Date", date);
                    setResult(RESULT_OK, outIntent);

                    finish();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*파일존재여부확인*/
    private boolean isFileExists(String fileName){
        File file = new File(getFilesDir(), fileName);
        return file.exists();
    }
}
