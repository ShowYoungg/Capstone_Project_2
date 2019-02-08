package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfitAndLossActivity extends AppCompatActivity {
    private TextView salesView, purchasesView, openingStockView, closingStockView, salesExpensesView,
            otherExpensesView, netProfitView, grossProfitView, additionOfCostView, gProfit, nProfit, dialogTextView;
    private String productName, sales, purchases, openingStock, closingStock, netProfit, grossProfit;
    private int purchasesBalance, purchasesBalanceX, salesBalance, salesExpenses, otherExpenses, additionOfCost = 0,
            sExpensesHolder = 0, oExpensesHolder = 0, totalPurchaseValue, totalSalesValue, i,
            newValue, stockValueBeforeSales = 0, stockValueBeforeSalesX = 0,
            reportIdentifier = -1;

    private double salesExpensesB, otherExpensesB, purchasesBalanceB,
            salesBalanceB, additionOfCostB = 0, sExpensesHolderB = 0,
            oExpensesHolderB = 0, newValueB, oStock, stockValueBeforeSalesB;
    private AlertDialog alertDialog;
    private EditText dialogEditText;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private Button firstQuarter, secondQuarter, fullYear, thirdQuarter, fourtQuater;
    private String[] names;
    private List<Transaction> transactionArrayList;
    private List<Transaction> transactionArrayList1;
    private List<Transaction> transactionArrayList2;
    private List<SalesCostAndExpenses> expensesArrayList;
    private InterstitialAd interstitialAd;
    AppDatabase productDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit_and_loss);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.LEFT);
            slide.addTarget(R.id.profit_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        List<Transaction> transactionArrayList = new ArrayList<>();
        List<Transaction> transactionArrayList1 = new ArrayList<>();
        List<Transaction> transactionArrayList2 = new ArrayList<>();
        List<SalesCostAndExpenses> expensesArrayList = new ArrayList<>();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544-3347511713");
        AdView mAdView = findViewById(R.id.adView4);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        salesView = findViewById(R.id.sales);
        purchasesView = findViewById(R.id.purchases);
        openingStockView = findViewById(R.id.opening_stock);
        closingStockView = findViewById(R.id.closing_stock);
        salesExpensesView = findViewById(R.id.sales_expenses);
        otherExpensesView = findViewById(R.id.other_expenses);
        netProfitView = findViewById(R.id.net_profit);
        grossProfitView = findViewById(R.id.gross_profit);
        additionOfCostView = findViewById(R.id.addition_of_costofsales);
        firstQuarter = findViewById(R.id.q1);
        secondQuarter = findViewById(R.id.q2);
        fullYear = findViewById(R.id.all_quarters);
        thirdQuarter = findViewById(R.id.q3);
        gProfit = findViewById(R.id.gprofit);
        nProfit = findViewById(R.id.nprofit);

        purchasesBalance = 0;
        salesBalance = 0;
        salesExpenses = 0;
        otherExpenses = 0;
        totalPurchaseValue = 0;
        totalSalesValue = 0;
        purchasesBalanceX = 0;

        if (savedInstanceState == null) {
            getOpeningStock();
        }

        thirdQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yyyy = getYyyy();

                Log.i("YEAR", "" + yyyy);

                final String firstDayOfTheMonth = "01/01/" + yyyy;
                final String endDayOfTheQuarter = "09/30/" + yyyy;

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });

        secondQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yyyy = getYyyy();

                Log.i("YEAR", "" + yyyy);

                final String firstDayOfTheMonth = "01/01/" + yyyy;
                final String endDayOfTheQuarter = "06/30/" + yyyy;

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });

        firstQuarter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String yyyy = getYyyy();

                Log.i("YEAR", "" + yyyy);

                final String firstDayOfTheMonth = "01/01/" + yyyy;
                final String endDayOfTheQuarter = "03/31/" + yyyy;

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });

        fullYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String yyyy = getYyyy();

                Log.i("YEAR", "" + yyyy);

                final String firstDayOfTheMonth = "01/01/" + yyyy;
                final String endDayOfTheQuarter = "12/31/" + yyyy;

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });
    }


    @Nullable
    private String getYyyy() {
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
            yyyy = "" + year.charAt(6) + year.charAt(7) + year.charAt(8) + year.charAt(9);
        }
        return yyyy;
    }

    private void getOpeningStock() {

        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.opening_stock_dialog, null);
        TextView dialogTextView1 = view.findViewById(R.id.opening_stock_info);
        Button button = view.findViewById(R.id.opening_stock_button);
        final EditText dialogEditText1 = view.findViewById(R.id.opening_stock_value);
        alertDialogBuilder1.setView(view);
        alertDialogBuilder1.setCancelable(false);
        alertDialogBuilder1.setTitle("Let's begin from last year");
        alertDialogBuilder1.setIcon(R.drawable.ic_notifications_black_24dp);

        alertDialogBuilder1.create();
        alertDialogBuilder1.setNegativeButton("No Stock", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String yyyy = getYyyy();
                Log.i("YEAR", "" + yyyy);
                String firstDayOfTheMonth = "01/01/" + yyyy;
                String endDayOfTheQuarter = "12/31/" + yyyy;
                searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);

                dialog.cancel();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogEditText1 != null) {
                    String openingStock = dialogEditText1.getText().toString().trim();
                    Log.i("SSFF12", "ostock is " + openingStock);
                    if (!openingStock.equals("")) {
                        oStock = Double.parseDouble(openingStock);
                        Log.i("SSFF", "ostock is " + oStock);

                        String yyyy = getYyyy();
                        Log.i("YEAR", "" + yyyy);
                        String firstDayOfTheMonth = "01/01/" + yyyy;
                        String endDayOfTheQuarter = "12/31/" + yyyy;
                        searchByMonth(firstDayOfTheMonth, endDayOfTheQuarter);

                        alertDialog.cancel();
                    } else {
                        Toast.makeText(ProfitAndLossActivity.this,
                                "Please input an amount or press 'No' to cancel", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alertDialog = alertDialogBuilder1.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("i", i);
        outState.putDouble("newValueB", newValueB);
        outState.putDouble("oExpensesHolderB", oExpensesHolderB);
        outState.putDouble("salesBalanceB", salesBalanceB);
        outState.putDouble("sExpensesHolderB", sExpensesHolderB);
        outState.putDouble("purchaseBalanceB", purchasesBalanceB);
        outState.putDouble("otherExpensesB", otherExpensesB);
        outState.putDouble("salesExpensesB", salesExpensesB);
        outState.putDouble("stockValueBeforeSalesB", stockValueBeforeSalesB);
        outState.putInt("stockValueBeforeSales", stockValueBeforeSales);
        outState.putDouble("oStock", oStock);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        i = savedInstanceState.getInt("i");

        newValueB = savedInstanceState.getDouble("newValueB");
        oExpensesHolderB = savedInstanceState.getDouble("oExpensesHolderB");
        salesBalanceB = savedInstanceState.getDouble("salesBalanceB");
        sExpensesHolderB = savedInstanceState.getDouble("sExpensesHolderB");
        purchasesBalanceB = savedInstanceState.getDouble("purchaseBalanceB");
        otherExpensesB = savedInstanceState.getDouble("otherExpensesB");
        salesExpensesB = savedInstanceState.getDouble("salesExpensesB");
        stockValueBeforeSalesB = savedInstanceState.getDouble("stockValueBeforeSalesB");
        oStock = savedInstanceState.getDouble("oStock");

        otherExpensesView.setText("(" + String.valueOf(BigDecimal.valueOf(otherExpensesB / 1000)) + ")");
        salesExpensesView.setText(String.valueOf(BigDecimal.valueOf(salesExpensesB / 1000)));
        purchasesView.setText(String.valueOf(BigDecimal.valueOf(purchasesBalanceB / 1000)));
        salesView.setText(String.valueOf(BigDecimal.valueOf(salesBalanceB / 1000)));
        openingStockView.setText(String.valueOf(BigDecimal.valueOf(oStock / 1000)));
        closingStockView.setText("(" + String.valueOf(BigDecimal.valueOf((purchasesBalanceB - stockValueBeforeSalesB) / 1000)) + ")");
        additionOfCostView.setText("(" + String.valueOf(BigDecimal.valueOf(newValueB / 1000)) + ")");
        double io = salesBalanceB - newValueB;
        openingStockView.setText(String.valueOf(BigDecimal.valueOf(oStock / 1000)));
        grossProfitView.setText(String.valueOf(BigDecimal.valueOf(io / 1000)));
        netProfitView.setText(String.valueOf(BigDecimal.valueOf((io - oExpensesHolderB) / 1000)));
    }


    public void searchByMonth(String startDate, String endDate) {

        //reportIdentifier = 11;

        salesExpensesB = 0;
        otherExpensesB = 0;
        purchasesBalanceB = 0;
        salesBalanceB = 0;
        additionOfCostB = 0;
        stockValueBeforeSalesB = 0;
        //oStock = 0;
        newValueB = 0;
        sExpensesHolderB = 0;
        oExpensesHolderB = 0;

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
                expensesArrayList = productDB.expensesDao().loadExpensesByDate(finalSsDate, finalEeDate);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (expensesArrayList != null) {
                    for (SalesCostAndExpenses t : expensesArrayList) {
                        String transactionType = t.getNature();
                        if (transactionType.equals("Cost of Sales")) {
                            String amount = t.getAmount();
                            double amountValue = Double.parseDouble(amount);
                            salesExpensesB += amountValue;
                        } else {
                            String amnt = t.getAmount();
                            double amountVal = Double.parseDouble(amnt);
                            otherExpensesB += amountVal;
                        }
                    }
                    sExpensesHolderB += salesExpensesB;
                    oExpensesHolderB += otherExpensesB;
                    otherExpensesView.setText("(" + String.valueOf(BigDecimal.valueOf(otherExpensesB / 1000)) + ")");
                    salesExpensesView.setText(String.valueOf(BigDecimal.valueOf(salesExpensesB / 1000)));
                }

                if (transactionArrayList != null) {
                    for (Transaction t : transactionArrayList) {
                        Log.i("STDETAILS", "" + t.getTotalPurchases() + " " + t.getTotalSales());
                        String transactionType = t.getTransactionType();
                        if (transactionType.equals("Incoming Stock")) {
                            String amount = t.getTotalAmount();
                            double amountValue = Double.parseDouble(amount);
                            purchasesBalanceB += amountValue;
                        } else {
                            String amnt = t.getTotalAmount();
                            double amountVal = Double.parseDouble(amnt);
                            salesBalanceB += amountVal;
                            double costPrice = Double.parseDouble(t.getCostPrice());
                            double q = Double.parseDouble(t.getQuantity());
                            double stockValueOfSales = costPrice * q;
                            //int a = Integer.parseInt(t.getCostPrice()) * Integer.parseInt(t.getQuantity());
                            stockValueBeforeSalesB += stockValueOfSales;
                        }
                    }

                    purchasesView.setText(String.valueOf(BigDecimal.valueOf(purchasesBalanceB / 1000)));
                    salesView.setText(String.valueOf(BigDecimal.valueOf(salesBalanceB / 1000)));

                    Log.i("ANA", "" + purchasesBalance + " " + stockValueBeforeSales + " " + purchasesBalanceB
                            + " " + stockValueBeforeSalesB);
                    openingStockView.setText(String.valueOf(BigDecimal.valueOf(oStock / 1000)));
                    closingStockView.setText("(" + String.valueOf(BigDecimal.valueOf((purchasesBalanceB - stockValueBeforeSalesB ) / 1000)) + ")");

                    newValueB = oStock + purchasesBalanceB - (purchasesBalanceB - stockValueBeforeSalesB) + sExpensesHolderB;
                    additionOfCostView.setText("(" + String.valueOf(BigDecimal.valueOf(newValueB / 1000)) + ")");

                    double io = salesBalanceB - newValueB;

                    if (salesBalanceB < newValueB) {
                        gProfit.setText("Gross Loss");
                        nProfit.setText("Net Loss");
                    }

                    grossProfitView.setText(String.valueOf(BigDecimal.valueOf(io / 1000)));
                    netProfitView.setText(String.valueOf(BigDecimal.valueOf((io - oExpensesHolderB) / 1000)));
                }
            }
        }, 150);
    }
}