package com.raspisaniyevuzov.app.ui.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.util.TextUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Adapter for infinity Week-by-week schedule
 */
public class WeekScheduleAdapter extends ArrayAdapter<WeekScheduleObject> {

    private final LayoutInflater mLayoutInflater;
    private final List<WeekScheduleObject> objects;
    private final Context context;
    private static boolean bgRotation = false;

    public WeekScheduleAdapter(List<WeekScheduleObject> items, Context context) {
        super(context, R.layout.schedule_week_item, items);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.objects = items;
        this.context = context;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
        final WeekScheduleObject object = objects.get(pos);
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.schedule_week_item, viewGroup, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (object != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(object.getDate());
            holder.tvDayOfWeek.setText(TextUtil.uppercase1stLetter(TimeUtil.convertMillisToWeekDay(calendar.getTimeInMillis())));

            if (object.isWeekend()) {
                bgRotation = !bgRotation;

                holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.blackSecondary));
                holder.llMain.setBackgroundResource(bgRotation ? R.drawable.week_item_selector : R.drawable.week_item_selector2);
                holder.tvLessonStartTime.setVisibility(View.INVISIBLE);

                if (calendar.get(Calendar.DAY_OF_WEEK) == 1 || calendar.get(Calendar.DAY_OF_WEEK) == 7)
                    holder.tvToday.setText(TimeUtil.isToday(object.getDate()) ? context.getString(R.string.today) + ", " + getCorrectDate(calendar) + ", " + context.getString(R.string.weekend_day) : getCorrectDate(calendar) + ", " + context.getString(R.string.weekend_day));
                else
                    holder.tvToday.setText(TimeUtil.isToday(object.getDate()) ? context.getString(R.string.today) + ", " + getCorrectDate(calendar) + ", " + context.getString(R.string.no_lessons) : getCorrectDate(calendar) + ", " + context.getString(R.string.no_lessons));

                holder.tvLessonStartTime.setTextColor(context.getResources().getColor(R.color.blackSecondary));
                holder.tvDayOfWeek.setTextColor(context.getResources().getColor(R.color.blackSecondary));
                holder.tvToday.setTextColor(context.getResources().getColor(R.color.blackSecondary));
            } else {
                holder.tvDayOfWeek.setTextColor(context.getResources().getColor(android.R.color.black));
                holder.tvLessonStartTime.setVisibility(View.VISIBLE);

                bgRotation = !bgRotation;

                holder.tvLessonStartTime.setTextColor(context.getResources().getColor(R.color.blackSecondary));
                holder.tvDayOfWeek.setTextColor(context.getResources().getColor(android.R.color.black));
                holder.tvToday.setTextColor(context.getResources().getColor(R.color.blackSecondary));
                holder.llMain.setBackgroundResource(bgRotation ? R.drawable.week_item_selector : R.drawable.week_item_selector2);

                if (TimeUtil.isTomorrow(object.getDate()))
                    holder.tvToday.setText(context.getString(R.string.tomorrow) + ", " + getCorrectDate(calendar));
                else
                    holder.tvToday.setText(TimeUtil.isToday(object.getDate()) ? context.getString(R.string.today) + ", " + getCorrectDate(calendar) : TimeUtil.convertMillisToSimpleDate(calendar.getTimeInMillis()));

                holder.tvLessonStartTime.setText(String.format(context.getString(R.string.lesson_time), TimeUtil.convertMillisToTime(TimeUtil.resetHoursAndMinutes(calendar).getTimeInMillis() + object.getTime())));
            }

            if (TimeUtil.isToday(object.getDate())) {
                holder.llMain.setBackgroundResource(R.drawable.week_item_today_selector);
                holder.tvLessonStartTime.setTextColor(context.getResources().getColor(android.R.color.white));
                holder.tvDayOfWeek.setTextColor(context.getResources().getColor(android.R.color.white));
                holder.tvToday.setTextColor(context.getResources().getColor(android.R.color.white));
            }
        }
        return convertView;
    }

    private String getCorrectDate(Calendar calendar) {
        return (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) ? TimeUtil.convertMillisToSimpleDate(calendar.getTimeInMillis()) : TimeUtil.convertMillisToSimpleDateWithYear(calendar.getTimeInMillis());
    }

    static class ViewHolder {
        TextView tvLessonStartTime;
        TextView tvToday;
        TextView tvDayOfWeek;
        RelativeLayout llMain;

        public ViewHolder(View view) {
            tvLessonStartTime = (TextView) view.findViewById(R.id.tvLessonStartTime);
            tvToday = (TextView) view.findViewById(R.id.tvToday);
            tvDayOfWeek = (TextView) view.findViewById(R.id.tvDayOfWeek);
            llMain = (RelativeLayout) view.findViewById(R.id.llMain);
        }
    }

}
