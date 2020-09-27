package com.filemanager.service.comparator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparatorTest {

    Comparator comparator = new Comparator();
    private static final String MESSAGE_ONE = "Company test_3543523656436";
    private static final String MESSAGE_TWO = "test";
    private static final String MESSAGE_THREE = "PwL_546.doc";

    @Test
    void compareValues_shouldGetFull_whenMessagesSame() {
        int actualPercentage = comparator.compareValues(MESSAGE_ONE, "Company test_3543523656436");
        assertEquals(100, actualPercentage);
    }

    @Test
    void compareValues_shouldGetFourPercentage_whenMessagesNotMatch() {
        int actualPercentage = comparator.compareValues(MESSAGE_ONE, "not match");
        assertEquals(4, actualPercentage);
    }

    @Test
    void compareValues_shouldGetPercentage_whenMessagesComparable() {
        int actualPercentage = comparator.compareValues(MESSAGE_ONE, "EST");
        assertEquals(12, actualPercentage);
    }

    @Test
    void compareValues_shouldGetSeventyFivePercentage_whenMessagesComparable() {
        int actualPercentage = comparator.compareValues(MESSAGE_TWO, "EST");
        assertEquals(75, actualPercentage);
    }

    @Test
    void compareValues_shouldGetNotZeroPercentage_whenMessagesComparable() {
        int actualPercentage = comparator.compareValues(MESSAGE_THREE, "PWD");
        assertNotEquals(0, actualPercentage);
    }
}