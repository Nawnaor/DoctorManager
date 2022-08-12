package project.kyawmyoag.doctormanager.ToDo;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int taskId;
    @ColumnInfo(name = "taskTitle")
    String taskTitle;
    @ColumnInfo(name = "date")
    String date;
    @ColumnInfo(name = "taskDescription")
    String taskDescription;

    @ColumnInfo(name = "phone")
    String phone;
    @ColumnInfo(name = "event")
    String event;
    @ColumnInfo(name = "charges")
    String charges;
    @ColumnInfo(name = "isComplete")
    boolean isComplete;


    public Task() {

    }

    public boolean isComplete() {
        return isComplete;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event=event;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

}
