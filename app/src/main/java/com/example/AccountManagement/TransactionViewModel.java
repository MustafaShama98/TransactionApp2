package com.example.AccountManagement;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.AccountManagement.serviceHolder.TransActionsRepository;

public class TransactionViewModel extends AndroidViewModel {
        public static final String Shared_PREFS= "sharedPrefs";
        public  static final boolean switch_option_delete = false;
        MutableLiveData<String> accountBalance = new MutableLiveData<>();
        private TransActionsRepository repo;
        private LiveData<List<Transaction>> allTransacionList;
        private ArrayList<Transaction> allTransactionList_normal;


        private MutableLiveData<List<Transaction>> mutableAllTransactionList = new MutableLiveData<>();

        public TransactionViewModel(@NonNull Application application) {
            super(application);
            repo = TransActionsRepository.getInstance(application);

            allTransacionList = repo.getAll();
            // Observe the LiveData and update mutableAllTransactionList when it changes
            allTransacionList.observeForever(transactions -> {
                mutableAllTransactionList.postValue(transactions);
                // Convert the LiveData to ArrayList and update allTransactionList_normal
                allTransactionList_normal = new ArrayList<>(transactions);
              //  Log.d("removed1", String.valueOf(ItemsRecycleAdapter.deletedTransactionList.get(0).amount));
            });


        }

    public void insert(Transaction transaction){
       repo.insert(transaction);
    }
    public LiveData<Integer> getSum(){return repo.getSum();}
    public void update(Transaction transaction){
        repo.update(transaction);
    }
    public LiveData<List<Transaction>> getAll(){
        return allTransacionList;
    }
    public LiveData<Transaction> getOldestTransactionOver7Days() {return repo.getOldestTransactionOver7Days();}
    public void delete(Transaction transaction){
        repo.delete(transaction);
    }
    public List<Transaction> getClosetTask(int day1, int month1 , int year1, int hour1, int minute1){
            return repo.getClosetTask(day1, month1, year1, hour1, minute1);
    }


    //function returns transaction that are oover 7 days with status "Pending"
    public List<Transaction> getTransactionsOver7Days() {
        List<Transaction> allTransactions = getAllTransactionList_normal();
        List<Transaction> transactionsOver7Days = new ArrayList<>();

        if (allTransactions != null) {
            // Get the current date
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Subtract 7 days from the current date
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            Date sevenDaysAgo = calendar.getTime();

            // Loop through all transactions and filter the ones older than 7 days
            for (Transaction transaction : allTransactions) {
                Calendar transactionDate = Calendar.getInstance();
                transactionDate.set(transaction.getYear(), transaction.getMonth() - 1, transaction.getDay());

                // Check if the transaction date is older than 7 days
                if (transactionDate.getTime().before(sevenDaysAgo) && transaction.getType().equals("Pending")) {
                    transactionsOver7Days.add(transaction);

                }
            }
        }

        if (!transactionsOver7Days.isEmpty()) {
            return transactionsOver7Days;
        } else {
            // Return null or handle the case when there are no transactions older than 7 days
            return null;
        }
    }







    public MutableLiveData<List<Transaction>> getMutableAllTransactionList() {
        return mutableAllTransactionList;
    }


    public ArrayList<Transaction> getAllTransactionList_normal() {
        return allTransactionList_normal;
    }
}


