package com.example.AccountManagement.serviceHolder;

import androidx.lifecycle.LiveData;

import com.example.AccountManagement.Transaction;

import java.util.ArrayList;
import java.util.List;


public interface TransactionAdapterListener {
        void onTransactionDelete(Transaction transaction);
        void notifyOnDelete(ArrayList<Transaction> transaction);
        void onTransactionAdd(Transaction transaction);
        List<Transaction> getAllList();
    List<Transaction> getClosetTask(int day1, int month1 , int year1, int hour1, int minute1);
    List<Transaction> getTransactionsOver7Days();

    }

