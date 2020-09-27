package com.filemanager.controller;

import com.filemanager.entity.OperationReport;
import com.filemanager.service.FileManager;
import com.filemanager.service.comparator.FileComparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for main UI window
 *
 * @author Andrii Oliinyk
 */
@Log4j2
public class PrimaryStageController {

    @FXML
    private Button filesFolder;

    @FXML
    private Button targetFolder;

    @FXML
    private TextField filesFolderField;

    @FXML
    private TextField targetFolderField;

    @FXML
    private Button sort;

    @FXML
    private CheckBox filesSubfolders;

    @FXML
    private CheckBox targetSubfolders;

    @FXML
    private ListView<String> listView;

    @FXML
    private TextField percentageMin;

    @FXML
    private ProgressBar progressBar;

    private List<Path> directories = new ArrayList<>();

    private List<Path> filesList = new ArrayList<>();

    private static final String SORT_STARTED = "Sorting files started in new thread";

    @FXML
    public void handleMouseClick(MouseEvent element) throws IOException {
        //TODO fix bug with opening
//        if(!Desktop.isDesktopSupported()){
//            System.out.println("Desktop is not supported");
//            return;
//        }
//        Desktop desktop = Desktop.getDesktop();
//        File file = new File(listView.getSelectionModel().getSelectedItem().toString());
//        if(file.exists()) desktop.open(file);
    }

    private static final String FILE_NOT_VALID_MESSAGE = "Files name is not valid";

    /**
     *  Top dialog field
     * * @param event
     */
    public void selectTargetFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            targetFolderField.setText(selectedDirectory.getPath());
            if (targetSubfolders.isSelected()) {
                setListAllFiles(new File(targetFolderField.getText()), true);
            } else {
                setListDirectFiles(new File(targetFolderField.getText()), true);
            }
        } else {
            log.error(FILE_NOT_VALID_MESSAGE);
            targetFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    /**
     *  Bottom dialog field
     * * @param event
     */
    public void selectFilesDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            filesFolderField.setText(selectedDirectory.getPath());
            if (filesSubfolders.isSelected()) {
                setListAllFiles(new File(filesFolderField.getText()), false);
            } else {
                setListDirectFiles(new File(filesFolderField.getText()), false);
            }
        } else {
            log.error(FILE_NOT_VALID_MESSAGE);
            filesFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    /**
     *  Top event button for selecting destination directory
     *  @param event
     */
    public void selectTargetSolders(ActionEvent event) {
        if (targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() &&
                !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            if (targetSubfolders.isSelected()) {
                setListAllFiles(new File(targetFolderField.getText()), true);
            } else {
                setListDirectFiles(new File(targetFolderField.getText()), true);
            }
        }
    }


    /**
     *  Event button for selecting files directory
     *  @param event
     */
    public void selectFileSolders(ActionEvent event) {
        if (filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() &&
                !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            if (filesSubfolders.isSelected()) {
                setListAllFiles(new File(filesFolderField.getText()), false);
            } else {
                setListDirectFiles(new File(filesFolderField.getText()), false);
            }
        }
    }

    /**
     *  Set min limit for matching filter
     * @param event
     * @return
     */
    public boolean setPercentageMin(ActionEvent event) {
        if (!Pattern.matches("[0-9]+", percentageMin.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Percentage of match can include only digits");
            alert.setHeaderText("Invalid input");
            alert.show();
            return false;
        }
        return true;
    }


    /**
     * Sort button (main activities)
     * @param event
     */
    public void sort(ActionEvent event) {
        if (!setPercentageMin(new ActionEvent())) {
            return;
        }
        if (targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() && !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE) &&
                filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() && !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            HashMap<String, Path> directoriesMap = new HashMap<>();
            for (Path path : directories) {
                directoriesMap.put(FilenameUtils.removeExtension(path.getFileName().toString()), path);
            }
            HashMap<String, Path> filesMap = new HashMap<>();
            for (Path path : filesList) {
                filesMap.put(FilenameUtils.removeExtension(path.getFileName().toString()), path);
            }
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            sort.setDisable(true);
            FileManager fileManager = new FileManager
                    (new FileComparator(),
                            Paths.get(filesFolderField.getText()),
                            directoriesMap,
                            filesMap,
                            progressBar,
                            Integer.parseInt(percentageMin.getText()));
            ExecutorService executorService
                    = Executors.newFixedThreadPool(1);
            log.debug(SORT_STARTED);
            executorService.execute(fileManager);
            progressBar.setVisible(false);
            sort.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Target and Files folders can't be empty");
            alert.setHeaderText("Invalid input");
            alert.show();
        }
    }


    /**
     *  For search directories or files in current and inner directories of root
     *
     * @param directory root
     * @param isDirectories set fitter for directory or keep it for files.
     *
     */
    private void setListAllFiles(File directory, boolean isDirectories) {
        listView.getItems().clear();
        if (directory == null) {
            return;
        }
        try (Stream<Path> allPaths = Files.walk(directory.toPath())) {

            List<Path> paths = (isDirectories) ?
                    allPaths
                            .filter(Files::isDirectory)
                            .collect(Collectors.toList()) :
                    allPaths
                            .filter(Files::isRegularFile)
                            .collect(Collectors.toList());

            for (Path path : paths) {
                if(!path.toString().contains(OperationReport.REPORT_TARGET_DIRECTORY_NAME)){
                    listView.getItems().add(path.toString());
                }
            }
            if (isDirectories) {
                directories = paths;
            } else {
                filesList = paths;
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Error with files display, pls try again");
            alert.setHeaderText("Invalid files");
            alert.show();
            listView.getItems().add("Invalid files");
        }
    }

    /**
     *  For search directories or files only in current root directory
     *
     * @param directory root
     * @param isDirectories set fitter for directory or keep it for files.
     *
     */
    private void setListDirectFiles(File directory, boolean isDirectories) {
        listView.getItems().clear();
        if (directory == null) {
            return;
        }
        List<File> files = Arrays.asList(Objects.requireNonNull(directory.listFiles()));
        for (File file : files) {
            if (file.getName().contains(FileManager.REPORT_NAME)) {
                continue;
            }
            if (isDirectories) {
                if (file.isDirectory()) {
                    listView.getItems().add(file.getPath());
                }
            } else {
                if (file.isFile()) {
                    listView.getItems().add(file.getPath());
                }
            }
        }
        if (isDirectories) {
            directories = files.stream().map(File::toPath).filter(Files::isDirectory).collect(Collectors.toList());
        } else {
            filesList = files.stream().map(File::toPath).filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }
}