package project.kyawmyoag.doctormanager.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import project.kyawmyoag.doctormanager.ToDo.Task;

@Dao
public interface OnDataBaseAction {

    @Query("SELECT * FROM Task")
    List<Task>getAllTasksList();

    @Query("DELETE FROM Task")
    void truncateTheList();

    @Insert
    void insertDataIntoTaskList(Task task);

    @Query("DELETE FROM Task WHERE taskId=:taskId")
    void deleteTaskFromId(int taskId);

    @Query("SELECT * FROM Task WHERE taskId=:taskId")
    Task selectDataFromAnId(int taskId);

    @Query("UPDATE Task SET taskTitle=:taskTitle,taskDescription=:taskDescription,phone=:phone," +
            "date=:taskDate,event=:taskEvent,charges=:charges WHERE taskId=:taskId")

    void updateAnExistingRow(int taskId,String taskTitle,String taskDescription,String phone,
                             String taskDate,String taskEvent,String charges);
}
