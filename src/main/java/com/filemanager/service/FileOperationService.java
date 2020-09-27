package com.filemanager.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static java.lang.String.format;

/**
 * All external operation with files on PC
 */
@Log4j2
public class FileOperationService {

    private static final String SUCCESS_MESSAGE = "File {} was moved to directory {}";
    private static final String FAIL_MESSAGE = "File can't be moved \n\r {}";

    public boolean copyFile(File file, File directory){
        try {
            FileUtils.copyFile(file, directory);
            log.debug(SUCCESS_MESSAGE, file, directory.getParent());
            return true;
        } catch (IOException e){
            log.error(FAIL_MESSAGE, e.getMessage());
            return false;
        }
    }

    String manageFile(Path source, Path destination) {
        try {
            String newDirectory = destination + File.separator  + source.toFile().getName();
            FileUtils.copyFile(source.toFile(), new File(newDirectory));
            return format("SUCCESS - %s . Moved file: '%s' to folder: '%s'", LocalDateTime.now(), source.getFileName(),destination);
        } catch (IOException e) {
            return format("ERROR - %s . File: '%s' can't be moved to folder: '%s'", LocalDateTime.now(), source.getFileName(),destination);
        }
    }
}