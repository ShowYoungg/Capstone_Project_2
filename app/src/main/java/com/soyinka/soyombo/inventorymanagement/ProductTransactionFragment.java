package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;


public class ProductTransactionFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText date, supplier, quantity, salesPrice, costPrice, totalPrice, discount;
    private Button qAddition, qSubtraction, saveDetails, conFirmPrice;
    private RadioButton incomingStock, outgoingStock;
    private Spinner spinner;
    private Date currentDate;
    private ImageView productImage;
    private String imagePath, nameProduct, totalAmount, updatedQuantity, updatedAmount,
            initialStateOfRadioButton_BeforeUpdate, filePath, dateString;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private int totalPurchases, totalSales, position, totalPurchaseValue, totalSalesValue;
    private int id, month, monthlyOpening;
    Object s;
    AppDatabase productDB;


    public ProductTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_transaction,
                container, false);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.LEFT);
            slide.addTarget(R.id.frag_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(getContext(),
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getActivity().getWindow().setEnterTransition(slide);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        productDB = AppDatabase.getInstance(getContext());

//        productDB = Room.databaseBuilder(getContext(), AppDatabase.class, "Inventory Database")
//                .fallbackToDestructiveMigration().build();

        date = rootView.findViewById(R.id.editText);
        supplier = rootView.findViewById(R.id.supplier);
        quantity = rootView.findViewById(R.id.quantity_text);
        salesPrice = rootView.findViewById(R.id.sales_price);
        costPrice = rootView.findViewById(R.id.cost_price);
        qAddition = rootView.findViewById(R.id.addition);
        qSubtraction = rootView.findViewById(R.id.subtraction);
        productImage = rootView.findViewById(R.id.product_image);
        saveDetails = rootView.findViewById(R.id.save_details);
        incomingStock = rootView.findViewById(R.id.radioButton1);
        outgoingStock = rootView.findViewById(R.id.radioButton2);
        spinner = rootView.findViewById(R.id.cash_credit_spinner);
        discount = rootView.findViewById(R.id.discount);
        totalPrice = rootView.findViewById(R.id.total_price);
        conFirmPrice = rootView.findViewById(R.id.total_price_title);

        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            LocalDateTime now = LocalDateTime.now();
            dateString = dtf.format(now);
            date.setText(String.valueOf(dtf.format(now)));
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            dateString = dateFormat.format(new Date());
            date.setText(String.valueOf(dateFormat.format(new Date())));
        }

        String firstChar = String.valueOf(dateString.charAt(0));
        String secondChar = String.valueOf(dateString.charAt(1));
        month = Integer.parseInt(firstChar) + Integer.parseInt(secondChar);

        quantity.setText("0");

        Bundle bundle = getArguments();
        if (bundle != null) {
            final Transaction[] tl1 = new Transaction[1];
            imagePath = bundle.getString("ImagePath");
            nameProduct = bundle.getString("ProductName");
            position = bundle.getInt("Position");
            if (position == 0) {
                Toast.makeText(getContext(), "From the interface" + position,
                        Toast.LENGTH_LONG).show();
            } else if (position == 45) {
                final Transaction tl = bundle.getParcelable("Transaction");
                if (tl != null) {
                    id = tl.getId();
                    nameProduct = tl.getProductName();

                    saveDetails.setText("Update");
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            tl1[0] = productDB.transactionDao().loadById(id);

                            supplier.setText(tl1[0].getSupplier());
                            date.setText(tl1[0].getDate());
                            costPrice.setText(tl1[0].getCostPrice());
                            salesPrice.setText(tl1[0].getSalesPrice());
                            quantity.setText(tl1[0].getQuantity());
                            updatedQuantity = tl1[0].getQuantity();
                            updatedAmount = tl1[0].getTotalAmount();
                            productImage.setImageResource(R.drawable.office);
                            if ((tl1[0].getTransactionType()).equals("Incoming Stock")) {
                                incomingStock.setChecked(true);
                                initialStateOfRadioButton_BeforeUpdate = "Incoming Stock";
                            } else {
                                outgoingStock.setChecked(true);
                                initialStateOfRadioButton_BeforeUpdate = "Outgoing Stock";
                            }

                        }
                    });
                }
            }
        }

        if (sharedPreferences != null) {
            totalPurchases = sharedPreferences.getInt("Total Purchases" + nameProduct, 0);
            totalSales = sharedPreferences.getInt("Total Sales" + nameProduct, 0);
            totalPurchaseValue = sharedPreferences.getInt("Total Purchase Value" + nameProduct, 0);
            totalSalesValue = sharedPreferences.getInt("Total Sales Value" + nameProduct, 0);

        } else {
            totalPurchases = 0;
            totalSales = 0;
            totalSalesValue = 0;
            totalPurchaseValue = 0;
        }


        if (imagePath != null) {
            Toast.makeText(getContext(), "Image path is not null: ",
                    Toast.LENGTH_LONG).show();
            productImage.setImageURI(Uri.parse(imagePath));
        } else {
            productImage.setImageResource(R.drawable.office);
        }

        qSubtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = quantity.getText().toString().trim();
                if (!q.equals("")) {
                    int i = Integer.parseInt(q);
                    if (i >= 1) {
                        i -= 1;
                        quantity.setText(String.valueOf(i));
                    }
                }
            }
        });

        qAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = quantity.getText().toString().trim();
                if (!q.equals("")) {
                    int i = Integer.parseInt(q);
                    if (i >= 0) {
                        i += 1;
                        quantity.setText(String.valueOf(i));
                    }
                }
            }
        });

        List<String> list = new ArrayList<>();

        list.add("Cash");
        list.add("Credit");
        list.add("Bank");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        Log.i("NAMESSABOVE", "this is the name: " + nameProduct);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase(date, supplier, quantity, salesPrice,
                        costPrice, incomingStock, outgoingStock, nameProduct);
            }
        });

        conFirmPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getDataFromFile();
                confirmPriceData();
            }
        });

        return rootView;
    }


    private void confirmPriceData() {
        String d = discount.getText().toString().trim();
        String q = quantity.getText().toString().trim();
        String sales = salesPrice.getText().toString().trim();
        String cost = costPrice.getText().toString().trim();

        boolean a = incomingStock.isChecked();
        boolean b = outgoingStock.isChecked();
        String in = null;

        if (a) {
            in = "Incoming Stock";
        } else {
            in = "Outgoing Stock";
        }

        if (!q.equals("") && !sales.equals("") && !cost.equals("")) {
            int sales_Price = (Integer.parseInt(q)) * (Integer.parseInt(sales));
            int costPrice = (Integer.parseInt(q)) * (Integer.parseInt(cost));
            if (d.equals("")) {
                d = "0";
            }
            int discountValue = Integer.parseInt(d);

            if (sales_Price <= costPrice) {
                Toast.makeText(getContext(), "Sales Price should be greater than Cost Price," +
                        " did you give discount?", Toast.LENGTH_SHORT).show();
            } else {

                if (in.equals("Incoming Stock")) {
                    totalAmount = String.valueOf(costPrice - discountValue);
                } else {
                    totalAmount = String.valueOf(sales_Price - discountValue);
                }

                totalPrice.setText(totalAmount);
            }
        }
    }


    int i = 0;

    public void saveToDatabase(EditText date, EditText supplier, EditText quantity,
                               EditText salesPrice, EditText costPrice,
                               RadioButton incomingStock, RadioButton outgoingStock, String nameOfProduct) {


        i = i + 1;
        String dates, date1 = null, suppliers, quantities, salesPrices, costPrices, transactionFund, inSt = null;
        Boolean inStocks, outStocks;
        dates = date.getText().toString().trim();
        Date d = new Date();
        suppliers = supplier.getText().toString().trim();
        quantities = quantity.getText().toString().trim();
        salesPrices = salesPrice.getText().toString().trim();
        costPrices = costPrice.getText().toString().trim();
        inStocks = incomingStock.isChecked();
        outStocks = outgoingStock.isChecked();

        if (inStocks) {
            inSt = "Incoming Stock";
        } else {
            inSt = "Outgoing Stock";
        }

        if (salesPrices.equals("") || costPrices.equals("")) {
            salesPrices = "0";
            costPrices = "0";
        }

        confirmPriceData();

        if ((Integer.parseInt(salesPrices) < Integer.parseInt(costPrices)) || quantities.equals("0")) {
            if (quantities.equals("0")) {
                Toast.makeText(getContext(), "Quantity cannot be zero", Toast.LENGTH_SHORT).show();
                getActivity().recreate();
            }

            if ((Integer.parseInt(salesPrices) < Integer.parseInt(costPrices))) {
                //Toast.makeText(getContext(), "Sales Price cannot be less than Cost Price", Toast.LENGTH_SHORT).show();
                Snackbar.make(getView(), "Sales Price cannot be less than Cost Price", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getActivity().recreate();
            }
        } else if (dates.equals("") || quantities.equals("") || salesPrices.equals("")
                || suppliers.equals("") || costPrices.equals("") || inSt.equals("") ||
                salesPrices.equals("0") || costPrices.equals("0")) {
            Toast.makeText(getContext(), "One or more of the field(s) are empty", Toast.LENGTH_SHORT).show();
            getActivity().recreate();
        } else if (!inStocks && !outStocks) {
            Snackbar.make(getView(), "Incoming or Outgoing?, Please select the transaction type", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            getActivity().recreate();
        } else if (totalPurchases < Integer.parseInt(quantities) && inSt.equals("Outgoing Stock")) {
            MainActivity.remindUserOfStockOut(getContext(),
                    "Some stock categories re-order level reached, replenish now",
                    "Inventory is low");

            Toast.makeText(getContext(), "You have not enough stock of this category in store, replenish stock", Toast.LENGTH_SHORT).show();
            getActivity().recreate();
        } else {

            final Transaction transaction = new Transaction();
            transaction.setDate(dates);
            transaction.setSupplier(suppliers);
            transaction.setQuantity(quantities);
            transaction.setSalesPrice(salesPrices);
            transaction.setCostPrice(costPrices);
            transaction.setUserId(i);
            transaction.setTransactionType(inSt);
            transaction.setTransactionFund(String.valueOf(s));
            transaction.setProductName(nameOfProduct);
            transaction.setTotalAmount(totalAmount);
            Log.i("VALID HERE ", nameOfProduct + transaction.getProductName() + transaction.getTransactionType());

            Toast.makeText(getContext(), "Testing Testing Testing", Toast.LENGTH_SHORT).show();

            final CashAndBank cashAndBank = new CashAndBank();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                Date datem = df.parse(dates);
                //datem = df.parse(dates)
                date1 = df.format(datem);
                transaction.setDat(datem);
                cashAndBank.setDat(datem);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Please leave the date in the previous format ", Toast.LENGTH_SHORT).show();
                return;
            }

            cashAndBank.setDate(date1);
            cashAndBank.setDescription(suppliers);
            cashAndBank.setAmount(totalAmount);
            cashAndBank.setTransactionFund(String.valueOf(s));
            cashAndBank.setTransactionType(inSt);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (position == 45) {
                if (inSt.equals("Incoming Stock") && initialStateOfRadioButton_BeforeUpdate.equals("Incoming Stock")) {

                    editor.putInt("Total Purchases" + nameOfProduct, totalPurchases
                            + Integer.parseInt(quantities) - Integer.parseInt(updatedQuantity));
                    editor.putInt("Total Purchase Value" + nameOfProduct, totalPurchaseValue
                            + Integer.parseInt(totalAmount) - Integer.parseInt(updatedAmount));

                    transaction.setTotalPurchases(totalPurchases + Integer.parseInt(quantities) - Integer.parseInt(updatedQuantity));
                    transaction.setTotalSales(totalSales);

                } else if (inSt.equals("Outgoing Stock") && initialStateOfRadioButton_BeforeUpdate.equals("Outgoing Stock")) {

                    editor.putInt("Total Sales" + nameOfProduct, totalSales
                            + Integer.parseInt(quantities) - Integer.parseInt(updatedQuantity));
                    editor.putInt("Total Sales Value" + nameOfProduct, totalSalesValue
                            + (Integer.parseInt(quantities) * Integer.parseInt(costPrices)) - (Integer.parseInt(updatedQuantity) * Integer.parseInt(costPrices)));

                    transaction.setTotalPurchases(totalSales + Integer.parseInt(quantities) - Integer.parseInt(updatedQuantity));
                    transaction.setTotalSales(totalPurchases);


                } else if (inSt.equals("Incoming Stock") && initialStateOfRadioButton_BeforeUpdate.equals("Outgoing Stock")) {

                    editor.putInt("Total Purchases" + nameOfProduct, totalPurchases + Integer.parseInt(quantities));
                    editor.putInt("Total Sales" + nameOfProduct, totalSales - Integer.parseInt(updatedQuantity));

                    editor.putInt("Total Sales Value" + nameOfProduct, totalSalesValue - (Integer.parseInt(updatedQuantity) * Integer.parseInt(costPrices)));
                    editor.putInt("Total Purchase Value" + nameOfProduct, totalPurchaseValue + Integer.parseInt(updatedAmount));

                    transaction.setTotalPurchases(totalPurchases + Integer.parseInt(quantities));
                    transaction.setTotalSales(totalSales - Integer.parseInt(updatedQuantity));

                } else if (inSt.equals("Outgoing Stock") && initialStateOfRadioButton_BeforeUpdate.equals("Incoming Stock")) {

                    editor.putInt("Total Sales" + nameOfProduct, totalSales + Integer.parseInt(quantities));
                    editor.putInt("Total Purchases" + nameOfProduct, totalPurchases - Integer.parseInt(updatedQuantity));

                    editor.putInt("Total Sales Value" + nameOfProduct, totalSalesValue + (Integer.parseInt(updatedQuantity) * Integer.parseInt(costPrices)));
                    editor.putInt("Total Purchase Value" + nameOfProduct, totalPurchaseValue - Integer.parseInt(updatedAmount));

                    transaction.setTotalPurchases(totalPurchases - Integer.parseInt(updatedQuantity));
                    transaction.setTotalSales(totalSales + Integer.parseInt(quantities));
                }

            } else {
                if (inSt.equals("Incoming Stock")) {
                    editor.putInt("Total Purchases" + nameOfProduct, totalPurchases + Integer.parseInt(quantities));
                    editor.putInt("Total Purchase Value" + nameOfProduct, totalPurchaseValue + Integer.parseInt(totalAmount));

                    transaction.setTotalPurchases(totalPurchases + Integer.parseInt(quantities));
                    transaction.setTotalSales(0);
                } else {
                    editor.putInt("Total Sales" + nameOfProduct, totalSales + Integer.parseInt(quantities));
                    editor.putInt("Total Sales Value" + nameOfProduct, totalSalesValue + (Integer.parseInt(quantities) * Integer.parseInt(costPrices)));

                    transaction.setTotalSales(totalSales + Integer.parseInt(quantities));
                    transaction.setTotalPurchases(0);
                }
            }

            editor.apply();

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (position != 45) {
                        productDB.transactionDao().insertData(transaction);
                        productDB.cashAndBankDao().insertFund(cashAndBank);
                    } else {
                        transaction.setId(id);
                        cashAndBank.setId(id);
                        productDB.transactionDao().updateData(transaction);
                        productDB.cashAndBankDao().updateFund(cashAndBank);
                    }
                }
            });
            Toast.makeText(getContext(), "Data Saved into Database",
                    Toast.LENGTH_SHORT).show();

            getActivity().finish();
            //startActivity(new Intent(getContext(), InventoryManagementActivity.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        s = parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
