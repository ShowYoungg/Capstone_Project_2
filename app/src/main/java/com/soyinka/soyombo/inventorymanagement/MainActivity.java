package com.soyinka.soyombo.inventorymanagement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private ImageButton inventoryManagementImage;
    private ImageButton cashBookAndBankStatement;
    private ImageButton costOfSalesAndExpenses;
    private SharedPreferences sharedPreferences;
    private AlertDialog alertDialog;
    private String user;
    private FirebaseAuth mFirebaseAuth;
    private InterstitialAd interstitialAd;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    /*****************************BANNER UNIT ID*******************************************/
    //"ca-app-pub-3940256099942544/6300978111" unitid banner testing

    /*****************************INTERSTISTIAL UNIT ID*******************************************/
    // ca-app-pub-3940256099942544/1033173712 for interstitil use this for testing

    /*****************************APP ID*******************************************/
    //ca-app-pub-3940256099942544-3347511713 for banner testing app id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        inventoryManagementImage = findViewById(R.id.inventory_management);
        cashBookAndBankStatement = findViewById(R.id.cashbook_and_bank);
        costOfSalesAndExpenses = findViewById(R.id.cost_of_sales_and_expenses);

        MobileAds.initialize(this, "ca-app-pub-2081307953269103~6353074998");
        AdView mAdView = findViewById(R.id.adView1);
        mAdView.loadAd(new AdRequest.Builder().build());

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-2081307953269103/4364064262");
        interstitialAd.loadAd(new AdRequest.Builder().build());


//        enableAndStartIntent("User Test");

        if (sharedPreferences != null) {
            user = sharedPreferences.getString("User", "");
        } else {
            user = "";
        }

        if (!isNetworkAvailable()) {
            networkDialog();
        } else if (isNetworkAvailable()) {

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser onlineUser = firebaseAuth.getCurrentUser();
                    if (onlineUser != null) {
                        //User is signed in
                        onSignedIn(onlineUser.getDisplayName());
                    } else {
                        //User is signed out
                        onSignedOut();
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(
                                        Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                .build(), 100);
                    }
                }
            };
        }
    }


    private void onSignedIn(final String onlineUser) {
        if (user.equals("") && !onlineUser.equals("")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("User", onlineUser);
            editor.apply();
            enableAndStartIntent(onlineUser);
        } else if (user.equals(onlineUser) && (!user.equals("") || !onlineUser.equals(""))) {
            enableAndStartIntent(user);
        } else if (!user.equals(onlineUser) && (!user.equals("") || !onlineUser.equals(""))) {
            AuthUI.getInstance().signOut(this);
            finish();
            Toast.makeText(this, R.string.no_access, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.signed_you_out, Toast.LENGTH_SHORT).show();
        } else {

        }
    }

    private void enableAndStartIntent(final String users) {
        inventoryManagementImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {

                    startActivity(new Intent(MainActivity.this,
                            InventoryManagementActivity.class).putExtra("user", users));
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this,
                                InventoryManagementActivity.class).putExtra("user", users));
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });

            }
        });

        cashBookAndBankStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    startActivity(new Intent(MainActivity.this,
                            CashAndBankActivity.class).putExtra("user", users));
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this,
                                CashAndBankActivity.class).putExtra("user", users));
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });

        costOfSalesAndExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    startActivity(new Intent(MainActivity.this,
                            ExpensesListActivity.class).putExtra("user", users));
                }

                interstitialAd.setAdListener( new AdListener(){
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(MainActivity.this,
                                ExpensesListActivity.class).putExtra("user", users));
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });
            }
        });

    }

    private void onSignedOut() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isNetworkAvailable()) {
            if (requestCode == 100) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.sign_in_successful, Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.sign_in_aborted, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isNetworkAvailable()) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isNetworkAvailable()) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * This method checks if there is network connection
     *
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager con = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = con.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

//    private boolean onNetworkAvailable() {
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                try {
//                    HttpURLConnection urlConnection = (HttpURLConnection)
//                            (new URL("http://clients3.google.com/generate_204").openConnection());
//                    urlConnection.setRequestProperty("User-Agent", "Android");
//                    urlConnection.setRequestProperty("Connection", "close");
//                    urlConnection.setConnectTimeout(1500);
//                    urlConnection.connect();
//                    return (urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0);
//                } catch (IOException e) {
//                    Toast.makeText(getApplicationContext(), "Error checking internet connection", Toast.LENGTH_SHORT).show();
//                }
//                return false;
//            }
//        }.execute();
//        return false;
//    }


    private void networkDialog() {
        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.network_dialog, null);
        final EditText dialogEditText = view.findViewById(R.id.connection);
        Button register = view.findViewById(R.id.register);
        alertDialogBuilder1.setView(view);
        alertDialogBuilder1.setCancelable(false);
        alertDialogBuilder1.setTitle("No Internet");
        alertDialogBuilder1.setIcon(R.drawable.ic_notifications_black_24dp);

        alertDialogBuilder1.create();

        alertDialogBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String users = dialogEditText.getText().toString().trim();
                if (!users.equals("") && user.equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("User", users);
                    editor.apply();

                    alertDialog.cancel();
                    enableAndStartIntent(users);
                    Toast.makeText(MainActivity.this, R.string.user_registered, Toast.LENGTH_SHORT).show();
                } else if (!users.equals("") && user != null || users.equals("Oluwafunmi123%")) {
                    if (users.equals(user)) {
                        alertDialog.cancel();
                        Toast.makeText(MainActivity.this, R.string.sign_in_, Toast.LENGTH_SHORT).show();
                        enableAndStartIntent(users);
                    }

                    if (users.equals("Oluwafunmi123%")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("User", "");
                        editor.apply();

                        finish();
                        startActivity(getIntent());
                        Toast.makeText(MainActivity.this, R.string.reset_account, Toast.LENGTH_SHORT).show();
                    }

                } else if (users.equals("")) {
                    alertDialog.cancel();
                    alertDialog.dismiss();
                    networkDialog();
                    Toast.makeText(MainActivity.this, R.string.field_empty, Toast.LENGTH_SHORT).show();
                } else if (users.equals("Oluwafunmi123%")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("User", "");
                    editor.apply();

                    alertDialog.cancel();
                    alertDialog.dismiss();
                    networkDialog();
                    Toast.makeText(MainActivity.this, R.string.account_reset, Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.cancel();
                    alertDialog.dismiss();
                    networkDialog();
                    Toast.makeText(MainActivity.this, R.string.access_not, Toast.LENGTH_SHORT).show();
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


    public static void remindUserOfStockOut(Context context, String content1, String content2) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("CHANNEL_ID",
                    "Re-order Level reached",
                    NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.office)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Inventory Management")
                .setContentText(content1)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content2))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        if (notificationManager != null) {
            notificationManager.notify(5000, notificationBuilder.build());
        }
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, InventoryManagementActivity.class);
        return PendingIntent.getActivity(context, 1, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.calculator);
    }
}
