package com.soyinka.soyombo.inventorymanagement;

import android.content.Context;
import android.icu.math.BigDecimal;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHOW on 1/9/2019.
 */

public class CashAndBankAdapter extends RecyclerView.Adapter<CashAndBankAdapter.CashAndBankViewHolder> implements Filterable {


    private Context mContext;
    private List<CashAndBank> mTransactionList;
    private List<CashAndBank> arrayList;
    private double balanceAmount;
    private int bAmount;

    public CashAndBankAdapter(Context context, List<CashAndBank> transactionList) {
        mContext = context;
        mTransactionList = transactionList;
        arrayList = new ArrayList<>(transactionList);

    }

    @Override
    public CashAndBankViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.cash_bank_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        CashAndBankViewHolder viewHolder = new CashAndBankViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CashAndBankViewHolder holder, int position) {

        //populate views
        final CashAndBank transaction = mTransactionList.get(position);

        balanceAmount = 0;
        if (transaction != null) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat df = new SimpleDateFormat("M/d/yy");
                String dateString = transaction.getDate();
                Date date = dateFormat.parse(dateString);
                String date1 = df.format(date);
                holder.date.setText(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.description.setText(transaction.getDescription());
            String s = transaction.getTransactionType();
            if (s.equals("Incoming Stock")) {
                String amount = transaction.getAmount();
                double balance = transaction.getBalance();
                double d;
                if (amount.length() >= 2 && amount.charAt(0) == '"'){
                    String changedAmount = amount.replaceAll("^\"|\"$", "");
                    d = Double.parseDouble(changedAmount);
                } else {
                    d = Double.parseDouble(amount);
                }

                java.math.BigDecimal dD = java.math.BigDecimal
                        .valueOf(d / 1000);
                balanceAmount -= d;
                holder.debit.setText("");
                holder.credit.setText(String.valueOf(dD));
                holder.balance.setText(String.valueOf(java.math.BigDecimal.valueOf(balance/1000)));
            } else {
                String a = transaction.getAmount();
                double balanced = transaction.getBalance();
                double d;

                if (a.length() >= 2 && a.charAt(0) == '"'){
                    String sa = a.replaceAll("^\"|\"$", "");
                    d = Double.parseDouble(sa);
                } else {
                    d = Double.parseDouble(a);
                }
                java.math.BigDecimal dD = java.math.BigDecimal
                        .valueOf(d / 1000);

                balanceAmount += d;
                holder.credit.setText("");
                holder.debit.setText(String.valueOf(dD));
                holder.balance.setText(String.valueOf(String.valueOf(java.math.BigDecimal.valueOf(balanced/1000))));

            }
        }
    }

    @Override
    public int getItemCount() {
        return mTransactionList.size();
    }


    public class CashAndBankViewHolder extends RecyclerView.ViewHolder {
        TextView date, description, credit, debit, balance;


        public CashAndBankViewHolder(View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            credit = itemView.findViewById(R.id.credit);
            debit = itemView.findViewById(R.id.debit);
            date = itemView.findViewById(R.id.date);
            balance = itemView.findViewById(R.id.balance);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CashAndBank> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CashAndBank t : arrayList) {
                    if (t.getDate().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(t);
                    }
                    if (t.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(t);
                    }
                    if (t.getAmount().toLowerCase().contains(filterPattern)) {
                        filteredList.add(t);
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

}
