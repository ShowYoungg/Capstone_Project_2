package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SHOW on 1/19/2019.
 */


public class ReadCSV {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";


    public static void readCsvFile(Context context, String fileName) {

        final AppDatabase productDB;
        productDB = Room.databaseBuilder(context, AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();


        BufferedReader fileReader = null;

        try {

            Toast.makeText(context, "running1", Toast.LENGTH_SHORT).show();

            //Create a new list of student to be filled by CSV file data
            List<Transaction> transactionList = new ArrayList<>();

            String line = "";

            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileName));
            Toast.makeText(context, "running2", Toast.LENGTH_SHORT).show();

            //Read the CSV file header to skip it
            fileReader.readLine();

            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                Toast.makeText(context, "line is not null", Toast.LENGTH_SHORT).show();

                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);
                if (tokens.length > 0) {
                    //Create a new transaction object and populate its data
                    Transaction transaction = new Transaction();
                    transaction.setDate(tokens[1]);
                    transaction.setSupplier(tokens[2]);
                    transaction.setQuantity(tokens[3]);
                    transaction.setSalesPrice(tokens[4]);
                    transaction.setCostPrice(tokens[5]);
                    transaction.setUserId(Integer.parseInt(String.valueOf(tokens[6])));
                    transaction.setTransactionType(tokens[7]);
                    transaction.setTransactionFund(tokens[8]);
                    transaction.setTotalPurchases(Integer.parseInt(String.valueOf(tokens[9])));
                    transaction.setTotalSales(Integer.parseInt(String.valueOf(tokens[10])));
                    transaction.setProductName(tokens[11]);
                    transaction.setTotalAmount(tokens[12]);
                    transactionList.add(transaction);
                    Toast.makeText(context, "t is populated"
                            + transaction.getTotalAmount(), Toast.LENGTH_SHORT).show();
                }
            }

            //Print the new student list
            for (final Transaction transaction : transactionList) {
                //System.out.println(transaction.toString());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        productDB.transactionDao().insertData(transaction);
                    }
                });

            }
        } catch (Exception e) {
            //System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null){
                    fileReader.close();
                }
            } catch (IOException e) {
                //System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }

    }

}



//try {
//    CSVReader reader = new CSVReader (new FileReader("d:/sample.csv"));
//    String[] nextLine;
//    while (nextLine = reader.readNext() != null){
//        if (next != null){
//            System.out.printLn(Arrays.toString(nextLine))
//        }
//        }
//        }
