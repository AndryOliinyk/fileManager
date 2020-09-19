package com.filemanager;


import com.sun.xml.internal.ws.api.ha.HaInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Controller {

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
    private ListView<Comparable> listView;

    @FXML
    private TextField percentageMin;

    @FXML
    private  ProgressBar progressBar;

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
            setListDirectory(selectedDirectory, targetSubfolders.isSelected());
        } else {
            targetFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    public void selectFilesDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            filesFolderField.setText(selectedDirectory.getPath());
            setListFiles(selectedDirectory, filesSubfolders.isSelected());
        } else {
            filesFolderField.setText(FILE_NOT_VALID_MESSAGE);
        }
    }

    public void selectTargetSolders(ActionEvent event) {
        if (targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() &&
                !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            setListDirectory(new File(targetFolderField.getText()), targetSubfolders.isSelected());
        }
    }

    public void selectFileSolders(ActionEvent event) {
        if (filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() &&
                !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)) {
            setListFiles(new File(filesFolderField.getText()), filesSubfolders.isSelected());
        }
    }


    public void sort(ActionEvent event) {
        if(targetFolderField.getText() != null && !targetFolderField.getText().isEmpty() && !targetFolderField.getText().equals(FILE_NOT_VALID_MESSAGE) &&
                filesFolderField.getText() != null && !filesFolderField.getText().isEmpty() && !filesFolderField.getText().equals(FILE_NOT_VALID_MESSAGE)){
            progressBar.setVisible(true);

            HashMap<String, Path> directoriesMap = new HashMap<>();
            for (Path path: directories){
                directoriesMap.put(path.getFileName().toString(), path);
            }
            HashMap<String, Path> filesMap = new HashMap<>();
            for (Path path: filesList){
                filesMap.put(path.getFileName().toString(), path);
            }
            System.out.println("ttt");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Target and Files folders can't be empty");
            alert.setHeaderText("Invalid input");
            alert.show();
        }
    }

    private List<Path> setListFiles(File directory, boolean subDirectory) {
        listView.getItems().clear();
        if (subDirectory) {
            try {
                List<Path> paths = Files.walk(directory.toPath())
                        .filter(Files::isRegularFile).collect(Collectors.toList());
                for (Path path : paths) {
                    listView.getItems().add(path);
                }
                filesList = paths;
                return filesList;
            } catch (IOException e) {
                listView.getItems().add("Error");
            }
        } else {
            List<File> files = Arrays.asList(directory.listFiles());
            assert files != null;
            for (File file : files) {
                if (file.isFile()) {
                    listView.getItems().add(file.getPath());
                }
            }
            filesList = files.stream().map(File::toPath).collect(Collectors.toList());
            return filesList;
        }
        return new ArrayList<>();
    }

    private  List<Path> setListDirectory(File directory, boolean subDirectory) {
        listView.getItems().clear();
        if (subDirectory) {
            try {
                List<Path> paths = Files.walk(directory.toPath())
                        .filter(Files::isDirectory).collect(Collectors.toList());
                for (Path path : paths) {
                    listView.getItems().add(path);
                }
                directories = paths;
                return directories;
            } catch (IOException e) {
                listView.getItems().add("Error");
            }
        } else {
            List<File> folder = Arrays.asList(directory.listFiles());
            assert folder != null;
            for (File file : folder) {
                if (file.isDirectory()) {
                    listView.getItems().add(file.getPath());
                }
            }
            directories = folder.stream().map(File::toPath).collect(Collectors.toList());
            return directories;
        }
        return new ArrayList<>();
    }
}
