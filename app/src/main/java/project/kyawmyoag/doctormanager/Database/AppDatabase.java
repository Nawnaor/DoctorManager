package project.kyawmyoag.doctormanager.Database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import project.kyawmyoag.doctormanager.ToDo.Task;

@Database(entities = {Task.class},version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract OnDataBaseAction dataBaseAction();
    private static volatile AppDatabase appDatabase;

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }


    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    public void clearAllTables() {

    }
}
