package com.example.AccountManagement.serviceHolder;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import com.example.AccountManagement.DBHolder.AppDB;
import com.example.AccountManagement.DBHolder.TransactionDB;
import com.example.AccountManagement.Transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//The Repository ist a simple Java class that abstracts the data layer from the rest of the app and mediates between different data sources, like a web service and a local cache.
// It hides the different database operations (like SQLite queries) and provides a clean API to the ViewModel.
//Since Room doesn't allow database queries on the main thread, we use AsyncTasks to execute them asynchronously.
//LiveData is fetched on a worker thread automatically, so we don't have to take care of this.
//Also, we will add a RoomDatabase.Callback to our database builder where we populate our database in the onCreate method so we don't start with an empty table. We can also override onOpen if we want to execute code every time our Room database is opened
public class TransActionsRepository {
    private TransactionDB transactionDao;
    private LiveData<List<Transaction>> allTransactionsList;
    private static TransActionsRepository instance;


    private TransActionsRepository(Application application){
        AppDB database = AppDB.getInstance(application);
        transactionDao = database.TransactionDao();
        allTransactionsList = transactionDao.getAll();
    }

    //signleton class
    public static synchronized TransActionsRepository getInstance(Application application) {
        if (instance == null) {
            instance = new TransActionsRepository(application);
        }
        return instance;
    }
    public LiveData<Integer> getSum(){
        return transactionDao.sum();
    }

    public List<Transaction>  getClosetTask(int day1, int month1 , int year1, int hour1, int minute1){
        return transactionDao.getClosetTask(day1,  month1 ,  year1,  hour1, minute1);
    }
    public void insert(Transaction transaction){
        new InsertTransactionAsyncTask(transactionDao).execute(transaction);
    }
    public void update(Transaction transaction){
        new UpdateTransactionAsyncTask(transactionDao).execute(transaction);
    }

    public LiveData<List<Transaction>> getAll(){
        return allTransactionsList;
    }
    public void delete(Transaction transaction){
        new DeleteTransactionAsyncTask(transactionDao).execute(transaction);
    }
    private static class InsertTransactionAsyncTask extends AsyncTask<Transaction, Void ,Void>{
        private TransactionDB transDao;

        public InsertTransactionAsyncTask(TransactionDB transactionDao) {
            this.transDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            transDao.insert(transactions[0]);
            return null;
        }
    }

    private static class DeleteTransactionAsyncTask extends AsyncTask<Transaction, Void ,Void>{
        private TransactionDB transDao;

        public DeleteTransactionAsyncTask(TransactionDB transactionDao) {
            this.transDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            transDao.delete(transactions[0]);
            return null;
        }
    }

    private static class getClosetTaskAsync extends AsyncTask<Transaction, Void ,Void>{
        private TransactionDB transDao;

        public getClosetTaskAsync(TransactionDB transactionDao) {
            this.transDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            // getClosetTask(int day1, int month1 , int year1, int hour1, int minute1);
            transDao.getClosetTask(transactions[0].getDay(),transactions[0].getMonth(),transactions[0].getYear(),
                    transactions[0].getHour(),transactions[0].getMinute());
            return null;
        }
    }

    private static class UpdateTransactionAsyncTask extends AsyncTask<Transaction, Void ,Void>{
        private TransactionDB transDao;

        public UpdateTransactionAsyncTask(TransactionDB transactionDao) {
            this.transDao = transactionDao;
        }

        @Override
        protected Void doInBackground(Transaction... transactions) {
            transDao.update(transactions[0]);
            return null;
        }
    }



    public LiveData<Transaction> getOldestTransactionOver7Days() {

    MutableLiveData<Transaction> oldestTransactionLiveData = new MutableLiveData<>();

    // Execute the AsyncTask to fetch the oldest transaction over 7 days
        new GetOldestTransactionAsyncTask(transactionDao, oldestTransactionLiveData).execute();

        return oldestTransactionLiveData;
}

private static class GetOldestTransactionAsyncTask extends AsyncTask<Void, Void, Transaction> {
    private TransactionDB transDao;
    private MutableLiveData<Transaction> oldestTransactionLiveData;

    public GetOldestTransactionAsyncTask(TransactionDB transactionDao, MutableLiveData<Transaction> oldestTransactionLiveData) {
        this.transDao = transactionDao;
        this.oldestTransactionLiveData = oldestTransactionLiveData;
    }

    @Override
    protected Transaction doInBackground(Void... voids) {
        // Execute the query to get the oldest transaction over 7 days
        return transDao.getOldestTransactionOver7Days();
    }

    @Override
    protected void onPostExecute(Transaction transaction) {
        // Post the result to the LiveData
        oldestTransactionLiveData.postValue(transaction);
    }
}
}
