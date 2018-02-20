package com.raspisaniyevuzov.app.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.LessonDao;
import com.raspisaniyevuzov.app.db.manager.TaskManager;
import com.raspisaniyevuzov.app.db.model.Audience;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.db.model.Teacher;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.task.EditTaskActivity;
import com.raspisaniyevuzov.app.ui.task.ViewTaskActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.ImageUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class LessonDetailsActivity extends BaseActivity {

    public static final String EXTRA_LESSON_DATE = "extra_lesson_data";
    public static final String EXTRA_LESSON_ID = "extra_lesson_id";

    private Lesson mLesson;
    private FloatingActionButton mFab;
    private View rootLayout;
    private boolean mLeaveActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_details);

        rootLayout = findViewById(R.id.root);

        long date = getIntent().getLongExtra(EXTRA_LESSON_DATE, 0);
        String lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);

        if (lessonId == null) return;

        Toolbar toolbar = initToolbarForActivity(TimeUtil.convertMillisToSimpleDate(date));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLesson = (Lesson) LessonDao.getInstance().get(lessonId, Lesson.class);
        if (mLesson == null) return;

        ((ImageView) findViewById(R.id.ivScheduleIcon)).setColorFilter(getResources().getColor(R.color.accentColor));

        TextView tvLessonType = (TextView) findViewById(R.id.tvLessonType);
        TextView tvAudience = (TextView) findViewById(R.id.tvAudience);
        TextView tvLessonEndTime = (TextView) findViewById(R.id.tvLessonEndTime);
        TextView tvLessonStartTimeHour = (TextView) findViewById(R.id.tvLessonStartTimeHour);
        TextView tvLessonStartTimeMin = (TextView) findViewById(R.id.tvLessonStartTimeMin);
        TextView tvSubjectName = (TextView) findViewById(R.id.tvSubjectName);
        TextView tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
        RelativeLayout rlSubgroupsContainer = (RelativeLayout) findViewById(R.id.rlSubgroupsContainer);
        View subgroupsDivider = findViewById(R.id.subgroupsDivider);
        TextView tvSubgroups = (TextView) findViewById(R.id.tvSubgroups);
        RelativeLayout rlTeacherContainer = (RelativeLayout) findViewById(R.id.rlTeacherContainer);

        if (mLesson.getSubgroups() != null && !mLesson.getSubgroups().isEmpty()) {
            rlSubgroupsContainer.setVisibility(View.VISIBLE);
            subgroupsDivider.setVisibility(View.VISIBLE);
            tvSubgroups.setText(mLesson.getSubgroups());
        } else {
            rlSubgroupsContainer.setVisibility(View.GONE);
            subgroupsDivider.setVisibility(View.GONE);
        }

        int hour = TimeUtil.convertMillisToHour(mLesson.getTimeStart());
        tvLessonStartTimeHour.setText((hour < 10) ? "0" + hour : String.valueOf(hour));

        int min = TimeUtil.convertMillisToMin(mLesson.getTimeStart());
        tvLessonStartTimeMin.setText((min < 10) ? "0" + min : String.valueOf(min));

        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : mLesson.getTeacher())
            if (teacher.getName() != null && !teacher.getName().isEmpty())
                teacherNames.add(teacher.getName());

        if (!teacherNames.isEmpty()) rlTeacherContainer.setVisibility(View.VISIBLE);
        else rlTeacherContainer.setVisibility(View.GONE);
        tvTeacherName.setText(TextUtils.join("\n", teacherNames));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        tvLessonEndTime.setText(TimeUtil.convertMillisToTime(calendar.getTimeInMillis() + mLesson.getTimeEnd()));

        tvSubjectName.setText(mLesson.getSubject().getName());

        tvLessonType.setText(mLesson.getType().toUpperCase());

        List<String> audienceNames = new ArrayList<>();
        for (Audience audience : mLesson.getAudience())
            audienceNames.add(audience.getName());

        tvAudience.setText(TextUtils.join("\n", audienceNames));

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLeaveActivity) {
                    mLeaveActivity = true;
                    int cx = rootLayout.getWidth() - ImageUtil.convertDpToPixel(54, LessonDetailsActivity.this);
                    int cy = rootLayout.getHeight() - ImageUtil.convertDpToPixel(54, LessonDetailsActivity.this);

                    float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

                    final SupportAnimator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout,
                            cx, cy, 0, finalRadius);
                    circularReveal.setDuration(300);

                    circularReveal.addListener(new SupportAnimator.AnimatorListener() {
                        @Override
                        public void onAnimationStart() {
                            rootLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd() {
                            Intent intent = new Intent(LessonDetailsActivity.this, EditTaskActivity.class);
                            intent.putExtra(EditTaskActivity.EXTRA_SUBJECT_ID, mLesson.getSubject().getId());
                            startActivity(intent);
                        }

                        @Override
                        public void onAnimationCancel() {

                        }

                        @Override
                        public void onAnimationRepeat() {

                        }
                    });

                    rootLayout.setVisibility(View.VISIBLE);
                    circularReveal.start();
                }
            }
        });

        showFab();

        updateTask();
    }

    private void showFab() {
        mFab.animate().scaleX(1).scaleY(1).setDuration(200).setStartDelay(500).start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void updateTask() {
        LinearLayout llTaskContainer = (LinearLayout) findViewById(R.id.llTaskContainer);
        LinearLayout llTaskList = (LinearLayout) findViewById(R.id.llTaskList);

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        llTaskList.removeAllViews();

        if (mLesson != null && mLesson.getSubject() != null) {
            RealmResults<Task> tasks = TaskManager.getOpenTaskBySubject(mLesson.getSubject());
            if (!tasks.isEmpty()) {
                llTaskContainer.setVisibility(View.VISIBLE);
                int i = 1;
                for (final Task task : tasks) {
                    if (i > 1)
                        llTaskList.addView(layoutInflater.inflate(R.layout.task_divider, null));
                    View item = layoutInflater.inflate(R.layout.task_list_item, null, false);
                    ((TextView) item.findViewById(R.id.tvNumber)).setText(i + ".");
                    ((TextView) item.findViewById(R.id.tvTaskText)).setText(TaskManager.getTaskText(task, this));
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(LessonDetailsActivity.this, ViewTaskActivity.class);
                            intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
                            startActivity(intent);
                        }
                    });
                    llTaskList.addView(item);
                    i++;
                }
            } else {
                llTaskContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLeaveActivity = false;
        rootLayout.setVisibility(View.INVISIBLE);
        updateTask();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.LESSON_SCREEN.type);
    }
}
