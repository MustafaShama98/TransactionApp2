package com.example.AccountManagement.DBHolder;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.lifecycle.MutableLiveData;
import com.example.AccountManagement.Transaction;

import java.util.List;

//The opreations we want to do on our Transaction table in database
@Dao
public interface TransactionDB {

    @Query("SELECT * FROM `Transaction` ORDER BY `year` DESC, `month` DESC, `day` DESC, `hour` DESC, `minute` DESC")
    LiveData<List<Transaction>> getAll();

    @Delete
    void delete(Transaction transaction);

    @Update
    public void update(Transaction transaction);

    @Query("SELECT SUM(amount) FROM `Transaction`")
    LiveData<Integer> sum();

    @Insert(onConflict = REPLACE)
    public void insert(Transaction transaction);

    @Query("SELECT * FROM `Transaction` where year=:year1 And day =:day1 And month=:month1  And hour=:hour1 And minute=:minute1")
    List<Transaction> getClosetTask(int day1, int month1 , int year1, int hour1, int minute1);



}
