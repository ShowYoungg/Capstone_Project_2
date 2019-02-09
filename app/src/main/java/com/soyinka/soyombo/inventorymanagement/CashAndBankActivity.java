package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class CashAndBankActivity extends AppCompatActivity {

    private ArrayList<CashAndBank> cashAndBanks;
    private ArrayList<CashAndBank> cashAndBanks2;
    private List<CashAndBank> cashAndBanksSearch;
    private List<CashAndBank> cashAndBanksSearch2;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private String startDateValue, endDateValue, cashAndBankSwitch;
    private EditText startDate, endDate;
    private Button submitButton;
    private Button addButton;
    private double balanceAmount;
    private double bAmount;
    private LinearLayoutManager layoutManager, layoutManager2;
    private CashAndBankAdapter cashAndBankAdapter;
    AppDatabase productDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_and_bank);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.START);
            slide.addTarget(R.id.layout_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        recyclerView = findViewById(R.id.cash_recycler);
        recyclerView2 = findViewById(R.id.cash_recycler2);
        balanceAmount = 0;
        bAmount = 0;

        cashAndBanks = new ArrayList<>();
        cashAndBanks2 = new ArrayList<>();
        cashAndBanksSearch = new ArrayList<>();
        cashAndBanksSearch2 = new ArrayList<>();
        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        submitButton = findViewById(R.id.send_for_search);
        addButton = findViewById(R.id.add_cash_bank);

        layoutManager = new LinearLayoutManager(CashAndBankActivity.this, VERTICAL, false);

        layoutManager2 = new LinearLayoutManager(CashAndBankActivity.this, VERTICAL, false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cashAndBankSwitch = "Cash";
                searchByDate(layoutManager, recyclerView, cashAndBankSwitch);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cashAndBankSwitch = "Bank";
                        searchByDate(layoutManager2, recyclerView2, cashAndBankSwitch);
                    }
                }, 750);

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CashAndBankActivity.this, AddCashBankActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCashBank(layoutManager, layoutManager2);
    }

    private void loadCashBank(final LinearLayoutManager layoutManager, final LinearLayoutManager layoutManager2) {
        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getCashAndBank().observe(this, new Observer<List<CashAndBank>>() {
            @Override
            public void onChanged(@Nullable List<CashAndBank> categoryTransaction) {

                if (categoryTransaction != null) {
                    cashAndBanksSearch2.addAll(categoryTransaction);
                    for (CashAndBank ct : categoryTransaction) {
                        String s = ct.getTransactionFund();
                        cashAndBanksSearch.add(ct);
                        if (s.equals("Cash")) {
                            CashAndBank c = new CashAndBank();
                            if ((ct.getTransactionType()).equals("Incoming Stock")){
                                String am = ct.getAmount();

                                if (am.length() >= 2 && am.charAt(0) == '"'){
                                    String changedAmount = am.replaceAll("^\"|\"$", "");
                                    balanceAmount -= Double.parseDouble(changedAmount);

                                } else {
                                    balanceAmount -= Double.parseDouble(ct.getAmount());
                                }
                            } else {

                                String amt = ct.getAmount();
                                if (amt.length() >= 2 && amt.charAt(0) == '"'){
                                    String changedAmount = amt.replaceAll("^\"|\"$", "");
                                    balanceAmount += Double.parseDouble(changedAmount);

                                } else {
                                    balanceAmount += Double.parseDouble(ct.getAmount());
                                }
                            }
                            c.setAmount(ct.getAmount());
                            c.setDescription(ct.getDescription());
                            c.setDate(ct.getDate());
                            c.setTransactionFund(ct.getTransactionFund());
                            c.setTransactionType(ct.getTransactionType());
                            c.setBalance(balanceAmount);
                            cashAndBanks.add(c);
                            setUpRecyclerView(cashAndBanks, layoutManager, recyclerView);
                        }

                        if (s.equals("Bank")) {
                            CashAndBank c = new CashAndBank();

                            if ((ct.getTransactionType()).equals("Incoming Stock")){
                                String am = ct.getAmount();

                                if (am.length() >= 2 && am.charAt(0) == '"'){
                                    String changedAmount = am.replaceAll("^\"|\"$", "");
                                    bAmount -= Double.parseDouble(changedAmount);

                                } else {
                                    bAmount -= Double.parseDouble(ct.getAmount());
                                }
                            } else {

                                String amt = ct.getAmount();
                                if (amt.length() >= 2 && amt.charAt(0) == '"'){
                                    String changedAmount = amt.replaceAll("^\"|\"$", "");
                                    bAmount += Double.parseDouble(changedAmount);

                                } else {
                                    bAmount += Double.parseDouble(ct.getAmount());
                                }
                            }

                            c.setAmount(ct.getAmount());
                            c.setDescription(ct.getDescription());
                            c.setDate(ct.getDate());
                            c.setTransactionFund(ct.getTransactionFund());
                            c.setTransactionType(ct.getTransactionType());
                            c.setBalance(bAmount);
                            cashAndBanks2.add(c);
                            setUpRecyclerView(cashAndBanks2, layoutManager2, recyclerView2);
                        }
                    }

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }


    public void searchByDate(LinearLayoutManager layoutManager,
                             RecyclerView recyclerView, String cashAndBankSwitch) {

        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);

        String sd = startDate.getText().toString().trim();
        String ed = endDate.getText().toString().trim();

        if (cashAndBanksSearch != null) {

            ArrayList<CashAndBank> tDate = new ArrayList<>();
            long dateInMilliseconds = 0;
            long dateInMillisecondsA = 0;
            long dateInMillisecondsB = 0;


            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = null;
            Date sDate = null;

            try {
                sDate = dateFormat1.parse(sd);
                Date sSdate = dateFormat1.parse(ed);
                dateInMillisecondsA = sDate.getTime();
                dateInMillisecondsB = sSdate.getTime();
                Log.i("FROM-AND-TO DATE", "" + sDate + " " + dateInMillisecondsA + "" + dateInMillisecondsB);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (CashAndBank t : cashAndBanksSearch) {
                String sF = t.getTransactionFund();
                if (sF.equals(cashAndBankSwitch)) {
                    String sD = t.getDate();

                    Log.i("SUBMIT A", "I am responding" + sd + ed);

                    try {
                        startDate = dateFormat.parse(sD);
                        dateInMilliseconds = startDate.getTime();
                        Log.i("DATA DATE", "" + dateInMilliseconds + " " + dateInMillisecondsA + " "+ dateInMillisecondsB);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (dateInMillisecondsA <= dateInMilliseconds && dateInMillisecondsB >= dateInMilliseconds) {
                        tDate.add(t);
                        setUpRecyclerView(tDate, layoutManager, recyclerView);
                        cashAndBankSwitch = "";

                        Log.i("SUBMIT B", "I am responding");
                    }
                }
            }
        }
    }


    private void setUpRecyclerView(ArrayList<CashAndBank> cb, LinearLayoutManager layoutManager, RecyclerView recyclerView) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        cashAndBankAdapter = new CashAndBankAdapter(getApplicationContext(), cb);
        recyclerView.setAdapter(cashAndBankAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);

        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search by any parameter");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cashAndBankAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}