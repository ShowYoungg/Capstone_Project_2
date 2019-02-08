package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class PayablesAndReceivablesActivity extends AppCompatActivity {

    private List<Transaction> transactionList;
    private List<Transaction> transactionList1;
    private List<Transaction> transactionArrayList1;
    private List<Transaction> transactionArrayList2;
    private RecyclerView recyclerView;
    private HistoryAndRecordAdapter payablesAndReceivables;
    private TextView textView;
    private String accountType;
    private Button twoMonths, recent, cancel;
    AppDatabase productDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payables_and_receivables);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.LEFT);
            slide.addTarget(R.id.expenses_slide2);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        transactionList = new ArrayList<>();
        transactionList1 = new ArrayList<>();
        transactionArrayList1 = new ArrayList<>();
        transactionArrayList2 = new ArrayList<>();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textView = findViewById(R.id.account_payables_receivables_title);
        recyclerView = findViewById(R.id.account_payables_receivables);
        twoMonths = findViewById(R.id.two_month);
        recent = findViewById(R.id.recent);
        cancel = findViewById(R.id.cancel);

        Intent i = getIntent();
        if (i != null) {
            accountType = i.getStringExtra("accounttype");
            Log.v("accounttype", accountType);
            if (accountType.equals("payables") && textView != null) {
                textView.setText("Account Payables");
            } else if (accountType.equals("receivables") && textView != null) {
                textView.setText("Account Receivables");
            }
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(PayablesAndReceivablesActivity.this, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getProductTransaction().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> categoryTransaction) {
                if (categoryTransaction != null) {
                    transactionList.addAll(categoryTransaction);
                    categoryTransaction.clear();
                    for (Transaction t : transactionList) {
                        if ((t.getTransactionFund().equals("Credit"))) {
                            Log.i("AccountFund", t.getTransactionFund());
                            if ((t.getTransactionType().equals("Incoming Stock"))
                                    && accountType.equals("payables")) {
                                //categoryTransaction.add(t);
                                transactionArrayList1.add(t);
                                payablesAndReceivables = new HistoryAndRecordAdapter(PayablesAndReceivablesActivity.this,
                                        transactionArrayList1);
                                recyclerView.setAdapter(payablesAndReceivables);
                            } else if ((t.getTransactionType().equals("Outgoing Stock"))
                                    && accountType.equals("receivables")) {
                                //categoryTransaction.add(t);
                                transactionArrayList2.add(t);
                                payablesAndReceivables = new HistoryAndRecordAdapter(PayablesAndReceivablesActivity.this,
                                        transactionArrayList2);
                                recyclerView.setAdapter(payablesAndReceivables);
                            }
                        } else {


                        }
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Transaction ton : transactionList) {
                    if (accountType.equals("receivables")) {
                        HistoryAndRecordAdapter psa = new HistoryAndRecordAdapter
                                (PayablesAndReceivablesActivity.this, transactionArrayList2);
                        recyclerView.setAdapter(psa);


                    } else if (accountType.equals("payables")){
                        HistoryAndRecordAdapter psa = new HistoryAndRecordAdapter
                                (PayablesAndReceivablesActivity.this, transactionArrayList1);
                        recyclerView.setAdapter(psa);
                    }
                }
            }
        });
        twoMonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String year = null;
                Date yd = null;
                String m = null;
                String firstDay = null;
                String yyyy = null;
                String firstDayOfTheMonth = null;

                try {
                    String ss = dateFormat.format(date);
                    yd = sdf.parse(ss);
                    year = sdf.format(yd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (year != null) {
                    int i;
                    if (!String.valueOf(year.charAt(1)).equals("1")) {
                        i = Integer.parseInt(String.valueOf(year.charAt(1))) - 1;
                        m = String.valueOf(i);
                        firstDay = "01";
                        yyyy = String.valueOf("20" + year.charAt(8) + year.charAt(9));
                        firstDayOfTheMonth = "" + m + "/" + firstDay + "/" + yyyy;
                    } else if (String.valueOf(year.charAt(1)).equals("1")) {
                        i = 12;
                        yyyy = String.valueOf("20" + year.charAt(8) + year.charAt(9));
                        int newYear = Integer.parseInt(String.valueOf(yyyy)) - 1;
                        m = String.valueOf(i);
                        firstDay = "01";
                        firstDayOfTheMonth = "" + m + "/" + firstDay + "/" + newYear;
                    }
                }
                Log.i("SSSSS", firstDayOfTheMonth + "/" + year);
                searchByMonth(firstDayOfTheMonth, year);
            }
        });

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String year = null;
                Date yd = null;
                String m = null;
                String firstDay = null;
                String yyyy = null;

                try {
                    String ss = dateFormat.format(date);
                    yd = sdf.parse(ss);
                    year = sdf.format(yd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (year != null) {
                    m = String.valueOf("" + year.charAt(0) + year.charAt(1));
                    firstDay = "01";
                    yyyy = String.valueOf("20" + year.charAt(8) + year.charAt(9));
                }
                String firstDayOfTheMonth = "" + m + "/" + firstDay + "/" + yyyy;
                searchByMonth(firstDayOfTheMonth, year);
            }
        });


    }

    public void searchByMonth(String startDate, String endDate) {

        final List<Transaction> tListA = new ArrayList<>();
        final List<Transaction> tListB = new ArrayList<>();

        Log.i("DATESS", startDate + " " + endDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        String sDate = null;
        String eDate = null;
        Date ssDate = null;
        Date eeDate = null;

        try {
            ssDate = sdf.parse(startDate);
            eeDate = sdf.parse(endDate);

            sDate = sdf.format(ssDate);
            eDate = sdf.format(eeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Date finalSsDate = ssDate;
        final Date finalEeDate = eeDate;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                transactionList1 = productDB.transactionDao().loadTransactionByDate(finalSsDate, finalEeDate);

            }
        });


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (Transaction tr : transactionList1) {
                    if ((tr.getTransactionFund()).equals("Credit") && (tr.getTransactionType()).equals("Incoming Stock")) {

                        if (accountType.equals("payables")) {
                            tListA.add(tr);
                        }
                    } else if ((tr.getTransactionFund()).equals("Credit") && (tr.getTransactionType()).equals("Outgoing Stock")) {

                        if (accountType.equals("receivables")) {
                            tListB.add(tr);
                        }
                    }
                }

                if (accountType.equals("payables")) {
                    HistoryAndRecordAdapter ps = new HistoryAndRecordAdapter
                            (PayablesAndReceivablesActivity.this, tListA);
                    recyclerView.setAdapter(ps);

                } else {
                    HistoryAndRecordAdapter psa = new HistoryAndRecordAdapter
                            (PayablesAndReceivablesActivity.this, tListB);
                    recyclerView.setAdapter(psa);
                }
            }
        }, 80);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}