package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class PriceListActivity extends AppCompatActivity{

    private List<Transaction> transactionList;
    private RecyclerView recyclerView;
    private PriceListAdapter priceListAdapter;
    private PriceListAdapter priceListAdapter2;
    private RecyclerView listRecyclerView;
    private ListView listView;
    private TextView mEmptyView;
    private SearchView searchView;
    private String[] strings;
    private ArrayAdapter mAdapter;
    AppDatabase productDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.START);
            slide.addTarget(R.id.list_recycler);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(2000);
            getWindow().setEnterTransition(slide);
        }

        transactionList = new ArrayList<>();
        recyclerView = findViewById(R.id.price_list_recycler_view);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(PriceListActivity.this, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        priceListAdapter = new PriceListAdapter(PriceListActivity.this, transactionList);
        recyclerView.setAdapter(priceListAdapter);

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getProductTransaction().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> categoryTransaction) {
                if (categoryTransaction != null){
                    transactionList.addAll(categoryTransaction);
                    categoryTransaction.clear();
                    for (Transaction t: transactionList) {
                        if ((t.getTransactionType().equals("Incoming Stock"))){
                            categoryTransaction.add(t);
                            priceListAdapter = new PriceListAdapter(PriceListActivity.this, categoryTransaction);
                            recyclerView.setAdapter(priceListAdapter);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        //mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search by name or prices");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                priceListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            //NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
