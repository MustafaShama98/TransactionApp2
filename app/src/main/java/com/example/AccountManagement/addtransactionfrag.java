package com.example.AccountManagement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.AccountManagement.DBHolder.AppDB;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class addtransactionfrag extends Fragment {
    EditText AddTransaction, AddDescTransaction;
    TextView typeTextView;
    Button addTransactionButton;
    Context conn;
    AppDB DataBase;
    public static addtransactionfrag addnotef;
    boolean timeselected , dateselected;

    TransactionViewModel transactionViewModel;
    public addtransactionfrag(Context con) {
        conn = con;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addnotef = this;
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addtransactionfrag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Refactor XML
        AddTransaction = view.findViewById(R.id.Addtransaction);
        AddDescTransaction = view.findViewById(R.id.AddDescreptionTransaction);
        addTransactionButton = view.findViewById(R.id.addTransactionButton);
        typeTextView = view.findViewById(R.id.type);
        timeselected = false;
        dateselected = false;

        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount,desc,type;

                amount = AddTransaction.getText().toString();
                desc = AddDescTransaction.getText().toString();
                type = "Sent";
               desc =  desc.substring(0, 1).toUpperCase() + desc.substring(1);

                /** current time and date **/
                ZonedDateTime tt = ZonedDateTime.now();
                ZonedDateTime IsraelDateTime = tt.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
                int hour = IsraelDateTime.getHour();
                int minute = IsraelDateTime.getMinute();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar c = Calendar.getInstance();
                String date = sdf.format(c.getTime());
                String day=date.substring(0, 2) , mounth=date.substring(3, 5) , year=date.substring(6, 10);

                if(!amount.isEmpty()){
                    //Adding to SQL

                    Transaction newTransaction = new Transaction(
                           -1* Integer.parseInt(amount),
                            desc,
                            type,
                            false,
                            Integer.parseInt(day),
                            Integer.parseInt(mounth),
                            Integer.parseInt(year),
                            hour,
                            Calendar.getInstance().getTime().getMinutes());
                    transactionViewModel.insert(newTransaction);
                    Toast.makeText(getContext(), "New transaction has been made.", Toast.LENGTH_SHORT).show();

                    // Go back to the previous fragment in the back stack
                    requireActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    Toast.makeText(getContext(), "Amount of transaction is required.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}