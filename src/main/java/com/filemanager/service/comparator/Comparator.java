package com.filemanager.service.comparator;

/**
 *  Compare values in percentage rate
 *
 * @author Andrii Oliinuk
 */
public class Comparator {

    public int compareValues(String message,  String matchToMessage) {
        if (message.equals(matchToMessage)) {
            return 100;
        }
        char[] compareWhatSymbols = getValueChars(message);
        char[] compareToSymbols = getValueChars(matchToMessage);
        int i = 0;
        int j = 0;
        int maxMatchSymbols = 0;
        while (i < compareWhatSymbols.length) {
            int matchSymbols = 0;
            int k = i;
            while (j < compareToSymbols.length) {
                if (compareWhatSymbols[k] == compareToSymbols[j]) {
                    matchSymbols++;
                    if(k >= compareWhatSymbols.length - 1){
                        break;
                    }
                    k++;
                } else {
                    matchSymbols = 0;
                }
                j++;
                if (maxMatchSymbols < matchSymbols) {
                    maxMatchSymbols = matchSymbols;
                }
            }
            i++;
            j = 0;
        }

        return (maxMatchSymbols == 0) ? 0 : maxMatchSymbols * 100 / compareWhatSymbols.length;
    }

    private char[] getValueChars(String message) {
        return message.toLowerCase().replaceAll("\\s+", "").toCharArray();
    }
}