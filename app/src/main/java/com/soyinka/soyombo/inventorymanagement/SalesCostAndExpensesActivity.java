package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SalesCostAndExpensesActivity extends AppCompatActivity {

    private EditText dateField, descriptionField, amountField;
    private RadioButton bank, cash, costOfSales, expenses;
    private String dateValue, description, amount, natureOfExpenses = "d", transactionFund = "d";
    private boolean bankValue, cashValue;
    private Button submit;
    private InterstitialAd interstitialAd;
    AppDatabase productDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_cost_and_expenses);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.START);
            slide.addTarget(R.id.s_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        MobileAds.initialize(this, "ca-app-pub-2081307953269103~6353074998");
        AdView mAdView = findViewById(R.id.adViewe);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2081307953269103/4364064262");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        //productDB = AppDatabase.getInstance(getContext());
        productDB = Room.databaseBuilder(this, AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        dateField = findViewById(R.id.date_field1);
        descriptionField = findViewById(R.id.description_field1);
        amountField = findViewById(R.id.amoumt_field1);
        bank = findViewById(R.id.radio11);
        cash = findViewById(R.id.radio21);
        costOfSales = findViewById(R.id.radio31);
        expenses = findViewById(R.id.radio41);
        submit = findViewById(R.id.submit_cash_bank1);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(dateField, descriptionField, amountField, bank, cash);
            }
        });
    }

    private void saveData(EditText dateField, EditText descriptionField, EditText amountField,
                          RadioButton bank, RadioButton cash) {
        dateValue = dateField.getText().toString().trim();
        description = descriptionField.getText().toString().trim();
        amount = amountField.getText().toString().trim();
        bankValue = bank.isChecked();
        cashValue = cash.isChecked();
        if (bankValue){
            transactionFund = "Bank";
        } else if(cashValue) {
            transactionFund = "Cash";
        }
        if (costOfSales.isChecked()){
            natureOfExpenses = "Cost of Sales";
        } else if (expenses.isChecked()){
            natureOfExpenses = "Expenses";
        }

        final SalesCostAndExpenses salesCostAndExpenses = new SalesCostAndExpenses();
        salesCostAndExpenses.setAmount(amount);
        salesCostAndExpenses.setDate(dateValue);
        salesCostAndExpenses.setDescription(description);
        salesCostAndExpenses.setNature(natureOfExpenses);

        final CashAndBank cashAndBank = new CashAndBank();
        cashAndBank.setAmount(amount);
        cashAndBank.setDate(dateValue);
        cashAndBank.setDescription(description);
        cashAndBank.setTransactionType("Incoming Stock");
        cashAndBank.setTransactionFund(transactionFund);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = simpleDateFormat.parse(dateValue);
            Log.i("TTTT", String.valueOf(date));
            salesCostAndExpenses.setDat(date);
            cashAndBank.setDat(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.enter_valid_date_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if ((!amount.equals("") && !dateValue.equals("") && !description.equals("")
                && !transactionFund.equals("") && !natureOfExpenses.equals(""))
                && (!natureOfExpenses.equals("d") && !transactionFund.equals("d")) ){

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    productDB.expensesDao().insertExpenses(salesCostAndExpenses);
                    productDB.cashAndBankDao().insertFund(cashAndBank);
                }
            });

            Toast.makeText(SalesCostAndExpensesActivity.this, R.string.expenses_recorded,
                    Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, ExpensesListActivity.class));
        } else {
            Toast.makeText(SalesCostAndExpensesActivity.this, R.string.field_not_complete, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ExpensesListActivity.class));
    }
}


