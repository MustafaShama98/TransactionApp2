package com.example.AccountManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AccountManagement.DBHolder.AppDB;
import com.example.AccountManagement.serviceHolder.TransactionAdapterListener;

import java.util.ArrayList;

public class ItemsRecycleAdapter extends RecyclerView.Adapter<ItemsRecycleAdapter.MyViewHolder> {
    public ArrayList<Transaction> transactionList;
    public static ArrayList<Transaction> deletedTransactionList = new ArrayList<Transaction>();;
    TransactionListFrag.TransactionListFragListener fragListner1;
    Context con;
    AppDB database;
    AlertDialog dialog;
    private TransactionAdapterListener transactionListCallback;
    public static ItemsRecycleAdapter listner;
    static MutableLiveData<ArrayList<Transaction>> deletedTransactionLiveData = new MutableLiveData<>();




    public ItemsRecycleAdapter(Context conn, TransactionListFrag transactionCallback) {
        this.con = conn;
        fragListner1 = TransactionListFrag.fragListner;
        this.transactionList = new ArrayList<Transaction>();
        this.transactionListCallback = transactionCallback;

    }//ItemsRecycleAdapter




    //creates a new ViewHolder object whenever the RecyclerView needs a new one.
// This is the moment when the row layout is inflated,
// passed to the ViewHolder object and each child view can be found and stored
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        MyViewHolder evh = new MyViewHolder(view, null);


        return evh;
    }


    //RecyclerView takes advantage of the fact that as you scroll and new rows come on screen also old rows disappear off screen. Instead of creating new view for each new row, an old view is recycled and reused by binding new data to it.
    //
    //This happens exactly in onBindViewHolder().
    // Initially you will get new unused view holders and you have to fill them with data you want to display.
    // But as you scroll you'll start getting view holders that were used for rows that went off screen
    // and you have to replace old data that they held with new data.
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.type.setText(transaction.type);
        holder.titleitem.setText("$"+Integer.toString(transaction.getTitle()));

        holder.describeItem.setText(transaction.getDescription());
        holder.dateItem.setText(transaction.getDay()+"/"+ transaction.getMonth()+"/"+ transaction.getYear());
        if(transaction.getMinute()<10)
            holder.itemTime.setText(transaction.getHour()+":0"+ transaction.getMinute());
        else
            holder.itemTime.setText(transaction.getHour()+":"+ transaction.getMinute());
        // Check if the transaction type is "Sent" and set the visibility of fbutton accordingly
      if (transaction.getType().equals("Sent") || transaction.getType().equals("Received")) {
            holder.itemEditButton.setVisibility(View.GONE); // Hide the fbutton'
            holder.itemEditButton.setEnabled(false);//disable button
            String typeTransaction  = holder.type.getText().toString();
            if(typeTransaction.equals("Sent") ||typeTransaction.equals("Received") ){
                holder.type.setTextColor(Color.GREEN);
            }
        } else {
            holder.itemEditButton.setVisibility(View.VISIBLE); // Show the fbutton
            holder.itemEditButton.setEnabled(true);
        }

      if(transaction.getDeleted() ){
          holder.describeItem.setText("lol");
          /**
          // Convert the hexadecimal color code to an integer value
          int backgroundColor = Color.parseColor("#EAE9E9");

          // Create a ColorStateList representing the desired background tint colors
          int[][] states = new int[][]{
                  new int[]{android.R.attr.state_pressed},
                  new int[]{-android.R.attr.state_pressed}
          };

          int[] colors = new int[]{
                  backgroundColor, // Set #EAE9E9 as the background tint when pressed
                  backgroundColor // Set #EAE9E9 as the background tint when not pressed
          };

          ColorStateList colorStateList = new ColorStateList(states, colors);

          holder.main_layout.setBackgroundTintList(colorStateList);
           **/
      }
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return transactionList.size();
    }//getItemCount

    public void setTransactionList(ArrayList<Transaction> transactions) {
        this.transactionList.clear();
        this.transactionList.addAll(transactions);
        notifyDataSetChanged();
    }

    // innerclass myViewHolder is a wrapper around a View that contains the layout for an individual item in the list.
    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout main_layout;
        TextView titleitem, describeItem, dateItem,itemTime,type;
        Button itemEditButton;
        // Inflate the item_layout XML


        //Responsiple for the front-end (interface + interaction ) of each row transaction
        public MyViewHolder(@NonNull View itemView, Object o) {
            super(itemView);
            main_layout = (LinearLayout)itemView.findViewById(R.id.item_layout);
            type = itemView.findViewById(R.id.type);
            titleitem = itemView.findViewById(R.id.titleitem);
            describeItem = itemView.findViewById(R.id.describeItem);
            dateItem = itemView.findViewById(R.id.dateItem);
            itemEditButton = itemView.findViewById(R.id.itemEditButton);
            itemTime = itemView.findViewById(R.id.itemTime);
          //  itemEditButton = itemView.findViewById(R.id.add_btn); // Initialize fbutton


            //Clicking on edit button in transaction row
            itemEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    fragListner1.DoInEditEvent(transactionList.get(pos));//opens edit page fragment
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();

                    dialog = new AlertDialog.Builder(con)
                            .setTitle("WARNING")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (pos != RecyclerView.NO_POSITION) {
                                        //call back from TransactionListFrag
                                        transactionListCallback.onTransactionDelete(transactionList.get(pos)); //deleting from db

                                        if (MainActivity.saveBoxValue) {

                                            saveDeletedTransactions(transactionList.get(pos));
                                        }
                                        // transactionList.remove(pos);
                                    }
                                   // notifyDataSetChanged();
                                   // notifyItemRemoved(pos);
                                  //   notifyItemRangeChanged(pos, transactionList.size());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.cancel();
                                }
                            }).show();
                    return false;
                }
            });


        //clicking anywhere on the row opens frag page details about the transaction
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    fragListner1.DoInViewEvent(transactionList.get(pos));
                }
            });
        }
    }//myviewHolder

    //---------------------------------DB handler  -----------------
/**  public void UpdateTaskList() {

        ItemsRecycleAdapter.listner.transactionList = new ArrayList<>();
        for(Transaction temp : all) {
            ItemsRecycleAdapter.listner.transactionList.add(temp);
        }
        ItemsRecycleAdapter.listner.notifyDataSetChanged();
    }
**/
    public static void saveDeletedTransactions(Transaction transaction) {
        deletedTransactionList.add(transaction);
      //  transaction.setDeleted(true);
        // Update the LiveData with the updated deletedTransactionList
        deletedTransactionLiveData.postValue(deletedTransactionList);
    }

}
