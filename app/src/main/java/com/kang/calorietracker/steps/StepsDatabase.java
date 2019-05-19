package com.kang.calorietracker.steps;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Steps.class}, version = 3, exportSchema = false)
public abstract class StepsDatabase extends RoomDatabase {
    public abstract StepsDao stepsDao();
    private static volatile StepsDatabase INSTANCE;

    static StepsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StepsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    StepsDatabase.class, "steps_database")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }
}