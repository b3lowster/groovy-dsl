// ============================================
// StringFunctions.java
// ============================================
package com.provectus.formula.functions;

import java.util.Arrays;
import java.util.List;

public class StringFunctions {
    
    /**
     * Count total number of letters in an array of strings
     */
    public static int countLetters(String[] strings) {
        if (strings == null || strings.length == 0) {
            return 0;
        }
        
        int count = 0;
        for (String str : strings) {
            if (str != null) {
                for (char c : str.toCharArray()) {
                    if (Character.isLetter(c)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    /**
     * Count total number of letters in a list of strings
     */
    public static int countLetters(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return 0;
        }
        return countLetters(strings.toArray(new String[0]));
    }
    
    /**
     * Count all characters (including spaces and special chars) in array
     */
    public static int countAllChars(String[] strings) {
        if (strings == null || strings.length == 0) {
            return 0;
        }
        
        int count = 0;
        for (String str : strings) {
            if (str != null) {
                count += str.length();
            }
        }
        return count;
    }
    
    /**
     * Count words in array of strings
     */
    public static int countWords(String[] strings) {
        if (strings == null || strings.length == 0) {
            return 0;
        }
        
        int count = 0;
        for (String str : strings) {
            if (str != null && !str.trim().isEmpty()) {
                count += str.trim().split("\\s+").length;
            }
        }
        return count;
    }
    
    /**
     * Get average word length across all strings
     */
    public static double averageWordLength(String[] strings) {
        if (strings == null || strings.length == 0) {
            return 0.0;
        }
        
        int totalLetters = countLetters(strings);
        int totalWords = countWords(strings);
        
        return totalWords > 0 ? (double) totalLetters / totalWords : 0.0;
    }
    
    /**
     * Concatenate all strings with a separator
     */
    public static String joinStrings(String[] strings, String separator) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        return String.join(separator, strings);
    }
    
    /**
     * Count vowels in array of strings
     */
    public static int countVowels(String[] strings) {
        if (strings == null || strings.length == 0) {
            return 0;
        }
        
        int count = 0;
        String vowels = "aeiouAEIOU";
        
        for (String str : strings) {
            if (str != null) {
                for (char c : str.toCharArray()) {
                    if (vowels.indexOf(c) != -1) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    
    /**
     * Count consonants in array of strings
     */
    public static int countConsonants(String[] strings) {
        return countLetters(strings) - countVowels(strings);
    }
}