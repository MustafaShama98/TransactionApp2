package com.example.AccountManagement;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.AccountManagement.serviceHolder.TransActionsRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedPreferenceViewModel extends AndroidViewModel {
    public static final String Shared_PREFS= "sharedPrefs";
    public  static final boolean switch_option_delete = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<Transaction> removedTransactionsInSP = new ArrayList<>();;
    private MutableLiveData<List<String>> removedTransactionsLiveData = new MutableLiveData<>();

    private TransActionsRepository repo;


    public SharedPreferenceViewModel(@NonNull Application application) {
            super( application);
            sharedPreferences = getApplication().getSharedPreferences(Shared_PREFS, Context.MODE_PRIVATE);
        repo = TransActionsRepository.getInstance(application);
        }

    public void saveDeletedTransactionsSP(ArrayList<Transaction> deletedTransactions) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson(); // Gson library is used to convert objects to JSON strings

        String transactionsJson = gson.toJson(deletedTransactions);
        editor.putString("removedTransactions", transactionsJson);
        editor.apply();
    }

    public ArrayList<Transaction> getRemovedListBySP() {
        String transactionsJson = sharedPreferences.getString("removedTransactions", "");
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<Transaction>>() {}.getType();
         removedTransactionsInSP = gson.fromJson(transactionsJson, type);



        return removedTransactionsInSP;
    }


}


