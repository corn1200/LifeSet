package com.example.lifeset;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimePicker picker = findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

//        앞서 설정한 값으로 보여주기
//        없으면 디폴트 값으로 현재시간
        SharedPreferences sharedPreferences =
                getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong
                ("nextNotifyTime", Calendar.getInstance().getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

//        이전 설정값으로 TimePicker 초기화
        Date nextDate = nextNotifyTime.getTime();
        String dateText = new SimpleDateFormat
                ("yyyy년 MM월 dd일 EE요일 a hh시 mm분",
                Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(),
                "다음 알람은 " + dateText + "으로 알람이 설정되었습니다!",
                Toast.LENGTH_SHORT).show();

        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat hourFormat = new SimpleDateFormat
                ("kk", Locale.getDefault());
        SimpleDateFormat minuteFormat = new SimpleDateFormat
                ("mm", Locale.getDefault());

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

//                현재 지정된 시간으로 알람 시간 설정
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

//                이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                Date currentDateTime = calendar.getTime();
                String dateText = new SimpleDateFormat
                        ("yyyy년 MM월 dd일 EE요일 a hh시 mm분",
                        Locale.getDefault()).format(currentDateTime);
                Toast.makeText(getApplicationContext(),
                        dateText + "으로 알람이 설정되었습니다!",
                        Toast.LENGTH_SHORT).show();

//                Preference 에 설정한 값 저장
                SharedPreferences.Editor editor =
                        getSharedPreferences("daily alarm",
                        MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", calendar.getTimeInMillis());
                editor.apply();

                diaryNotification(calendar);
            }
        });
    }

    public void diaryNotification(Calendar calendar) {
//        무조건 알람을 사용
        Boolean dailyNotify = true;

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (this, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

//        사용자가 매일 알람을 허용했다면
        if (dailyNotify) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle
                            (AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

//            부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}