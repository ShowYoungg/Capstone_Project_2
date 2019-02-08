package com.soyinka.soyombo.inventorymanagement;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.IntentService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class InventoryManagementActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProductCategory productCategory;
    private ProductCategory findNameFromDatabase;
    private int count;
    private static int totalPurchases, totalSales;
    private File file;
    private String filePath, dateString;
    private ImageView imageView;
    private TextView snap;
    private EditText productCode;
    private EditText productName;
    private EditText productDescription;
    private int totalPurchaseValue, totalSalesValue, quantities, month, newMonth, openingStock, totalStored = 0;
    List<ProductCategory> productCategoriesList1;
    private Button saveButton;
    private AlertDialog dialog;
    private List<ProductCategory> productCategoryList;
    private ProductCategoryAdapter productCategoryAdapter;
    private ListView productList;
    private static SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private final int TAKE_PHOTO_CODE = 1;
    private final int GET_PHOTO_CODE = 2;
    private int onCreateOnStartNotifier = -1;
    private List<String[]> studentss;
    AppDatabase productDB;
    private boolean mTwoPane;
    private Bitmap photo;
    private String user;
    private InterstitialAd interstitialAd;
    public static final String UPDATE_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    private onProductTransactionClickListener mCallback;


    public interface onProductTransactionClickListener {
        void onProductSelected(int clickedPosition, ProductCategory p);
    }

    public InventoryManagementActivity() {
    }


    @SuppressLint("PrivateApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_management);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        productDB = AppDatabase.getInstance(getApplicationContext());
//        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
//                .fallbackToDestructiveMigration().build();

        MobileAds.initialize(this, "ca-app-pub-3940256099942544-3347511713");
        AdView mAdView = findViewById(R.id.adView5);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
        dateString = dateFormat.format(new java.util.Date());
        String firstChar = String.valueOf(dateString.charAt(0));
        String secondChar = String.valueOf(dateString.charAt(1));
        month = Integer.parseInt(firstChar) + Integer.parseInt(secondChar);

        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
        editor2.putInt("month", month);
        editor2.apply();

        if (sharedPreferences2 != null) {
            newMonth = sharedPreferences2.getInt("month", 0);
            openingStock = sharedPreferences2.getInt("openingStock" + newMonth, 0);
            Log.i("SINGLETON", "" + newMonth + " " + month + " " + openingStock);
        } else {
            newMonth = month;
        }

        if (findViewById(R.id.product_frag_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        Intent intent = getIntent();
        if (intent != null) {
            Intent data = intent.getParcelableExtra(Intent.EXTRA_INTENT);
            //Intent data = intent.getParcelableExtra("data");
            int requestCode = intent.getIntExtra("requestcode", -256);
            int resultcode = intent.getIntExtra("resultcode", -257);
            Log.i("CODES FROM CAMERA", " " + requestCode + resultcode);

            if (data != null) {
                if (data.getExtras() != null) {
                    photo = (Bitmap) data.getExtras().get("data");
                }
            }

            user = intent.getStringExtra("user");
        }

        productCategoriesList1 = new ArrayList<>();
        productCategoryList = new ArrayList<>();
        productCategoryAdapter = new ProductCategoryAdapter(this, productCategoriesList1, mTwoPane, mCallback);
        productList = findViewById(R.id.list_item);
        productList.setAdapter(productCategoryAdapter);
        count = 0;


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(InventoryManagementActivity.this,
                        InventoryManagementActivity.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userTextView = headerView.findViewById(R.id.user);
        userTextView.setText("Welcome " + user);

        if (mTwoPane) {
            ProductTransactionFragment p = new ProductTransactionFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.product_frag_container, p).commit();
        }
        if (savedInstanceState == null) {
            productDialog();
        } else {
            loadProductInformation();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (newMonth != month) {
                        SharedPreferences.Editor editor3 = sharedPreferences2.edit();
                        editor3.putInt("openingStock" + newMonth, totalStored);
                        editor3.apply();
                    }
                }
            }, 200);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (onCreateOnStartNotifier != -1) {
            loadProductInformation();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inventory_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.view_history_by_category) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    Main2Activity.class));
        }
        if (id == R.id.view_all_history) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    Main2Activity.class));
        }

        if (id == R.id.signout) {
            AuthUI.getInstance().signOut(this);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    List<String[]> students = new ArrayList<String[]>();

    private void writeToCSV() {

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/Inventory/");
        dir.mkdirs();
        String nameOfcsv = "excel";
        file = new File(dir + "/" + nameOfcsv + ".csv/");

        final String fileName = file.toString();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Path", fileName);
        editor.apply();

        final String[] id = new String[1];
        final String[] name = new String[1];
        final String[] q = new String[1];
        final String[] cost = new String[1];
        final String[] sales = new String[1];
        final String[] supplier = new String[1];
        final String[] amount = new String[1];
        final String[] totalPurchase = new String[1];
        final String[] totalSales = new String[1];
        final String[] transactionFund = new String[1];
        final String[] transactionType = new String[1];
        final String[] date = new String[1];
        final String[] userId = new String[1];
        final String[] dat = new String[1];


        ProductCategoryViewModel productCategoryViewModel1 =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel1.getProductTransaction().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(@Nullable List<Transaction> categoryTransaction) {


                if (categoryTransaction != null) {
                    for (Transaction ct : categoryTransaction) {

                        id[0] = String.valueOf(ct.getId());
                        name[0] = ct.getProductName();
                        q[0] = ct.getQuantity();
                        date[0] = ct.getDate();
                        userId[0] = String.valueOf(ct.getUserId());
                        cost[0] = ct.getCostPrice();
                        sales[0] = ct.getSalesPrice();
                        supplier[0] = ct.getSupplier();
                        amount[0] = ct.getTotalAmount();
                        totalPurchase[0] = String.valueOf(ct.getTotalPurchases());
                        totalSales[0] = String.valueOf(ct.getTotalSales());
                        transactionFund[0] = ct.getTransactionFund();
                        transactionType[0] = ct.getTransactionType();
                        dat[0] = String.valueOf(ct.getDat());

                        studentss = new ArrayList<String[]>();

                        studentss.add(new String[]{date[0], supplier[0], q[0], sales[0],
                                cost[0], userId[0], transactionType[0], transactionFund[0],
                                totalPurchase[0], totalSales[0], name[0], amount[0], dat[0]});

                        students.addAll(studentss);
                    }
                }
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CSVWriter csvWriter = null;
                try {
                    csvWriter = new CSVWriter(new FileWriter(fileName));
                    csvWriter.writeAll(students);
                    csvWriter.close();
                    MainActivity.remindUserOfStockOut(InventoryManagementActivity.this,
                            "Download Complete",
                            "Download Complete");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 300);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                openDownloadedCSV(fileName);
            }
        }, 1000);
    }


    private void openDownloadedCSV(String filePath){
        Uri path = Uri.fromFile(new File(filePath));
        Intent csvIntent = new Intent(Intent.ACTION_VIEW);
        csvIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        csvIntent.setDataAndType(path, "text/csv");
        try{
            startActivity(csvIntent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(this, "Error opening file", Toast.LENGTH_SHORT).show();
        }
    }


    private void restoreFromCSV() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 7);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.export) {
            // Handle the camera action
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            } else {
                writeToCSV();
            }

            interstitialAd.setAdListener( new AdListener(){
                @Override
                public void onAdClosed() {
                    writeToCSV();
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } else if (id == R.id.restore) {
            //Handle share
            //restoreFromCSV();
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            } else {
                restoreFromCSV();
            }

            interstitialAd.setAdListener( new AdListener(){
                @Override
                public void onAdClosed() {
                    restoreFromCSV();
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } else if (id == R.id.share) {
            //Handle share
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            } else {
                shareApp();
            }

            interstitialAd.setAdListener( new AdListener(){
                @Override
                public void onAdClosed() {
                    shareApp();
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });

        } else if (id == R.id.inventory_management1) {

        } else if (id == R.id.cash_bank_statement) {
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            } else {
                startActivity(new Intent(InventoryManagementActivity.this,
                        CashAndBankActivity.class));
            }

            interstitialAd.setAdListener( new AdListener(){
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(InventoryManagementActivity.this,
                            CashAndBankActivity.class));

                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } else if (id == R.id.cost_of_sales_and_expenses1) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    ExpensesListActivity.class));

        } else if (id == R.id.inventory_history) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    Main2Activity.class));

        } else if (id == R.id.price_list) {
            if (interstitialAd.isLoaded()){
                interstitialAd.show();
            } else {
                startActivity(new Intent(InventoryManagementActivity.this,
                        PriceListActivity.class));
            }

            interstitialAd.setAdListener( new AdListener(){
                @Override
                public void onAdClosed() {
                    startActivity(new Intent(InventoryManagementActivity.this,
                            PriceListActivity.class));

                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });

        } else if (id == R.id.sales_journal) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    SalesJournal.class));

        } else if (id == R.id.purchase_journal) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    PurchasesJournal.class));

        } else if (id == R.id.account_receivables) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    PayablesAndReceivablesActivity.class)
                    .putExtra("accounttype", "receivables"));

        } else if (id == R.id.account_payables) {
            startActivity(new Intent(InventoryManagementActivity.this,
                    PayablesAndReceivablesActivity.class)
                    .putExtra("accounttype", "payables"));
        } else if (id == R.id.profit_loss) {

            startActivity(new Intent(InventoryManagementActivity.this,
                    ProfitAndLossActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");

        File originalApk = new File(filePath);

        try {
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory()) {
                if (!tempFile.mkdirs()) {
                    return;
                }
            }
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes)
                    .replace(" ", "").toLowerCase() + ".apk");

            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }

            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void productDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.product_category_dialog, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Add Product Category");
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadProductInformation();
                dialog.cancel();
            }
        });

        alertDialogBuilder.create();
        dialog = alertDialogBuilder.show();

        final EditText productCode = dialog.findViewById(R.id.product_code);
        final EditText reorderQuantity = dialog.findViewById(R.id.reorder_level);
        imageView = dialog.findViewById(R.id.product_image);
        final EditText productDescription = dialog.findViewById(R.id.product_description);
        final EditText productName = dialog.findViewById(R.id.product_name);
        snap = dialog.findViewById(R.id.tap_to_snap);
        saveButton = dialog.findViewById(R.id.save_button);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Intent>() {
                    @Override
                    protected Intent doInBackground(Void... voids) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                        return cameraIntent;
                    }
                }.execute();
            }
        });

        if (photo != null) {
            imageView.setImageBitmap(photo);
            snap.setVisibility(View.GONE);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = productName.getText().toString().trim();
                String description = productDescription.getText().toString().trim();
                String code = productCode.getText().toString().trim();
                int reorderAt = Integer.parseInt(reorderQuantity.getText().toString().trim());

                if (!name.equals("")) {
                    final ProductCategory productCategory = new ProductCategory();
                    productCategory.setProductName(name);
                    productCategory.setProductDescription(description);
                    productCategory.setProductCode(code);
                    productCategory.setReorderLevel(reorderAt);


                    try {
                        count++;
                        saveImage(productCategory.getProductName() + count, imageView);
                        productCategory.setImagePath(String.valueOf(file));
                        Log.i("TAG", "FILEPATH" + String.valueOf(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            productDB.dataDao().insertData(productCategory);
                            Log.i("POJO", "saved");
                            //productDB.close();
                            loadProductInformation();
                            dialog.cancel();
                        }
                    });

                } else {
                    Toast.makeText(InventoryManagementActivity.this,
                            "You need to have at least a product to save; else, cancel",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadProductInformation() {
        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getProductTask().observe(this, new Observer<List<ProductCategory>>() {
            @Override
            public void onChanged(@Nullable List<ProductCategory> productCategories) {
                if (productCategories != null) {

                    ProductCategoryAdapter adapter = new ProductCategoryAdapter(
                            InventoryManagementActivity.this, productCategories, mTwoPane, mCallback);
                    productList.setAdapter(adapter);

                    for (ProductCategory p : productCategories) {
                        if (sharedPreferences != null) {
                            totalPurchaseValue = sharedPreferences.getInt("Total Purchase Value"
                                    + p.getProductName(), 0);
                            totalSalesValue = sharedPreferences.getInt("Total Sales Value"
                                    + p.getProductName(), 0);
                            totalStored += (totalPurchaseValue - totalSalesValue);
                        }
                    }
                }
            }
        });
        InventoryService.startActionForInventoryData(getApplicationContext());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    if (data.getData() != null) {
                        final String pathHolder = data.getData().getPath();
                        if (pathHolder.endsWith("csv")) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getDataFromFile(new File(pathHolder));
                                }
                            }, 500);
                        }
                    }
                }
                break;
            case TAKE_PHOTO_CODE:
                if (resultCode == RESULT_OK) {

                    if (data.getData() != null) {
                        photo = (Bitmap) data.getExtras().get("data");

                        Log.i("LEVELSTOTEST", "Successfully Implemented");

                        finish();
                        startActivity(new Intent(InventoryManagementActivity.this, AddCashBankActivity.class)
                                .putExtra(Intent.EXTRA_INTENT, data).putExtra("requestcode", requestCode)
                                .putExtra("resultcode", resultCode));
                    }
                }
                break;
        }
    }

    private void saveImage(String fileName, ImageView image) throws FileNotFoundException {

        if (image.getDrawable() != null) {
            Bitmap bitmapImage = ((BitmapDrawable) image.getDrawable()).getBitmap();

            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/ProductImage/");
            dir.mkdirs();

            file = new File(dir + "/" + fileName + ".png/");
            //productCategory.setImagePath(String.valueOf(file));
            Log.i("File path ", "File Path " + file);

            FileOutputStream out = null;

            try {
                out = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int c;
    List<Transaction> transactionList;
    List<CashAndBank> cashAndBankList;
    List<ProductCategory> pcList;
    List<String> stringsList;

    private void getDataFromFile(File fileToGet) {

        transactionList = new ArrayList<>();
        cashAndBankList = new ArrayList<>();
        pcList = new ArrayList<>();
        stringsList = new ArrayList<>();

        c = c + 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToGet));
            String line;

            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");

                final Transaction transaction = new Transaction();
                for (int i = 0; i < record.length - 1; i++) {

                    String s = record[i].replaceAll("^\"|\"$", "");
                    record[i] = s;
                }

                transaction.setDate(record[0]);
                transaction.setSupplier(record[1]);
                transaction.setQuantity(record[2]);
                transaction.setSalesPrice(record[3]);
                transaction.setCostPrice(record[4]);
                transaction.setUserId(c);
                transaction.setTransactionType(record[6]);
                transaction.setTransactionFund(record[7]);
                transaction.setTotalPurchases(Integer.parseInt(record[8]));
                transaction.setTotalSales(Integer.parseInt(record[9]));
                transaction.setProductName(record[10]);
                transaction.setTotalAmount(record[11]);

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    java.util.Date d = df.parse(record[0]);
                    transaction.setDat(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //transaction.setDat(Date.valueOf(record[12]));
                transactionList.add(transaction);

                Toast.makeText(this, "" + record[0] + "/" + record[1], Toast.LENGTH_SHORT).show();

                final CashAndBank cashAndBank = new CashAndBank();
                cashAndBank.setDate(transaction.getDate());
                cashAndBank.setDescription(transaction.getSupplier());
                cashAndBank.setAmount(transaction.getTotalAmount());
                cashAndBank.setTransactionFund(transaction.getTransactionFund());
                cashAndBank.setTransactionType(transaction.getTransactionType());
                cashAndBank.setDat(transaction.getDat());
                cashAndBankList.add(cashAndBank);

                if (sharedPreferences != null) {
                    totalPurchases = sharedPreferences.getInt("Total Purchases" + transaction.getProductName(), 0);
                    totalSales = sharedPreferences.getInt("Total Sales" + transaction.getProductName(), 0);
                    totalPurchaseValue = sharedPreferences.getInt("Total Purchase Value" + transaction.getProductName(), 0);
                    totalSalesValue = sharedPreferences.getInt("Total Sales Value" + transaction.getProductName(), 0);
                } else {
                    totalPurchases = 0;
                    totalSales = 0;
                    totalSalesValue = 0;
                    totalPurchaseValue = 0;
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                quantities = Integer.parseInt(transaction.getQuantity());

                if ((transaction.getTransactionType()).equals("Incoming Stock")) {
                    editor.putInt("Total Purchases" + transaction.getProductName(), totalPurchases + quantities);
                    editor.putInt("Total Purchase Value" + transaction.getProductName(),
                            totalPurchaseValue + Integer.parseInt(transaction.getTotalAmount()));
                } else {
                    editor.putInt("Total Sales" + transaction.getProductName(), totalSales + quantities);
                    editor.putInt("Total Sales Value" + transaction.getProductName(), totalSalesValue
                            + (Integer.parseInt(transaction.getQuantity()) * Integer.parseInt(transaction.getCostPrice())));
                }

                editor.apply();

                final ProductCategory pc = new ProductCategory();
                if (stringsList.isEmpty()) {
                    pc.setProductName(transaction.getProductName());
                    pc.setProductDescription(transaction.getProductName());
                    pc.setImagePath("");
                    pc.setProductCode(transaction.getProductName());
                    pcList.add(pc);
                    stringsList.add(transaction.getProductName());
                } else {
                    if (!stringsList.contains(transaction.getProductName())) {
                        pc.setProductName(transaction.getProductName());
                        pc.setProductDescription(transaction.getProductName());
                        pc.setImagePath("");
                        pc.setProductCode(transaction.getProductName());
                        pcList.add(pc);
                        stringsList.add(transaction.getProductName());
                    }
                }
            }


            if (transactionList != null && cashAndBankList != null && pcList != null) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        for (ProductCategory p : pcList) {
                            productDB.dataDao().insertData(p);
                        }

                        for (Transaction t : transactionList) {
                            productDB.transactionDao().insertData(t);
                        }

                        for (CashAndBank c : cashAndBankList) {
                            productDB.cashAndBankDao().insertFund(c);
                        }

                    }
                });
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "operation aborted", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("ResourceAsColor")
    public static void getSharedPreferencesData(Context context, String productName,
                                                TextView totalInventory,
                                                TextView totalIncomingStock,
                                                TextView totalOutgoingStock, View view,
                                                int reorderLevel, LinearLayout linearLayout) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences != null) {
            totalPurchases = sharedPreferences.getInt("Total Purchases" + productName, 0);
            totalSales = sharedPreferences.getInt("Total Sales" + productName, 0);
            int inStore = sharedPreferences.getInt("InStore" + productName, 0);
            totalIncomingStock.setText("Incoming: " + String.valueOf(totalPurchases));
            totalOutgoingStock.setText("Outgoing: " + String.valueOf(totalSales));
            totalInventory.setText("In-Store: " + String.valueOf(totalPurchases - totalSales));


            if ((totalPurchases - totalSales) <= reorderLevel && totalPurchases != 0 && totalSales != 0) {
                view.setBackgroundColor(R.color.colorRed);
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        onCreateOnStartNotifier = 100;
    }
}