package com.example.AccountManagement;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.AccountManagement.BrodcastHolder.BroadcastReceiverListner;
import com.example.AccountManagement.serviceHolder.ForegroundService;
import com.example.AccountManagement.serviceHolder.TransactionAdapterListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements TransactionListFrag.TransactionListFragListener, BottomNavigationView.OnNavigationItemSelectedListener, TransactionAdapterListener {
    private static MainActivity main;
    BroadcastReceiver br = new BroadcastReceiverListner();
    IntentFilter filter = new IntentFilter();
    TransactionViewModel transactionViewModel;
    BottomNavigationView bottomNavigationView;
    public static boolean saveBoxValue = false;
    public static boolean displayBox=false;
    private static boolean notfiBoxValue = false ;

    public boolean isNotfiBoxValue() {
        return notfiBoxValue;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main= (MainActivity) this;
        bottomNavigationView = findViewById(R.id.bottom_navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        // Create the ViewModel instance
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        /************************************************ define service **********************************************************/

        String[] permissions = { Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_NETWORK_STATE };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,permissions,1);
            }
        } else {
        }
        //filter.addAction(Intent.CONNECTIVITY_CHANGE);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(br, filter);
       /**        old       **/
        if(!checkServiceRunning(ForegroundService.class)){

            Intent serviceIntent = new Intent(this, ForegroundService.class);
           // serviceIntent.putExtra("main_activity_instance", (CharSequence) this);

            startForegroundService(serviceIntent);

        }
    }

    private boolean checkServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


    public static MainActivity getactiv() {
        return main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home:
                TransactionListFrag transactionListFrag = new TransactionListFrag();
                fragment  = transactionListFrag;
                MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                        add(R.id.fragContainer, transactionListFrag).//add on top of the static fragment
                        addToBackStack("AAA").//cause the back button scrolling through the loaded fragments
                        commit();
                MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
                break;
            case R.id.profile:
              //  fragment = new usersFragment();
                break;
            case R.id.settings:
               /** getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content ,new MyPreferences()).addToBackStack(null)
                        .commit();**/
                SettingsFragment settingsFragment = new SettingsFragment();
                fragment  = settingsFragment;
                MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragContainer, settingsFragment).//add on top of the static fragment
                        addToBackStack("AAA").//cause the back button scrolling through the loaded fragments
                        commit();
                MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
                break;
        }
        if (fragment != null) {
          //   getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout, fragment).commit();
        }
        return true;
    }

    // Method to update the saveBoxValue in MainActivity
    public void updateSaveBoxValue(boolean value) {
        saveBoxValue = value;

    }
    public void updateNotfiBoxValue(boolean value) {
         notfiBoxValue = value;

    }

    /************************************************ talking with frag **********************************************************/
//opens edit page
    @Override
    public void DoInEditEvent(Transaction transaction) {
        Edittransactionfrag edittransactionfrag = new Edittransactionfrag();
        edittransactionfrag.EditSelectedTransaction(transaction);
        MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                add(R.id.fragContainer, edittransactionfrag).//add on top of the static fragment
                addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                commit();
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void DoInViewEvent(Transaction transaction) {
        itemViewrFrag viewerFrag = new itemViewrFrag();
        viewerFrag.EditSelectedTask(transaction);
        MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                add(R.id.fragContainer, viewerFrag).//add on top of the static fragment
                addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                commit();
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
    }





    @Override
    protected void onStart() {
        super.onStart();
        filter.addAction("com.TaskRemainder.CUSTOM_INTENT");
        registerReceiver(br, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(br);
    }

    @Override
    public void onTransactionDelete(Transaction transaction) {
        transactionViewModel.delete(transaction);
    }

    @Override
    public void notifyOnDelete(ArrayList<Transaction> transaction) {

    }

    @Override
    public void onTransactionAdd(Transaction transaction) {
        transactionViewModel.insert(transaction);
    }

    @Override
    public List<Transaction> getAllList() {
        List<Transaction> transactions = transactionViewModel.getAll().getValue();
        return transactions != null ? transactions : new ArrayList<>();
    }

    @Override
    public List<Transaction> getClosetTask(int day1, int month1, int year1, int hour1, int minute1) {
        return null;
    }

    @Override
    public List<Transaction>  getTransactionsOver7Days() {
        return transactionViewModel.getTransactionsOver7Days();
    }



}