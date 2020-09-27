package com.filemanager.service;


import com.filemanager.entity.Entry;
import com.filemanager.service.comparator.FileComparator;
import de.saxsys.javafx.test.JfxRunner;


import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ProgressBar;


import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(JfxRunner.class)
@Ignore("Not finished")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileManagerTest {

    String directoryOne = "company_1";
    String directoryTwo = "company_2";
    String directoryThree = "some company";
    String fileOne = "comp_file.pdf";
    String fileTwo = "company1_42342343254325435.txt";
    String fileThree = "someCompany 43f4943857384.log";
    String fileFour = "Company general_435345364546tg.js";
    String fileFive = "company some_345235345345.java";

    private FileManager fileManager;

    FileComparator comparator = mock(FileComparator.class);
    HashMap<String, Path> directories;
    HashMap<String, Path> files;

    @BeforeAll
    void init() {
        new JFXPanel();
        ProgressBar progressBar = new ProgressBar();
        directories = new HashMap<>();
        directories.put(directoryOne, Paths.get(directoryOne));
        directories.put(directoryTwo, Paths.get(directoryTwo));
        directories.put(directoryThree, Paths.get(directoryThree));
        files = new HashMap<>();
        files.put(fileOne, Paths.get(fileOne));
        files.put(fileTwo, Paths.get(fileTwo));
        files.put(fileThree, Paths.get(fileThree));
        files.put(fileFour, Paths.get(fileFour));
        files.put(fileFive, Paths.get(fileFive));
        fileManager = new FileManager(comparator, null, directories, files, progressBar, 20);
    }

//    @Test
//    void call_shouldReturnReport_whenInputValid() {
//        when(comparator.getMaxMatched(anyString(), any(), anyInt())).thenReturn(new Entry<>("some company", 50));
//        fileManager.call();
//    }
}