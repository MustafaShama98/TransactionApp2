package com.example.AccountManagement;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AccountManagement.serviceHolder.TransactionAdapterListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionListFrag extends Fragment implements TransactionAdapterListener {
//home page frag
    float summ =0;
    TextView accountBalance, typeTextView;
    String type;
    RecyclerView recycleView;
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    Button fbutton;
    public static TransactionListFragListener fragListner;
    private TransactionViewModel transactionViewModel;
    View view;
    public TransactionListFrag() {
        // Required empty public constructor
    }

    //used in fragment to create layout and inflate view (like mainActivity oncreate)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        // Get the ViewModel instance from the MainActivity
        transactionViewModel = ((MainActivity) requireActivity()).transactionViewModel;

        typeTextView = view.findViewById(R.id.type);

        //binding the recycle view with this frag (transaction list)
        recycleView = view.findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        // Pass the Fragment itself as the callback to the Adapter
        ItemsRecycleAdapter madapter = new ItemsRecycleAdapter(getContext(), this);
        layoutManager = new LinearLayoutManager(getContext());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(madapter);

       // transactionList = transactionViewModel.getAll();
        // Observe LiveData changes in the ViewModel and update the Adapter
        transactionViewModel.getMutableAllTransactionList().observe(getViewLifecycleOwner(), transactions -> {

            if (transactions != null && !transactions.isEmpty()) {
                madapter.setTransactionList((ArrayList<Transaction>) transactions);

                //Log.d("print5",transactionViewModel.getTransactionsOver7Days().get(1).toString());
;                // madapter.notifyDataSetChanged();
                // You can also call notifyDataSetChanged() here if necessary
            }
        });

        // Create the observer which updates the UI.
        final Observer<Integer> balanceObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable final Integer sum) {
                // Update the UI, in this case, a TextView.

                accountBalance = view.findViewById(R.id.BalanceSum);
                accountBalance.setText("$ "+String.valueOf(sum) );
                if(sum == null) accountBalance.setText("$ "+String.valueOf(0) );;
            }
        };
        // Observe the LiveData on any change
        transactionViewModel.getSum().observe(getViewLifecycleOwner(), balanceObserver);

        //add transaction
        //if type = sent, recieved you can't edit only on pending
        fbutton = view.findViewById(R.id.add_btn);
        // Check if the type is "Sent" or "Received" and set the visibility of the Button accordingly


            fbutton.setVisibility(View.VISIBLE); // Show the Button
            fbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    {
                        addtransactionfrag fragB = new addtransactionfrag(getContext());
                        MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                                add(R.id.fragContainer, fragB).//add on top of the static fragment
                                addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                                commit();
                        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
                    }
                }
            });


    }//onViewCreated


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }


    public String getBankBalance(){

        return accountBalance.toString();
    }

    // Implement the interface methods to handle database operations

    @Override
    public void  onTransactionDelete(Transaction transaction) {
// Handle delete operation here, use your database or ViewModel to delete the transaction
        transactionViewModel.delete(transaction);
    }

    @Override
    public void notifyOnDelete(ArrayList<Transaction> transaction) {
        transactionViewModel.getMutableAllTransactionList().setValue(transaction);

    }


    @Override
    public void onTransactionAdd(Transaction transaction) {

    }

    @Override
    public List<Transaction> getAllList() {

        return transactionList;
    }

    @Override
    public List<Transaction> getClosetTask(int day1, int month1, int year1, int hour1, int minute1) {
        return transactionViewModel.getClosetTask(day1, month1, year1, hour1, minute1);
    }

    @Override
    public List<Transaction> getTransactionsOver7Days() {
       return transactionViewModel.getTransactionsOver7Days();
    }

    public void transactionTypeUI(TextView textView) {

    }


    /**************************************** interface for talking with other fragments ****************************************/
    public interface TransactionListFragListener {
        public void DoInEditEvent(Transaction transaction);
       public void DoInViewEvent(Transaction transaction);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.fragListner = (TransactionListFragListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'FragAListener'");
        }
        super.onAttach(context);
    }
}