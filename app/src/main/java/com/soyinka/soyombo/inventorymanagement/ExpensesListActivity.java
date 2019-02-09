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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class ExpensesListActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;
    private List<SalesCostAndExpenses> salesCostAndExpensesList;
    private List<SalesCostAndExpenses> salesCostAndExpensesList1;
    private List<SalesCostAndExpenses> expensesArrayList;
    private RecyclerView recyclerView;
    private EditText startDate, endDate;
    private Date sDate, eDate;
    private Button submitButton, thisMonth, lastMonth;
    private ExpensesAdapter expensesAdapter;
    AppDatabase productDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);

        Slide slide = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            slide = new Slide(Gravity.START);
            slide.addTarget(R.id.expenses_slide);
            slide.setInterpolator(AnimationUtils.loadInterpolator(this,
                    android.R.interpolator.linear_out_slow_in));
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
        }

        salesCostAndExpensesList = new ArrayList<>();
        salesCostAndExpensesList1 = new ArrayList<>();
        expensesArrayList = new ArrayList<>();

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.expenses_recycler);
        floatingActionButton = findViewById(R.id.add_expenses);
        startDate = findViewById(R.id.start_datee);
        endDate = findViewById(R.id.end_datee);
        submitButton = findViewById(R.id.send_for_searche);
        thisMonth = findViewById(R.id.this_month);
        lastMonth = findViewById(R.id.last_month);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(ExpensesListActivity.this, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByDate(startDate, endDate);
            }
        });

        lastMonth.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(ExpensesListActivity.this, "Enter valid date format e.g 01/25/2019", Toast.LENGTH_SHORT).show();
                }

                if (year != null) {
                    int i;
                    if (!String.valueOf(year.charAt(1)).equals("1")){
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
                        firstDayOfTheMonth = ""+ m + "/" + firstDay + "/" + newYear;
                    }
                }
                Log.i("SSSSS", firstDayOfTheMonth + "/" + year);
                searchByMonth(firstDayOfTheMonth, year);
            }
        });

        thisMonth.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(ExpensesListActivity.this, "Enter valid date format e.g 01/25/2019", Toast.LENGTH_SHORT).show();
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpensesListActivity.this, SalesCostAndExpensesActivity.class));
            }
        });

        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getExpenses().observe(this, new Observer<List<SalesCostAndExpenses>>() {
            @Override
            public void onChanged(@Nullable List<SalesCostAndExpenses> expenses) {

                if (expenses != null) {
                    salesCostAndExpensesList.addAll(expenses);
                    expensesAdapter = new ExpensesAdapter(getApplicationContext(), salesCostAndExpensesList);
                    recyclerView.setAdapter(expensesAdapter);
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


    @Override
    protected void onStart() {
        super.onStart();

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
                expensesAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public void searchByMonth(String startDate, String endDate) {

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
            Toast.makeText(this, "Enter valid date format e.g 01/25/2019", Toast.LENGTH_SHORT).show();
        }

        final Date finalSsDate = ssDate;
        final Date finalEeDate = eeDate;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                expensesArrayList = productDB.expensesDao().loadExpensesByDate(finalSsDate, finalEeDate);
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (expensesArrayList != null) {
                    salesCostAndExpensesList.clear();
                    salesCostAndExpensesList.addAll(expensesArrayList);
                    expensesAdapter = new ExpensesAdapter(getApplicationContext(), salesCostAndExpensesList);
                    recyclerView.setAdapter(expensesAdapter);
                }
            }
        }, 150);
    }


    public void searchByDate(EditText editText, EditText endDateEditText) {

        String sd = editText.getText().toString().trim();
        String ed = endDateEditText.getText().toString().trim();
        if (salesCostAndExpensesList != null) {

            List<SalesCostAndExpenses> tDate = new ArrayList<>();
            long dateInMilliseconds = 0;
            long dateInMillisecondsA = 0;
            long dateInMillisecondsB = 0;


//            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = null;
            Date sDate = null;
            Date sSdate = null;

            try {
                sDate = dateFormat1.parse(sd);
                sSdate = dateFormat1.parse(ed);
                dateInMillisecondsA = sDate.getTime();
                dateInMillisecondsB = sSdate.getTime();
                Log.i("FROM-AND-TO DATE", " " + dateInMillisecondsA + " " + dateInMillisecondsB);

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, "Enter valid date format e.g 01/25/2019", Toast.LENGTH_SHORT).show();
            }

            for (SalesCostAndExpenses t : salesCostAndExpensesList) {
                String sD = t.getDate();
                try {
                    startDate = dateFormat1.parse(sD);
                    dateInMilliseconds = startDate.getTime();
                    Log.i("DATA DATE", "" + startDate + " " + dateInMilliseconds);

                    Log.i("SUBMIT A", "I am responding" + dateInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Enter valid date format e.g 01/25/2019", Toast.LENGTH_SHORT).show();
                }

                if (dateInMillisecondsA <= dateInMilliseconds && dateInMillisecondsB >= dateInMilliseconds) {
                    tDate.add(t);

                    Log.i("SUBMIT B", "I am responding");

                    expensesAdapter = new ExpensesAdapter(getApplicationContext(), tDate);
                    recyclerView.setAdapter(expensesAdapter);
                }
            }
        }
    }


}
