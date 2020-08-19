package me.dogeon.unhash;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.dogeon.unhash.core.Unhash;
import me.dogeon.unhash.core.Variables;
import me.dogeon.unhash.util.Language;

public class Main extends Application {

    public static ProgressBar progressbar = new ProgressBar();
    public static Label prompt = new Label();

    static TextField mchomeTF;
    static ComboBox<String> versionCB;

    enum MainLabel {
        FD("main.fclim.desc", 6),
        FL("main.fclim.label", 4),
        HS("main.hashtable.desc", 8),
        MC("main.mchome.label", 0),
        MN("main.desc", 9),
        VR("main.version.label", 2);

        MainLabel(String translate, int y) {
            this.translate = translate;
            this.y = y;
        }

        private final String translate;
        private final int y;
        private Label label;
    }

    @Override
    public void start(Stage mainStage) {
        // grid pane init
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // add labels
        for (MainLabel ml : MainLabel.values()) {
            ml.label = new Label(Language.get(ml.translate));
            ml.label.setWrapText(true);
            grid.add(ml.label, 0, ml.y);
        }
        MainLabel.FD.label.setId("fclim-desc");
        MainLabel.MN.label.setId("main-desc");
        MainLabel.HS.label.setId("hashtable-desc");

        // other components
        // mchome dir
        mchomeTF = new TextField(Variables.mchome);
        mchomeTF.textProperty().addListener((obs, o, n) -> Variables.mchome = n);

        Button mchomeButton = new Button("...");
        mchomeButton.setOnAction((e) -> showFileChooser());

        HBox mchomeHB = new HBox();
        mchomeHB.getChildren().addAll(mchomeTF, mchomeButton);
        HBox.setHgrow(mchomeTF, Priority.ALWAYS);
        mchomeHB.setMaxWidth(Float.MAX_VALUE);
        grid.add(mchomeHB, 0, 1);

        // assets version chooser
        versionCB = new ComboBox<>();
        updateVersions();
        versionCB.getSelectionModel().selectedIndexProperty().addListener(
                (obs, o, n) -> Variables.mcversion = versionCB.getItems().get((int) n));
        versionCB.setMaxWidth(Float.MAX_VALUE);
        grid.add(versionCB, 0, 3);

        // file count limit input
        TextField fclimInput = new TextField();
        fclimInput.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.length() == 0) {
                Variables.fclimit = -1;
                return;
            }
            try {
                Variables.fclimit = Integer.parseInt(n);
            } catch (Exception e) {
                fclimInput.setText(o);
            }
        });
        grid.add(fclimInput, 0, 5);

        // hashtable checkbox
        CheckBox hashtableCB = new CheckBox(Language.get("main.hashtable.label"));
        hashtableCB.selectedProperty().addListener(
                (obs, o, n) -> Variables.writeHashtable = n);
        grid.add(hashtableCB, 0, 7);

        // commit
        Button commitButton = new Button(Language.get("main.commit"));
        commitButton.setMaxWidth(Float.MAX_VALUE);
        commitButton.setOnAction((e) -> Unhash.unhash());
        grid.add(commitButton, 0, 10);

        progressbar.setMaxWidth(Float.MAX_VALUE);
        progressbar.setVisible(false);
        grid.add(progressbar, 0, 11);

        prompt.setWrapText(true);
        prompt.setVisible(false);
        grid.add(prompt, 0, 12);

        // scene & stage init
        Scene scene = new Scene(grid, 560, 560);
        mainStage.setTitle(Language.get("main.title"));
        mainStage.setScene(scene);
        try {
            scene.getStylesheets().add("assets/style.css");
        } catch (Exception ignored) {
        }
        mainStage.getIcons().add(new Image("assets/icon.png"));
        mainStage.show();
    }

    public static void main(String[] args) {
        if (args.length >= 2) Locale.setDefault(new Locale(args[0], args[1]));
        launch(args);
    }

    private static void showFileChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(Variables.mchome));
        try {
            File f = chooser.showDialog(null);
            Variables.mchome = f.toString();
            mchomeTF.setText(Variables.mchome);
            updateVersions();
        } catch (Exception e) {
            System.out.println("hi");
        }
    }

    private static void updateVersions() {
        try {
            versionCB.getItems().clear();
            for (String versionName : Objects.requireNonNull(new File(Variables.mchome, "assets/indexes").list())) {
                if (versionName.endsWith(".json"))
                    versionCB.getItems().add(versionName.substring(0, versionName.length() - 5));
            }
            if (versionCB.getItems().size() > 0) {
                versionCB.setValue(versionCB.getItems().get(0));
                Variables.mcversion = versionCB.getValue();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, Language.get("error.non_mcfolder")).showAndWait();
            e.printStackTrace();
        }
    }

    public static void setProgress(float val) {
        progressbar.setProgress(val);
    }

    public static void setPrompt(String text) {
        prompt.setText(text);
    }
}