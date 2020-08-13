package me.dogeon.unhash.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Language {

	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
		"assets/lang/app",
		Locale.getDefault(),
		new ResourceBundle.Control() {
			public ResourceBundle newBundle(String base, Locale loc, String fmt, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
				String bund_name = toBundleName(base, loc);
				String res_name = toResourceName(bund_name, "lang");
				try (InputStream stream = loader.getResourceAsStream(res_name)) {
					if (stream != null) {
						try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
							return new PropertyResourceBundle(reader);
						}
					}
				}
				return super.newBundle(base, loc, fmt, loader, reload);
			}
		});

	public static String get(String key) {
		return BUNDLE.getString(key);
	}
}