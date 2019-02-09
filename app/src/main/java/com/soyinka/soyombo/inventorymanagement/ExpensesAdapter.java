package com.soyinka.soyombo.inventorymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHOW on 1/15/2019.
 */

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder> implements Filterable {
    private Context mContext;
    private List<SalesCostAndExpenses> mExpensesList;
    private List<SalesCostAndExpenses> arrayList;

    public ExpensesAdapter(Context context, List<SalesCostAndExpenses> expensesList) {
        mContext = context;
        mExpensesList = expensesList;
        arrayList = new ArrayList<>(expensesList);

    }

    @Override
    public ExpensesAdapter.ExpensesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.cash_bank_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ExpensesAdapter.ExpensesViewHolder viewHolder = new ExpensesAdapter.ExpensesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ExpensesViewHolder holder, int position) {
        final SalesCostAndExpenses costAndExpenses = mExpensesList.get(position);

        if (costAndExpenses != null) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat df = new SimpleDateFormat("M/d/yy");
                String dateString = costAndExpenses.getDate();
                Date date = dateFormat.parse(dateString);
                String date1 = df.format(date);
                holder.date.setText(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //holder.date.setText(costAndExpenses.getDate());
            holder.description.setText(costAndExpenses.getDescription());
            double amount = Double.parseDouble(costAndExpenses.getAmount());
            //double dod = amount/1000;
            BigDecimal d = BigDecimal.valueOf(amount/1000);
            holder.balance.setText(String.valueOf(d));

            if ((costAndExpenses.getNature()).equals("Cost of Sales")) {
                holder.debit.setText(R.string.cs);
                holder.credit.setText("");
            } else {
                holder.debit.setText("");
                holder.credit.setText(R.string.ex);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mExpensesList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }


    public class ExpensesViewHolder extends RecyclerView.ViewHolder {
        TextView date, description, credit, debit, balance;


        public ExpensesViewHolder(View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            credit = itemView.findViewById(R.id.credit);
            debit = itemView.findViewById(R.id.debit);
            date = itemView.findViewById(R.id.date);
            balance = itemView.findViewById(R.id.balance);
        }
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SalesCostAndExpenses> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SalesCostAndExpenses t : arrayList) {
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

            mExpensesList.clear();
            mExpensesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}


//
//public class CashAndBankAdapter extends RecyclerView.Adapter<CashAndBankAdapter.CashAndBankViewHolder> implements Filterable {
//
//
//    private Context mContext;
//    private List<CashAndBank> mTransactionList;
//    private List<CashAndBank> arrayList;
//
//    public CashAndBankAdapter(Context context, List<CashAndBank> transactionList) {
//        mContext = context;
//        mTransactionList = transactionList;
//        arrayList = new ArrayList<>(transactionList);
//
//    }
//
//    @Override
//    public CashAndBankViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        Context context = viewGroup.getContext();
//        int layoutIdForListItem = R.layout.cash_bank_list_item;
//        LayoutInflater inflater = LayoutInflater.from(context);
//        boolean shouldAttachToParentImmediately = false;
//
//        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
//        CashAndBankViewHolder viewHolder = new CashAndBankViewHolder(view);
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(CashAndBankViewHolder holder, int position) {
//
//        //populate views
//        final CashAndBank transaction = mTransactionList.get(position);
//
//        if (transaction != null){
//
//            try {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//                SimpleDateFormat df = new SimpleDateFormat("M/d/yy");
//                String dateString = transaction.getDate();
//                Date date = dateFormat.parse(dateString);
//                String date1 = df.format(date);
//                holder.date.setText(date1);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            holder.description.setText(transaction.getDescription());
//            String s = transaction.getTransactionType();
//            if (s.equals("Incoming Stock")){
//                String amount = transaction.getAmount();
//                double d = (Integer.parseInt(amount))/1000;
//                holder.debit.setText("");
//                holder.credit.setText(String.valueOf(d));
//                holder.balance.setText("");
//            } else {
//                String a = transaction.getAmount();
//                double d = (Integer.parseInt(a))/1000;
//                holder.credit.setText("");
//                holder.debit.setText(String.valueOf(d));
//                holder.balance.setText("");
//            }
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return mTransactionList.size();
//    }
//
//
//
//    public class CashAndBankViewHolder extends RecyclerView.ViewHolder {
//        TextView date, description, credit, debit, balance;
//
//
//        public CashAndBankViewHolder(View itemView) {
//            super(itemView);
//
//            description = itemView.findViewById(R.id.description);
//            credit = itemView.findViewById(R.id.credit);
//            debit = itemView.findViewById(R.id.debit);
//            date = itemView.findViewById(R.id.date);
//            balance = itemView.findViewById(R.id.balance);
//        }
//    }
//
//    @Override
//    public Filter getFilter() {
//        return exampleFilter;
//    }
//
//    private Filter exampleFilter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            List<CashAndBank> filteredList = new ArrayList<>();
//            if (constraint == null || constraint.length() == 0){
//                filteredList.addAll(arrayList);
//            } else {
//                String filterPattern = constraint.toString().toLowerCase().trim();
//                for ( CashAndBank t: arrayList) {
//                    if (t.getDate().toLowerCase().startsWith(filterPattern)){
//                        filteredList.add(t);
//                    }
//                    if (t.getDescription().toLowerCase().contains(filterPattern)){
//                        filteredList.add(t);
//                    }
//                    if (t.getAmount().toLowerCase().contains(filterPattern)){
//                        filteredList.add(t);
//                    }
//                }
//            }
//            FilterResults results = new FilterResults();
//            results.values = filteredList;
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//
//            mTransactionList.clear();
//            mTransactionList.addAll((List) results.values);
//            notifyDataSetChanged();
//        }
//    };
//
//}

