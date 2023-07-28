package com.example.AccountManagement;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.ArrayList;
import java.util.List;

// SharedPreferenceViewModel is responsible for saving and retrieving the removed transactions
// from SharedPreferences,
// and the Fragment observes the LiveData to get
// updates whenever the data changes in SharedPreferences.
public class SettingsFragment extends PreferenceFragmentCompat {
    private boolean saveBoxValue = false, displayBoxValue = false, notfiBoxValue = false;
    private SharedPreferenceViewModel sharedPreferenceViewModel;
    private TransactionViewModel transactionViewModel;
    SwitchPreferenceCompat saveBoxPreference, displayTransactionsBox, notfiBox;
    private ArrayList<Transaction> restoredDeletedTransactions;
    private MutableLiveData<List<Transaction>> mutableAllTransactionList = new MutableLiveData<>();
    private ArrayList<Transaction> currentTransactions;
    private ArrayList<Transaction> mergedList;
    private boolean transactionsDeleted = false; // to keep track of deleted transactions


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPreferenceViewModel = new ViewModelProvider(requireActivity()).get(SharedPreferenceViewModel.class);
        transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

// Get the SwitchPreference by its key
        saveBoxPreference = findPreference("SaveBox");
        displayTransactionsBox = findPreference("DisplayDeletedTransactionBox");
        notfiBox = findPreference("notifcations");


        // Observe the deletedTransactionLiveData to save changes to SharedPreferences
        ItemsRecycleAdapter.deletedTransactionLiveData.observe(this, new Observer<ArrayList<Transaction>>() {
            @Override
            public void onChanged(ArrayList<Transaction> deletedTransactionList) {
                // Check if saveBoxValue is true, then save the updated deletedTransactionList to SharedPreferences
                if (saveBoxValue) {
                    sharedPreferenceViewModel.saveDeletedTransactionsSP(deletedTransactionList);
                }
            }
        });


        // Set a listener to get the value when it changes
        saveBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue will be a boolean representing the new value of the SwitchPreference
                saveBoxValue = (Boolean) newValue;
                // Get the MainActivity instance and update the saveBoxValue
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.updateSaveBoxValue(saveBoxValue);

                }
                return true; // Return true to update the preference with the new value
            }
        });//saveBoxPreference

        displayTransactionsBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue will be a boolean representing the new value of the SwitchPreference
                displayBoxValue = (Boolean) newValue;
                // Get the MainActivity instance and update the saveBoxValue
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.updateSaveBoxValue(saveBoxValue);

                }
                // Restore deleted transactions from SharedPreferences
                restoredDeletedTransactions = sharedPreferenceViewModel.getRemovedListBySP();
                // Update the value in the ViewModel
                if (displayBoxValue) {

                    for (int i = 0; i < restoredDeletedTransactions.size(); i++)
                        Log.d("print2", restoredDeletedTransactions.get(i).toString());
                    // Merge restoredDeletedTransactions with the current list of transactions
                    currentTransactions = transactionViewModel.getAllTransactionList_normal();
                    mergedList = new ArrayList<>(currentTransactions);
                    mergedList.addAll(restoredDeletedTransactions);

                    // Update the LiveData with the merged list to display all transactions
                    transactionViewModel.getMutableAllTransactionList().postValue(mergedList);

                    // Add restored deleted transactions to the database
                    for (Transaction transaction : restoredDeletedTransactions) {
                        transactionViewModel.insert(transaction);
                    }
                } else {

                    // Update the LiveData with the current list of transactions only
                    transactionViewModel.getMutableAllTransactionList().postValue(transactionViewModel.getAllTransactionList_normal());
                    if(restoredDeletedTransactions != null)
                    // Remove restored deleted transactions from the database
                     for (Transaction transaction : restoredDeletedTransactions) {
                        transactionViewModel.delete(transaction);
                    }
                }
                transactionViewModel.getMutableAllTransactionList().
                        postValue(transactionViewModel.getAllTransactionList_normal());//update live data observers


                return true; // Return true to update the preference with the new value
            }
        });//displayTransactionsBox

        notfiBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                notfiBoxValue = (Boolean) newValue;
                // Get the MainActivity instance and update the saveBoxValue
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.updateNotfiBoxValue(notfiBoxValue);

                }
                return true; // Return true to update the preference with the new value
            }
        });
    }

    public ArrayList<Transaction> getrestoredDeletedTransactions() {
        return restoredDeletedTransactions;
    }

    public boolean getSaveBoxValue() {
        return saveBoxValue;
    }
}