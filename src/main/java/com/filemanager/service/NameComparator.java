package com.filemanager.service;

import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;


/**
 *   Compare two string messages and provide percent of matching
 *
 * @author Andrii Oliinuk
 */
public class NameComparator {
    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    private double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    public Optional<String> directorySorter(Set<String> directoriesName, String fileName, int compareMin) {
        String directoryName = null;
        double maxRate = 0;
        for (String directory : directoriesName) {
            double rate = similarity(directory, fileName);
            if (compareMin > (int) (rate * 100)) {
                continue;
            }
            if (Double.compare(maxRate, rate) == 0) {
                return Optional.of(format("Multiple options: 1) %s 2) %s", directoryName, directory));
            }
            if (Double.compare(maxRate, rate) < 0) {
                directoryName = directory;
                maxRate = rate;
            }
        }
        return (directoryName == null) ? Optional.empty() : Optional.of(directoryName);
    }

    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}