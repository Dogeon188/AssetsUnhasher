package me.dogeon.unhash.ui;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import me.dogeon.unhash.core.Unhash;
import me.dogeon.unhash.core.Variables;
import me.dogeon.unhash.util.Language;

public class MainPane extends GridPane {

    static ComboBox<String> versionCB;
    static TextField mchomeTF;
    static Button mchomeButton;
    static HBox mchomeHB;
    static TextField fclimInput;
    static CheckBox hashtableCB;
    static Button commitButton;

    public static ProgressBar progressbar = new ProgressBar();
    public static Label prompt = new Label();

    private enum MainLabel {
        MCHOME("main.mchome.label", 0),
        VERSION("main.version.label", 2),
        FCLIM_L("main.fclim.label", 4),
        FCLIM_D("main.fclim.desc", 6),
        HASH("main.hashtable.desc", 8),
        MAIN("main.desc", 9);

        MainLabel(String translate, int y) {
            this.translate = translate;
            this.y = y;
        }

        private final String translate;
        private int y;
        private Label label;
    }

    public MainPane() {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10);
        this.setVgap(15);
        this.setPadding(new Insets(25, 25, 25, 25));

        for (MainLabel ml : MainLabel.values()) {
            ml.label = new Label(Language.get(ml.translate));
            ml.label.setWrapText(true);
            this.add(ml.label, 0, ml.y);
        }
        MainLabel.FCLIM_D.label.setId("fclim-desc");
        MainLabel.MAIN.label.setId("main-desc");
        MainLabel.HASH.label.setId("hashtable-desc");

        addComponents();
    }

    private void addComponents() {
        mchomeTF = new TextField(Variables.mchome);
        mchomeTF.textProperty().addListener((obs, o, n) -> {
            Variables.mchome = n;
        });

        mchomeButton = new Button("...");
        mchomeButton.setOnAction((e) -> { showFileChooser(); });

        mchomeHB = new HBox();
        mchomeHB.getChildren().addAll(mchomeTF, mchomeButton);
        HBox.setHgrow(mchomeTF, Priority.ALWAYS);
        mchomeHB.setMaxWidth(Float.MAX_VALUE);
        this.add(mchomeHB, 0, 1);

        versionCB = new ComboBox<String>();
        updateVersions();
        versionCB.getSelectionModel().selectedIndexProperty().addListener(
            (obs, o, n) -> { Variables.mcversion = versionCB.getItems().get((int) n).toString(); });
        versionCB.setMaxWidth(Float.MAX_VALUE);
        this.add(versionCB, 0, 3);

        fclimInput = new TextField();
        fclimInput.textProperty().addListener((obs, o, n) -> {
            if (n == null || n.length() == 0) {
                Variables.fclimit = -1;
                return;
            }
            try {
                Variables.fclimit = Integer.parseInt(n);
            } catch (Exception e) { fclimInput.setText(o); }
        });
        this.add(fclimInput, 0, 5);

        hashtableCB = new CheckBox(Language.get("main.hashtable.label"));
        hashtableCB.selectedProperty().addListener(
            (obs, o, n) -> { Variables.writeHashtable = n; });
        this.add(hashtableCB, 0, 7);

        commitButton = new Button(Language.get("main.commit"));
        commitButton.setMaxWidth(Float.MAX_VALUE);
        commitButton.setOnAction((e) -> { Unhash.unhash(); });
        this.add(commitButton, 0, 10);

        progressbar.setMaxWidth(Float.MAX_VALUE);
        progressbar.setVisible(false);
        this.add(progressbar, 0, 11);

        prompt.setWrapText(true);
        prompt.setVisible(false);
        this.add(prompt, 0, 12);

    }

    private static void updateVersions() {
        try {
            versionCB.getItems().clear();
            for (String versionName : new File(Variables.mchome, "assets/indexes").list()) {
                if (versionName.substring(versionName.length() - 5) == ".json") {
                    versionCB.getItems().add(versionName.substring(0, versionName.length() - 5));
                }
            }
            versionCB.setValue(versionCB.getItems().get(0));
            Variables.mcversion = versionCB.getValue().toString();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, Language.get("error.non_mcfolder")).showAndWait();
        }
    }

    private static void showFileChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(Variables.mchome));
        File f = chooser.showDialog(null);
        Variables.mchome = f.toString();
        mchomeTF.setText(Variables.mchome);
        updateVersions();
    }

    public static void setProgress(float val) {
        progressbar.setProgress(val);
    }

    public static void setPrompt(String text) {
        prompt.setText(text);
    }
}