package com.yichongliu.lyccoursetable.ui.coursedetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.yichongliu.lyccoursetable.R;
import com.yichongliu.lyccoursetable.bean.LycCourse;

import com.yichongliu.lyccoursetable.ui.editcourse.LycEditActivity;
import com.yichongliu.lyccoursetable.ui.main.MainActivity;
import com.yichongliu.lyccoursetable.util.LycFileUtils;
import com.yichongliu.lyccoursetable.util.LycUtils;

import java.util.List;

/**
 * Author: Yichong·Liu
 * Date:2020.5.26 */
public class LycCourseDetailsActivity extends AppCompatActivity {
    public static final String KEY_COURSE_INDEX = "course_index";
    private static final String[] aStrWeek = new String[]{
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
    };
    public static final int EDIT_ID = 0;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyc_course_details);
        Button button = findViewById(R.id.btn_edit);

        setActionBar();

        mIndex = getIntent().getIntExtra(KEY_COURSE_INDEX, 0);

        setCourseTextView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LycCourseDetailsActivity.this, LycEditActivity.class);
                intent.putExtra(LycEditActivity.EXTRA_COURSE_INDEX, mIndex);
                startActivityForResult(intent, EDIT_ID);
            }
        });

        ImageView imageView = findViewById(R.id.iv_bg);
        setCardViewAlpha();


        LycUtils.setBackGround(this, imageView);

    }

    /**
     * 设置CardView透明度
     */
    private void setCardViewAlpha() {
        CardView cardView = findViewById(R.id.cv_course_details);
        LycUtils.setCardViewAlpha(cardView);
    }

    /**
     * 通知主界面更新
     */
    private void setUpdateResult() {
        Intent intent = new Intent();
        intent.putExtra(LycEditActivity.EXTRA_UPDATE_TIMETABLE, true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ID) {
            if (data != null) {
                if (data.getBooleanExtra(LycEditActivity.EXTRA_UPDATE_TIMETABLE, false)) {
                    setCourseTextView();
                }
                setResult(RESULT_OK, data);//回传给MainActivity,让其更新课表
            }

        }
    }

    /**
     * 初始化TextView
     */
    private void setCourseTextView() {
        LycCourse course = MainActivity.sCourseList.get(mIndex);
        TextView textView = findViewById(R.id.tv_class_name);
        textView.setText(course.getName());

        textView = findViewById(R.id.tv_class_room);

        textView.setText(course.getClassRoom());

        textView = findViewById(R.id.tv_class_num);

        int class_start = course.getClassStart();
        int class_num = course.getClassLength();
        textView.setText(String.format(getString(R.string.schedule_section),
                aStrWeek[course.getDayOfWeek() - 1], class_start, (class_start + class_num - 1)));

        textView = findViewById(R.id.tv_week_of_term);
//        textView.setText(String.format(getString(R.string.week_of_term_format),
//                course.getWeekOfTerm(), course.getWeekOptions()));
        textView.setText(LycUtils.getFormatStringFromWeekOfTerm(course.getWeekOfTerm()));

        textView = findViewById(R.id.tv_teacher);
        textView.setText(course.getTeacher());
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
     * 菜单栏
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_delete:
                final AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("您确定要删除该课程吗?")
                        .create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.sCourseList.remove(mIndex);
                        new LycFileUtils<List<LycCourse>>().saveToJson(LycCourseDetailsActivity.this,
                                MainActivity.sCourseList,
                                LycFileUtils.TIMETABLE_FILE_NAME);
                        Toast.makeText(LycCourseDetailsActivity.this, "成功删除", Toast.LENGTH_SHORT).show();
                        setUpdateResult();
                        alertDialog.dismiss();
                        finish();

                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

