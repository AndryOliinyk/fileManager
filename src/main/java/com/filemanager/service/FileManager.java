package com.filemanager.service;

import com.filemanager.entity.Entry;
import com.filemanager.entity.OperationReport;
import com.filemanager.service.comparator.FileComparator;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 *  Main functional class (GOD object)
 *  Manage results of filtering, init report file
 */
@Log4j2
public class FileManager extends Task<OperationReport> {

    private final Map<String, Path> directories;
    private final Map<String, Path> files;
    private final ProgressBar progressBar;
    private final int limit;
    private final Path saveDirectory;
    private final FileComparator fileComparator;
    public static final String REPORT_NAME = "file-manager-report";
    private static final String LOW_MATCH_ERROR_MESSAGE = "ERROR - %s. File: '%s'  Can't be matched. The most similar '%s' -  %s percentage";
    private static final String ZERO_MATCH_ERROR_MESSAGE = "ERROR - %s. File: '%s'  Can't be matched with any folder";
    private static final String CONFLICT_ERROR_MESSAGE = "ERROR - %s. %s";
    private static final String THREAD_INTERACTED = "Thread was interacted. {}";

    private static final String FILE_START_MESSAGE = "File '{}' search result Percentage:{}, Directory: {}";

    public FileManager(FileComparator fileComparator,
                       Path saveDirectory,
                       Map<String, Path> directories,
                       Map<String, Path> files,
                       ProgressBar progressBar,
                       int limit) {

        this.directories = directories;
        this.files = files;
        this.progressBar = progressBar;
        this.limit = limit;
        this.fileComparator = fileComparator;
        this.saveDirectory = saveDirectory;
    }

    @Override
    public OperationReport call() {
        LinkedList<String> result = new LinkedList<>();
        Set<File> invalidFiles = new HashSet<>();
        int count = 0;
        FileOperationService fileService = new FileOperationService();
        progressBar.setVisible(true);
        for (Map.Entry<String, Path> file : files.entrySet()) {
            try {
                Entry<String, Integer> matchResult = fileComparator.getMaxMatched(file.getKey(), directories.keySet(), limit);
                int matchScore = matchResult.getValue();
                String targetDirectory = matchResult.getKey();
                log.debug(FILE_START_MESSAGE, file.getKey(), matchScore, targetDirectory);
                if (limit <= matchScore && directories.containsKey(targetDirectory)) {
                    result.add(fileService.manageFile(file.getValue(), directories.get(targetDirectory)));
                } else if (matchScore == 0) {
                    result.add(format(ZERO_MATCH_ERROR_MESSAGE, LocalDateTime.now(), file.getKey()));
                } else {
                    invalidFiles.add(file.getValue().toFile());
                    result.add(format(LOW_MATCH_ERROR_MESSAGE, LocalDateTime.now(), file.getKey(), targetDirectory, matchScore));
                }
            } catch (IllegalAccessError e) {
                log.error(e.getMessage());
                invalidFiles.add(file.getValue().toFile());
                result.add(format(CONFLICT_ERROR_MESSAGE, LocalDateTime.now(), e.getMessage()));
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error(THREAD_INTERACTED, e.getMessage());
                Thread.currentThread().interrupt();
            }
            count++;
            double percentage = (double) count / (double) files.size();
            progressBar.setProgress(percentage);
        }
        OperationReport report = new OperationReport(REPORT_NAME, saveDirectory, invalidFiles);
        report.setRecord(result);
        report.save();
        progressBar.setVisible(false);
        return report;
    }
}