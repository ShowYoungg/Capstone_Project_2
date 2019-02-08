package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;
import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class Main2Activity extends AppCompatActivity {

    private ArrayList<String> list;
    public static List<Transaction> transactionArrayList;
    public static List<Transaction> transactionArrayList1;
    public static List<ProductCategory> productCategoryArrayList;
    public static String nameC;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    AppDatabase productDB;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        list = new ArrayList<>();
        productCategoryArrayList = new ArrayList<>();
        transactionArrayList = new ArrayList<>();
        transactionArrayList1 = new ArrayList<>();

        productDB = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        ProductCategoryViewModel productCategoryViewModel =
                ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
        productCategoryViewModel.getProductTask().observe(this, new Observer<List<ProductCategory>>() {
            @Override
            public void onChanged(@Nullable List<ProductCategory> categoryTransaction) {

                if (categoryTransaction != null){
        //             productCategoryArrayList = categoryTransaction;
                    for ( ProductCategory ct: categoryTransaction) {
                        nameC = ct.getProductName();
                        list.add(nameC);
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_PARCELABLE = "section_parcelable";


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String nameArgs) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_PARCELABLE, nameArgs);
            fragment.setArguments(args);
            return fragment;
        }

        private int i;
        public String nameArgs;
        private static SharedPreferences sharedPreferences;
        private static int totalPurchases, totalSales;
        private RecyclerView recyclerView;
        private HistoryAndRecordAdapter historyAndRecordAdapter;
        AppDatabase productDB;
        private List<Transaction> tl;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_history, container, false);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            productDB = Room.databaseBuilder(getContext(), AppDatabase.class, "Inventory Database")
                    .fallbackToDestructiveMigration().build();

            tl = new ArrayList<>();

            ImageView productImage = rootView.findViewById(R.id.product_image);
            TextView productName = rootView.findViewById(R.id.product_name);
            TextView totalInventory = rootView.findViewById(R.id.inv_total);
            TextView totalIncomingStock = rootView.findViewById(R.id.incoming_stock);
            TextView totalOutgoingStock = rootView.findViewById(R.id.outgoing_stock);
            recyclerView = rootView.findViewById(R.id.history_list);

            Bundle bundle = getArguments();
            if (bundle != null){
                nameArgs= getArguments().getString(ARG_PARCELABLE);
                productName.setText(nameArgs);
                displayFragment(productImage, nameArgs ,  totalInventory, totalIncomingStock,
                        totalOutgoingStock, getArguments().getInt(ARG_SECTION_NUMBER));
            }

            return rootView;
        }


        private void displayFragment(final ImageView imageView, final String name, final TextView totalStock,
                                     final TextView in, final TextView out, final int position){

            ProductCategoryViewModel productCategoryViewModel =
                    ViewModelProviders.of(this).get(ProductCategoryViewModel.class);
            productCategoryViewModel.getProductTransaction().observe(this, new Observer<List<Transaction>>() {
                @Override
                public void onChanged(@Nullable List<Transaction> categoryTransaction) {

                    if (categoryTransaction != null){
                        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        if (sharedPreferences != null) {
                            totalPurchases = sharedPreferences.getInt("Total Purchases"
                                    + name, 0);
                            totalSales = sharedPreferences.getInt("Total Sales"
                                    + name, 0);
                        }

                //        nameArgs = name;
                        totalStock.setText("In-Store: " + String.valueOf(totalPurchases - totalSales));
                        in.setText("Purchases: " + String.valueOf(totalPurchases));
                        out.setText("Sales: " + String.valueOf(totalSales));
                        imageView.setImageResource(R.drawable.calculator);

                        for (Transaction t: categoryTransaction) {
                            Log.i("TOTAL FFF", name + " " + t.getProductName() + t.getSupplier() + " "+ t.getSalesPrice());
                            if (t.getProductName() != null){
                                if ((t.getProductName()).equals(name)){
                                    //int user = t.getUserId();
                                    int totalSales = t.getTotalSales();
                                    int totalPurchases = t.getTotalPurchases();

                                    tl.add(new Transaction(t.getId(),t.getDate(), t.getSupplier(),
                                            t.getQuantity(), t.getSalesPrice(), t.getCostPrice(),
                                            t.getUserId(),t.getTransactionType(),t.getTransactionFund(),
                                            totalPurchases, totalSales, t.getProductName(), t.getTotalAmount(), t.getDat()));
                                    setRecyclerView(tl);
//                            Log.i("PRODUCT", ""+ t.getQuantity()
//                                    + ""+ t.getProductName() +" "+ categoryTransaction.get(2).getProductName());
                                }
                            }
                        }
                    }
                }
            });
        }

        private void setRecyclerView(List<Transaction> tList) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setHasFixedSize(true);

            if (tList != null){
            Log.i("SHOW", tList.get(0).getProductName());
            historyAndRecordAdapter = new HistoryAndRecordAdapter(getContext(), tList);
            recyclerView.setAdapter(historyAndRecordAdapter);
            }
        }


    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        Bundle b;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

//            b = new Bundle();
//            b.putBundle("", bundle);
        }

        int j = 0;
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);



            do {

                Fragment fragment = PlaceholderFragment.newInstance(0, list.get(j));
                j++;
                return fragment;
            } while ( j <= position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return  list.get(position);
        }
    }
}