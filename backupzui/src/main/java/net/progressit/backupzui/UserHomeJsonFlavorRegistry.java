package net.progressit.backupzui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import net.progressit.backupzui.api.FlavorSettings;

public class UserHomeJsonFlavorRegistry implements FlavorRegistry {
	
	public static class UserHomeJsonFlavorConfig{
		private float version;
		private String versionDate;
		private List<FlavorSettings> settings;
	}
	
	private UserHomeJsonFlavorConfig config = null;
	private final List<FlavorSettings> flavorExecs = new ArrayList<>();
	private final Map<String, FlavorSettings> flavorExecMap = new LinkedHashMap<>();
	
	@Inject
	public UserHomeJsonFlavorRegistry() {
		load();
	}
	
	private void load() {
		File userHomeJsonFlavorConfig = getConfigFile();
		if(userHomeJsonFlavorConfig.exists()) {
			Gson g = new Gson();
			try(FileReader fr = new FileReader(userHomeJsonFlavorConfig)){
				config = g.fromJson(fr, UserHomeJsonFlavorConfig.class);
				if(config!=null) {
					init(config.settings);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void init(List<FlavorSettings> settings) {
		flavorExecs.clear();
		flavorExecs.addAll(settings);
		for(FlavorSettings f:flavorExecs) {
			flavorExecMap.put(f.getFlavorName(), f);
		}
	}
	
	@Override
	public List<FlavorSettings> getOrderedFlavorExecs() {
		return flavorExecs;
	}
	@Override
	public FlavorSettings getSettings(String flavorName) {
		FlavorSettings settings =  flavorExecMap.get(flavorName);
		return settings;
	}
	@Override
	public List<String> getFlavorNames(){
		List<String> names = new ArrayList<>();
		names.addAll( flavorExecMap.keySet() );
		return names;
	}

	@Override
	public boolean isAvailable() {
		return config != null;
	}

	@Override
	public void saveSettings(List<FlavorSettings> settings) {
		UserHomeJsonFlavorConfig newConfig = new UserHomeJsonFlavorConfig();
		if(config==null) {
			newConfig.version = 0.1f;
		}else {
			newConfig.version = config.version + 0.1f;
		}
		newConfig.versionDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		newConfig.settings = settings;
		
		saveConfig(newConfig);
	}

	private void saveConfig(UserHomeJsonFlavorConfig inConfig) {
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		File userHomeJsonFlavorConfig = getConfigFile();
		try(FileWriter fw = new FileWriter(userHomeJsonFlavorConfig)){
			g.toJson(inConfig, fw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	private File getConfigFile() {
		String userHome = System.getProperty("user.home");
		File userHomeJsonFlavorConfig = new File(userHome, "backupzui.config.json");	
		return userHomeJsonFlavorConfig;
	}
	
	@Override
	public float getVersion() {
		return config==null?0:config.version;
	}

	@Override
	public String getVersionDate() {
		return config==null?"-":config.versionDate;
	}
}
