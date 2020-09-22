package com.filemanager.service;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Log4j2
public class FileManager extends Task<String> {

    private final Map<String, Path> directories;
    private final Map<String, Path> files;
    private final ProgressBar progressBar;
    private final int limit;

    public FileManager(Map<String, Path> directories, Map<String, Path> files, ProgressBar progressBar, int limit) {
        this.directories = directories;
        this.files = files;
        this.progressBar = progressBar;
        this.limit = limit;
    }

    @Override
    public String call()  {
        StringBuilder result = new StringBuilder();
        int count = 0;
        NameComparator nameComparator = new NameComparator();
        progressBar.setVisible(true);
        for (Map.Entry<String, Path> file : files.entrySet()) {
            Optional<String> directoryName = nameComparator.directorySorter(directories.keySet(), file.getKey(), limit);
            if (directoryName.isPresent() && directories.containsKey(directoryName.get())) {
                result.append(copyFile(file.getValue(), directories.get(directoryName.get())));
            } else {
                result.append("[ERROR] ").append(LocalDateTime.now()).append(". File: '").append(file).append("'. Can't be matched");
            }
            result.append(System.getProperty("line.separator"));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            count++;
            double percentage = (double) count / (double) files.size();
            progressBar.setProgress(percentage);
        }
        String logFileName = LocalDate.now() + "_File manager report.txt";
        try (FileWriter myWriter = new FileWriter(logFileName)){
            Files.deleteIfExists(Paths.get(logFileName));
            myWriter.write(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressBar.setVisible(false);
        return logFileName;
    }

    private String moveFile(Path source, Path destination) {
        try {
            Files.move(source, Paths.get(destination + File.separator + source.getFileName()));
            return "[INFO] " + LocalDateTime.now() + ". Moved file: '" + source.getFileName() + "' to folder:" + destination;
        } catch (IOException e) {
            return "[ERROR] " + LocalDateTime.now() + ". File: '" + source.getFileName() + "' can't be moved to folder:" + destination;
        }
    }

    private String copyFile(Path source, Path destination) {
        try {
            FileUtils.copyDirectory(source.toFile(), destination.toFile());
            log.debug("");
            return "[INFO] " + LocalDateTime.now() + ". Moved file: '" + source.getFileName() + "' to folder:" + destination;
        } catch (IOException e) {
            log.error("");
            return "[ERROR] " + LocalDateTime.now() + ". File: '" + source.getFileName() + "' can't be moved to folder:" + destination;
        }
    }
}
