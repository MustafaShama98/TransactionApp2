package com.example.AccountManagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.AccountManagement.DBHolder.AppDB;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class Edittransactionfrag extends Fragment {

    Transaction selectedTransaction;
    EditText EditTransanction,EditDesc;

    Button updatebutton ,date, time;;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;
    public static int finalyear , finalmonth , finalday, finalhour, finalminute;
    AppDB DataBase;
    TransactionViewModel transactionViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
         transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditTransanction = view.findViewById(R.id.Edittransactionn);

        EditDesc = view.findViewById(R.id.EditDesc1);
        updatebutton = view.findViewById(R.id.updatebutton);

        Edit_widget();

        LocalDate lt = LocalDate.now();
        int year = lt.getYear();
        int month = lt.getMonthValue()-1;
        int day = lt.getDayOfMonth();
        Log.i("pr","onViewCreated5");
        ZonedDateTime tt = ZonedDateTime.now();
        ZonedDateTime IsraelDateTime = tt.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
        int hour = IsraelDateTime.getHour();
        int minute = IsraelDateTime.getMinute();

        finalyear = selectedTransaction.year;
        finalmonth = selectedTransaction.month;
        finalday = selectedTransaction.day;
        finalhour = selectedTransaction.hour;
        finalminute = selectedTransaction.minute;

//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth
////                        ,onDateSetListener,year,month,day);
////                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                datePickerDialog.show();
//
//            }
//        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                finalyear=i;
                finalmonth=i1+1;
                finalday =i2;
            }
        };


        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                finalhour = i;
                finalminute = i1;
            }
        };

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transanctions,desc,type;
                transanctions = EditTransanction.getText().toString();
                desc = EditDesc.getText().toString();
                // Update the selectedTransaction object with new data

                //remove the old task from the sql
               transactionViewModel.delete(selectedTransaction);
                //ItemsRecycleAdapter.listner.removeTask(selectedTransaction);

                //Adding to SQL
                Transaction newTransaction = new Transaction(-1*Integer.parseInt(transanctions),
                        desc,
                        "Pending",
                        false,
                        Calendar.getInstance().getTime().getDay(),
                        Calendar.getInstance().getTime().getMonth(),
                        Calendar.getInstance().getTime().getYear(),
                        Calendar.getInstance().getTime().getHours(),
                        Calendar.getInstance().getTime().getMinutes());
                transactionViewModel.insert(newTransaction);
                // Go back to the previous fragment in the back stack
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
//                ItemsRecycleAdapter.listner.transactionList.add(newTransaction);

            }
        });
    }


    public void EditSelectedTransaction(Transaction transaction){
        selectedTransaction = transaction;
    }

    public void  Edit_widget(){
        EditTransanction.setText(Integer.toString(selectedTransaction.getTitle()));
        EditDesc.setText(selectedTransaction.getDescription());
    }


}