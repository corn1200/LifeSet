package com.example.lifeset;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    String CHANNEL_ID = "0000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        정의해야하는 각 알림에 대한 고유한 정수
        final int notificationId = 1234;

        createNotificationChannel();
        NotificationCompat.Builder builder = notificationBuilder();

//        알림을 표시하려면 NotificationManagerCompat.notify()를 호출하여
//        알림의 고유 ID와 NotificationCompat.Builder.build()의 결과를 전달
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
//        NotificationMangerCompat.notify()에 전달하는 알림 ID를 저장해야함
//        알림을 업데이트하거나 삭제하기 위해 필요함
    }

    private NotificationCompat.Builder notificationBuilder() {
        String textTitle = "this Notify";
        String textContent = "this is Notification";
        String textContentLarge =
                "this is Notification, are you? lorem ipsum, who are? and, would you?";

//        모든 알림은 일반적으로 앱에서 알림에 상응하는 활동을 열려면 탭에 응답해야 함
//        이 작업을 하려면 PendingIntent 객체로 정의된 콘텐츠
//        인텐트를 지정하여 setContentIntent()에 전달해야 함
//        아래 내용은 앱에서 활동에 대한 명시적 인텐트를 만드는 것
        Intent intent = new Intent(this, AlertDialog.class);
//        setFlags() 메서드는 사용자가 알림을 통해 앱을 연 후 예상되는 탐색 환경을
//        유지하는 데 도움이 됨, 이 메서드 사용 여부는 시작하는 활동 유형에 따라 결정
//        알림의 응답에만 존재하는 활동, 앱의 일반 앱 흐름에 존재하는 활동
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity
                (this, 0, intent, 0);

//        알람에 사용자가 신속하게 상호작용할 수 있는 작업 버튼을 최대 세개 제공 가능
//        이 작업 버튼은 사용자가 알림을 탭할 때 실행되는 작업과 중복되지 않아야 함
//        작업 버튼을 추가하기 위해 PendingIntent 를 addAction() 메서드에 전달
//        백그라운드에서 작업을 실행하는 BroadcastReceiver 를 시작하는 것과 같이
//        다양한 작업을 할 수 있으므로 작업을 실행해도 이미 열려 있는 앱이 중단되지 않음
        Intent snoozeIntent = new Intent
                (this, UpdateNotificationBroadcast.class);
        snoozeIntent.setAction(Intent.ACTION_SEND);
        snoozeIntent.putExtra(CHANNEL_ID, 0);
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast
                (this, 0, snoozeIntent, 0);

//        Builder 객체를 사용하여 알림 콘텐츠와 채널을 설정함
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this, CHANNEL_ID)
//                작은 아이콘을 설정, 사용자가 볼 수 있는 필수 콘텐츠
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
//                제목 설정
                .setContentTitle(textTitle)
//                본문 텍스트 설정
                .setContentText(textContent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(textContentLarge))
//                알림 우선순위 설정, Android 7.1 이하에서 알림이 얼마나 강제적인지 결정
//                Android 8.0 이상의 경우 채널 중요도를 대신 설정해야함
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                사용자가 알림을 탭할 때 실행되는 인텐트를 설정함
                .setContentIntent(pendingIntent)
//                사용자가 방해 금지 모드를 사용하도록 설정한 경우 Android 에서는
//                사전 정의된 시스템 전체 카테고리를 사용하여 지정된 알림으로
                .setCategory(NotificationCompat.CATEGORY_ALARM)
//                사용자가 알림을 탭하면 자동으로 알림을 삭제하도록 설정
                .setAutoCancel(true)
                .addAction(R.drawable.ic_baseline_add_comment_24,
                        getString(R.string.snooze), snoozePendingIntent);
//        Builder 생성자의 경우 채널 ID를 제공해야함
//        Android 8.0 이상에서는 호환성을 유지하기 위해 필요하지만 이전 버전에선 무시
        return builder;
    }

//    Android 8.0 이상에서 알림을 제공하려면 먼저 NotificationChannel 인스턴스를
//    createNotificationChannel()에 전달하여 앱의 알림 채널을 시스템에 등록해야함
    private void createNotificationChannel() {
//        NotificationChannel 을 생성하지만 NotificationChannel 클래스는 지원
//        라이브러리가 아닌 새로운 클래스이므로 API 26 이상에서만 사용 가능
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel
                    (CHANNEL_ID, name, importance);
            channel.setDescription(description);
//            시스템에 채널을 등록
//            이 후에는 중요도 또는 기타 알림 동작을 변경 불가
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
//        Android 8.0 이상에서 알림을 게시하려면 채널을 만들어야 하므로 앱이
//        시작하자마자 이 코드를 실행해야함, 기존 알림 채널을 만들면 아무 작업도
//        실행되지 않으므로 이 코드를 반복적으로 호출하는 것이 안전함

//        NotificationChannel 생성자에는 importance 가 필요하므로
//        NotificationManager 클래스의 상수 중 하나를 사용함
//        이 매개변수에 따라 이 채널에 속하는 모든 알림을
//        사용자에게 전달하는 방법이 결정됨
//        단, Android 7.1 이하를 지원하려면 위에 표시된 대로
//        setPriority()를 사용하여 우선순위도 설정해야함
     }
}