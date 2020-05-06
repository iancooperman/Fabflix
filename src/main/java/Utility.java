package main.java;

import java.util.HashMap;

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

    // Treat the inputted hashMap like a multiset/defaultdict
    public static void defaultHashMapAdd(HashMap<String, Integer> hashMap, String key, int defaultValue) {
        if (!hashMap.containsKey(key)) {
            hashMap.put(key, defaultValue);
        }
        else {
            Integer oldValue = hashMap.get(key);
            hashMap.replace(key, ++oldValue);
        }
    }
}
