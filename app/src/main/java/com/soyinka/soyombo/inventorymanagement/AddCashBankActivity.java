package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class AddCashBankActivity extends AppCompatActivity {

    private EditText dateField, descriptionField, amountField;
    private RadioButton bank, cash, payment, collection;
    private String dateValue, description, amount, transactionType= "d", transactionFund = "d";
    private boolean bankValue, cashValue;
    private InterstitialAd interstitialAd;
    private Button submit;
    AppDatabase productDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash_bank);

        //productDB = AppDatabase.getInstance(getContext());
        productDB = Room.databaseBuilder(this, AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        dateField = findViewById(R.id.date_field);
        descriptionField = findViewById(R.id.description_field);
        amountField = findViewById(R.id.amoumt_field);
        bank = findViewById(R.id.radio1);
        cash = findViewById(R.id.radio2);
        payment = findViewById(R.id.radio3);
        collection = findViewById(R.id.radio4);
        submit = findViewById(R.id.submit_cash_bank);

        MobileAds.initialize(this, "ca-app-pub-2081307953269103~6353074998");
        AdView mAdView = findViewById(R.id.adViewa);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2081307953269103/4364064262");
        interstitialAd.loadAd(new AdRequest.Builder().build());


        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {

            Intent data = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            //Intent data = intent.getParcelableExtra("data");
            if (data != null) {
                finish();
                startActivity(new Intent(this, InventoryManagementActivity.class)
                        .putExtra(Intent.EXTRA_INTENT, data));
            }
        }

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

        if (payment.isChecked()){
            transactionType = "Incoming Stock";
        } else if (collection.isChecked()){
            transactionType = "Outgoing Stock";
        }

        final CashAndBank cashAndBank = new CashAndBank();
        cashAndBank.setAmount(amount);
        //cashAndBank.setDate(dateValue);
        cashAndBank.setDescription(description);
        cashAndBank.setTransactionType(transactionType);
        cashAndBank.setTransactionFund(transactionFund);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = simpleDateFormat.parse(dateValue);
            Log.i("TTTT", String.valueOf(date));
            String da = simpleDateFormat.format(date);
            cashAndBank.setDate(da);
            cashAndBank.setDat(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.enter_valid_date_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if ((!amount.equals("") && !dateValue.equals("") && !description.equals("")
                && !transactionFund.equals("") && !transactionType.equals(""))
                && (!transactionType.equals("d") && !transactionFund.equals("d"))){

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    productDB.cashAndBankDao().insertFund(cashAndBank);
                }
            });
            Toast.makeText(AddCashBankActivity.this, R.string.transaction_completed,
                    Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, CashAndBankActivity.class));
        } else {
            Toast.makeText(AddCashBankActivity.this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, CashAndBankActivity.class));
    }
}
