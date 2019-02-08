package com.soyinka.soyombo.inventorymanagement;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by SHOW on 1/7/2019.
 */

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.PriceListViewHolder> implements Filterable {

    private Context mContext;
    private List<Transaction> mTransactionList;
    private List<Transaction> arrayList;
    LayoutInflater inflater;

    public PriceListAdapter(Context context, List<Transaction> transactionList) {
        mContext = context;
        mTransactionList = transactionList;
        arrayList = new ArrayList<>(transactionList);
    }

    @Override
    public PriceListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.price_list_items;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        PriceListViewHolder viewHolder = new PriceListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PriceListViewHolder holder, int position) {

        //populate views
        final Transaction transaction = mTransactionList.get(position);

        if (transaction != null) {
            holder.productName.setText(transaction.getProductName());
            holder.costPrice.setText(transaction.getCostPrice());
            holder.salesPrice.setText(transaction.getSalesPrice());
        }

    }

    @Override
    public int getItemCount() {
        if (mTransactionList != null){
            return mTransactionList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Transaction> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(arrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for ( Transaction t: arrayList) {
                    if (t.getProductName().toLowerCase().startsWith(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getSalesPrice().toLowerCase().contains(filterPattern)){
                        filteredList.add(t);
                    }
                    if (t.getCostPrice().toLowerCase().contains(filterPattern)){
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

    public class PriceListViewHolder extends RecyclerView.ViewHolder {
        TextView productName, costPrice, salesPrice;


        public PriceListViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            costPrice = itemView.findViewById(R.id.cost_price);
            salesPrice = itemView.findViewById(R.id.sales_price);
        }
    }

}