package com.soyinka.soyombo.inventorymanagement;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHOW on 1/5/2019.
 */

public class HistoryAndRecordAdapter extends RecyclerView.Adapter<HistoryAndRecordAdapter.HistoryViewHolder> implements Filterable {

    private Context mContext;
    private List<Transaction> mTransactionList;
    private List<Transaction> arrayList;

    public HistoryAndRecordAdapter(Context context, List<Transaction> transactionList) {
        mContext = context;
        mTransactionList = transactionList;
        arrayList = new ArrayList<>(transactionList);

    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.history_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        HistoryViewHolder viewHolder = new HistoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {

        //populate views
        final Transaction transaction = mTransactionList.get(position);

        if (transaction != null){
            holder.supplierName.setText(transaction.getSupplier());
            holder.quantity.setText(mContext.getString(R.string.qty_) + " " + transaction.getQuantity());
            holder.transactionFund.setText(transaction.getTransactionFund());
            holder.transactionType.setText(transaction.getTransactionType());
            holder.costPrice.setText(mContext.getString(R.string.cost_price_) + " " +  transaction.getCostPrice());
            holder.salesPrice.setText(mContext.getString(R.string.sales_price_) + " " + transaction.getSalesPrice()
                    + "\n" + mContext.getString(R.string.amount_) + " " + transaction.getTotalAmount());
            holder.date.setText(transaction.getDate());
        }

        //int i = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)mContext).finish();
                mContext.startActivity(new Intent(mContext, ProductTransactionActivity.class)
                        .putExtra("Transaction", transaction).putExtra("switchcounter", 45));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTransactionList.size();
    }

    public List<Transaction> getmTransactionList(){
        return mTransactionList;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transaction> filteredList = new ArrayList<>();
            long dateInMilliseconds = 0;
            long dateInMillisecondsA = 0;
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(arrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( Transaction t: arrayList) {

                    if (t.getSupplier().toLowerCase().startsWith(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getSalesPrice().toLowerCase().contains(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getCostPrice().toLowerCase().contains(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getTransactionFund().toLowerCase().contains(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getDate().toLowerCase().contains(filterPattern)){
                        List<Transaction> f = new ArrayList<>();
                        f.add(t);
                        for (Transaction transaction: f) {
                            String sD = t.getDate();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                            Date startDate = null;
                            try {

                                startDate = dateFormat.parse(sD);
                                dateInMilliseconds = startDate.getTime();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                dateInMillisecondsA = (dateFormat.parse(filterPattern)).getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (dateInMillisecondsA >= dateInMilliseconds){
                                filteredList.add(t);
                            }
                        }
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mTransactionList.clear();
            mTransactionList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView supplierName, quantity, transactionFund, transactionType,
                costPrice, salesPrice, date;


        public HistoryViewHolder(View itemView) {
            super(itemView);

            supplierName = itemView.findViewById(R.id.supplier_name);
            quantity = itemView.findViewById(R.id.quantity);
            transactionFund = itemView.findViewById(R.id.transaction_fund);
            transactionType = itemView.findViewById(R.id.transaction_type);
            costPrice = itemView.findViewById(R.id.cost_price);
            salesPrice = itemView.findViewById(R.id.sales_price);
            date = itemView.findViewById(R.id.date);
        }
    }
}