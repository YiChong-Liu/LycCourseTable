package com.yichongliu.lyccoursetable.ui.settime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yichongliu.lyccoursetable.R;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.yichongliu.lyccoursetable.bean.LycTime;
import com.yichongliu.lyccoursetable.ui.main.MainActivity;
import com.yichongliu.lyccoursetable.util.LycConfig;
import com.yichongliu.lyccoursetable.util.LycFileUtils;
import com.yichongliu.lyccoursetable.util.LycUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * Author: Yichong·Liu
 * Date:2020.6.1 */
public class LycSetTimeActivity extends AppCompatActivity {
    private OptionsPickerView mOptionsPv;
    private TimeAdapter timeAdapter = new TimeAdapter();
    private List<String> timeList = new ArrayList<>(18 * 12);
    private LycTime[] times = new LycTime[LycConfig.getMaxClassNum()];
    public static final String EXTRA_UPDATE_Time = "time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyc_set_time);
        setActionBar();
        RecyclerView recyclerView = findViewById(R.id.rv_time_list);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(timeAdapter);

        initData();
        ImageView imageView = findViewById(R.id.iv_bg_set_time);
        LycUtils.setBackGround(this, imageView);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0, len = times.length; i < len; i++) {
            try {
                if (MainActivity.sTimes == null || MainActivity.sTimes.length == 0) {
                    times[i] = new LycTime();
                } else {
                    times[i] = (LycTime) MainActivity.sTimes[i].clone();
                }

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
        for (int i = 6; i < 24; i++) {
            for (int j = 0; j < 12; j++) {
                timeList.add(LycUtils.formatTime(i) + ":" + LycUtils.formatTime(j * 5));
            }
        }
    }

    /**
     * 初始化ActionBar
     */
    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.course_details);
    }

    /**
     * 通知主界面更新
     */
    private void setUpdateResult() {
        Log.d("settime", "通知更新");
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATE_Time, true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_time, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_save) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("是否保存该时间表并退出?")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new LycFileUtils<LycTime[]>().saveToJson(
                                    LycSetTimeActivity.this,
                                    times,
                                    LycFileUtils.TIME_FILE_NAME);
                            MainActivity.sTimes = times;
                            //通知主界面更新
                            setUpdateResult();
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            alertDialog.show();
        } else if (id == R.id.menu_delete) {
            for (int i = 0; i < times.length; i++) {
                times[i].setStart("");
                times[i].setEnd("");
            }
            timeAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTimeSelectDialog(final TextView textView, final int index) {

        mOptionsPv = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                String start = timeList.get(options1);
                String end = timeList.get(options2);

                times[index].setStart(start);
                times[index].setEnd(end);
                textView.setText(times[index].toString());
            }
        }).setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
            @Override
            public void onOptionsSelectChanged(int options1, int options2, int options3) {
                if (options1 >= options2) {
                    mOptionsPv.setSelectOptions(options1, options1 + 1);
                }
            }
        }).build();

        mOptionsPv.setTitleText("时间");
        mOptionsPv.setNPicker(timeList, timeList, null);
        if (!times[index].getEnd().isEmpty()) {
            mOptionsPv.setSelectOptions(getOptionsIndex(times[index].getStart()),
                    getOptionsIndex(times[index].getEnd()));
        } else if (index > 0 && !times[index - 1].getEnd().isEmpty()) {
            int option = getOptionsIndex(times[index - 1].getEnd()) + 2;
            mOptionsPv.setSelectOptions(option, option + 9);
        } else {
            mOptionsPv.setSelectOptions(0, 1);
        }
        mOptionsPv.show();
    }

    private int getOptionsIndex(String time) {
        String[] strings = time.split(":");
        int hour = Integer.parseInt(strings[0]);
        int minute = Integer.parseInt(strings[1]);
        return (hour - 6) * 12 + minute / 5;
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.classTv.setText("第 " + (position + 1) + " 节");
            holder.timeTv.setText(times[position].toString());
            if (!holder.linearLayout.hasOnClickListeners()) {
                holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimeSelectDialog(holder.timeTv, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return times.length;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView classTv;
            private TextView timeTv;
            private LinearLayout linearLayout;

            public ViewHolder(View view) {
                super(view);
                classTv = view.findViewById(R.id.tv_class_num);
                timeTv = view.findViewById(R.id.tv_time);
                linearLayout = view.findViewById(R.id.ll_set_time);
            }

        }
    }
}

