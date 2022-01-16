package net.progressit.backupzui.api;

import java.nio.file.Path;

public interface Flavor {
	public String getFlavorName();
	public void isMatched(Path pathToFolder);
}
