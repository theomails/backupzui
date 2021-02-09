package net.progressit.backupzui;

import java.util.List;

import net.progressit.backupzui.api.FlavorSettings;

public interface FlavorRegistry {

	List<FlavorSettings> getOrderedFlavorExecs();

	FlavorSettings getSettings(String flavorName);

	List<String> getFlavorNames();

}