package net.progressit.backupzui;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;

import net.progressit.backupzui.api.FlavorSettings;

public class FlavorRegistryProvider implements Provider<FlavorRegistry>{
	
	@Inject
	public FlavorRegistryProvider() {
	}
	
	public FlavorRegistry get() {
		UserHomeJsonFlavorRegistry jsonRegistry = new UserHomeJsonFlavorRegistry();
		if(jsonRegistry.isAvailable()) {
			return jsonRegistry;
		}else {
			HardcodedFlavorRegistry hcRegistry = new HardcodedFlavorRegistry();
			List<FlavorSettings> hcSettings = hcRegistry.getOrderedFlavorExecs();
			jsonRegistry.saveSettings(hcSettings);
			//There is no API to reload. So creating new one.
			jsonRegistry = new UserHomeJsonFlavorRegistry();
			return jsonRegistry;
		}
	}
}
