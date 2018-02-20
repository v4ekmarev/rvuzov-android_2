/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raspisaniyevuzov.app.ui.task;

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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.manager.TaskManager;
import com.raspisaniyevuzov.app.db.model.Task;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class CompletedTaskFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(
                R.layout.fragment_task_list, container, false);
        setupListView(listView);
        return listView;
    }

    private void setupListView(ListView listView) {
        final RealmResults<Task> mItems = getCompletedTaskList();
        listView.setAdapter(new CompletedTaskAdapter(getActivity(),
                mItems, true));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = mItems.get(position);
                Intent intent = new Intent(getActivity(), ViewTaskActivity.class);
                intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
                startActivity(intent);
            }
        });
    }

    private RealmResults<Task> getCompletedTaskList() {
        return TaskManager.getAllCompleted();
    }

    public class CompletedTaskAdapter
            extends RealmBaseAdapter<Task> implements ListAdapter {

        public CompletedTaskAdapter(Context context,
                                    RealmResults<Task> realmResults,
                                    boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView tvTaskText;
            public final TextView tvDayLeft;
            public final TextView tvSubjectName;
            public final ImageView ivMoreAction;
            public final FrameLayout flMoreAction;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvTaskText = (TextView) view.findViewById(R.id.tvTaskText);
                tvDayLeft = (TextView) view.findViewById(R.id.tvDayLeft);
                tvSubjectName = (TextView) view.findViewById(R.id.tvSubjectName);
                ivMoreAction = (ImageView) view.findViewById(R.id.ivMoreAction);
                flMoreAction = (FrameLayout) view.findViewById(R.id.flMoreAction);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.completed_task_list_item,
                        parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final Task task = realmResults.get(position);
            holder.tvTaskText.setText(TaskManager.getTaskText(task, getContext()));
            holder.tvSubjectName.setText(task.getSubject().getName());

            holder.ivMoreAction.setColorFilter(getContext().getResources().getColor(R.color.red));
            holder.flMoreAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    // Inflate the menu from xml
                    popup.getMenuInflater().inflate(R.menu.completed_task_actions, popup.getMenu());
                    // Setup menu item selection
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    Intent intent = new Intent(getContext(), EditTaskActivity.class);
                                    intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, task.getId());
                                    startActivity(intent);
                                    return true;
                                case R.id.menu_open:
                                    TaskManager.open(task);
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

        public RealmResults<Task> getRealmResults() {
            return realmResults;
        }
    }

}
