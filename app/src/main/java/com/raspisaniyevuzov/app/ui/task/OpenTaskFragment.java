package com.raspisaniyevuzov.app.ui.task;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.manager.TaskManager;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.util.LogUtil;
import com.raspisaniyevuzov.app.util.TextUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;

import java.util.Calendar;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class OpenTaskFragment extends Fragment {

    private static final int OPEN_TASK_REQUEST_CODE = 123;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listview = (ListView) inflater.inflate(
                R.layout.fragment_task_list, container, false);
        setupListView(listview);
        return listview;
    }

    private void setupListView(ListView listView) {
        final RealmResults<Task> mItems = getOpenTaskList();
        OpenTaskAdapter mAdapter = new OpenTaskAdapter(getActivity(), mItems, true
        );
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = mItems.get(position);
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

    }

    private RealmResults<Task> getOpenTaskList() {
        return TaskManager.getAllOpen();
    }

    public class OpenTaskAdapter extends RealmBaseAdapter<Task> implements ListAdapter {

        public OpenTaskAdapter(Context context,
                               RealmResults<Task> realmResults,
                               boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.open_task_list_item,
                        parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            final Task task = realmResults.get(position);

            holder.tvTaskText.setText(TaskManager.getTaskText(task, getContext()));

            if (task.getSubject() == null) {
                LogUtil.e(OpenTaskFragment.class.getSimpleName(), "task.getSubject() == null");
                holder.tvSubjectName.setText("");
            } else
                holder.tvSubjectName.setText(task.getSubject().getName());

            if (task.getDateEnd() != null && task.getDateEnd().getTime() > 0) {
                holder.llDayLeft.setVisibility(View.VISIBLE);
                int days = TimeUtil.getDayLeft(Calendar.getInstance().getTimeInMillis(), task.getDateEnd().getTime() + 24 * TimeUtil.HOUR);
                holder.tvDayLeft.setText(String.valueOf(days));
                String[] daysValues = {getString(R.string.no_days), getString(R.string.day1), getString(R.string.day2), getString(R.string.day3)};
                holder.tvDays.setText(TextUtil.getCorrectValuesByCount(days, daysValues));

                if (days < 1) {
                    holder.ivHot.setVisibility(View.VISIBLE);
                    holder.llDayLeft.setVisibility(View.GONE);
                } else {
                    holder.llDayLeft.setVisibility(View.VISIBLE);
                    holder.ivHot.setVisibility(View.GONE);
                    holder.tvDayLeft.setTextColor(getResources().getColor(R.color.taskDayLeftColor));
                    holder.tvDays.setTextColor(getResources().getColor(R.color.taskDayLeftColor));
                }
            } else {
                holder.llDayLeft.setVisibility(View.GONE);
                holder.ivHot.setVisibility(View.GONE);
            }

            holder.ivMoreAction.setColorFilter(getContext().getResources().getColor(R.color.red));
            holder.flMoreAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.open_task_actions, popup.getMenu());
                    // Setup menu item selection
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    Intent intent = new Intent(getContext(), EditTaskActivity.class);
                                    intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
                                    startActivity(intent);
                                    return true;
                                case R.id.menu_complete:
                                    holder.ivComplete.setVisibility(View.VISIBLE);
                                    ViewPropertyAnimator animatorCompat = holder.ivComplete.animate().scaleX(2).scaleY(2).setDuration(500);
                                    animatorCompat.setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            holder.ivComplete.setVisibility(View.GONE);
                                            holder.ivComplete.setScaleX(0);
                                            holder.ivComplete.setScaleY(0);
                                            TaskManager.complete(task);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {
                                        }
                                    }).start();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
            return convertView;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView tvTaskText;
            public final TextView tvDayLeft;
            public final TextView tvSubjectName;
            public final ImageView ivMoreAction;
            public final FrameLayout flMoreAction;
            public final LinearLayout llDayLeft;
            public final TextView tvDays;
            public final ImageView ivHot;
            public final ImageView ivComplete;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvTaskText = (TextView) view.findViewById(R.id.tvTaskText);
                tvDayLeft = (TextView) view.findViewById(R.id.tvDayLeft);
                tvSubjectName = (TextView) view.findViewById(R.id.tvSubjectName);
                ivMoreAction = (ImageView) view.findViewById(R.id.ivMoreAction);
                flMoreAction = (FrameLayout) view.findViewById(R.id.flMoreAction);
                llDayLeft = (LinearLayout) view.findViewById(R.id.llDayLeft);
                tvDays = (TextView) view.findViewById(R.id.tvDays);
                ivHot = (ImageView) view.findViewById(R.id.ivHot);
                ivComplete = (ImageView) view.findViewById(R.id.ivComplete);
            }
        }
    }

}
