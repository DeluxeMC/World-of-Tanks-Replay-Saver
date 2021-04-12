import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.w3c.dom.Text;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class Main extends Application {


    private String txtOrigin;
    private String txtArchive;

    private String rename;

    private String settingsDir;
    private String settingsPathString;

    private File originFile;
    private File archiveFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("WOT Replay Saver");

        primaryStage.setOnCloseRequest(e -> {
            settingsRestartProtocol();
            Platform.exit();
        });

        directoryCreator();

        //button creators
        Button btnSave = new Button("Save Replay");
        Button btnChooseOrigin = new Button();
        Button btnChooseArchive = new Button();

        btnSave.setStyle("-fx-font-size: 15pt;");

        //button icons
        Image open = new Image("icons/open.png");
        ImageView view = new ImageView(open);
        ImageView view2 = new ImageView(open);

        btnChooseOrigin.setGraphic(view);
        btnChooseArchive.setGraphic(view2);

        //labels
        Label lblOrigin = new Label("Replay Folder:");
        Label lblArchive = new Label("Archive Folder:");


        //text field creators
        TextField txtFilePathOrigin = new TextField();
        TextField txtFilePathArchive = new TextField();

        //text field listeners
        txtFilePathOrigin.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                txtOrigin = newValue;
                System.out.println(txtOrigin);
            }
        });
        txtFilePathArchive.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                txtArchive = newValue;
                System.out.println(txtArchive);
            }
        });

        //the reason settings init is down here is because I have to pass the text box objects through the settingsInit() constructor to change the text in the boxes.
        settingsInit(txtFilePathOrigin,txtFilePathArchive);

        //file chooser

            FileChooser fileChooserOrigin = new FileChooser();
            fileChooserOrigin.setTitle("Choose your current \"replay_last_battle\" file");
            fileChooserOrigin.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Replay Files", "*.wotreplay"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            DirectoryChooser directoryChooserArchive = new DirectoryChooser();
            directoryChooserArchive.setTitle("Choose your save location");


        //button listeners
        btnChooseOrigin.setOnAction(event -> {
            originFile = fileChooserOrigin.showOpenDialog(new Stage());
            txtFilePathOrigin.setText(originFile.toString());
            System.out.println("Origin File: "+originFile);
        });
        btnChooseArchive.setOnAction(event -> {
            archiveFile = directoryChooserArchive.showDialog(new Stage());
            txtFilePathArchive.setText(archiveFile.toString());
            System.out.println("Archive File: "+archiveFile);
        });
        btnSave.setOnAction(event -> {
            saveReplay();
        });


        //Layout Design
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);
        //ok for future refference put the allignment declaration ABOVE EVERYTHING ELSE because I went on a wild goose chase trying to center the GridPane.

        HBox hboxOrigin = new HBox();
        HBox hboxArchive = new HBox();
        HBox hboxSave = new HBox();
        hboxOrigin.setSpacing(10);
        hboxArchive.setSpacing(10);
        hboxSave.setSpacing(10);
        hboxSave.setAlignment(Pos.CENTER);

        hboxOrigin.getChildren().addAll(lblOrigin,txtFilePathOrigin,btnChooseOrigin);
        hboxArchive.getChildren().addAll(lblArchive,txtFilePathArchive,btnChooseArchive);
        hboxSave.getChildren().add(btnSave);

        grid.add(hboxOrigin,0,1);
        grid.add(hboxArchive,0,2);
        grid.add(hboxSave,0,3);

        StackPane root = new StackPane();
        root.getChildren().add(grid);
        primaryStage.setScene(new Scene(root,350,200));
        primaryStage.setX(100);
        primaryStage.setY(700);
        primaryStage.show();
    }

    public void saveReplay() {

        Stage dialog = new Stage(); // new stage
        dialog.initModality(Modality.APPLICATION_MODAL);
        // Defines a modal window that blocks events from being
        // delivered to any other application window.
        GridPane gridReplay = new GridPane();
        gridReplay.setPadding(new Insets(10));
        gridReplay.setAlignment(Pos.CENTER);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Button btnRename = new Button("Rename");
        btnRename.setStyle("-fx-font-size: 13pt;");

        TextField txtRename = new TextField();


        txtRename.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("")) {
                rename = newValue;
                System.out.println(rename);
            }
        });

        btnRename.setOnAction(event -> {

            try {

                String tmp;

                tmp = archiveFile.toString() + "\\" + rename + ".wotreplay";

                Path tmpPath = Paths.get(tmp);

                System.out.println(tmp);
                System.out.println(tmpPath);

                if (originFile.exists()) {
                    try {
                        Files.copy(originFile.toPath(), tmpPath);
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    System.out.println(originFile.toPath());
                    System.out.println(archiveFile.toPath());
                    closeProgram();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                System.out.println("Some text fields are blank");
            }
        });

        gridReplay.setHgap(10);
        gridReplay.setVgap(12);

        hbox.getChildren().add(btnRename);

        gridReplay.add(txtRename,0,0);
        gridReplay.add(hbox,0,1);

        Scene dialogScene = new Scene(gridReplay, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    public void directoryCreator() {

        settingsDir = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

        settingsDir += "\\WOT Replay Saver\\";

        File settingsDirectory = new File(settingsDir);
        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }
    }


    //settings reader
    private void settingsInit(TextField otxt, TextField atxt) {

        List<File> fileList = new ArrayList<>();

        settingsPathString = settingsDir + "settings.txt";

        //this is the dirtiest fix for the code being out of order in the book but it works
        //creating the settings file
        try {
            File settingsTmp = new File(settingsPathString);
            if (settingsTmp.createNewFile()) {
                System.out.println("Settings File Created Successfully: " + settingsTmp.getName());
            } else {
                System.out.println("Settings file not created");
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        try {
            //reading settings from txt file
            BufferedReader br = new BufferedReader(new FileReader(settingsPathString));
            String line = "";

            while ((line = br.readLine()) != null) {
                try {
                    if (line.length() > 0) {
                        System.out.println(line);
                        fileList.add(new File(line));
                    }
                } catch (NoSuchElementException NO_ELEMENT) {
                    System.out.println("Finished adding previous folder paths");
                }
            }
            br.close();

            originFile = fileList.get(0);
            archiveFile = fileList.get(1);

            otxt.setText(originFile.toString());
            atxt.setText(archiveFile.toString());
        } catch (IOException io) {
            System.out.println("Error initilizing settings");
            io.printStackTrace();
        } catch (IndexOutOfBoundsException ioobex) {
            System.out.println("No settings exist yet");
        }
    }


    //settings writer
    public void settingsRestartProtocol() {

        //checking and creating the settings file if necessary
        try {
            File settingsStorage = new File(settingsPathString);
            if (settingsStorage.createNewFile()) {
                System.out.println("Settings File Created Successfully: " + settingsStorage.getName());
            } else {
                System.out.println("Settings file not created");
            }
        } catch (IOException io) {
            io.printStackTrace();
        }

        //writing to settings file
        try {
            FileWriter fileWriter = new FileWriter(settingsPathString);

            fileWriter.write(originFile.toString());

            fileWriter.write(System.getProperty("line.separator"));

            fileWriter.write(archiveFile.toString());


            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    //what to do when closing the program
    public void closeProgram() {
        try {
            settingsRestartProtocol();
            Platform.exit();
        }
        catch (Exception ex) {
            System.out.println("Error in closing program" + System.getProperty("line.separator"));
            ex.printStackTrace();
            Platform.exit();
        }
    }
}
