package com.example.AccountManagement.serviceHolder;

import com.example.AccountManagement.Transaction;

import java.util.ArrayList;
import java.util.List;


public interface TransactionAdapterListener {
        void onTransactionDelete(Transaction transaction);
        void notifyOnDelete(ArrayList<Transaction> transaction);

        void onTransactionAdd(Transaction transaction);
        List<Transaction> getAllList();
    List<Transaction> getClosetTask(int day1, int month1 , int year1, int hour1, int minute1);
        // Add other methods for other operations as needed
    }

