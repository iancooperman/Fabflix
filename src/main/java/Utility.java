package main.java;

public class Utility {
    // function for determining price of movie by year
    public static String yearToPrice(String year) {
        int yearInt = Integer.parseInt(year);

        if (yearInt >= 2018) {
            return "4.99";
        }
        else if (yearInt >= 2015) {
            return "2.99";
        }
        else {
            return "0.99";
        }
    }
}
