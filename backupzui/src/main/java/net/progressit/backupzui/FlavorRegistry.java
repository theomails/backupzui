package net.progressit.backupzui;

import java.util.List;

import net.progressit.backupzui.api.FlavorSettings;

public interface FlavorRegistry {

	boolean isAvailable();
	
	List<FlavorSettings> getOrderedFlavorExecs();

	FlavorSettings getSettings(String flavorName);
	
	void saveSettings(List<FlavorSettings> settings);

	List<String> getFlavorNames();
	
	float getVersion();
	
	String getVersionDate();

}