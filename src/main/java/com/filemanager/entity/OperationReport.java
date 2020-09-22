package com.filemanager.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Optional;

@Data
@Log4j2
public class OperationReport {

    private String name;
    @Setter(AccessLevel.NONE)
    private LocalDate reportDay;
    private LinkedList<String> record;
    private static final String NOT_DIRECTORY_MESSAGE = "Path '{}' isn't directory. Can't continue";
    private static final String INVALID_WRITE_MESSAGE = "Can't write records to file :'{}'";
    private static final String FILE_TYPE = ".txt";


    public OperationReport(String name) {
        this.name = name;
        reportDay = LocalDate.now();
        record = new LinkedList<>();
    }

    /**
     * Save report as txt file
     *
     * @param directory where report need to be saved
     * @return report in txt file
     */
    public Optional<File> createDocument(Path directory) {
        if (!Files.isDirectory(directory)) {
            log.debug(NOT_DIRECTORY_MESSAGE, directory);
            return Optional.empty();
        }
        String reportName = directory + File.separator + name + FILE_TYPE;
        File report = new File(reportName);
        try (FileWriter writer = new FileWriter(report)) {
            for (String result : record) {
                writer.write(result + System.lineSeparator());
            }
            return Optional.of(report);
        } catch (IOException e) {
            log.debug(INVALID_WRITE_MESSAGE, reportName);
            return Optional.empty();
        }
    }
}