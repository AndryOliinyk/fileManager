package com.filemanager.service.comparator;

import com.filemanager.entity.Entry;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

import static java.lang.String.format;

/**
 * Compare files  names and provide percent of matching
 *
 * @author Andrii Oliinuk
 */
@Log4j2
public class FileComparator extends Comparator {

    private static final String INVALID_VALUE_MESSAGE = "Conflict of match for %s. Expected 1, but was %s - %s percentage";

    public int compareFileNames(File compareWhat, File compareTo) {
        String compareWhatName = FilenameUtils.removeExtension(compareWhat.getName());
        String compareToName = FilenameUtils.removeExtension(compareTo.getName());
        return compareValues(compareWhatName, compareToName);
    }

    public Entry<String,Integer> getMaxMatched(String compareWhat, Set<String> compareTo, int minLimit) throws IllegalAccessError {
        int maxMatch = 0;
        List<String> matchCounter = new ArrayList<>();
        String maxMatchMessage = null;
        for (String message : compareTo) {
            int compareMatch = compareValues(compareWhat, message);
            if (maxMatch < compareMatch) {
                maxMatchMessage = message;
                maxMatch = compareMatch;
                matchCounter = new ArrayList<>();
                matchCounter.add(message);
            } else if (maxMatch == compareMatch) {
                matchCounter.add(message);
            }
        }
        if ( minLimit <= maxMatch  && matchCounter.size() > 1) {
            log.error(format(INVALID_VALUE_MESSAGE, compareWhat, matchCounter, maxMatch));
            throw new IllegalAccessError(format(INVALID_VALUE_MESSAGE, compareWhat, matchCounter, maxMatch));
        }
        return new Entry(maxMatchMessage, maxMatch);
    }
}
