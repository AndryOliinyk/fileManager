package com.filemanager.controller;

import com.filemanager.service.FileManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public void selectTargetFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            targetFolderField.setText(selectedDirectory.getPath());
            if (!targetSubfolders.isSelected()) {
                setListAllFiles(new File(targetFolderField.getText()), true);
            } else {
                setListDirectFiles(new File(targetFolderField.getText()), true);
            }
        } else {
            targetFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    public void selectFilesDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            filesFolderField.setText(selectedDirectory.getPath());
            if (!filesSubfolders.isSelected()) {
                setListAllFiles(new File(filesFolderField.getText()), false);
            } else {
                setListDirectFiles(new File(filesFolderField.getText()), false);
            }
        } else {
            filesFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    public void selectTargetSolders(ActionEvent event) {
        if (targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() &&
                !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            if (!targetSubfolders.isSelected()) {
                setListAllFiles(new File(targetFolderField.getText()), true);
            } else {
                setListDirectFiles(new File(targetFolderField.getText()), true);
            }
        }
    }

    public void selectFileSolders(ActionEvent event) {
        if (filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() &&
                !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            if (!filesSubfolders.isSelected()) {
                setListAllFiles(new File(filesFolderField.getText()), false);
            } else {
                setListDirectFiles(new File(filesFolderField.getText()), false);
            }
        }
    }

    public boolean setPercentageMin(ActionEvent event) {
        if (!Pattern.matches("[0-9]+", percentageMin.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Percentage of match can include only digits");
            alert.setHeaderText("Invalid input");
            alert.show();
            return false;
        }
        return true;
    }

    public void sort(ActionEvent event) {
        if (!setPercentageMin(new ActionEvent())) {
            return;
        }
        if (targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() && !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE) &&
                filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() && !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            HashMap<String, Path> directoriesMap = new HashMap<>();
            for (Path path : directories) {
                directoriesMap.put(path.getFileName().toString(), path);
            }
            HashMap<String, Path> filesMap = new HashMap<>();
            for (Path path : filesList) {
                filesMap.put(path.getFileName().toString(), path);
            }
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            sort.setDisable(true);
            FileManager fileManager = new FileManager(directoriesMap, filesMap, progressBar, Integer.parseInt(percentageMin.getText()));
            ExecutorService executorService
                    = Executors.newFixedThreadPool(1);
            executorService.execute(fileManager);
            progressBar.setVisible(false);
            sort.setDisable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Target and Files folders can't be empty");
            alert.setHeaderText("Invalid input");
            alert.show();
        }
    }

    private void setListDirectFiles(File directory, boolean directories) {
        listView.getItems().clear();
        if (directory == null){
            return;
        }
        try (Stream<Path> allPaths = Files.walk(directory.toPath())) {

            List<Path> paths = (directories) ?
                    allPaths
                            .filter(Files::isDirectory)
                            .collect(Collectors.toList()) :
                    allPaths
                            .filter(Files::isRegularFile)
                            .collect(Collectors.toList());

            for (Path path : paths) {
                listView.getItems().add(path.toString());
            }
            filesList = paths;
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Error with files display, pls try again");
            alert.setHeaderText("Invalid files");
            alert.show();
            listView.getItems().add("Invalid files");
        }
    }

    private void setListAllFiles(File directory, boolean directories) {
        listView.getItems().clear();
        if (directory == null){
            return;
        }
        List<File> files = Arrays.asList(directory.listFiles());
        for (File file : files) {
            if (directories) {
                if (file.isDirectory()) {
                    listView.getItems().add(file.getPath());
                }
            } else {
                if (file.isFile()) {
                    listView.getItems().add(file.getPath());
                }
            }
        }
        filesList = files.stream().map(File::toPath).collect(Collectors.toList());
    }
}