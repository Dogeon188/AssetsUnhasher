package me.dogeon.unhash.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import me.dogeon.unhash.Main;
import me.dogeon.unhash.util.Language;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Unhash {

	public static void unhash() {
        if (!Variables.flagUnhashing) {
            new Thread(() -> {
				Variables.flagUnhashing = true;
            	try { _unhash(); } catch (Exception e) {
            		Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, Language.get("error.unknown")).showAndWait());
            	}
				Variables.flagUnhashing = false;
            }).start();
        }
    }

    private static void _unhash() throws Exception{
		File src_dir = new File(Variables.mchome, "assets/objects");
		File dst_dir = new File(Variables.mchome, "assets_unhash/" + Variables.mcversion);
		File hashtable_path = new File(dst_dir.toString() + "/hash.tmp");

		if (!src_dir.exists()) {
			Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, Language.get("error.non_mcfolder")).showAndWait());
            return;
		}

		dst_dir.mkdirs();

		JSONObject obj;
		obj = (JSONObject) ( (JSONObject) new JSONParser().parse(new FileReader(
				String.format("%s/assets/indexes/%s.json", Variables.mchome, Variables.mcversion)
		))).get("objects");

		int fileCount = 0;
		int fileCountLimit = Variables.fclimit;
		if (fileCountLimit == -1) fileCountLimit = obj.size();

		FileWriter hashtable = new FileWriter(hashtable_path);

		Main.progressbar.setVisible(true);
        Main.prompt.setVisible(true);

		for (Object key : obj.keySet()) {
			if (fileCount >= fileCountLimit) break;
			String hash_str = ((JSONObject) obj.get(key)).get("hash").toString();

			File dst_path = new File(dst_dir, key.toString());
			File src_path = new File(src_dir, hash_str.substring(0,2) + "/" + hash_str);
			new File(dst_path.getParent()).mkdirs();
			copyFile(src_path, dst_path);

			float fcp = 1f * fileCount / fileCountLimit;

            Platform.runLater(() -> {
                Main.setProgress(fcp);
                Main.setPrompt(String.format(
                	Language.get("prog.prompt"),
                	Math.round(fcp * 100),
                	hash_str,
                	key.toString()
                ));
            });

			hashtable.write(String.format("%s -> %s\n", hash_str, key.toString()));

			fileCount++;
		}

		hashtable.close();
		if (Variables.writeHashtable) {
			copyFile(hashtable_path, new File(dst_dir.toString() + ".txt"));
		}
		hashtable_path.delete();

		Main.progressbar.setVisible(false);
        Platform.runLater(() -> Main.setPrompt(Language.get("done.label")));
        Thread.sleep(1000);
        Main.prompt.setVisible(false);
	}

	private static void copyFile(File src, File dest) throws IOException {
		try (FileChannel i = new FileInputStream(src).getChannel(); FileChannel o = new FileOutputStream(dest).getChannel()) {
			o.transferFrom(i, 0, i.size());
		}
	}
}