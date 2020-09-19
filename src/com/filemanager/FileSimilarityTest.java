package com.filemanager;



import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileSimilarityTest {

    List<String> directories = new ArrayList<>();
    List<String> files = new ArrayList<>();

    @BeforeAll
    void  init(){
        directories.add("PwC");
        directories.add("Company A");
        directories.add("PwD");
        directories.add("Company B");
        files.add("Company_4543.txt");
        files.add("CompanyA_3443.txt");
        files.add("PwL_546.doc");
    }

    @Test
    void test_existCompany(){
        FileSimilarity similarity = new FileSimilarity();
        String actual = similarity.directorySorter(directories,  "Company A_3443.txt").get();
        assertEquals("Company A", actual);
    }

    @Test
    void test_fallsCompany(){
        FileSimilarity similarity = new FileSimilarity();
        assertEquals("Multiple options: 1) PwC 2) PwD", similarity.directorySorter(directories,  "PwL_546.doc").get());
    }

}