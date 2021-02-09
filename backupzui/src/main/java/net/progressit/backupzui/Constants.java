package net.progressit.backupzui;

public class Constants {
	public static final Constants INSTANCE = new Constants();
	/**
	 * Returns all the Known flavors as strings. 
	 * <p>Potentially, flavor could indicate not only which files to copy, but also
	 * dictate how deletions and additions will be handled. On top of this, they could
	 * even decide the list of operations that would be possible on the folder.
	 * @return
	 */
	public String[] getKnownFolderFlavors() {
		return new String[] {
				"drive","drive-level1", // C:// etc, and base folders of the drive, eg. C:/THEO
				"windows", "sys-misc", "program-files", "recycle-bin", //OS start
				"user-home","user-documents","user-media","user-desktop","user-downloads", //OS end
				"backup", // Already backup folders
				"program-stores", // Maven repository etc.
				"eclipse-java","eclipse-ws", //code-start
				"zide-project","zide-ws",
				"vue-project","vue-ws",
				"static-site", //code-end
				"designs","docs-and-bills","data-and-dumps",
				"generic" //Last resort flavor, where everything in the folder is copied intact.
			};
	}
}
