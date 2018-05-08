package no.nordicsemi.android.blinky.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ButtonsDao {

    @Insert(onConflict = REPLACE)
    void addButton(CorButton corButton);

    @Query("select * from CorButton")
    LiveData<List<CorButton>> getAllCorButtons();

    @Query("select * from CorButton order by id DESC LIMIT :prefNums ")
    LiveData<List<CorButton>> getButtons(int prefNums);



    @Query("select * from CorButton where id = :id")
    LiveData<CorButton> getItemById(long id);

//    @Query("select initialized from CorButton")
////    Boolean getInitialized();

    @Delete
    void deleteButton(CorButton corButton);





}
