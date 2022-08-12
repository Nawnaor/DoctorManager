package project.kyawmyoag.doctormanager.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import com.google.firebase.database.DatabaseReference;


public class DatabaseClient {
    private Context mCtx;
    private static DatabaseClient mInstance;

    //Our app dataase object
    private AppDatabase appDatabase;
    DatabaseReference databaseReference;

    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        appDatabase= Room.databaseBuilder(mCtx,AppDatabase.class,"Task.db")
                .fallbackToDestructiveMigration().build();

    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
