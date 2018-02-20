package com.raspisaniyevuzov.app.db.dbimport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.raspisaniyevuzov.app.misc.scheduleclasses.ScheduleTask;
import com.raspisaniyevuzov.app.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DbAdapterScheduleTask {
    private final Context context;
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public DbAdapterScheduleTask(Context context) {
        this.context = context;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(ScheduleTask scheduleTask) {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.KEY_TASK_DATA, Utils.makeByteArrayFromObject(scheduleTask));
        return values;
    }

    public long createEntry(ScheduleTask scheduleTask) {
        final ContentValues initialValues = createContentValues(scheduleTask);
        return database.insert(DbHelper.TABLE_NAME_TASKS, null, initialValues);
    }

    public boolean deleteEntry(long rowId) {
        return database.delete(DbHelper.TABLE_NAME_TASKS, DbHelper.KEY_TASK_ROWID + "=" + rowId, null) > 0;
    }

    public List<ScheduleTask> fetchAllEntries() {
        final Cursor mCursor = database.query(DbHelper.TABLE_NAME_TASKS, new String[]{DbHelper.KEY_TASK_DATA, DbHelper.KEY_TASK_ROWID}, null, null, null, null, DbHelper.KEY_TASK_ROWID);
        if (mCursor != null) {
            mCursor.moveToFirst();
        } else {
            return null;
        }
        final int taskCount = mCursor.getCount();
        final List<ScheduleTask> scheduleTasks = new ArrayList<ScheduleTask>();
        for (int i = 0; i < taskCount; i++) {
            final byte[] data = mCursor.getBlob(mCursor.getColumnIndexOrThrow(DbHelper.KEY_TASK_DATA));

            LogUtil.d(DbAdapterScheduleTask.class.getSimpleName(), "data.length=" + data.length);

            final ScheduleTask objectData = Utils.makeObjectFromByteArray(data);
            final long rowId = mCursor.getLong(mCursor.getColumnIndexOrThrow(DbHelper.KEY_TASK_ROWID));

            LogUtil.d(DbAdapterScheduleTask.class.getSimpleName(), "rowId=" + rowId);

            objectData.setRowId(rowId);

            scheduleTasks.add(objectData);

            mCursor.moveToNext();
        }
        mCursor.close();
        return scheduleTasks;
    }

    public ScheduleTask fetchEntryAt(long rowId) throws SQLException {
        final Cursor mCursor = database.query(true, DbHelper.TABLE_NAME_TASKS, new String[]{DbHelper.KEY_TASK_DATA}, DbHelper.KEY_TASK_ROWID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        } else {
            return null;
        }

        final byte[] data = mCursor.getBlob(mCursor.getColumnIndexOrThrow(DbHelper.KEY_TASK_DATA));
        final ScheduleTask objectData = Utils.makeObjectFromByteArray(data);

        mCursor.close();

        return objectData;
    }

    public DbAdapterScheduleTask open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

}
