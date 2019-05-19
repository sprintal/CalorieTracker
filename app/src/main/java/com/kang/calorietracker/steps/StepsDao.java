package com.kang.calorietracker.steps;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface StepsDao {
    @Query("SELECT * FROM Steps")
    List<Steps> getAll();

    @Query("SELECT * FROM Steps WHERE email = :email")
    List<Steps> findByEmail(String email);

    @Insert
    void insertAll(Steps... steps);

    @Insert
    long insert(Steps steps);

    @Update(onConflict = REPLACE)
    public void update(Steps... steps);

    @Query("DELETE FROM Steps")
    void deleteAll();
}