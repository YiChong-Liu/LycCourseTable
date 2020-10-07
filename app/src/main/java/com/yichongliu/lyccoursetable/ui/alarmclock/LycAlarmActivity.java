package com.yichongliu.lyccoursetable.ui.alarmclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.yichongliu.lyccoursetable.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Author: Yichong·Liu
 * Date:2020.6.6
 */
public class LycAlarmActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private EditText hourEd, minutesEd, secondsEd;
    private TextView alarmTV;
    private Button deleteAlarm;
    private Button alarmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyc_alarm);

        initAlarm();
        initView();

    }

    // 初始化控件
    private void initView() {
        hourEd = (EditText) findViewById(R.id.ed_hour);
        minutesEd = (EditText) findViewById(R.id.ed_min);
        secondsEd = (EditText) findViewById(R.id.ed_seconds);
        alarmTV = (TextView) findViewById(R.id.tv_alarm);
        alarmBtn = (Button) findViewById(R.id.btn_alarm);
        deleteAlarm=(Button)findViewById(R.id.delete_alarm);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour = Integer.parseInt(hourEd.getText().toString().trim());
                int mMinute = Integer.parseInt(minutesEd.getText().toString().trim());
                int mSeconds = Integer.parseInt(secondsEd.getText().toString().trim());
                // 设置闹钟时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, mHour);
                calendar.set(Calendar.MINUTE, mMinute);
                calendar.set(Calendar.SECOND, mSeconds);

                setAlarm(calendar);
            }
        });

        deleteAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmManager.cancel(pendingIntent);
                alarmTV.setText("");
            }
        });
    }


    // 初始化闹钟
    private void initAlarm() {
        // 实例化AlarmManager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 设置闹钟触发动作
        Intent intent = new Intent(this, LycAlarmBroadcast.class);
        intent.setAction("startAlarm");
        //PendingIntent.getBroadcast(Context context,int requestCode,Intent intent,int flags)，
        // 该待定意图发生时，效果相当于Context.sendBroadcast(Intent)
        pendingIntent = PendingIntent.getBroadcast(this, 110, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 设置闹钟 有三种方式:
        //alarmManager.set(AlarmManager.RTC_WAKEUP,calendar., PendingIntent );
        //alarmManager.setRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);
        //alarmManager.setInexactRepeating(@AlarmType int type, long triggerAtMillis, long intervalMillis, PendingIntent operation);

        // 取消闹钟
        //alarmManager.cancel(pendingIntent);
    }

    // 设置闹钟
    private void setAlarm(Calendar calendar) {
//      alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 5), pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        alarmTV.setText(new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(calendar.getTime()));
        Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();//设置成功并自动返回
        hourEd.setText("");
        minutesEd.setText("");
        secondsEd.setText("");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }



}
