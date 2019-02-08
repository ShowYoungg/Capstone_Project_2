package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class SalesJournal extends AppCompatActivity {

    private List<Transaction> transactionList;
    private List<Transaction> transactionList11;
    private List<Transaction> transactionSearch;
    private List<Transaction> transactionArrayList;
    private RecyclerView recyclerView;
    private HistoryAndRecordAdapter salesJournalAdapter;
    private SharedPreferences sharedPreferences;
    private int totalPurchases, totalSales, position, salesValue;
    private String productName, quantity, costPrice, startDateValue, endDateValue;
    private EditText startDate, endDate;
    private InterstitialAd interstitialAd;
    private Button submitButton, twoMonths, recent, cancel;
    private AlertDialog alertDialog;
    AppDatabase productDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_journal);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.LEFT);
            slide.addTarget(R.id.sales_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        transactionList = new ArrayList<>();
        transactionList11 = new ArrayList<>();
        transactionSearch = new ArrayList<>();
        transactionArrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.sale_journal);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        submitButton = findViewById(R.id.send_for_search);
        twoMonths = findViewById(R.id.two_month);
        recent = findViewById(R.id.recent);
        cancel = findViewById(R.id.cancel);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544-3347511713");
        AdView mAdView = findViewById(R.id.adView3);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        startDateValue = startDate.getText().toString().trim();
        endDateValue = endDate.getText().toString().trim();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(SalesJournal.this, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByDate(startDate, endDate);
            }
        });

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        loadSalesData();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                confirmDelete(viewHolder);
            }
        }).attachToRecyclerView(recyclerView);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    recreate();
                }
            }
        });

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                recreate();
                interstitialAd.loadAd(new AdRequest.Builder().build());
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

    private void loadSalesData() {
        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getProductTransaction().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> categoryTransaction) {
                if (categoryTransaction != null) {
                    transactionList.addAll(categoryTransaction);
                    categoryTransaction.clear();
                    for (Transaction t : transactionList) {
                        if ((t.getTransactionType().equals("Outgoing Stock"))) {
                            categoryTransaction.add(t);
                            transactionSearch.add(t);
                            transactionList11.add(t);
                            salesJournalAdapter = new HistoryAndRecordAdapter(SalesJournal.this, transactionList11);
                            recyclerView.setAdapter(salesJournalAdapter);
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
        mSearchView.setQueryHint("Search by supplier");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                salesJournalAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    private void searchByMonth(String startDate, String endDate) {

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
                transactionArrayList = productDB.transactionDao().loadTransactionByDate(finalSsDate, finalEeDate);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (transactionArrayList != null) {
                    transactionList11.clear();
                    for (Transaction t : transactionArrayList) {
                        if ((t.getTransactionType().equals("Outgoing Stock"))) {
                            transactionList11.add(t);
                        }
                    }
                    salesJournalAdapter = new HistoryAndRecordAdapter(SalesJournal.this, transactionList11);
                    recyclerView.setAdapter(salesJournalAdapter);
                }
            }
        }, 150);
    }


    public void confirmDelete(final RecyclerView.ViewHolder viewHolder) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Confirm delete");
        alertDialogBuilder.setIcon(R.drawable.ic_notifications_black_24dp);
        alertDialogBuilder.setMessage("Are you sure you want to delete this transaction? This cannot be undone");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        position = viewHolder.getAdapterPosition();
                        List<Transaction> transactionsToDelete = salesJournalAdapter.getmTransactionList();
                        productName = transactionsToDelete.get(position).getProductName();
                        quantity = transactionsToDelete.get(position).getQuantity();
                        costPrice = transactionsToDelete.get(position).getCostPrice();

                        productDB.transactionDao().deleteData(transactionsToDelete.get(position));

                        if (sharedPreferences != null) {
                            totalSales = sharedPreferences.getInt("Total Sales" + productName, 0);
                            salesValue = sharedPreferences.getInt("Total Sales Value" + productName, 0);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("Total Sales" + productName, totalSales - Integer.parseInt(quantity));
                            editor.putInt("Total Sales Value" + productName, salesValue
                                    - ((Integer.parseInt(costPrice)) * (Integer.parseInt(quantity))));
                            editor.apply();
                        }
                        finish();
                        startActivity(new Intent(SalesJournal.this, SalesJournal.class));
                    }
                });
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(SalesJournal.this, SalesJournal.class));
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void searchByDate(EditText editText, EditText endDateEditText) {

        String sd = editText.getText().toString().trim();
        String ed = endDateEditText.getText().toString().trim();
        if (transactionSearch != null) {

            List<Transaction> tDate = new ArrayList<>();
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

            for (Transaction t : transactionSearch) {
                String sD = t.getDate();

                Log.i("SUBMIT A", "I am responding" + sd + ed);

                try {
                    startDate = dateFormat.parse(sD);
                    dateInMilliseconds = startDate.getTime();
                    Log.i("DATA DATE", "" + startDate + " " + dateInMilliseconds);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (dateInMillisecondsA <= dateInMilliseconds && dateInMillisecondsB >= dateInMilliseconds) {
                    tDate.add(t);
                    Log.i("SUBMIT B", "I am responding");

                    salesJournalAdapter = new HistoryAndRecordAdapter(SalesJournal.this, tDate);
                    recyclerView.setAdapter(salesJournalAdapter);
                } else if (dateInMillisecondsA < dateInMilliseconds && dateInMillisecondsB < dateInMilliseconds) {

                }
            }
        }
    }


}
