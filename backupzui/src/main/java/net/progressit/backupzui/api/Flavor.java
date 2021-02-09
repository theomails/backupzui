package net.progressit.backupzui.api;

import java.nio.file.Path;

public interface Flavor {
	public String getFlavorName();
	public void isMatched(Path pathToFolder);
	/**
	 * <ul><li>Copy only needed/useful files</li>
	 * <li>Copy only if missing or changed</li>
	 * <li>Deleted at source files could probably be appended with .del in backup</li></ul>
	 * @param oriAbsolute
	 * @param destAbsolute
	 * @return
	 */
	public FCopyResult backup(Path oriAbsolute, Path destAbsolute);
}
