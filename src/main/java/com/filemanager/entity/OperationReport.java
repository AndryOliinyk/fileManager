package com.filemanager.entity;

import com.filemanager.service.FileOperationService;
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
import java.util.Set;

import static java.lang.String.format;

/**
 *  Class for accumulation results of program work
 *
 * @author Andrii Oliinuk
 */
@Data
@Log4j2
public class OperationReport {
    private String name;
    @Setter(AccessLevel.NONE)
    private LocalDate reportDay;
    private LinkedList<String> record;
    @Setter(AccessLevel.NONE)
    private Path saveDirectory;
    private Set<File> invalidFiles;
    public static final String REPORT_TARGET_DIRECTORY_NAME = "file-manager-result";
    private static final String NOT_DIRECTORY_MESSAGE = "Path '{}' isn't directory. Can't continue";
    private static final String INVALID_WRITE_MESSAGE = "Can't write records to file :'{}'";
    private static final String FILE_TYPE = ".txt";
    private static final String INVALID_FILE_MOVEMENT_MESSAGE = "Can't move invalid file '%s' into required report directory '%s'";

    private static final String REMOVE_OLD_DIRECTORY_MESSAGE = "Old report-result directory {} was successfully removed";

    public OperationReport( String name,  Path saveDirectory,  Set<File> invalidFiles) {
        this.name = name;
        reportDay = LocalDate.now();
        record = new LinkedList<>();
        this.saveDirectory = saveDirectory;
        this.invalidFiles = invalidFiles;
    }

    /**
     * Save report as txt file
     *
     * @return report in txt file
     */
    public Optional<File> save() {
        if (!Files.isDirectory(saveDirectory)) {
            log.debug(NOT_DIRECTORY_MESSAGE, saveDirectory);
            return Optional.empty();
        }
        File targetDirectory = new File(saveDirectory.toString() + File.separator + REPORT_TARGET_DIRECTORY_NAME);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdir();
        }
        String reportName = targetDirectory + File.separator + reportDay + "_" + name + FILE_TYPE;
        if (new File(reportName).exists()) {
            if(new File(reportName).delete()){
                log.debug(REMOVE_OLD_DIRECTORY_MESSAGE, reportName);
            }
        }
        FileOperationService operationService = new FileOperationService();
        for (File invalidFile : invalidFiles) {
            if(!operationService.copyFile(invalidFile, new File(targetDirectory + File.separator + invalidFile.getName()))){
                record.add(format(INVALID_FILE_MOVEMENT_MESSAGE, invalidFile.getName(), targetDirectory));
            }
        }
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