package net.progressit.backupzui.logic;

import java.nio.file.Path;

import net.progressit.backupzui.api.FlavorSettings;

public interface FlavorService {

	String detectFlavor(Path folder, FlavorSettings currentFlavorSettingsOpt);

	boolean isFolderAllowed(FlavorSettings flavorSettings, Path dir);

	boolean isFileAllowed(FlavorSettings flavorSettings, Path file);

}