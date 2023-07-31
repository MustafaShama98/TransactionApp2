package com.example.AccountManagement.DBHolder;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.AccountManagement.Transaction;


@Database(entities = {Transaction.class},version = 4)
public  abstract class AppDB extends RoomDatabase { //singleton class
    private static AppDB instance;
    public abstract TransactionDB TransactionDao();



    public static synchronized AppDB getInstance(Context context) { //static constructor, synchroinzied so only 1 thread can access db at time
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDB.class,"bank-database")
                    .allowMainThreadQueries()
                   // .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();

        }
        return instance;
    }


    private static final Migration MIGRATION_2_3 = new Migration(2, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // You need to execute a SQL query to rename the column and convert its data type
            database.execSQL("ALTER TABLE `Transaction` RENAME TO `temp_Transaction`");
            database.execSQL("CREATE TABLE `Transaction` " +
                    "(`amount` INTEGER NOT NULL, " +
                    "`description` TEXT NOT NULL, " +
                    "`year` INTEGER NOT NULL, " +
                    "`type` STRING NOT NULL, " +
                    "`deleted` BOOLEAN NOT NULL, " +
                    "`month` INTEGER NOT NULL, " +
                    "`day` INTEGER NOT NULL, " +
                    "`hour` INTEGER NOT NULL, " +
                    "`minute` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`amount`, `description`, `day`, `month`, `year`, `hour`, `minute`))");
            database.execSQL("INSERT INTO `Transaction` (`amount`, `description`, `year`, `month`, `day`, `hour`, `minute`) " +
                    "SELECT CAST(`title` AS INTEGER) AS `amount`, `description`, `year`, `month`, `day`, `hour`, `minute` " +
                    "FROM `temp_Transaction`");
            database.execSQL("DROP TABLE `temp_Transaction`");
        }
    };


    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);
        new PopulateDbAsyncTask(instance).execute();
    }
};

 private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{
        private TransactionDB transactionDao;

        private PopulateDbAsyncTask (AppDB db){
            transactionDao = db.TransactionDao();
        }
     @Override
     protected Void doInBackground(Void... voids) {
        transactionDao.insert(new Transaction(4,"To: xx","Sent",false,23,7,2023,11,31));
         transactionDao.insert(new Transaction(23,"To: yy","Sent",false,24,7,2023,14,16));
         transactionDao.insert(new Transaction(345,"To: yy","Pending",false,25,7,2023,14,12));
         transactionDao.insert(new Transaction(12,"To: yy","Received",false,24,7,2023,14,13));
         transactionDao.insert(new Transaction(5,"To: zz","Pending",false,20,7,2023,14,13));

         return null;
     }
 }
}
