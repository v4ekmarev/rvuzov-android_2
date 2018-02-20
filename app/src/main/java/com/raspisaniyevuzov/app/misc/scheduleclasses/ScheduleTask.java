package com.raspisaniyevuzov.app.misc.scheduleclasses;

import com.raspisaniyevuzov.app.db.dbimport.StringUtils;

import java.util.List;

public class ScheduleTask extends SortableScheduleClass {
    private static final long serialVersionUID = -479560375583604921L;

    private String description;
    private ScheduleLesson lesson;

	private long rowId;
    private boolean isComplete;
    private List<String> imagesURI;

    public ScheduleTask(String id, String description, ScheduleLesson lesson, boolean isComplete, List<String> imagesURI, long rowId) {
        super.setName(StringUtils.ellipsize(description, 60, 0));
        super.setId(id);

        this.description = description;
        this.lesson = lesson;
        this.isComplete = isComplete;
        this.imagesURI = imagesURI;
        this.rowId = rowId;
    }

    @Override
    public int compareTo(SortableScheduleInterface another) {
        if (another instanceof ScheduleTask) {
            final ScheduleTask anotherTask = (ScheduleTask) another;
            final int comparsionResult = getName().compareTo(anotherTask.getName());
            if (comparsionResult == 0) {
                if (isComplete) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return comparsionResult;
            }
        } else {
            return super.compareTo(another);
        }
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImagesURI() {
        return imagesURI;
    }

    public ScheduleLesson getLesson() {
		return lesson;
	}

    public void setLesson(ScheduleLesson lesson) {
		this.lesson = lesson;
	}

    public int getDay() {
        return lesson.getDay();
    }

    public String getLessonId() {
        return lesson.getId();
    }

    public String getLessonName() {
        return lesson.getName();
    }

    public int getLessonParity() {
        return lesson.getParity();
    }

    public int getLessonType() {
        return lesson.getType();
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagesURI(List<String> imagesURI) {
        this.imagesURI = imagesURI;
    }
    
    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
    
    public long getRowId() {
        return rowId;
    }
}
