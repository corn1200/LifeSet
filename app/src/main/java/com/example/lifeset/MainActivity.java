package com.example.lifeset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimePicker picker = findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        SharedPreferences sharedPreferences =
                getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong
                ("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        Date nextDate = nextNotifyTime.getTime();
        String dateText = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분",
                Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(),
                "다음 알람은 " + dateText + "으로 알람이 설정되었습니다!",
                Toast.LENGTH_SHORT).show();

        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat hourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int preHour = Integer.parseInt(hourFormat.format(currentTime));
        int preMinute = Integer.parseInt(minuteFormat.format(currentTime));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(preHour);
            picker.setMinute(preMinute);
        } else {
            picker.setCurrentHour(preHour);
            picker.setCurrentMinute(preMinute);
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, hour24, minute;
                String ampm;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour24 = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }

                if (hour24 > 12) {
                    ampm = "PM";
                    hour = hour24 - 12;
                } else {
                    hour = hour24;
                    ampm = "AM";
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
            }
        });
    }
}