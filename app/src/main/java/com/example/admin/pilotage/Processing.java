package com.example.admin.pilotage;


import java.util.ArrayList;


/**
 * This class contains the methods used to process arrays
 */
public class Processing {

    /**
     * An efficient stream searching class based on the Knuth-Morris-Pratt algorithm.
     */
    public Processing() {
    }

    /**
     * Finds the first occurrence of a pattern in the text.
     * @param data Array in which we want to search the pattern
     * @param pattern byte pattern to look for in the data
     * @return The index of the first occurence of the patern
     */
    public int indexOf(byte[] data, byte[] pattern) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        if (data.length == 0) return -1;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    /**
     * Finds all the occurences of a pattern in an array.
     * @param buffer Array in which we want to search the pattern
     * @param pattern byte pattern to look for in the data
     * @param total_bytesRead Used to keep track of all the bytes that are read independently of each buffer
     * @return The list of all the indexes of the first pattern occurrences
     */
    public ArrayList indexOf_bufferedData(byte[] buffer, byte[] pattern, int total_bytesRead) {
        int[] failure = computeFailure(pattern);

        int pattern_Index = 0;
        int data_Index = 0;
        int result = 0;

        ArrayList<Integer> index_list = new ArrayList<>();

        if (buffer.length == 0){
            // No data
        };

        // We go through all the buffer
        while( data_Index < buffer.length) {

            while (pattern_Index > 0 && pattern[pattern_Index] != buffer[data_Index]) {
                pattern_Index = failure[pattern_Index - 1];
            }

            if (pattern[pattern_Index] == buffer[data_Index]) {
                pattern_Index++;
            }
            if (pattern_Index == pattern.length) {
                pattern_Index = 0;
                result = data_Index - pattern.length + 1;
                result = result + total_bytesRead;
                index_list.add(result);
            }
            data_Index++;
        }
        return index_list;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }
        return failure;
    }
}


