package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;


public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n========================");
            System.out.println("Welcome to MyLedgerApp!");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit ($)");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.println("========================");
            System.out.print("Please enter here: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("x")) {
                System.out.println("Thank you for using MyLedgerApp!");
            }

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // TODO: create file if it does not exist, then read each line,
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] part = line.split("\\|");
                LocalDate date = LocalDate.parse(part[0], DATE_FMT);
                LocalTime time = LocalTime.parse(part[1], TIME_FMT);
                String description = part[2];
                String vendor = part[3];
                double price = Double.parseDouble(part[4]);
                Transaction transaction = new Transaction(date, time, description, vendor, price);
                transactions.add(transaction);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
//user input with if statement if user puts in amount less than zero for deposit
        LocalDateTime dateTime = null;
        String description = "";
        String vendor = "";
        double amount = 0;


        while (dateTime == null) {
            try {
                System.out.print("Please enter date and time(e.g., \"YYYY-MM-DD hh:mm:ss\"): ");
                dateTime = LocalDateTime.parse(scanner.nextLine(), DATETIME_FMT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid input, please try again using format \"YYYY-MM-DD hh:mm:ss\"");
            }
        }

        while (description.isEmpty() || vendor.isEmpty()){
            System.out.print("Please enter the description of your deposit: ");
            description = scanner.nextLine().trim();
            System.out.print("Please enter the name of the vendor: ");
            vendor = scanner.nextLine().trim();
            if (description.isEmpty() || vendor.isEmpty()) {
                System.out.println("Error: Description and Vendor cannot be blank.");
            }
        }
        while (amount <= 0){
            try {
                System.out.print("Please enter the deposit amount: ");
                amount = scanner.nextDouble();
                scanner.nextLine();
                if (amount <= 0) {
                    System.out.println("Value must be greater than 0, please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error, invalid input, please enter number");
                scanner.nextLine();
            }
        }

//add to array list after user input
        LocalDate datePart = dateTime.toLocalDate();
        LocalTime timePart = dateTime.toLocalTime();
        Transaction deposit = new Transaction(datePart, timePart, description, vendor, amount);
        transactions.add(deposit);
        //write to file with toString method override for preferred format
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.newLine();
            writer.write(deposit.toString());
            //close writer to update file
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private static void addPayment(Scanner scanner) {
        LocalDateTime dateTime = null;
        String description = "";
        String vendor = "";
        double amount = 0;
        while (dateTime == null) {
            try {
                System.out.print("Please enter date and time(e.g., \"YYYY-MM-DD hh:mm:ss\"): ");
                dateTime = LocalDateTime.parse(scanner.nextLine().trim(), DATETIME_FMT);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid input, please try again using format \"YYYY-MM-DD hh:mm:ss\"");
            }
        }
        while (description.isEmpty() || vendor.isEmpty()){
            System.out.print("Please enter the description of payment: ");
            description = scanner.nextLine().trim();
            System.out.print("Please enter the name of vendor: ");
             vendor = scanner.nextLine().trim();
            if (description.isEmpty() || vendor.isEmpty()) {
                System.out.println("Error: Description and Vendor cannot be blank.");
            }
        }
       while (amount <= 0){
           try {
               System.out.print("Please enter the payment amount: ");
               amount = scanner.nextDouble();
               scanner.nextLine();
               if (amount <= 0) {
                   System.out.println("Value must be greater than 0, please try again.");
               }
           } catch (Exception e) {
               System.out.println("Error, invalid input, please enter number");
               scanner.nextLine();
           }
       }

//add to array list after user input
        LocalDate datePart = dateTime.toLocalDate();
        LocalTime timePart = dateTime.toLocalTime();
        Transaction payment = new Transaction(datePart, timePart, description, vendor, -amount);
        transactions.add(payment);
        //write to file with toString method override for preferred format
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.newLine();
            writer.write(payment.toString());
            //close writer to update file
            writer.close();
        } catch (IOException e) {

        }

    }


/* ------------------------------------------------------------------
   Ledger menu
   ------------------------------------------------------------------ */
private static void ledgerMenu(Scanner scanner) {

    boolean running = true;
    while (running) {
        transactions.sort(Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());
        System.out.println("Ledger");
        System.out.println("Choose an option:");
        System.out.println("A) All");
        System.out.println("D) Deposits");
        System.out.println("P) Payments");
        System.out.println("R) Reports");
        System.out.println("H) Home");

        String input = scanner.nextLine().trim();

        switch (input.toUpperCase()) {
            case "A" -> displayLedger();
            case "D" -> displayDeposits();
            case "P" -> displayPayments();
            case "R" -> reportsMenu(scanner);
            case "H" -> running = false;
            default -> System.out.println("Invalid option");
        }
    }
}

/* ------------------------------------------------------------------
   Display helpers: show data in neat columns
   ------------------------------------------------------------------ */
private static void displayLedger() {
    for (Transaction transaction : transactions) {
        System.out.println(transaction);
    }

}

private static void displayDeposits() {
    for (Transaction transaction : transactions) {
        if (transaction.getAmount() > 0) {
            System.out.println(transaction);
        }
    }
}

private static void displayPayments() {
    for (Transaction transaction : transactions) {
        if (transaction.getAmount() < 0) {
            System.out.println(transaction);
        }
    }
}

/* ------------------------------------------------------------------
   Reports menu
   ------------------------------------------------------------------ */
private static void reportsMenu(Scanner scanner) {
    boolean running = true;
    while (running) {
        System.out.println("Reports");
        System.out.println("Choose an option:");
        System.out.println("1) Month To Date");
        System.out.println("2) Previous Month");
        System.out.println("3) Year To Date");
        System.out.println("4) Previous Year");
        System.out.println("5) Search by Vendor");
        System.out.println("6) Custom Search");
        System.out.println("0) Back");

        String input = scanner.nextLine().trim();

        switch (input) {
            case "1" -> {
                LocalDate today = LocalDate.now();
                LocalDate start = today.withDayOfMonth(1);
                filterTransactionsByDate(start, today);
            }
            case "2" -> {
                //get today (minus a month) and put in variable to be used in method parameter
                LocalDate now = LocalDate.now();
                LocalDate lastMonth = now.minusMonths(1);
                LocalDate start = lastMonth.withDayOfMonth(1);
                LocalDate end = lastMonth.with(TemporalAdjusters.lastDayOfMonth());
                filterTransactionsByDate(start, end);
            }
            case "3" -> {
                LocalDate today = LocalDate.now();
                LocalDate start = today.withDayOfYear(1);
                filterTransactionsByDate(start, today);
            }
            case "4" -> {
                LocalDate now = LocalDate.now();
                LocalDate lastYear = now.minusYears(1);
                LocalDate start = lastYear.withDayOfYear(1);
                LocalDate end = lastYear.with(TemporalAdjusters.lastDayOfYear());
                filterTransactionsByDate(start, end);
            }
            case "5" -> {
                System.out.print("Please enter vendor: ");
                String vendor = scanner.nextLine();
                filterTransactionsByVendor(vendor);
            }
            case "6" -> customSearch(scanner);
            case "0" -> running = false;
            default -> System.out.println("Invalid option");
        }
    }
}

/* ------------------------------------------------------------------
   Reporting helpers
   ------------------------------------------------------------------ */
private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
    for (Transaction transaction : transactions) {
        if (!transaction.getDate().isBefore(start) && !transaction.getDate().isAfter(end)) {
            System.out.println(transaction);
        }
    }
}

private static void filterTransactionsByVendor(String vendor) {
    for (Transaction transaction : transactions) {
        if (transaction.getVendor().equalsIgnoreCase(vendor)) {
            System.out.println(transaction);
        }
    }
}

private static void customSearch(Scanner scanner) {
    // TODO – prompt for any combination of date range, description,
    //        vendor, and exact amount, then display matches
        /*System.out.println("Please enter beginning date (e.g, \"YYYY-MM-dddd\"): ");
        LocalDate startDate = scanner.();*/
}

/* ------------------------------------------------------------------
   Utility parsers (you can reuse in many places)
   ------------------------------------------------------------------ */
private static LocalDate parseDate(String s) {
    if (s == null || s.isBlank()) {
        return null;
    }
    return LocalDate.parse(s, DATE_FMT);
}

private static Double parseDouble(String s) {
    /* TODO – return Double   or null */
    return null;
}
}
