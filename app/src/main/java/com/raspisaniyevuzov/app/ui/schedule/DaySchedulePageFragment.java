package com.raspisaniyevuzov.app.ui.schedule;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.manager.LessonManager;
import com.raspisaniyevuzov.app.db.model.Audience;
import com.raspisaniyevuzov.app.db.model.Lesson;
import com.raspisaniyevuzov.app.db.model.Teacher;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.ScheduleFragment;
import com.raspisaniyevuzov.app.util.TextUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 25.09.2015.
 */
public class DaySchedulePageFragment extends BaseFragment {

    public static final String EXTRA_SELECTED_DATE = "extra_selected_date";
    public static final String EXTRA_SELECTED_INDEX = "extra_selected_index";
    LinearLayout mScheduleLayout;
    private boolean mLeaveActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.day_schedule_page_fragment, container, false);
        mScheduleLayout = (LinearLayout) rootView.findViewById(R.id.llMain);

        updateLesson((getArguments() != null) ? getArguments().getLong(EXTRA_SELECTED_DATE) : Calendar.getInstance().getTimeInMillis());

        rootView.setTag(getArguments().getInt(EXTRA_SELECTED_INDEX));

        return rootView;
    }

    private void updateLesson(final long selectedDate) {
        RealmResults<Lesson> lessons = LessonManager.getLessonsForDate(new Date(selectedDate));

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.lesson_item_header, null);
        final TextView tvDate = (TextView) header.findViewById(R.id.tvDate);
        tvDate.setText(TextUtil.uppercase1stLetter(TimeUtil.convertMillisToDateWithWeekDay(selectedDate)));

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeekScheduleActivity.class);
                intent.putExtra(EXTRA_SELECTED_DATE, selectedDate);
                startActivityForResult(intent, ScheduleFragment.SELECT_DATE_REQUEST_CODE);
            }
        });

        mScheduleLayout.addView(header);

        if (lessons.size() > 0) {
            for (int i = 0; i < lessons.size(); i++) {
                final Lesson lesson = lessons.get(i);

                View view = LayoutInflater.from(getActivity()).inflate(R.layout.lesson_item, null);
                View main = view.findViewById(R.id.rlMain);

                final TextView tvLessonType = (TextView) main.findViewById(R.id.tvLessonType);
                final TextView tvAudience = (TextView) main.findViewById(R.id.tvAudience);
                final TextView tvLessonEndTime = (TextView) main.findViewById(R.id.tvLessonEndTime);
                final TextView tvLessonStartTimeHour = (TextView) main.findViewById(R.id.tvLessonStartTimeHour);
                final TextView tvLessonStartTimeMin = (TextView) main.findViewById(R.id.tvLessonStartTimeMin);
                final TextView tvSubjectName = (TextView) main.findViewById(R.id.tvSubjectName);
                final TextView tvTeacherName = (TextView) main.findViewById(R.id.tvTeacherName);
                final TextView tvSubgroups = (TextView) main.findViewById(R.id.tvSubgroups);

                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mLeaveActivity) {
                            if (lesson.isValid()) {
                                mLeaveActivity = true;
                                Intent intent = new Intent(getActivity(), LessonDetailsActivity.class);
                                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_ID, lesson.getId());
                                intent.putExtra(LessonDetailsActivity.EXTRA_LESSON_DATE, selectedDate);
                                startActivity(intent);
                            }
                        }
                    }
                });

                if (i == lessons.size() - 1)
                    main.setBackgroundResource(R.drawable.lesson_item_footer_selector);

                if (lesson.getSubgroups() != null && !lesson.getSubgroups().isEmpty()) {
                    tvSubgroups.setText(String.format(getActivity().getString(R.string.subgroups), lesson.getSubgroups()));
                    tvSubgroups.setVisibility(View.VISIBLE);
                } else tvSubgroups.setVisibility(View.GONE);

                List<String> teacherNames = new ArrayList<>();
                for (Teacher teacher : lesson.getTeacher())
                    teacherNames.add(teacher.getName());

                int hour = TimeUtil.convertMillisToHour(lesson.getTimeStart());
                tvLessonStartTimeHour.setText((hour < 10) ? "0" + hour : String.valueOf(hour));
                int min = TimeUtil.convertMillisToMin(lesson.getTimeStart());
                tvLessonStartTimeMin.setText((min < 10) ? "0" + min : String.valueOf(min));

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                tvLessonEndTime.setText(TimeUtil.convertMillisToTime(calendar.getTimeInMillis() + lesson.getTimeEnd()));
                tvTeacherName.setText(TextUtils.join("\n", teacherNames));
                tvSubjectName.setText(lesson.getSubject().getName());

                tvLessonType.setText(lesson.getType().toUpperCase());

                List<String> audienceNames = new ArrayList<>();
                for (Audience audience : lesson.getAudience())
                    audienceNames.add(audience.getName());
                tvAudience.setText(TextUtils.join("\n", audienceNames));

                mScheduleLayout.addView(view);

                if (i < lessons.size() - 1)
                    mScheduleLayout.addView(LayoutInflater.from(getActivity()).inflate(R.layout.lesson_item_divider, null));
            }

            View footer = LayoutInflater.from(getActivity()).inflate(R.layout.lesson_item_footer, null);
            mScheduleLayout.addView(footer);

        } else {
            View weekendView = LayoutInflater.from(getActivity()).inflate(R.layout.weekend_view, null);
            mScheduleLayout.addView(weekendView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLeaveActivity = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment mCallbackFragment = getActivity().getSupportFragmentManager().findFragmentByTag(ScheduleFragment.class.getSimpleName());
        if (mCallbackFragment != null)
            mCallbackFragment.onActivityResult(requestCode, resultCode, data);
    }

}
