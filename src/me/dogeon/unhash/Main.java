package me.dogeon.unhash;

import java.util.Locale;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import me.dogeon.unhash.ui.MainPane;
import me.dogeon.unhash.util.Language;

public class Main extends Application{

	@Override
	public void start(Stage mainStage) {
		Scene scene = new Scene(new MainPane(), 650, 700);
		mainStage.setTitle(Language.get("main.title"));
		mainStage.setScene(scene);
		try {
			scene.getStylesheets().add("assets/style.css");
		} catch (Exception e) {}
		mainStage.getIcons().add(new Image("assets/icon.png"));
		mainStage.show();
	}

	public static void main(String[] args) {
		if (args.length >= 2) {
			Locale.setDefault(new Locale(args[0], args[1]));
		}
		launch(args);
	}
}